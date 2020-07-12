package com.app.web.BottomSheet;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BottomNavigationView bottomNavigationView;


    WebView webView;
    private String webUrl = "https://www.telemart.pk/";
    private BottomSheetDialog bottomSheetDialog;
    ProgressBar progressBarWeb;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;

    SwipeRefreshLayout swipeRefreshLayout;
private int desktopmodevalue=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_main);

        bottomNavigationView =findViewById(R.id.BottomNav);
bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMehod);
        webView = (WebView) findViewById(R.id.myWebView);
        webView.loadUrl(webUrl);
        progressBarWeb = (ProgressBar) findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(this);
        bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
        View bottomSheetDialogView= getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetDialogView);

        View ReloadView=bottomSheetDialogView.findViewById(R.id.Reload);
        View HomeView=bottomSheetDialogView.findViewById(R.id.Home);
        View ShareView=bottomSheetDialogView.findViewById(R.id.share);
        View ShrCrntpgView=bottomSheetDialogView.findViewById(R.id.shrCrntpg);
        View MenuView=bottomNavigationView.findViewById(R.id.nav_menu);
        View RtUsView=bottomSheetDialogView.findViewById(R.id.RtApp);
        View CloseView=bottomSheetDialogView.findViewById(R.id.close);
        View External=bottomSheetDialogView.findViewById(R.id.external);
        View DesktopView=bottomSheetDialogView.findViewById(R.id.desktop);
        ReloadView.setOnClickListener(this);
        HomeView.setOnClickListener(this);
        ShareView.setOnClickListener(this);
        ShrCrntpgView.setOnClickListener(this);
        MenuView.setOnClickListener(this);
        RtUsView.setOnClickListener(this);
        CloseView.setOnClickListener(this);
        External.setOnClickListener(this);
        DesktopView.setOnClickListener(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);


        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.YELLOW,Color.GREEN);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();


                checkConnection();

            }
        });



        if(savedInstanceState !=null){
            webView.restoreState(savedInstanceState);
        }
        else
        {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.reload();
            checkConnection();

        }





        //Solved WebView SwipeUp Problem
        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (webView.getScrollY() == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String s, final String s1, final String s2, final String s3, long l) {

                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {


                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                                request.setMimeType(s3);
                                String cookies = CookieManager.getInstance().getCookie(s);
                                request.addRequestHeader("cookie",cookies);
                                request.addRequestHeader("User-Agent",s1);
                                request.setDescription("Downloading File.....");
                                request.setTitle(URLUtil.guessFileName(s,s2,s3));
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                                s,s2,s3));
                                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.enqueue(request);
                                Toast.makeText(MainActivity.this, "Downloading File..", Toast.LENGTH_SHORT).show();

                            }
//For Permi
                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response ) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }

/*code for tel-link*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                final String Urls =url;
                if(Urls.contains("mailto:") || Urls.contains("sms:") || Urls.contains("tel:")||Urls.contains("whatsapp:")){

                    webView.goBack();
                    webView.goForward();
                    Intent i =new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Urls));
                    startActivity(i);

                }




                super.onPageStarted(view, url, favicon);
            }



            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("telemart.pk")) {
                    view.loadUrl(url);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
                return true;


            }});













        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBarWeb.setVisibility(View.VISIBLE);
                progressBarWeb.setProgress(newProgress);
                /*setTitle("Loading...");
                progressDialog.show();*/
                if(newProgress ==100){

                    progressBarWeb.setVisibility(View.GONE);
                    /*setTitle(view.getTitle());
                    progressDialog.dismiss();*/

                }


                super.onProgressChanged(view, newProgress);
            }
        });







    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMehod =new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch(menuItem.getItemId()) {

                        case R.id.nav_previous:
                            onBackPressed();
                            break;

                        case R.id.nav_next:

                            if (webView.canGoForward()) {
                                webView.goForward();
                            }

                            break;

                       



                    }
                    return false;
                }
            };
/*Code for Deep linking also in AndroidMainefest file*/
    @Override
    protected void onStart() {
        super.onStart();
        try {
            Intent intent= getIntent();
            Uri data=intent.getData();
            webView.loadUrl(data.toString());
        }
        catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finishAffinity();
                        }
                    }).show();
        }
    }

    public void setDesktopMode(WebView webView,boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }












    public void checkConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if(wifi.isConnected()){

            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);


        }
        else if (mobileNetwork.isConnected()){

            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
        else{

            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_previous:
                onBackPressed();
                break;

            case R.id.nav_next:

                if(webView.canGoForward()){
                    webView.goForward();
                }

                break;





        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
















    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id){
            case R.id.nav_menu:
                bottomSheetDialog.show();
                break;

            case R.id.Reload:
                webView.reload();
                checkConnection();

                bottomSheetDialog.dismiss();

                break;
            case R.id.Home:
                webView.loadUrl("https://www.telemart.pk/");

                bottomSheetDialog.dismiss();
                break;
            case R.id.share:
                final String appPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                bottomSheetDialog.dismiss();
                break;
            case R.id.shrCrntpg:
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
                String sharingBody=webView.getUrl();
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                shareIntent.putExtra(Intent.EXTRA_TEXT,sharingBody);
                startActivity(Intent.createChooser(shareIntent,"Share Using"));

                bottomSheetDialog.dismiss();
                break;
            case R.id.RtApp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                bottomSheetDialog.dismiss();
                break;
            case R.id.close:

                bottomSheetDialog.dismiss();
                break;

            case R.id.external:

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.telemart.pk/")));
                bottomSheetDialog.dismiss();
                break;
            case R.id.desktop:
                bottomSheetDialog.dismiss();
                if(desktopmodevalue==0){
                    setDesktopMode(webView,true);
                    desktopmodevalue=1;
                }
                else
                {
                    setDesktopMode(webView,false);
                    desktopmodevalue=0;
                }
                break;

        }
    }


}

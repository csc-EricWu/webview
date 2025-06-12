package com.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private WebView mWebView;
    private boolean hasLoadedPage = false;  // 标记页面是否成功加载
    private boolean pageFailedToLoad = false;  // 标记加载失败
    private boolean loadError = false;

    private final String URL = "https://baidu.com";
    private NetworkReceiver networkReceiver;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exitTaskThreeMonthsAfter();

        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        NetworkReceiver.setCallback(isConnected -> {
            if (isConnected && pageFailedToLoad) {
                runOnUiThread(() -> mWebView.reload());
            }
        });

        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSaveFormData(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);


        mWebView.setWebViewClient(new MyWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!loadError) {
                    hasLoadedPage = true;  // 只有没有错误才算成功加载
                    pageFailedToLoad = false;
                }
                loadError = false;  // 重置，准备下一次加载
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                loadError = true;
                pageFailedToLoad = true;
            }
        });

        // REMOTE RESOURCE
        mWebView.loadUrl(URL);

        // LOCAL RESOURCE
//         mWebView.loadUrl("file:///android_asset/index.html");


    }
    private void exitTaskThreeMonthsAfter() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
//            Toast.makeText(this, BuildConfig.BUILD_DATE, Toast.LENGTH_SHORT).show();
            Date baseDate = formatter.parse(BuildConfig.BUILD_DATE); // 使用构建时的日期
            if (baseDate == null) return;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(baseDate);
            calendar.add(Calendar.MONTH, 2); // 加 2 个月
            Date futureDate = calendar.getTime();
            Date now = new Date();
            if (now.after(futureDate)) {
                // 关闭应用
                finishAffinity(); // 关闭所有 Activity
                System.exit(0);   // 强制退出进程（不推荐在生产使用）
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

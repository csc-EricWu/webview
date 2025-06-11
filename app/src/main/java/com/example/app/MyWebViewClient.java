package com.example.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class MyWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUri(view, Uri.parse(url));
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // 继续加载页面
    }

    private boolean handleUri(WebView view, Uri uri) {
        String scheme = uri.getScheme();
        // 允许 http、https 在 WebView 内加载
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)|| "file".equalsIgnoreCase(scheme)) {
            return false; // WebView 内打开
        }

        // 其他协议交由系统处理，例如 tel:、mailto:、geo: 等
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace(); // 可选：提示用户未安装应用支持该链接
        }
        return true; // 已处理
    }
}

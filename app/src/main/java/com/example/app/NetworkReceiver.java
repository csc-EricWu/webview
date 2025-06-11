package com.example.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 监听网络变化的BroadcastReceiver，兼容低版本Android（API < 23）
 */
public class NetworkReceiver extends BroadcastReceiver {

    public interface NetworkCallback {
        void onNetworkChanged(boolean isConnected);
    }

    private static NetworkCallback callback;

    public static void setCallback(NetworkCallback cb) {
        callback = cb;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isNetworkConnected(context);
        if (callback != null) {
            callback.onNetworkChanged(isConnected);
        }
    }

    /**
     * 判断当前网络是否连接
     * @param context 上下文
     * @return true 已连接，false 未连接
     */
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}

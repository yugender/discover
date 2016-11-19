package com.yahoo.android.discover.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by yboini on 11/18/16.
 */

public class NetworkStatus {
    private static NetworkStatus instance = new NetworkStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    boolean connected = false;

    public static NetworkStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}

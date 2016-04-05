package com.example.kimas.spotspacer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean status = NetworkUtil.getConnectivityStatusString(context);
        Intent i = new Intent("broadcastInternet");
        i.putExtra("connected", status);
        context.sendBroadcast(i);

    }
}
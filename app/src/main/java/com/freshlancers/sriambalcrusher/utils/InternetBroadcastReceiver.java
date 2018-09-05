package com.freshlancers.sriambalcrusher.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.freshlancers.sriambalcrusher.AppController;

/**
 * Created by Shriram on 16-Nov-17.
 */

public class InternetBroadcastReceiver extends BroadcastReceiver {


    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppController.isActivityVisible()) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork == null) {
                    if (firstConnect) {

                        firstConnect = false;
                        Intent i = new Intent();
                        i.setClassName("com.freshlancers.sriambalcrusher", "com.freshlancers.sriambalcrusher.utils.MyDialog");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);

                    } else {
                        firstConnect = false;
                    }
                } else {
                    firstConnect = true;
                }

            } catch (Exception e) {
                Log.e("tag", "no Internet available " + e);
            }
        }

    }

}
package com.freshlancers.sriambalcrusher.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.freshlancers.sriambalcrusher.R;

/**
 * Created by Shriram on 16-Nov-17.
 */

public class MyDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.no_internet);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.setFinishOnTouchOutside(false);

        Button button = (Button) findViewById(R.id.okay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(MyDialog.this)) {
                    finish();
                } else {
                    Toast.makeText(MyDialog.this, "No Internet !!! Please make sure you have an active internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean network = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnected()) {

                network = true;
            } else {
                Log.e("tag UTIL", "no Internet available");
            }
        } catch (Exception e) {

            network = false;
        }
        return network;
    }

    @Override
    public void onBackPressed() {
        //disabling backPress
    }
}
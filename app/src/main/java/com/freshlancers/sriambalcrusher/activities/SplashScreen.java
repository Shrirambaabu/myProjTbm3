package com.freshlancers.sriambalcrusher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.freshlancers.sriambalcrusher.R;
import com.freshlancers.sriambalcrusher.utils.Prefs;

/**
 * Created by Ashith VL on 10/20/2017.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (Prefs.getAccessTokenFromPrefs() != null) {
                    Intent i = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    // close this activity
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    // close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}

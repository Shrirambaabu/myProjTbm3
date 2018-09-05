package com.freshlancers.sriambalcrusher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.freshlancers.sriambalcrusher.AppController;

/**
 * Created by sunil on 26-03-2016.
 */
public class Prefs {

    private static SharedPreferences mSharedPreferences = null;
    private static final String PREFS_NAME = "CRUSHER_INFO";

    //Keys to access info from SharedPreferences
    private static final String ACCESS_TOKEN_PREFS_KEY = "access_token";

    public static void setAccessTokenInPrefs(String accessToken) {
        setDataInPrefs(ACCESS_TOKEN_PREFS_KEY, accessToken);
    }

    public static String getAccessTokenFromPrefs() {
        return getSharedPreference().getString(ACCESS_TOKEN_PREFS_KEY, null);
    }

    private static SharedPreferences getSharedPreference() {
        if (mSharedPreferences == null) {
            mSharedPreferences = AppController.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        return mSharedPreferences;
    }

    private static void setDataInPrefs(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference().edit();
        editor.putString(key, value);
        editor.apply();
    }

}
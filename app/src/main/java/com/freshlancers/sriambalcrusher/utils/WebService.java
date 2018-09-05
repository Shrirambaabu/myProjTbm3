package com.freshlancers.sriambalcrusher.utils;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freshlancers.sriambalcrusher.AppController;
import com.freshlancers.sriambalcrusher.interfaces.VolleyCallback;

/**
 * Created by Shriram on 14-Nov-17.
 */

public class WebService {

    private static final String TAG = "WebService";

    public static void stringRequest(String url,final VolleyCallback volleyCallback){


        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                volleyCallback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onSuccess(error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request, TAG);

    }

}

package com.tac.reportingDemo.network;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tac.reportingDemo.infratructure.MyApplication;

import java.util.Map;

public class MyVolley {

    private static MyVolley mInstance = null;
    private RequestQueue mRequestQueue;

    protected MyVolley() {
        //request queue
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static MyVolley getInstance() {
        if (mInstance == null) {
            mInstance = new MyVolley();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    // Method to construct URL with parameters
    public static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().toString();
    }

    public static String handleVolleyError(VolleyError error) {
        String message = null;
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            message = "Bad network Connection";
        } else if (error instanceof AuthFailureError) {
            message = "Failed to perform a request";
        } else if (error instanceof ServerError) {
            message = "Server error";
        } else if (error instanceof NetworkError) {
            message = "Network error while performing a request";
        } else if (error instanceof ParseError) {
            message = "Server response could not be parsed";
        }
        return message;
    }
}

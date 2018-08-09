package com.example.androidwebservice;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton instance;
    private static Context context;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        VolleySingleton.context = context;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (instance == null) instance = new VolleySingleton(context);

        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // This is the key to avoid leaking the Activity or BroadcastReceiver if passed one in.
            Context applicationContext = context.getApplicationContext();
            requestQueue = Volley.newRequestQueue(applicationContext);
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}

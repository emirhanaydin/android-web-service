package com.example.androidwebservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private DatabaseHelper databaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return;

        {
            int type = networkInfo.getType();
            if (type != ConnectivityManager.TYPE_WIFI && type != ConnectivityManager.TYPE_MOBILE)
                return;
        }

        Cursor cursor = databaseHelper.getUnsyncedNames();
        if (!cursor.moveToFirst()) return;

        do {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));

            saveNameToDatabase(id, name);
        } while (cursor.moveToNext());
    }

    private void saveNameToDatabase(final int id, final String name) {
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("error")) return;

                    // Update the status in SQLite
                    databaseHelper.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);

                    // Send broadcast to refresh the list
                    context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_WEB_SERVICE, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}

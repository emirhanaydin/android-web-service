package com.example.androidwebservice;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String URL_WEB_SERVICE = "http://192.168.1.100/android-web-service/keywords.php";
    public static final String DATA_SAVED_BROADCAST = "com.example.datasaved";
    private DatabaseHelper databaseHelper;
    private EditText editTextKeyword;
    private EditText editTextValue;
    private ListView listViewKeywords;
    private List<Keyword> keywords;

    private KeywordAdapter keywordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        databaseHelper = new DatabaseHelper(this);
        keywords = new ArrayList<>();

        Button buttonPost = findViewById(R.id.buttonPost);
        editTextKeyword = findViewById(R.id.editTextKeyword);
        editTextValue = findViewById(R.id.editTextValue);
        listViewKeywords = findViewById(R.id.listViewKeywords);

        buttonPost.setOnClickListener(this);

        loadKeywordsFromLocal();

        // To update sync status
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadKeywordsFromLocal();
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    /**
     * Load the keywords from the database with updated sync status.
     */
    private void loadKeywordsFromLocal() {
        keywords.clear();

        Cursor cursor = databaseHelper.getKeywords();

        if (cursor.moveToFirst()) {
            do {
                String keyword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KEYWORD));
                String value = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_VALUE));
                boolean synced = 0 != cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_SYNCED));
                keywords.add(new Keyword(keyword, value, synced));
            } while (cursor.moveToNext());
        }

        keywordAdapter = new KeywordAdapter(this, R.layout.keywords, keywords);
        listViewKeywords.setAdapter(keywordAdapter);
    }

    private void refreshList() {
        keywordAdapter.notifyDataSetChanged();
    }

    private void saveKeywordToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving name...");
        progressDialog.show();

        final String keyword = editTextKeyword.getText().toString().trim();
        final String value = editTextValue.getText().toString().trim();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean synced = !jsonObject.getBoolean("error");
                    saveKeywordsToLocal(keyword, value, synced);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                saveKeywordsToLocal(keyword, value, false);
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_WEB_SERVICE, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("keyword", keyword);
                params.put("value", value);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void saveKeywordsToLocal(String keyword, String value, boolean synced) {
        editTextKeyword.setText("");
        databaseHelper.addKeyword(keyword, value, synced);
        keywords.add(new Keyword(keyword, value, synced));
        refreshList();
    }

    @Override
    public void onClick(View v) {
        saveKeywordToServer();
    }
}

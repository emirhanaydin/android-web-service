package com.example.androidwebservice;

import android.app.ProgressDialog;
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

import java.util.Collections;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextSearch;
    private ListView listViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button buttonSearch = findViewById(R.id.buttonSearch);
        editTextSearch = findViewById(R.id.editTextSearch);
        listViewSearch = findViewById(R.id.listViewSearch);

        buttonSearch.setOnClickListener(this);
    }

    private void searchKeywordOnServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching keyword...");
        progressDialog.show();

        final String keyword = editTextSearch.getText().toString().trim();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String value = jsonObject.getString("value");
                    boolean synced = !jsonObject.getBoolean("error");
                    updateKeywordViews(keyword, value, synced);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        };

        String url = MainActivity.URL_WEB_SERVICE + "?keyword=" + keyword;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void updateKeywordViews(String keyword, String value, boolean synced) {
        editTextSearch.setText("");

        Keyword kw = new Keyword(keyword, value, synced);
        KeywordAdapter keywordAdapter = new KeywordAdapter(this, R.layout.keywords, Collections.singletonList(kw));
        listViewSearch.setAdapter(keywordAdapter);
        keywordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        searchKeywordOnServer();
    }
}

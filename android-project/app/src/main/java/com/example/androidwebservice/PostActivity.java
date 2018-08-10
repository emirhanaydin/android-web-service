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
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String URL_WEB_SERVICE = "http://192.168.1.100/android-web-service/keywords.php";
    private EditText editTextKeyword;
    private EditText editTextValue;
    private ListView listViewKeywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Button buttonPost = findViewById(R.id.buttonPost);
        editTextKeyword = findViewById(R.id.editTextKeyword);
        editTextValue = findViewById(R.id.editTextValue);
        listViewKeywords = findViewById(R.id.listViewKeywords);

        buttonPost.setOnClickListener(this);
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

                updateKeywordViews(keyword, value, false);
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

    private void updateKeywordViews(String keyword, String value, boolean synced) {
        editTextKeyword.setText("");
        editTextValue.setText("");

        // Update the list view
        Keyword kw = new Keyword(keyword, value, synced);
        KeywordAdapter keywordAdapter = new KeywordAdapter(this, R.layout.keywords, Collections.singletonList(kw));
        listViewKeywords.setAdapter(keywordAdapter);
        keywordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        saveKeywordToServer();
    }
}

package com.example.androidwebservice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button buttonList = findViewById(R.id.buttonRefresh);
        listViewList = findViewById(R.id.listViewList);

        listKeywordsOnServer();

        buttonList.setOnClickListener(this);
    }

    private void listKeywordsOnServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Listing the records...");
        progressDialog.show();

        final Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<Integer> ids = convertFromJSONArray(jsonObject.getJSONArray("ids"));
                    List<String> keywords = convertFromJSONArray(jsonObject.getJSONArray("keywords"));
                    List<String> values = convertFromJSONArray(jsonObject.getJSONArray("values"));

                    updateKeywordViews(ids, keywords, values);
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

        String url = MainActivity.URL_WEB_SERVICE + "?keywords";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, listener, errorListener);

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private <T> List<T> convertFromJSONArray(JSONArray array) throws JSONException {
        ArrayList<T> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            list.add((T) array.get(i));
        }

        return list;
    }

    private void updateKeywordViews(List<Integer> ids, List<String> keywords, List<String> values) {
        List<Keyword> kws = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            kws.add(new Keyword(keywords.get(i), values.get(i), true));
        }

        KeywordAdapter keywordAdapter = new KeywordAdapter(this, R.layout.keywords, kws);
        listViewList.setAdapter(keywordAdapter);
        keywordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        listKeywordsOnServer();
    }
}

package com.example.androidwebservice;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

    public static final String URL_WEB_SERVICE = "http://192.168.1.100/android-web-service/save-name.php";
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    public static final String DATA_SAVED_BROADCAST = "com.example.datasaved";
    private DatabaseHelper databaseHelper;
    private Button buttonSave;
    private EditText editTextName;
    private ListView listViewNames;
    private List<Name> names;
    private BroadcastReceiver broadcastReceiver;

    private NameAdapter nameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        names = new ArrayList<>();

        buttonSave = findViewById(R.id.buttonSave);
        editTextName = findViewById(R.id.editTextName);
        listViewNames = findViewById(R.id.listViewNames);

        buttonSave.setOnClickListener(this);

        loadNamesFromDatabase();

        // To update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadNamesFromDatabase();
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    /**
     * Load the names from the database with updated sync status.
     */
    private void loadNamesFromDatabase() {
        names.clear();

        Cursor cursor = databaseHelper.getNames();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                int status = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS));
                names.add(new Name(name, status));
            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);
    }

    private void refreshList() {
        nameAdapter.notifyDataSetChanged();
    }

    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving name...");
        progressDialog.show();

        final String name = editTextName.getText().toString().trim();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getBoolean("error")
                            ? NAME_NOT_SYNCED_WITH_SERVER
                            : NAME_SYNCED_WITH_SERVER;
                    saveNameToLocalStorage(name, status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER);
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_WEB_SERVICE, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void saveNameToLocalStorage(String name, int status) {
        editTextName.setText("");
        databaseHelper.addName(name, status);
        names.add(new Name(name, status));
        refreshList();
    }

    @Override
    public void onClick(View v) {
        saveNameToServer();
    }
}

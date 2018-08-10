package com.example.androidwebservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String URL_WEB_SERVICE = "http://192.168.1.100/android-web-service/keywords.php";

    Button buttonSave;
    Button buttonSearch;
    Button buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonList = findViewById(R.id.buttonList);

        buttonSave.setOnClickListener(this);
        buttonSearch.setOnClickListener(this);
        buttonList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSave) {
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        } else if (v == buttonSearch) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
    }
}

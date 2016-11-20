package com.ordogfioka.smarthomelabor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv =(TextView)findViewById(R.id.textBox);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onClick(View view) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    String postURL = "http://google.com";
                    HttpGet post = new HttpGet(postURL);

                    HttpResponse responseGet = client.execute(post);
                    tv.setText(responseGet.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

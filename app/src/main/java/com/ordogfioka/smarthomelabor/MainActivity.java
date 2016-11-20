package com.ordogfioka.smarthomelabor;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

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
        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://oktnb144.inf.elte.hu:8082/consumers/my_json_consumer";
            HttpGet post = new HttpGet(postURL);

            HttpResponse responseGet = client.execute(post);
            tv.append(responseGet.toString());
/*            HttpEntity resEntityGet = responseGet.getEntity();
            if (resEntityGet != null) {
                //do something with the response
                Log.i("GET ", EntityUtils.toString(resEntityGet));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

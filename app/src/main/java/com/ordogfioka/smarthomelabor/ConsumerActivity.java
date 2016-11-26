package com.ordogfioka.smarthomelabor;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ConsumerActivity extends AppCompatActivity {
    ListView listView = null;
    List<String> messages = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        listView = (ListView)findViewById(R.id.consumerListView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,messages);
        listView.setAdapter(adapter);
        messages.add((String) getIntent().getExtras().get("topic"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        new DoBackgroundTask().execute();
    }

    private class DoBackgroundTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet("http://google.com");

            try {
                // Execute POST
                HttpResponse httpResponse = httpClient.execute(httpPost);
                response = httpResponse.toString();
            } catch (Exception e) {
                response = e.toString();
            }
            response = "[{\"key\":null,\"value\":{\"name\":\"testUser\"},\"partition\":0,\"offset\":0}]";
            try {

                JSONArray jsonArray = new JSONArray(response);
                response = jsonArray.getJSONObject(0).getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String finalResponse = response;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(finalResponse);
                }
            });
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    new DoBackgroundTask().execute();
                }
            }, 1000);
        }
    }
}

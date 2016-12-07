package com.ordogfioka.smarthomelabor;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsumerActivity extends AppCompatActivity {
    ListView listView = null;
    List<String> messages = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int MAXIMUM_NUMBER_OF_MESSAGES = 20;
    private String topic;
    private String IMEI = "my_consumer_instance2";
    private boolean repeatQuery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        listView = (ListView)findViewById(R.id.consumerListView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,messages);
        listView.setAdapter(adapter);
        topic = (String) getIntent().getExtras().get("topic");
        messages.add(topic);
        thread.start();

    }
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            try {
                RequestBody body  = RequestBody.create(JSON,"{\"name\": \""+IMEI+"\", \"format\": \"json\", \"auto.offset.reset\": \"smallest\"}");
                Request request = new Request.Builder()
                        .url("http://oktnb144.inf.elte.hu:8082/consumers/my_json_consumer")
                        .addHeader("Content-Type","application/vnd.kafka.v1+json")
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                if(200 == response.code()){
                    repeatQuery = true;
                    new DoBackgroundTask().execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        repeatQuery = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://oktnb144.inf.elte.hu:8082/consumers/my_json_consumer/instances/" + IMEI;
                Request request = new Request.Builder()
                        .url(url)
                        .delete()
                        .build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response res = client.newCall(request).execute();
                    System.out.println(res.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class DoBackgroundTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://oktnb144.inf.elte.hu:8082/consumers/my_json_consumer/instances/"+IMEI+"/topics/"+topic)
                    .addHeader("Accept","application/vnd.kafka.json.v1+json")
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray array = new JSONArray(response.body().string());
                for(int i = 0;i<array.length();i++) {
                    JSONObject obj = new JSONObject(array.get(i).toString());
                    final String value = obj.getString("value");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (messages.size() > MAXIMUM_NUMBER_OF_MESSAGES)
                                messages.remove(1);
                            adapter.add(value);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(repeatQuery == false)
                return;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    new DoBackgroundTask().execute();
                }
            }, 1000);
        }
    }
}

package com.ordogfioka.smarthomelabor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {
    ListView listView = null;
    List<String> arrayOfUsers = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private JSONArray jsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =(ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayOfUsers);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFromList =(String) (listView.getItemAtPosition(i));
                Intent intent = new Intent(MainActivity.this,ConsumerActivity.class);
                intent.putExtra("topic",selectedFromList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onClick(View view) {
        new Thread(new Runnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://oktnb144.inf.elte.hu:8082/topics")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    jsonList = new JSONArray(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Response finalResponse = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.clear();
                        for(int i=0;i<jsonList.length();i++)
                            try {
                                adapter.add((String) jsonList.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                });

            }
        }).start();
    }
}

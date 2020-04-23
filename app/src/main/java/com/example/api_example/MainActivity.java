package com.example.api_example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent_from_signup = getIntent();
        String user_info = intent_from_signup.getStringExtra("user_info");

        TextView textView = findViewById(R.id.user_info);
        if (user_info == null) {
            Log.d(TAG, "ログインに誤りがあるため元の画面に。");
            FirebaseAuth.getInstance().signOut();
        } else {
            textView.setText(user_info + "がログインしています");
        }


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "東京");
        map.put("id", "130010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "さいたま");
        map.put("id", "110010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "千葉");
        map.put("id", "120010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "横浜");
        map.put("id", "140010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "前橋");
        map.put("id", "100010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "宇都宮");
        map.put("id", "090010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "水戸");
        map.put("id", "080010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "甲府");
        map.put("id", "190010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "長野");
        map.put("id", "200010");
        list.add(map);


        ListView lvCityList = (ListView) findViewById(R.id.lvCityList);

        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list, android.R.layout.simple_expandable_list_item_1, from, to);

        lvCityList.setAdapter(adapter);
        lvCityList.setOnItemClickListener(new ListItemClickListener());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

    }


    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            String cityName = item.get("name");
            String cityId = item.get("id");

            Intent intent = new Intent(MainActivity.this, WeatherInfoActivity.class);
            intent.putExtra("cityName", cityName);
            intent.putExtra("cityId", cityId);
            startActivity(intent);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutbutton:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

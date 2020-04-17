package com.example.api_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();

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
}

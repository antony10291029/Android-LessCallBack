package com.cn.test.nocallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Lin on 16/3/30.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listview = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
        adapter.add("Simulation Http demo");
        adapter.add("Simulation Pay demo");
        adapter.add("Simulation Download file demo");
        listview.setAdapter(adapter);
        setContentView(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                switch (position)
                {
                    case 0:
                        intent.setClass(MainActivity.this,HttpActivity.class);
                        break;
                    case 1:
                        intent.setClass(MainActivity.this,PayActivity.class);
                        break;
                    case 2:
                        intent.setClass(MainActivity.this,CatchActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
    }
}

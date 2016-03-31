package com.cn.test.nocallback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.lxz.utils.json.JsonPathGeneric;
import org.lxz.utils.sync.TaskFlowTools;
import org.lxz.utils.sync.TaskFlowTools.TaskFlow;

import http.HttpRequest;

public class HttpActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(http);
    }

    View.OnClickListener http = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ip = null;
            city = null;
            tv.setText("");
            TaskFlowTools.inject(this).run();
        }
        String ip;
        String city;

        @TaskFlow(thread = true, id = 0)
        public void sendIP() {
            String json = new HttpRequest().sendGet("http://pv.sohu.com/cityjson", "ie=utf-8");
            ip = JsonPathGeneric.getGenericInString(json.split("=")[1], "$.cip");
            append("http://pv.sohu.com/cityjson?ie=utf-8");
            append(json);
        }

        @TaskFlow(thread = false, id = 1)
        public void getIP() {
            tv.append(Html.fromHtml("<font color='red'>"+"\n"+"ip:"+ip+"\n"+"</font>"));
        }

        @TaskFlow(thread = true, id = 2)
        public void sendCity() {
            String json = new HttpRequest().sendGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php", "format=js&ip=" + ip);
            city = JsonPathGeneric.getGenericInString(json.split("=")[1], "$.city");
            append("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + ip);
            append(json);
        }

        @TaskFlow(thread = false, id = 3)
        public void getCity() {
            tv.append(Html.fromHtml("<font color='red'>"+"\n"+"city:"+city+"\n"+"</font>"));
        }

        public void append(final String str) {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.append("\n"+str+"\n");
                }
            });
        }
    };


}

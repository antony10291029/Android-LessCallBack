package com.cn.test.nocallback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.lxz.utils.json.JsonPathGeneric;
import org.lxz.utils.sync.TaskFlowTools;

import http.HttpRequest;

/**
 * Created by Lin on 16/3/30.
 */
public class PayActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(pay);
        btn.setText("Simulation payment");
    }

    View.OnClickListener pay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tv.setText("");
            flow = TaskFlowTools.inject(this);
            flow.run();
        }
        TaskFlowTools flow;
        String gold;
        String out_trade_no = "1199";
        boolean isPay = true;

        @TaskFlowTools.TaskFlow(thread = true, id = 0)
        public void getGold() {
            String json = new HttpRequest().sendPost("http://httpbin.org/post", "out_trade_no=" + out_trade_no + "&gold=998");
            gold = JsonPathGeneric.getGenericInString(json, "$.form.gold");
        }

        @TaskFlowTools.TaskFlow(thread = false, id = 1)
        public void showPay() {
            tv.append("begin pay\n");
            AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);
            builder.setMessage("is pay order" + out_trade_no + " " + "gold=" + gold);
            builder.setTitle("pay dialog ");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    password();
                }

            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    print();

                }
            });
            builder.create().show();
            flow.setNextStep(false);
        }


        @TaskFlowTools.TaskFlow(thread = false, id = 2)
        public void password() {
            new AlertDialog.Builder(PayActivity.this).setTitle("entry password").setIcon(
                    android.R.drawable.ic_dialog_info).setView(
                    new EditText(PayActivity.this)).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isPay = true;
                    flow.setStepByID(5);
                    flow.nextStep();
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            print();
                        }
                    }).show();
            flow.setNextStep(false);
        }

        @TaskFlowTools.TaskFlow(thread = false, id = 5)
        public void exit() {
            print();
        }

        public void print() {
            tv.append(out_trade_no + "\n");
            tv.append(gold + "\n");
            tv.append("" + isPay);
        }
    };
}

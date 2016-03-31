package com.cn.test.nocallback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.lxz.utils.sync.TaskFlowTools;

/**
 * Created by Lin on 16/3/31.
 */
public class CatchActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;
    private ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(downloadFile);
        btn.setText("simulation download file");
    }

    private View.OnClickListener downloadFile = new View.OnClickListener() {
        TaskFlowTools flow;

        boolean isAnalogdiskisfull=true;
        Handler handler=new Handler();
        @Override
        public void onClick(View v) {
            flow = TaskFlowTools.inject(this);
            isAnalogdiskisfull=true;
            flow.run();

        }


        public boolean createErrorDialog() {
            final boolean[] isbreak = {false};
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new AlertDialog.Builder(CatchActivity.this).setIcon(
                            android.R.drawable.btn_star).setTitle("error").setMessage(
                            "Disk full").setPositiveButton("try",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    flow.wakeUp();

                                }
                            })
                            .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressdialog.dismiss();
                                    flow.wakeUp();
                                }
                            }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isbreak[0] =true;
                                    flow.setStepByID(3);
                                    flow.nextStep();
                                    flow.wakeUp();

                                }
                            }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
            flow.waitFor();
            return isbreak[0];

        }

        @TaskFlowTools.TaskFlow(thread = false, id = 1)
        public void createDialog() {
            progressdialog = new ProgressDialog(CatchActivity.this);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
            progressdialog.setCancelable(true);// 设置是否可以通过点击Back键取消
            progressdialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            progressdialog.setTitle("提示");
            progressdialog.setMax(100);
            progressdialog.show();
        }

        @TaskFlowTools.TaskFlow(thread = true, id = 2)
        public void downloading() {
            while (progressdialog.getProgress() < 100) {
                progressdialog.setProgress(progressdialog.getProgress() + 1);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(isAnalogdiskisfull&&progressdialog.getProgress()==65)
                {
                    isAnalogdiskisfull=false;
                    if(createErrorDialog()){
                        break;
                    };

                }

            }
            flow.setNextStep(false);

        }

        @TaskFlowTools.TaskFlow(thread = true, id = 3)
        public void cancel() {
            while (progressdialog.getProgress() > 0) {
                progressdialog.setProgress(progressdialog.getProgress() - 1);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            progressdialog.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    progressdialog.dismiss();
                }
            });
            flow.setNextStep(false);
        }


    };


}

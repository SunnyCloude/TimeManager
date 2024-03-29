package com.lx.javawork.timemanager;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//这里全屏显示

        setContentView(R.layout.activity_welcome);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }  //这里将自带的标题栏隐藏掉

        handler.sendEmptyMessageDelayed(0,2000);  //定个时 1秒

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };  //消息处理对象,负责发送与处理消息


    public void getHome(){
        Intent intent = new Intent(WelcomeActivity.this, TimeManagerActivity.class);
        startActivity(intent);
        finish();
    }
}

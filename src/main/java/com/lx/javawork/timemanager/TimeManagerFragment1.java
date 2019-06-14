package com.lx.javawork.timemanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManagerFragment1 extends Fragment {

    private Button mBtnStart;
    private CirclePgBar cpb;
    private String mStartTime,mEndTime;
    private ScreenStatusReceiver mScreenStatusReceiver;
    private boolean isRegister;
    private LinearLayout barcontainer;
    private int StudyTime;
    private String today;
    private IOnMessageClick listener;

    public interface IOnMessageClick{
        void onClick(boolean isTimeing);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener=(IOnMessageClick)context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_timemanager1_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //cpb=view.findViewById(R.id.cpb);
        mBtnStart=view.findViewById(R.id.bae);
        barcontainer=view.findViewById(R.id.barcontainer);
        isRegister=false;

        // 获取今天的日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
        Date date = new Date(System.currentTimeMillis());
        today=simpleDateFormat.format(date);
        String[] d=today.split("\\.");
        today=Integer.parseInt(d[0])+"."+Integer.parseInt(d[1]);

        //初始化CirclePgBar
        cpb=new CirclePgBar(this.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,40,0,0);
        cpb.setLayoutParams(layoutParams);
        //读取数据
        FileOperation fileOperation=new FileOperation();
        String StrStudyTime=fileOperation.FileRead("records.txt",this.getContext(),today);
        StudyTime=Integer.parseInt(StrStudyTime);
        cpb.setTargetProgress(StudyTime/60,StudyTime%60);//设置ProgressBar的进度
        barcontainer.addView(cpb);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnStart.getText().toString().equals("开始学习吧！")){
                    register();
                    new AlertDialog.Builder(TimeManagerFragment1.this.getContext()).setTitle("提示")
                            .setMessage("现在请锁屏，放下手机，开始学习/工作吧！").show();
                    mBtnStart.setText("结束学习");
                }else{
                    new AlertDialog.Builder(TimeManagerFragment1.this.getContext()).setTitle("提示")
                            .setMessage("确定要结束本次学习记录？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBtnStart.setText("开始学习吧！");
                                    Toast.makeText(TimeManagerFragment1.this.getContext(),"本次学习记录已结束",Toast.LENGTH_LONG).show();
                                    unregister();
                                }
                            }).setNegativeButton("不，我点错了",null).show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isRegister)
            unregister();
    }

    public void register() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        this.getContext().registerReceiver(mScreenStatusReceiver, screenStatusIF);
        isRegister=true;
        listener.onClick(true);
    }

    public void unregister() {
        this.getContext().unregisterReceiver(mScreenStatusReceiver);

        String record=today+":"+StudyTime+",";

        FileOperation fileOperation=new FileOperation();
        fileOperation.FileSave("records.txt",this.getContext(),record);
        isRegister=false;
        listener.onClick(false);
    }

    private int TimeGap(String time1, String time2){
        int t1=Integer.parseInt(time1);
        int t2=Integer.parseInt(time2);
        return ((t2/10000)*24*60+((t2%10000)/100)*60+(t2%100))-((t1/10000)*24*60+((t1%10000)/100)*60+(t1%100));
    }
    class ScreenStatusReceiver extends BroadcastReceiver {
        String SCREEN_ON = "android.intent.action.SCREEN_ON";
        String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCREEN_ON.equals(intent.getAction())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmm");// HH:mm:ss//获取当前时间
                Date date = new Date(System.currentTimeMillis());
                mEndTime=simpleDateFormat.format(date);
                int timeGap=TimeGap(mStartTime,mEndTime);
                StudyTime+=timeGap;

                barcontainer.removeView(cpb);
                cpb=new CirclePgBar(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,40,0,0);
                cpb.setLayoutParams(layoutParams);
                cpb.setTargetProgress(StudyTime/60,StudyTime%60);
                barcontainer.addView(cpb);

            }
            else if (SCREEN_OFF.equals(intent.getAction())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmm");// HH:mm:ss//获取当前时间
                Date date = new Date(System.currentTimeMillis());
                mStartTime=simpleDateFormat.format(date);
            }
        }
    }
}

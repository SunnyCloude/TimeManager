package com.lx.javawork.timemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeManagerFragment3 extends Fragment {

    private Button mStart;
    private RadioButton circle1,circle2,circle3,circle4,rest1,rest2,rest3,rest4;
    private Spinner work;
    private TextView mTvHint;
    private LinearLayout clock;
    private Chronometer mChronometer;
    private IOnTomatoClick listener;
    private TextView timetype;
    private int resttime,circle,currentcircle;
    private Vibrator vibrator;
    private long[] patter;
    private Message msg;
    private Handler handler;
    private Thread thread;
    private String today;

    public interface IOnTomatoClick{
        void onTomatoClick(boolean isTimeing);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener=(IOnTomatoClick) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_timemanager3_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
    }

    @SuppressLint("HandlerLeak")
    private void Init(View v){
        rest1=v.findViewById(R.id.resttime1);
        rest2=v.findViewById(R.id.resttime2);
        rest3=v.findViewById(R.id.resttime3);
        rest4=v.findViewById(R.id.resttime4);
        circle1=v.findViewById(R.id.circle1);
        circle2=v.findViewById(R.id.circle2);
        circle3=v.findViewById(R.id.circle3);
        circle4=v.findViewById(R.id.circle4);
        work=v.findViewById(R.id.worktime);
        mStart=v.findViewById(R.id.btn_start);
        mTvHint=v.findViewById(R.id.hint);
        clock=v.findViewById(R.id.clock);
        timetype=v.findViewById(R.id.timetype);
        mChronometer =v.findViewById(R.id.timer);
        mChronometer.setFormat("%s");

        rest1.setOnCheckedChangeListener(new Checkchange());
        rest2.setOnCheckedChangeListener(new Checkchange());
        rest3.setOnCheckedChangeListener(new Checkchange());
        rest4.setOnCheckedChangeListener(new Checkchange());
        circle1.setOnCheckedChangeListener(new Checkchange());
        circle2.setOnCheckedChangeListener(new Checkchange());
        circle3.setOnCheckedChangeListener(new Checkchange());
        circle4.setOnCheckedChangeListener(new Checkchange());

        resttime=0;circle=0;
        vibrator = (Vibrator) TimeManagerFragment3.this.getContext().getSystemService(TimeManagerFragment3.this.getContext().VIBRATOR_SERVICE);
        patter=new long[5];
        patter[1]=200;
        patter[2]=100;
        patter[3]=200;
        patter[4]=1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.hh.mm");
        Date date = new Date(System.currentTimeMillis());
        today=simpleDateFormat.format(date);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStart.getText().equals("开始番茄吧！")){
                    if(resttime!=0&&circle!=0){
                        currentcircle=0;
                        clock.setVisibility(View.VISIBLE);
                        listener.onTomatoClick(true);
                        setEnabled(false);
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.start();
                        ThreadTime0();
                        mStart.setText("结束番茄");
                        new AlertDialog.Builder(TimeManagerFragment3.this.getContext()).setTitle("提示")
                                .setMessage("番茄时钟已经开始计时").show();
                    }else{
                        new AlertDialog.Builder(TimeManagerFragment3.this.getContext()).setTitle("提示")
                                .setMessage("请选择【休息时间】和【循环次数】").show();
                    }
                }else{
                    new AlertDialog.Builder(TimeManagerFragment3.this.getContext()).setTitle("提示")
                            .setMessage("确定要结束番茄时钟吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    clock.setVisibility(View.INVISIBLE);
                                    listener.onTomatoClick(false);
                                    setEnabled(true);
                                    resttime=0;circle=0;
                                    rest1.setChecked(false);
                                    rest2.setChecked(false);
                                    rest3.setChecked(false);
                                    rest4.setChecked(false);
                                    circle1.setChecked(false);
                                    circle2.setChecked(false);
                                    circle3.setChecked(false);
                                    circle4.setChecked(false);
                                    work.setSelection(0);
                                    thread.interrupt();
                                    Toast.makeText(TimeManagerFragment3.this.getContext(),"本次番茄时钟已结束",Toast.LENGTH_LONG).show();
                                    mStart.setText("开始番茄吧！");
                                }
                            }).setNegativeButton("不，我点错了",null).show();
                }
            }
        });

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle=msg.getData();
                int isTime=bundle.getInt("isTime");
                if(isTime==0){
                    vibrator.vibrate(patter,0);
                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());

                    new AlertDialog.Builder(TimeManagerFragment3.this.getContext())
                            .setTitle("提示").setMessage("休息一下吧")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    timetype.setText("休息时间");
                                    vibrator.cancel();
                                    mChronometer.start();
                                }
                            }).setCancelable(false).show();

                    if(currentcircle!=circle-1){
                        ThreadTime1();
                    }else {
                        ThreadTime2();
                    }
                }else if (isTime==1){
                    vibrator.vibrate(patter,0);
                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    new AlertDialog.Builder(TimeManagerFragment3.this.getContext())
                            .setTitle("提示").setMessage("开始工作吧！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    timetype.setText("工作时间");
                                    vibrator.cancel();
                                    mChronometer.start();
                                }
                            }).setCancelable(false).show();

                    currentcircle++;
                    ThreadTime0();
                }else if (isTime==2){
                    vibrator.vibrate(patter,0);
                    mChronometer.stop();
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                    new AlertDialog.Builder(TimeManagerFragment3.this.getContext())
                            .setTitle("提示").setMessage("恭喜你完成了一次番茄时钟！")
                            .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String content=today+":"+work.getSelectedItem().toString()+":"+resttime+":"+circle+",";
                                    new FileOperation().FileSave("TomatoRecords.txt", TimeManagerFragment3.this.getContext(),content);
                                    timetype.setText("工作时间");
                                    clock.setVisibility(View.INVISIBLE);
                                    vibrator.cancel();
                                    listener.onTomatoClick(false);
                                    setEnabled(true);
                                    resttime=0;circle=0;
                                    rest1.setChecked(false);
                                    rest2.setChecked(false);
                                    rest3.setChecked(false);
                                    rest4.setChecked(false);
                                    circle1.setChecked(false);
                                    circle2.setChecked(false);
                                    circle3.setChecked(false);
                                    circle4.setChecked(false);
                                    work.setSelection(0);
                                    mStart.setText("开始番茄吧！");
                                }
                            }).setCancelable(false).show();
                }
            }
        };

        mTvHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("番茄工作法").setMessage(R.string.tomato_time).show();
            }
        });
        rest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest2.setChecked(false);
                rest3.setChecked(false);
                rest4.setChecked(false);
                resttime=Integer.parseInt(rest1.getText().toString());
            }
        });
        rest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest1.setChecked(false);
                rest3.setChecked(false);
                rest4.setChecked(false);
                resttime=Integer.parseInt(rest2.getText().toString());
            }
        });
        rest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest2.setChecked(false);
                rest1.setChecked(false);
                rest4.setChecked(false);
                resttime=Integer.parseInt(rest3.getText().toString());
            }
        });
        rest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest2.setChecked(false);
                rest3.setChecked(false);
                rest1.setChecked(false);
                resttime=Integer.parseInt(rest4.getText().toString());
            }
        });
        circle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle2.setChecked(false);
                circle3.setChecked(false);
                circle4.setChecked(false);
                circle=Integer.parseInt(circle1.getText().toString());
            }
        });
        circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle1.setChecked(false);
                circle3.setChecked(false);
                circle4.setChecked(false);
                circle=Integer.parseInt(circle2.getText().toString());
            }
        });
        circle3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle2.setChecked(false);
                circle1.setChecked(false);
                circle4.setChecked(false);
                circle=Integer.parseInt(circle3.getText().toString());
            }
        });
        circle4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle2.setChecked(false);
                circle3.setChecked(false);
                circle1.setChecked(false);
                circle=Integer.parseInt(circle4.getText().toString());
            }
        });
    }

    private void setEnabled(boolean isenable){
        work.setEnabled(isenable);
        rest1.setEnabled(isenable);
        rest2.setEnabled(isenable);
        rest3.setEnabled(isenable);
        rest4.setEnabled(isenable);
        circle1.setEnabled(isenable);
        circle2.setEnabled(isenable);
        circle3.setEnabled(isenable);
        circle4.setEnabled(isenable);
    }

    private class Checkchange implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked==false){
                buttonView.setTextColor(getResources().getColor(R.color.darkblue));
            }else{
                buttonView.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }
    private void ThreadTime0(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);//设置工作时间Integer.parseInt(work.getSelectedItem().toString().trim())*6000

                    Bundle bundle = new Bundle();
                    bundle.putInt("isTime", 0);
                    msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);//向主线程传递数据
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread=new Thread(runnable);
        thread.start();//开始线程
    }
    private void ThreadTime1(){
        //Integer.parseInt(work.getSelectedItem().toString().trim())*6000
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);//Integer.parseInt(work.getSelectedItem().toString().trim())*6000

                    Bundle bundle = new Bundle();
                    bundle.putInt("isTime", 1);
                    msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread=new Thread(runnable);
        thread.start();
    }
    private void ThreadTime2(){
        //Integer.parseInt(work.getSelectedItem().toString().trim())*6000
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);//Integer.parseInt(work.getSelectedItem().toString().trim())*6000

                    Bundle bundle = new Bundle();
                    bundle.putInt("isTime", 2);
                    msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread=new Thread(runnable);
        thread.start();
    }
}

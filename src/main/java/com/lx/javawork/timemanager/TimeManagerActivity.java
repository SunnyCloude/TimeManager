package com.lx.javawork.timemanager;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class TimeManagerActivity extends AppCompatActivity implements TimeManagerFragment1.IOnMessageClick, TimeManagerFragment3.IOnTomatoClick {

    private TextView mTvtitle;
    private ImageView mIv1,mIv2,mIvtitle;
    private FrameLayout fl;
    private Switch mSwitch;
    private TimeManagerFragment1 mAnimation1Fragment;
    private TimeManagerFragment2 mAnimation2Fragment;
    private TimeManagerFragment3 mAnimation3Fragment;
    private TimeManagerFragment4 mAnimation4Fragment;
    private boolean mTiming;
    private boolean mTimingTomato;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timemanager);

        mTiming=false;
        mTimingTomato=false;
        mIv1=findViewById(R.id.bar1);
        mIv2=findViewById(R.id.bar2);
        mTvtitle=findViewById(R.id.tvtitle);
        mIvtitle=findViewById(R.id.ivtitle);
        mSwitch=findViewById(R.id.sw);
        fl=findViewById(R.id.fl);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WAKE_LOCK,Manifest.permission.VIBRATE},1);

        mAnimation1Fragment=new TimeManagerFragment1();
        getSupportFragmentManager().beginTransaction().add(R.id.fl,mAnimation1Fragment,"clock").commitAllowingStateLoss();

        mIv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(mIv1,mIv2);
                if(mSwitch.isChecked()){
                    if(!mTimingTomato){
                        mAnimation3Fragment = new TimeManagerFragment3();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl, mAnimation3Fragment).commitAllowingStateLoss();
                    }
                }else{
                    if(!mTiming) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl, mAnimation1Fragment).commitAllowingStateLoss();
                    }
                }
            }
        });
        mIv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSwitch.isChecked()){
                    if(mTimingTomato){
//                        new AlertDialog.Builder(AnimationActivity.this).setTitle("提示")
//                                .setMessage("请点击【结束番茄】后，再进行查看")
//                                .setPositiveButton("好的",null).show();
                        Toast.makeText(TimeManagerActivity.this,"请点击【结束番茄】后，再进行查看",Toast.LENGTH_LONG).show();
                    }else {
                        onclick(mIv2, mIv1);
                        mAnimation4Fragment = new TimeManagerFragment4();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl, mAnimation4Fragment).commitAllowingStateLoss();
                    }
                }else{
                    if(mTiming){
//                        new AlertDialog.Builder(AnimationActivity.this).setTitle("提示")
//                                .setMessage("请点击【结束学习】后，再进行查看")
//                                .setPositiveButton("好的",null).show();
                        Toast.makeText(TimeManagerActivity.this,"请点击【结束学习】后，再进行查看",Toast.LENGTH_LONG).show();
                    }else{
                        onclick(mIv2,mIv1);
                        mAnimation2Fragment=new TimeManagerFragment2();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl,mAnimation2Fragment).commitAllowingStateLoss();
                    }
                }
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mTiming){
//                        new AlertDialog.Builder(AnimationActivity.this).setTitle("提示")
//                                .setMessage("请点击【结束学习】后，再进行查看")
//                                .setPositiveButton("好的",null).show();
                        Toast.makeText(TimeManagerActivity.this,"请点击【结束学习】后，再进行查看",Toast.LENGTH_LONG).show();
                        mSwitch.setChecked(false);
                    }else{
                        mTvtitle.setText("番茄工作法");
                        mIvtitle.setImageResource(R.mipmap.tomato);
                        mAnimation3Fragment=new TimeManagerFragment3();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl,mAnimation3Fragment).commitAllowingStateLoss();
                    }
                }
                else
                {
                    if(mTimingTomato){
//                        new AlertDialog.Builder(AnimationActivity.this).setTitle("提示")
//                                .setMessage("请点击【结束番茄】后，再进行查看")
//                                .setPositiveButton("好的",null).show();
                        Toast.makeText(TimeManagerActivity.this,"请点击【结束番茄】后，再进行查看",Toast.LENGTH_LONG).show();
                        mSwitch.setChecked(true);
                    }else {
                        mTvtitle.setText("学习时间记录");
                        mIvtitle.setImageResource(R.mipmap.study);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl,mAnimation1Fragment).commitAllowingStateLoss();
                    }
                }
                onclick(mIv1,mIv2);
            }
        });

    }
    private void onclick(ImageView i1,ImageView i2){
        i1.setBackgroundColor(getResources().getColor(R.color.darkblue));
        i2.setBackgroundColor(getResources().getColor(R.color.darkbluetrans));
    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event){
//        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
//            return true;
//        }else if(event.getKeyCode()==KeyEvent.KEYCODE_HOME) {
//            return true;
//        }else{
//            return super.dispatchKeyEvent(event);
//        }
//    }
    @Override
    public void onClick(boolean isTimeing) {
        mTiming=isTimeing;
    }

    @Override
    public void onTomatoClick(boolean isTimeing) {
        mTimingTomato=isTimeing;
    }
}

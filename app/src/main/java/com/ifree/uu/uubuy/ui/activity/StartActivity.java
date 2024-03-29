package com.ifree.uu.uubuy.ui.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.ifree.uu.uubuy.R;
import com.ifree.uu.uubuy.app.MyApplication;
import com.ifree.uu.uubuy.config.Constant;
import com.ifree.uu.uubuy.ui.base.BaseActivity;
import com.ifree.uu.uubuy.uitls.SPUtil;
import com.ifree.uu.uubuy.uitls.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: 小火
 * Email:1403241630@qq.com
 * Created by 2018/8/17.
 * Description:
 */
public class StartActivity extends AppCompatActivity {
    private static final int SHOW_TIME_MIN = 1000;// 最小显示时间
    private long mStartTime;// 开始时间
    private boolean IsFirst;//第一次进入应用
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    long loadingTime = System.currentTimeMillis() - mStartTime;// 计算一下总共花费的时间
                    if (loadingTime < SHOW_TIME_MIN) {
                        // 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入
                        mHandler.postDelayed(goToMainActivity, SHOW_TIME_MIN - loadingTime);
                    } else {
                        mHandler.post(goToMainActivity);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //进入下一个Activity
    Runnable goToMainActivity = new Runnable() {
        @Override
        public void run() {
            IsFirst = SPUtil.getBoolean(StartActivity.this, Constant.FIRST_COME, true);
            if (IsFirst) {
                startActivity(new Intent(StartActivity.this,
                        GuideActivity.class));
                SPUtil.putBoolean(StartActivity.this, Constant.FIRST_COME, false);
                finish();
            } else {
                startActivity(new Intent(StartActivity.this,
                        MainActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().getDecorView().setBackgroundResource(R.mipmap.splash);
        super.onCreate(savedInstanceState);
        StatusBarUtil.fullScreen(StartActivity.this);
        mStartTime = System.currentTimeMillis();//记录开始时间1
        checkPermission();
    }


    private void saveTag() {
        SPUtil.putBoolean(StartActivity.this, Constant.FIRST_COME, false);
    }

    @Override
    public void finish() {
        super.finish();
        saveTag();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(goToMainActivity);//移除回调
    }

    private void checkPermission() {
        //判断Android版本   获取需要的权限
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionStrs = new ArrayList<>();
            int hasWriteSdcardPermission =
                    ContextCompat.checkSelfPermission(
                            StartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSdcardPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionStrs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissionStrs.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionStrs.add(Manifest.permission.CALL_PRIVILEGED);
                permissionStrs.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
                permissionStrs.add(Manifest.permission.READ_PHONE_STATE);
                permissionStrs.add(Manifest.permission.READ_CONTACTS);
                permissionStrs.add(Manifest.permission.RECORD_AUDIO);
                permissionStrs.add(Manifest.permission.VIBRATE);
                permissionStrs.add(Manifest.permission.CAMERA);

            }
            String[] stringArray = permissionStrs.toArray(new String[0]);
            if (permissionStrs.size() > 0) {
                requestPermissions(stringArray,
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
            mHandler.sendEmptyMessage(1);
        }else {
            mHandler.sendEmptyMessage(1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                mHandler.sendEmptyMessage(1);
                break;
        }
    }
}

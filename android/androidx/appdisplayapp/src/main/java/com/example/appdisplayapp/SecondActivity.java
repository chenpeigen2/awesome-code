package com.example.appdisplayapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnBind, btnUnbind, btnGetCount, btnGetRandom;
    private TextView tvResult;

    // 服务代理接口
    private MyService01 mService;
    // 标识服务是否已绑定
    private boolean mBound = false;

    // 定义 ServiceConnection 对象
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 当绑定成功时调用
            Log.d(TAG, "onServiceConnected: 绑定成功");
            MyService01.MyBinder binder = (MyService01.MyBinder) service;
            mService = binder.getService();
            mBound = true;
            tvResult.setText("服务已绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 当与服务连接意外断开时调用（如服务崩溃或被杀死）
            Log.d(TAG, "onServiceDisconnected: 连接断开");
            mBound = false;
            mService = null;
            tvResult.setText("服务连接断开");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        initViews();

        // 绑定按钮点击后绑定服务
        btnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, MyService01.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                // BIND_AUTO_CREATE：如果服务尚未创建，则创建服务
            }
        });

        // 解绑按钮点击后解绑服务
        btnUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                    mService = null;
                    tvResult.setText("服务已解绑");
                }
            }
        });

        // 调用服务中的 getCount()
        btnGetCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound && mService != null) {
                    int count = mService.getCount();
                    tvResult.setText("当前计数: " + count);
                } else {
                    tvResult.setText("服务未绑定");
                }
            }
        });

        // 调用服务中的 getRandomNumber()
        btnGetRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound && mService != null) {
                    int random = mService.getRandomNumber();
                    tvResult.setText("随机数: " + random);
                } else {
                    tvResult.setText("服务未绑定");
                }
            }
        });
    }

    private void initViews() {
        btnBind = findViewById(R.id.btn_bind);
        btnUnbind = findViewById(R.id.btn_unbind);
        btnGetCount = findViewById(R.id.btn_get_count);
        btnGetRandom = findViewById(R.id.btn_get_random);
        tvResult = findViewById(R.id.tv_result);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 避免服务泄漏，在 Activity 不可见时解绑服务
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            mService = null;
            Log.d(TAG, "onStop: 自动解绑服务");
        }
    }
}

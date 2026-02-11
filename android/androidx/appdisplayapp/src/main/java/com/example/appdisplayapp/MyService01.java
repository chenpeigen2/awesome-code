package com.example.appdisplayapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.os.Binder;
import android.util.Log;

import java.util.Random;

public class MyService01 extends Service {
    private static final String TAG = "MyService01";
    // 用于模拟业务数据
    private int mCount = 0;
    private Random mRandom = new Random();

    // 定义 Binder 对象，内部类继承 Binder
    public class MyBinder extends Binder {
        // 返回当前服务的实例，这样客户端就可以调用服务中的公开方法
        public MyService01 getService() {
            return MyService01.this;
        }
    }

    // 创建 Binder 实例
    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: 服务绑定");
        return mBinder;
    }

    // ---------- 供客户端调用的业务方法 ----------

    /**
     * 获取当前的计数值
     */
    public int getCount() {
        return ++mCount;
    }

    /**
     * 获取一个随机数
     */
    public int getRandomNumber() {
        return mRandom.nextInt(100);
    }
}

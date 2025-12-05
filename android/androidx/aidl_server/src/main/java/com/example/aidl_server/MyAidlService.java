package com.example.aidl_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.appdisplayapp.IMyAidlInterface;

public class MyAidlService extends Service {
    private static final String TAG = "MyAidlService";

    // 实现AIDL接口
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            Log.d(TAG, "收到加法请求: " + a + " + " + b);
            int result = a + b;
            // 这里可以添加业务逻辑
            return result;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "AIDL服务已创建");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "客户端绑定服务");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "客户端解绑服务");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AIDL服务已销毁");
    }
}

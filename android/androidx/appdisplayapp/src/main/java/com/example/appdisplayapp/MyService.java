package com.example.appdisplayapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class MyService extends Service {
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            // 在此实现具体的业务逻辑
            Log.d("MyService", "Received request to add: " + a + " and " + b);
            return a + b;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder; // 返回实现了 AIDL 接口的 Binder 对象
    }

}

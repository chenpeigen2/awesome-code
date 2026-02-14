package com.example.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.common.IRenderService;
import com.example.common.SurfacePackageWrapper;
import com.example.common.WindowConfig;

/**
 * 渲染服务
 * 
 * 提供跨进程渲染接口，客户端通过AIDL调用
 */
public class RenderService extends Service {
    private static final String TAG = "RenderService";

    private HostManager mHostManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());
        mHostManager = HostManager.getInstance(this);
    }

    private final IRenderService.Stub mBinder = new IRenderService.Stub() {
        @Override
        public IBinder getHostToken() throws RemoteException {
            Log.d(TAG, "getHostToken");
            return mHostManager.getHostToken();
        }

        @Override
        public int getDisplayId() throws RemoteException {
            return mHostManager.getDisplayId();
        }

        @Override
        public void showWindow(String windowId, WindowConfig config, 
                              SurfacePackageWrapper surfacePackageWrapper) 
                throws RemoteException {
            Log.d(TAG, "showWindow: " + windowId);
            if (surfacePackageWrapper != null) {
                mHostManager.showWindow(windowId, config, surfacePackageWrapper.getSurfacePackage());
            }
        }

        @Override
        public void hideWindow(String windowId) throws RemoteException {
            Log.d(TAG, "hideWindow: " + windowId);
            mHostManager.hideWindow(windowId);
        }

        @Override
        public void updateWindowPosition(String windowId, int x, int y) throws RemoteException {
            Log.d(TAG, "updateWindowPosition: " + windowId);
            mHostManager.updateWindowPosition(windowId, x, y);
        }

        @Override
        public void updateWindowSize(String windowId, int width, int height) throws RemoteException {
            Log.d(TAG, "updateWindowSize: " + windowId);
            mHostManager.updateWindowSize(windowId, width, height);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }
}

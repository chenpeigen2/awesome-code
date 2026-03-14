package com.example.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControlViewHost;
import android.view.WindowManager;

import com.example.common.IClientCallback;
import com.example.common.IClientService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client 端渲染服务
 * 
 * 架构流程：
 * 1. Server 绑定此 Service
 * 2. Server 调用 registerHost(callback) 注册回调
 * 3. Server 调用 setHostToken(token, displayId) 传递 HostToken
 * 4. Server 调用 createSurfacePackage(windowId, width, height) 请求创建
 * 5. Client 创建 SurfacePackage 后通过 callback.onSurfacePackageReady() 返回
 */
public class ClientRenderService extends Service {
    private static final String TAG = "ClientRenderService";

    // 主线程 Handler
    private Handler mMainHandler;

    // Server 传递的 HostToken
    private IBinder mHostToken;
    private int mDisplayId;
    
    // Server 注册的回调
    private IClientCallback mCallback;
    
    // 窗口 ID -> SurfaceControlViewHost 映射
    private final Map<String, SurfaceControlViewHost> mPackages = new ConcurrentHashMap<>();
    
    // 窗口计数
    private int mWindowCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    private final IClientService.Stub mBinder = new IClientService.Stub() {
        @Override
        public void registerHost(IClientCallback callback) throws RemoteException {
            Log.d(TAG, "registerHost: callback=" + callback);
            mCallback = callback;
        }

        @Override
        public void unregisterHost(IClientCallback callback) throws RemoteException {
            Log.d(TAG, "unregisterHost");
            if (mCallback == callback) {
                mCallback = null;
            }
        }

        @Override
        public void setHostToken(IBinder hostToken, int displayId) throws RemoteException {
            Log.d(TAG, "setHostToken: token=" + hostToken + ", displayId=" + displayId);
            mHostToken = hostToken;
            mDisplayId = displayId;
        }

        @Override
        public void createSurfacePackage(String windowId, int width, int height) throws RemoteException {
            Log.d(TAG, "createSurfacePackage: " + windowId + ", size=" + width + "x" + height);
            
            // 切换到主线程执行
            mMainHandler.post(() -> createSurfacePackageOnMainThread(windowId, width, height));
        }

        @Override
        public void destroySurfacePackage(String windowId) throws RemoteException {
            Log.d(TAG, "destroySurfacePackage: " + windowId);
            
            // 切换到主线程执行
            mMainHandler.post(() -> {
                SurfaceControlViewHost scvh = mPackages.remove(windowId);
                if (scvh != null) {
                    scvh.release();
                    Log.d(TAG, "SurfacePackage released: " + windowId);
                }
            });
        }
    };

    /**
     * 在主线程创建 SurfacePackage
     * 必须在 UI 线程执行，因为涉及 View 和 Handler 创建
     */
    private void createSurfacePackageOnMainThread(String windowId, int width, int height) {
        if (mHostToken == null) {
            Log.e(TAG, "HostToken is null!");
            notifyError(windowId, "HostToken is null");
            return;
        }
        
        if (mCallback == null) {
            Log.e(TAG, "Callback is null!");
            return;
        }
        
        try {
            // 创建 SurfaceControlViewHost
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            android.view.Display display = wm.getDefaultDisplay();
            
            SurfaceControlViewHost scvh = new SurfaceControlViewHost(this, display, mHostToken);
            
            // 创建渲染内容
            DemoRenderView renderView = new DemoRenderView(this);
            mWindowCount++;
            renderView.setLabel("窗口 #" + mWindowCount);
            
            // 设置 View
            scvh.setView(renderView, width, height);
            
            // 获取 SurfacePackage
            SurfaceControlViewHost.SurfacePackage surfacePackage = scvh.getSurfacePackage();
            
            // 存储 SCVH 以便后续管理
            mPackages.put(windowId, scvh);
            
            // 通过回调返回 SurfacePackage 给 Server
            mCallback.onSurfacePackageReady(windowId, surfacePackage);
            
            Log.d(TAG, "SurfacePackage created and sent: " + windowId);
            
        } catch (Exception e) {
            Log.e(TAG, "createSurfacePackage error", e);
            notifyError(windowId, e.getMessage());
        }
    }

    private void notifyError(String windowId, String error) {
        if (mCallback != null) {
            try {
                mCallback.onError(windowId, error);
            } catch (RemoteException e) {
                Log.e(TAG, "notifyError failed", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: PID=" + android.os.Process.myPid());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        // 清理所有资源
        for (SurfaceControlViewHost scvh : mPackages.values()) {
            scvh.release();
        }
        mPackages.clear();
        mCallback = null;
        mHostToken = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}


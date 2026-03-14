package com.example.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControlViewHost;

import com.example.common.IClientCallback;
import com.example.common.IClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * Server 端连接管理器
 * 
 * 负责绑定 Client 的 ClientRenderService：
 * 1. bindService 连接 Client
 * 2. registerHost 注册回调
 * 3. setHostToken 传递 HostToken
 * 4. createSurfacePackage 请求创建 SurfacePackage
 * 5. 收到 SurfacePackage 后交给 HostManager 渲染
 */
public class ServerConnectionManager {
    private static final String TAG = "ServerConnManager";

    private static ServerConnectionManager sInstance;

    private final Context mContext;
    private final Handler mHandler;
    private IClientService mClientService;
    private boolean mServiceConnected = false;
    private ServiceConnection mServiceConnection;
    private HostManager mHostManager;
    private final List<ConnectionCallback> mCallbacks = new ArrayList<>();
    private int mWindowCount = 0;

    public interface ConnectionCallback {
        void onClientConnected();
        void onClientDisconnected();
        void onWindowCreated(String windowId);
        void onWindowDestroyed(String windowId);
        void onError(String error);
    }

    // Server 实现的回调，用于接收 Client 返回的 SurfacePackage
    private final IClientCallback mClientCallback = new IClientCallback.Stub() {
        @Override
        public void onSurfacePackageReady(String windowId, SurfaceControlViewHost.SurfacePackage surfacePackage) 
                throws RemoteException {
            Log.d(TAG, "onSurfacePackageReady: " + windowId);
            
            mHandler.post(() -> {
                if (mHostManager != null && surfacePackage != null) {
                    // 使用默认配置显示窗口
                    com.example.common.WindowConfig config = new com.example.common.WindowConfig();
                    mHostManager.showWindow(windowId, config, surfacePackage);
                    
                    notifyWindowCreated(windowId);
                }
            });
        }

        @Override
        public void onSurfacePackageDestroyed(String windowId) throws RemoteException {
            Log.d(TAG, "onSurfacePackageDestroyed: " + windowId);
            notifyWindowDestroyed(windowId);
        }

        @Override
        public void onError(String windowId, String error) throws RemoteException {
            Log.e(TAG, "Client error: " + windowId + " - " + error);
            notifyError(error);
        }
    };

    private ServerConnectionManager(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        mHostManager = HostManager.getInstance(context);
    }

    public static synchronized ServerConnectionManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ServerConnectionManager(context);
        }
        return sInstance;
    }

    /**
     * 添加连接回调
     */
    public void addCallback(ConnectionCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    /**
     * 移除连接回调
     */
    public void removeCallback(ConnectionCallback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * 是否已连接 Client
     */
    public boolean isClientConnected() {
        return mServiceConnected && mClientService != null;
    }

    /**
     * 连接 Client Service
     */
    public void connectClient(String clientPackage, String serviceAction) {
        Log.d(TAG, "connectClient: " + clientPackage + ", action=" + serviceAction);
        
        Intent intent = new Intent(serviceAction);
        intent.setPackage(clientPackage);
        
        Intent explicitIntent = createExplicitIntent(intent);
        if (explicitIntent == null) {
            notifyError("找不到客户端应用: " + clientPackage);
            return;
        }
        
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: " + name);
                mClientService = IClientService.Stub.asInterface(service);
                mServiceConnected = true;
                
                // 注册回调
                try {
                    mClientService.registerHost(mClientCallback);
                    
                    // 传递 HostToken
                    IBinder hostToken = mHostManager.getHostToken();
                    int displayId = mHostManager.getDisplayId();
                    
                    if (hostToken != null) {
                        mClientService.setHostToken(hostToken, displayId);
                        Log.d(TAG, "HostToken sent to client");
                    } else {
                        Log.w(TAG, "HostToken is null, waiting...");
                    }
                    
                    notifyClientConnected();
                    
                } catch (RemoteException e) {
                    Log.e(TAG, "registerHost error", e);
                    notifyError("注册回调失败: " + e.getMessage());
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                mClientService = null;
                mServiceConnected = false;
                notifyClientDisconnected();
            }
        };
        
        boolean result = mContext.bindService(explicitIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
    }

    /**
     * 连接默认 Client
     */
    public void connectClient() {
        connectClient("com.example.client", "com.example.client.RENDER_SERVICE");
    }

    /**
     * 断开 Client
     */
    public void disconnectClient() {
        if (mServiceConnection != null) {
            try {
                if (mClientService != null) {
                    mClientService.unregisterHost(mClientCallback);
                }
                mContext.unbindService(mServiceConnection);
            } catch (Exception e) {
                Log.e(TAG, "unbindService error", e);
            }
            mServiceConnection = null;
        }
        mClientService = null;
        mServiceConnected = false;
    }

    /**
     * 请求创建窗口
     */
    public String requestCreateWindow(int width, int height) {
        if (!isClientConnected()) {
            notifyError("Client 未连接");
            return null;
        }
        
        try {
            // 确保 HostToken 已传递
            IBinder hostToken = mHostManager.getHostToken();
            if (hostToken != null) {
                mClientService.setHostToken(hostToken, mHostManager.getDisplayId());
            }
            
            mWindowCount++;
            String windowId = "window_" + System.currentTimeMillis() + "_" + mWindowCount;
            
            mClientService.createSurfacePackage(windowId, width, height);
            Log.d(TAG, "createSurfacePackage requested: " + windowId);
            
            return windowId;
            
        } catch (RemoteException e) {
            Log.e(TAG, "requestCreateWindow error", e);
            notifyError("创建窗口失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 请求销毁窗口
     */
    public void requestDestroyWindow(String windowId) {
        if (!isClientConnected()) {
            return;
        }
        
        try {
            mClientService.destroySurfacePackage(windowId);
            mHostManager.hideWindow(windowId);
            Log.d(TAG, "destroySurfacePackage: " + windowId);
        } catch (RemoteException e) {
            Log.e(TAG, "requestDestroyWindow error", e);
        }
    }

    /**
     * 当 HostToken 准备好后调用
     */
    public void onHostTokenReady(IBinder hostToken) {
        if (isClientConnected() && mClientService != null) {
            try {
                mClientService.setHostToken(hostToken, mHostManager.getDisplayId());
                Log.d(TAG, "HostToken updated to client");
            } catch (RemoteException e) {
                Log.e(TAG, "setHostToken error", e);
            }
        }
    }

    private Intent createExplicitIntent(Intent implicitIntent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        
        if (resolveInfo == null || resolveInfo.isEmpty()) {
            Log.w(TAG, "No services found for: " + implicitIntent);
            return null;
        }
        
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        
        return explicitIntent;
    }

    private void notifyClientConnected() {
        mHandler.post(() -> {
            for (ConnectionCallback callback : mCallbacks) {
                callback.onClientConnected();
            }
        });
    }

    private void notifyClientDisconnected() {
        mHandler.post(() -> {
            for (ConnectionCallback callback : mCallbacks) {
                callback.onClientDisconnected();
            }
        });
    }

    private void notifyWindowCreated(String windowId) {
        mHandler.post(() -> {
            for (ConnectionCallback callback : mCallbacks) {
                callback.onWindowCreated(windowId);
            }
        });
    }

    private void notifyWindowDestroyed(String windowId) {
        mHandler.post(() -> {
            for (ConnectionCallback callback : mCallbacks) {
                callback.onWindowDestroyed(windowId);
            }
        });
    }

    private void notifyError(String error) {
        mHandler.post(() -> {
            for (ConnectionCallback callback : mCallbacks) {
                callback.onError(error);
            }
        });
    }
}

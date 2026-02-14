package com.example.client;

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
import android.view.Gravity;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.WindowManager;

import com.example.common.IRenderService;
import com.example.common.SurfacePackageWrapper;
import com.example.common.WindowConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端管理器
 * 
 * 独立于Activity，管理与服务端的连接和渲染
 */
public class ClientManager {
    private static final String TAG = "ClientManager";

    private static ClientManager sInstance;

    private final Context mContext;
    private final Handler mHandler;
    private IRenderService mRenderService;
    private boolean mServiceConnected = false;
    private final List<String> mWindowIds = new ArrayList<>();
    private ClientCallback mCallback;
    private ServiceConnection mServiceConnection;
    private int mWindowOffset = 0;

    public interface ClientCallback {
        void onServiceConnected();
        void onServiceDisconnected();
        void onWindowCreated(String windowId, int windowCount);
        void onWindowRemoved(String windowId, int windowCount);
        void onError(String error);
    }

    private ClientManager(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized ClientManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ClientManager(context);
        }
        return sInstance;
    }

    /**
     * 设置回调
     */
    public void setCallback(ClientCallback callback) {
        mCallback = callback;
    }

    /**
     * 设置窗口偏移量（每次添加窗口时的Y偏移）
     */
    public void setWindowOffset(int offset) {
        mWindowOffset = offset;
    }

    /**
     * 是否已连接服务
     */
    public boolean isServiceConnected() {
        return mServiceConnected && mRenderService != null;
    }

    /**
     * 获取窗口数量
     */
    public int getWindowCount() {
        return mWindowIds.size();
    }

    /**
     * 获取所有窗口ID
     */
    public List<String> getWindowIds() {
        return new ArrayList<>(mWindowIds);
    }

    /**
     * 连接服务端
     */
    public void connectService(String serverPackage, String serviceAction) {
        Intent intent = new Intent(serviceAction);
        intent.setPackage(serverPackage);

        Intent explicitIntent = createExplicitIntent(intent);
        if (explicitIntent == null) {
            notifyError("找不到服务端应用: " + serverPackage);
            return;
        }

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: " + name);
                mRenderService = IRenderService.Stub.asInterface(service);
                mServiceConnected = true;
                notifyServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                mRenderService = null;
                mServiceConnected = false;
                notifyServiceDisconnected();
            }
        };

        boolean result = mContext.bindService(explicitIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
    }

    /**
     * 连接默认服务端
     */
    public void connectService() {
        connectService("com.example.server", "com.example.server.RENDER_SERVICE");
    }

    /**
     * 断开服务
     */
    public void disconnectService() {
        if (mServiceConnection != null) {
            try {
                mContext.unbindService(mServiceConnection);
            } catch (Exception e) {
                Log.e(TAG, "unbindService error", e);
            }
            mServiceConnection = null;
        }
        mRenderService = null;
        mServiceConnected = false;
        mWindowIds.clear();
    }

    /**
     * 显示窗口
     */
    public String showWindow(View contentView, int width, int height, int x, int y) {
        if (!isServiceConnected()) {
            notifyError("服务未连接");
            return null;
        }

        try {
            IBinder hostToken = mRenderService.getHostToken();
            if (hostToken == null) {
                notifyError("HostToken为空");
                return null;
            }

            int displayId = mRenderService.getDisplayId();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            android.view.Display display = windowManager.getDefaultDisplay();

            SurfaceControlViewHost scvh = new SurfaceControlViewHost(mContext, display, hostToken);
            scvh.setView(contentView, width, height);

            SurfaceControlViewHost.SurfacePackage surfacePackage = scvh.getSurfacePackage();

            String windowId = "window_" + System.currentTimeMillis();
            int offsetY = y + (mWindowIds.size() * mWindowOffset);
            WindowConfig config = new WindowConfig(width, height, x, offsetY, Gravity.TOP | Gravity.LEFT);

            mRenderService.showWindow(windowId, config, new SurfacePackageWrapper(surfacePackage));
            mWindowIds.add(windowId);

            notifyWindowCreated(windowId);
            return windowId;

        } catch (RemoteException e) {
            Log.e(TAG, "showWindow: RemoteException", e);
            notifyError("远程调用失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 显示窗口（使用默认位置）
     */
    public String showWindow(View contentView, int width, int height) {
        return showWindow(contentView, width, height, 50, 50);
    }

    /**
     * 隐藏指定窗口
     */
    public boolean hideWindow(String windowId) {
        if (!isServiceConnected()) {
            return false;
        }

        try {
            mRenderService.hideWindow(windowId);
            mWindowIds.remove(windowId);
            notifyWindowRemoved(windowId);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "hideWindow: RemoteException", e);
            return false;
        }
    }

    /**
     * 隐藏最后一个窗口
     */
    public boolean hideLastWindow() {
        if (mWindowIds.isEmpty()) {
            return false;
        }
        String lastId = mWindowIds.get(mWindowIds.size() - 1);
        return hideWindow(lastId);
    }

    /**
     * 隐藏所有窗口
     */
    public void hideAllWindows() {
        if (!isServiceConnected()) {
            return;
        }

        try {
            for (String windowId : new ArrayList<>(mWindowIds)) {
                mRenderService.hideWindow(windowId);
            }
            mWindowIds.clear();
        } catch (RemoteException e) {
            Log.e(TAG, "hideAllWindows: RemoteException", e);
        }
    }

    /**
     * 更新窗口位置
     */
    public boolean updateWindowPosition(String windowId, int x, int y) {
        if (!isServiceConnected()) {
            return false;
        }

        try {
            mRenderService.updateWindowPosition(windowId, x, y);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "updateWindowPosition: RemoteException", e);
            return false;
        }
    }

    /**
     * 更新窗口尺寸
     */
    public boolean updateWindowSize(String windowId, int width, int height) {
        if (!isServiceConnected()) {
            return false;
        }

        try {
            mRenderService.updateWindowSize(windowId, width, height);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "updateWindowSize: RemoteException", e);
            return false;
        }
    }

    private Intent createExplicitIntent(Intent implicitIntent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, PackageManager.MATCH_DEFAULT_ONLY);

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

    private void notifyServiceConnected() {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onServiceConnected();
            }
        });
    }

    private void notifyServiceDisconnected() {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onServiceDisconnected();
            }
        });
    }

    private void notifyWindowCreated(String windowId) {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onWindowCreated(windowId, mWindowIds.size());
            }
        });
    }

    private void notifyWindowRemoved(String windowId) {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onWindowRemoved(windowId, mWindowIds.size());
            }
        });
    }

    private void notifyError(String error) {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onError(error);
            }
        });
    }
}

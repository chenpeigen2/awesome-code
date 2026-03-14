package com.example.remoteviews.client;

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
import android.widget.RemoteViews;

import com.example.remoteviews.common.IRemoteViewsCallback;
import com.example.remoteviews.common.IRemoteViewsService;
import com.example.remoteviews.common.RemoteViewInfo;

import java.util.ArrayList;
import java.util.List;

public class RemoteViewsClient {
    private static final String TAG = "RemoteViewsClient";

    private static RemoteViewsClient sInstance;

    private final Context mContext;
    private final Handler mHandler;
    private IRemoteViewsService mService;
    private boolean mServiceConnected = false;
    private ClientCallback mCallback;
    private ServiceConnection mServiceConnection;
    private final List<String> mLoadedViewIds = new ArrayList<>();

    public interface ClientCallback {
        void onServiceConnected();
        void onServiceDisconnected();
        void onRemoteViewsReceived(String viewId, RemoteViews remoteViews);
        void onViewClicked(String viewId, String action);
        void onError(String error);
    }

    private final IRemoteViewsCallback mCallbackStub = new IRemoteViewsCallback.Stub() {
        @Override
        public void onRemoteViewsUpdated(RemoteViewInfo info) throws RemoteException {
            Log.d(TAG, "onRemoteViewsUpdated: " + info.getViewId());
            mHandler.post(() -> {
                if (mCallback != null) {
                    mCallback.onRemoteViewsReceived(info.getViewId(), info.getRemoteViews());
                }
            });
        }

        @Override
        public void onViewClicked(String viewId, String action) throws RemoteException {
            Log.d(TAG, "onViewClicked: viewId=" + viewId + ", action=" + action);
            mHandler.post(() -> {
                if (mCallback != null) {
                    mCallback.onViewClicked(viewId, action);
                }
            });
        }

        @Override
        public void onServiceReady() throws RemoteException {
            Log.d(TAG, "onServiceReady");
            mHandler.post(() -> {
                if (mCallback != null) {
                    mCallback.onServiceConnected();
                }
            });
        }
    };

    private RemoteViewsClient(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized RemoteViewsClient getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RemoteViewsClient(context);
        }
        return sInstance;
    }

    public void setCallback(ClientCallback callback) {
        mCallback = callback;
    }

    public boolean isServiceConnected() {
        return mServiceConnected && mService != null;
    }

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
                mService = IRemoteViewsService.Stub.asInterface(service);
                mServiceConnected = true;
                
                try {
                    mService.registerCallback(mCallbackStub);
                } catch (RemoteException e) {
                    Log.e(TAG, "registerCallback error", e);
                }
                
                notifyServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                mService = null;
                mServiceConnected = false;
                notifyServiceDisconnected();
            }
        };

        boolean result = mContext.bindService(explicitIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
    }

    public void connectService() {
        connectService(
                com.example.remoteviews.common.Constants.SERVER_PACKAGE,
                com.example.remoteviews.common.Constants.SERVICE_ACTION
        );
    }

    public void disconnectService() {
        if (mService != null) {
            try {
                mService.unregisterCallback(mCallbackStub);
            } catch (RemoteException e) {
                Log.e(TAG, "unregisterCallback error", e);
            }
        }
        
        if (mServiceConnection != null) {
            try {
                mContext.unbindService(mServiceConnection);
            } catch (Exception e) {
                Log.e(TAG, "unbindService error", e);
            }
            mServiceConnection = null;
        }
        mService = null;
        mServiceConnected = false;
        mLoadedViewIds.clear();
    }

    public RemoteViews getRemoteViews(String viewId) {
        if (!isServiceConnected()) {
            notifyError("服务未连接");
            return null;
        }

        try {
            RemoteViewInfo info = mService.getRemoteViewInfo(viewId);
            if (info != null) {
                mLoadedViewIds.add(viewId);
                return info.getRemoteViews();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getRemoteViews error", e);
            notifyError("获取RemoteViews失败: " + e.getMessage());
        }
        return null;
    }

    public void updateText(String viewId, String text) {
        if (!isServiceConnected()) {
            notifyError("服务未连接");
            return;
        }

        try {
            mService.updateText(viewId, text);
        } catch (RemoteException e) {
            Log.e(TAG, "updateText error", e);
            notifyError("更新文本失败: " + e.getMessage());
        }
    }

    public void setViewVisibility(String viewId, int visibility) {
        if (!isServiceConnected()) {
            return;
        }

        try {
            mService.setViewVisibility(viewId, visibility);
        } catch (RemoteException e) {
            Log.e(TAG, "setViewVisibility error", e);
        }
    }

    public List<String> getLoadedViewIds() {
        return new ArrayList<>(mLoadedViewIds);
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

    private void notifyError(String error) {
        mHandler.post(() -> {
            if (mCallback != null) {
                mCallback.onError(error);
            }
        });
    }
}

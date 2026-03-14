package com.example.remoteviews.server;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.remoteviews.common.IRemoteViewsCallback;
import com.example.remoteviews.common.IRemoteViewsService;
import com.example.remoteviews.common.RemoteViewInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteViewsService extends Service {
    private static final String TAG = "RemoteViewsService";
    
    private final RemoteCallbackList<IRemoteViewsCallback> mCallbacks = 
            new RemoteCallbackList<>();
    private final Map<String, RemoteViewData> mViewDataMap = new HashMap<>();
    private final AtomicInteger mUpdateCounter = new AtomicInteger(0);
    
    private static class RemoteViewData {
        String title;
        String content;
        int backgroundColor;
        int visibility;
        
        RemoteViewData(String title, String content, int backgroundColor) {
            this.title = title;
            this.content = content;
            this.backgroundColor = backgroundColor;
            this.visibility = android.view.View.VISIBLE;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());
        initDefaultViews();
    }

    private void initDefaultViews() {
        mViewDataMap.put("main_view", new RemoteViewData(
                "跨进程渲染演示",
                "这是一个RemoteViews跨进程渲染示例",
                Color.parseColor("#4CAF50")
        ));
        mViewDataMap.put("card_view", new RemoteViewData(
                "卡片视图",
                "点击按钮更新内容",
                Color.parseColor("#2196F3")
        ));
        mViewDataMap.put("list_view", new RemoteViewData(
                "列表项",
                "列表内容展示",
                Color.parseColor("#FF9800")
        ));
    }

    private final IRemoteViewsService.Stub mBinder = new IRemoteViewsService.Stub() {
        @Override
        public RemoteViewInfo getRemoteViewInfo(String viewId) throws RemoteException {
            Log.d(TAG, "getRemoteViewInfo: " + viewId);
            RemoteViews remoteViews = createRemoteViews(viewId);
            if (remoteViews != null) {
                RemoteViewData data = mViewDataMap.get(viewId);
                String description = data != null ? data.title : "Unknown View";
                return new RemoteViewInfo(viewId, remoteViews, description);
            }
            return null;
        }

        @Override
        public void updateText(String viewId, String text) throws RemoteException {
            Log.d(TAG, "updateText: viewId=" + viewId + ", text=" + text);
            RemoteViewData data = mViewDataMap.get(viewId);
            if (data != null) {
                data.content = text;
                notifyViewsUpdated(viewId);
            }
        }

        @Override
        public void updateImage(String viewId, byte[] imageData) throws RemoteException {
            Log.d(TAG, "updateImage: viewId=" + viewId);
        }

        @Override
        public void setViewVisibility(String viewId, int visibility) throws RemoteException {
            Log.d(TAG, "setViewVisibility: viewId=" + viewId + ", visibility=" + visibility);
            RemoteViewData data = mViewDataMap.get(viewId);
            if (data != null) {
                data.visibility = visibility;
                notifyViewsUpdated(viewId);
            }
        }

        @Override
        public void registerCallback(IRemoteViewsCallback callback) throws RemoteException {
            Log.d(TAG, "registerCallback");
            mCallbacks.register(callback);
            callback.onServiceReady();
        }

        @Override
        public void unregisterCallback(IRemoteViewsCallback callback) throws RemoteException {
            Log.d(TAG, "unregisterCallback");
            mCallbacks.unregister(callback);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + intent);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "com.example.remoteviews.server.UPDATE_VIEWS".equals(intent.getAction())) {
            String viewId = intent.getStringExtra(Constants.EXTRA_VIEW_ID);
            Log.d(TAG, "onStartCommand: update views for " + viewId);
            
            mUpdateCounter.incrementAndGet();
            
            if (viewId != null) {
                notifyViewsUpdated(viewId);
            }
        }
        return START_NOT_STICKY;
    }

    private RemoteViews createRemoteViews(String viewId) {
        RemoteViewData data = mViewDataMap.get(viewId);
        if (data == null) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(
                getPackageName(),
                R.layout.remote_view_layout
        );

        remoteViews.setTextViewText(R.id.tv_title, data.title);
        remoteViews.setTextViewText(R.id.tv_content, data.content);
        remoteViews.setInt(R.id.card_container, "setBackgroundColor", data.backgroundColor);
        remoteViews.setTextViewText(R.id.tv_counter, "更新次数: " + mUpdateCounter.get());
        
        Intent clickIntent = new Intent(Constants.ACTION_BUTTON_CLICK);
        clickIntent.setComponent(new android.content.ComponentName(getPackageName(), 
                ButtonClickReceiver.class.getName()));
        clickIntent.putExtra(Constants.EXTRA_VIEW_ID, viewId);
        
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                this,
                viewId.hashCode(),
                clickIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );
        remoteViews.setOnClickPendingIntent(R.id.btn_action, pendingIntent);

        return remoteViews;
    }

    private void notifyViewsUpdated(String viewId) {
        int count = mCallbacks.beginBroadcast();
        try {
            RemoteViewInfo info = mBinder.getRemoteViewInfo(viewId);
            for (int i = 0; i < count; i++) {
                try {
                    mCallbacks.getBroadcastItem(i).onRemoteViewsUpdated(info);
                } catch (RemoteException e) {
                    Log.e(TAG, "notifyViewsUpdated error", e);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getRemoteViewInfo error", e);
        } finally {
            mCallbacks.finishBroadcast();
        }
    }

    public void incrementCounter() {
        mUpdateCounter.incrementAndGet();
    }
}

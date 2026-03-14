package com.example.remoteviews.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class ButtonClickReceiver extends BroadcastReceiver {
    private static final String TAG = "ButtonClickReceiver";
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: action=" + action);
        
        if (Constants.ACTION_BUTTON_CLICK.equals(action)) {
            String viewId = intent.getStringExtra(Constants.EXTRA_VIEW_ID);
            Log.d(TAG, "Button clicked for view: " + viewId);
            
            mHandler.post(() -> {
                Toast.makeText(context, "服务端收到点击事件: " + viewId, Toast.LENGTH_SHORT).show();
            });
            
            Intent serviceIntent = new Intent(context, RemoteViewsService.class);
            serviceIntent.setAction("com.example.remoteviews.server.UPDATE_VIEWS");
            serviceIntent.putExtra(Constants.EXTRA_VIEW_ID, viewId);
            context.startService(serviceIntent);
        }
    }
}

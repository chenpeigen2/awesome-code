package com.example.remoteviews.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServerActivity extends AppCompatActivity {
    private static final String TAG = "ServerActivity";
    private TextView mStatusText;
    private TextView mPidText;
    private TextView mClientCountText;
    private int mClientCount = 0;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String viewId = intent.getStringExtra(Constants.EXTRA_VIEW_ID);
            Log.d(TAG, "Received update request for: " + viewId);
            Toast.makeText(context, "更新视图: " + viewId, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        
        mStatusText = findViewById(R.id.tv_status);
        mPidText = findViewById(R.id.tv_pid);
        mClientCountText = findViewById(R.id.tv_client_count);
        
        mPidText.setText("服务端进程 PID: " + android.os.Process.myPid());
        mStatusText.setText("服务状态: 运行中");
        
        IntentFilter filter = new IntentFilter("com.example.remoteviews.server.UPDATE_VIEWS");
        registerReceiver(mUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        
        Log.d(TAG, "ServerActivity created, PID=" + android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mUpdateReceiver);
        } catch (Exception e) {
            Log.e(TAG, "unregisterReceiver error", e);
        }
    }
}

package com.example.remoteviews.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.remoteviews.common.Constants;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";

    private RemoteViewsClient mClient;
    private FrameLayout mRemoteViewsContainer;
    private TextView mStatusText;
    private TextView mPidText;
    private Button mBtnConnect;
    private Button mBtnLoadMain;
    private Button mBtnLoadCard;
    private Button mBtnUpdate;
    private Button mBtnDisconnect;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        mPidText = findViewById(R.id.tv_pid);
        mStatusText = findViewById(R.id.tv_status);
        mRemoteViewsContainer = findViewById(R.id.remote_views_container);
        mBtnConnect = findViewById(R.id.btn_connect);
        mBtnLoadMain = findViewById(R.id.btn_load_main);
        mBtnLoadCard = findViewById(R.id.btn_load_card);
        mBtnUpdate = findViewById(R.id.btn_update);
        mBtnDisconnect = findViewById(R.id.btn_disconnect);

        mPidText.setText("客户端进程 PID: " + android.os.Process.myPid());

        mClient = RemoteViewsClient.getInstance(this);
        mClient.setCallback(new RemoteViewsClient.ClientCallback() {
            @Override
            public void onServiceConnected() {
                Log.d(TAG, "onServiceConnected");
                mHandler.post(() -> {
                    mStatusText.setText("服务状态: 已连接");
                    mStatusText.setTextColor(getColor(android.R.color.holo_green_dark));
                    updateButtonStates(true);
                    Toast.makeText(ClientActivity.this, "服务连接成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onServiceDisconnected() {
                Log.d(TAG, "onServiceDisconnected");
                mHandler.post(() -> {
                    mStatusText.setText("服务状态: 未连接");
                    mStatusText.setTextColor(getColor(android.R.color.holo_red_dark));
                    updateButtonStates(false);
                    mRemoteViewsContainer.removeAllViews();
                });
            }

            @Override
            public void onRemoteViewsReceived(String viewId, RemoteViews remoteViews) {
                Log.d(TAG, "onRemoteViewsReceived: " + viewId);
                mHandler.post(() -> {
                    displayRemoteViews(remoteViews);
                    Toast.makeText(ClientActivity.this, "RemoteViews已更新: " + viewId, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onViewClicked(String viewId, String action) {
                Log.d(TAG, "onViewClicked: viewId=" + viewId + ", action=" + action);
                mHandler.post(() -> {
                    Toast.makeText(ClientActivity.this, 
                            "收到点击事件: " + viewId, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error);
                mHandler.post(() -> {
                    Toast.makeText(ClientActivity.this, "错误: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });

        mBtnConnect.setOnClickListener(v -> connectService());
        mBtnLoadMain.setOnClickListener(v -> loadMainView());
        mBtnLoadCard.setOnClickListener(v -> loadCardView());
        mBtnUpdate.setOnClickListener(v -> updateRemoteViews());
        mBtnDisconnect.setOnClickListener(v -> disconnectService());

        updateButtonStates(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClient.isServiceConnected()) {
            updateButtonStates(true);
            mStatusText.setText("服务状态: 已连接");
            mStatusText.setTextColor(getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.disconnectService();
    }

    private void connectService() {
        mStatusText.setText("服务状态: 连接中...");
        mStatusText.setTextColor(getColor(android.R.color.holo_orange_dark));
        mClient.connectService();
    }

    private void loadMainView() {
        RemoteViews remoteViews = mClient.getRemoteViews(Constants.VIEW_ID_MAIN);
        if (remoteViews != null) {
            displayRemoteViews(remoteViews);
        }
    }

    private void loadCardView() {
        RemoteViews remoteViews = mClient.getRemoteViews(Constants.VIEW_ID_CARD);
        if (remoteViews != null) {
            displayRemoteViews(remoteViews);
        }
    }

    private void updateRemoteViews() {
        if (mClient.isServiceConnected()) {
            mClient.updateText(Constants.VIEW_ID_MAIN, 
                    "更新时间: " + System.currentTimeMillis());
        }
    }

    private void disconnectService() {
        mClient.disconnectService();
        mRemoteViewsContainer.removeAllViews();
        mStatusText.setText("服务状态: 未连接");
        mStatusText.setTextColor(getColor(android.R.color.holo_red_dark));
        updateButtonStates(false);
    }

    private void displayRemoteViews(RemoteViews remoteViews) {
        if (remoteViews == null) {
            return;
        }

        try {
            mRemoteViewsContainer.removeAllViews();
            View view = remoteViews.apply(this, mRemoteViewsContainer);
            mRemoteViewsContainer.addView(view);
            Log.d(TAG, "RemoteViews displayed successfully");
        } catch (Exception e) {
            Log.e(TAG, "displayRemoteViews error", e);
            Toast.makeText(this, "显示RemoteViews失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateButtonStates(boolean connected) {
        mBtnConnect.setEnabled(!connected);
        mBtnLoadMain.setEnabled(connected);
        mBtnLoadCard.setEnabled(connected);
        mBtnUpdate.setEnabled(connected);
        mBtnDisconnect.setEnabled(connected);
    }
}

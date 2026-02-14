package com.example.server;

import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 服务端宿主Activity
 * 
 * 职责：
 * 1. 提供UI界面
 * 2. 提供容器给HostManager
 */
public class HostActivity extends AppCompatActivity {
    private static final String TAG = "HostActivity";

    private FrameLayout mHostContainer;
    private TextView mStatusText;
    private HostManager mHostManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
        setupHostManager();
    }

    private void setupUI() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#1A1A2E"));
        rootLayout.setPadding(32, 32, 32, 32);

        TextView titleText = new TextView(this);
        titleText.setText("跨进程渲染 - 服务端");
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(22);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 8);
        rootLayout.addView(titleText);

        TextView processText = new TextView(this);
        processText.setText("PID: " + android.os.Process.myPid());
        processText.setTextColor(Color.parseColor("#4ECDC4"));
        processText.setTextSize(12);
        processText.setGravity(Gravity.CENTER);
        processText.setPadding(0, 0, 0, 16);
        rootLayout.addView(processText);

        mStatusText = new TextView(this);
        mStatusText.setText("状态: 初始化中...");
        mStatusText.setTextColor(Color.parseColor("#AAAAAA"));
        mStatusText.setTextSize(14);
        mStatusText.setPadding(0, 0, 0, 16);
        rootLayout.addView(mStatusText);

        Button clearButton = new Button(this);
        clearButton.setText("清除所有窗口");
        clearButton.setBackgroundColor(Color.parseColor("#666666"));
        clearButton.setTextColor(Color.WHITE);
        clearButton.setOnClickListener(v -> {
            if (mHostManager != null) {
                mHostManager.clearAllWindows();
            }
        });
        rootLayout.addView(clearButton);

        mHostContainer = new FrameLayout(this);
        mHostContainer.setBackgroundColor(Color.parseColor("#16213E"));
        FrameLayout.LayoutParams hostParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 500);
        mHostContainer.setLayoutParams(hostParams);
        rootLayout.addView(mHostContainer);

        TextView hint = new TextView(this);
        hint.setText("↑ 宿主容器：客户端渲染内容将显示在此");
        hint.setTextColor(Color.parseColor("#666666"));
        hint.setTextSize(11);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 8, 0, 0);
        rootLayout.addView(hint);

        setContentView(rootLayout);
    }

    private void setupHostManager() {
        mHostManager = HostManager.getInstance(this);
        mHostManager.setCallback(new HostManager.HostCallback() {
            @Override
            public void onTokenReady(IBinder token) {
                updateStatus("就绪 - Token已获取");
            }

            @Override
            public void onWindowCountChanged(int count) {
                updateStatus("窗口数: " + count);
            }
        });
        mHostManager.setContainer(mHostContainer);
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> mStatusText.setText("状态: " + status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mHostManager != null && mHostManager.getHostToken() != null) {
            updateStatus("就绪 - Token已获取");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        updateStatus("警告: 服务端在后台，渲染可能失效");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mHostManager != null) {
            mHostManager.release();
        }
    }
}

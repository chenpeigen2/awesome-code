package com.example.server;

import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 服务端宿主Activity（新架构）
 * 
 * 架构流程：
 * 1. Server 提供 SurfaceView 容器和 HostToken
 * 2. Server 绑定 Client 的 ClientRenderService
 * 3. Server 通过 AIDL 传递 HostToken 给 Client
 * 4. Client 创建 SurfacePackage 并通过回调返回
 * 5. Server 渲染 SurfacePackage
 */
public class HostActivity extends AppCompatActivity {
    private static final String TAG = "HostActivity";

    private FrameLayout mHostContainer;
    private TextView mStatusText;
    private EditText mWidthEdit;
    private EditText mHeightEdit;
    private HostManager mHostManager;
    private ServerConnectionManager mConnectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
        setupHostManager();
        setupConnectionManager();
    }

    private void setupUI() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#1A1A2E"));
        rootLayout.setPadding(32, 32, 32, 32);

        TextView titleText = new TextView(this);
        titleText.setText("跨进程渲染 - 服务端（新架构）");
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

        // 尺寸配置
        TextView sizeTitle = new TextView(this);
        sizeTitle.setText("请求 Client 渲染尺寸:");
        sizeTitle.setTextColor(Color.WHITE);
        sizeTitle.setTextSize(14);
        sizeTitle.setPadding(0, 0, 0, 8);
        rootLayout.addView(sizeTitle);

        LinearLayout sizeLayout = new LinearLayout(this);
        sizeLayout.setOrientation(LinearLayout.HORIZONTAL);
        sizeLayout.setPadding(0, 0, 0, 16);

        TextView widthLabel = new TextView(this);
        widthLabel.setText("宽:");
        widthLabel.setTextColor(Color.WHITE);
        sizeLayout.addView(widthLabel);

        mWidthEdit = new EditText(this);
        mWidthEdit.setText("300");
        mWidthEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mWidthEdit.setBackgroundColor(Color.parseColor("#2A2A4A"));
        mWidthEdit.setTextColor(Color.WHITE);
        mWidthEdit.setPadding(16, 8, 16, 8);
        sizeLayout.addView(mWidthEdit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView heightLabel = new TextView(this);
        heightLabel.setText(" 高:");
        heightLabel.setTextColor(Color.WHITE);
        sizeLayout.addView(heightLabel);

        mHeightEdit = new EditText(this);
        mHeightEdit.setText("300");
        mHeightEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mHeightEdit.setBackgroundColor(Color.parseColor("#2A2A4A"));
        mHeightEdit.setTextColor(Color.WHITE);
        mHeightEdit.setPadding(16, 8, 16, 8);
        sizeLayout.addView(mHeightEdit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(sizeLayout);

        // 请求创建按钮
        Button requestButton = new Button(this);
        requestButton.setText("请求 Client 创建 SurfacePackage");
        requestButton.setBackgroundColor(Color.parseColor("#4ECDC4"));
        requestButton.setTextColor(Color.WHITE);
        requestButton.setOnClickListener(v -> requestCreateWindow());
        rootLayout.addView(requestButton);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 8, 0, 0);

        Button clearButton = new Button(this);
        clearButton.setText("清除所有窗口");
        clearButton.setBackgroundColor(Color.parseColor("#666666"));
        clearButton.setTextColor(Color.WHITE);
        clearButton.setOnClickListener(v -> clearAllWindows());
        buttonLayout.addView(clearButton, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button disconnectButton = new Button(this);
        disconnectButton.setText("断开 Client");
        disconnectButton.setBackgroundColor(Color.parseColor("#FF6B6B"));
        disconnectButton.setTextColor(Color.WHITE);
        disconnectButton.setOnClickListener(v -> disconnectClient());
        buttonLayout.addView(disconnectButton, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(buttonLayout);

        // 渲染容器
        mHostContainer = new FrameLayout(this);
        mHostContainer.setBackgroundColor(Color.parseColor("#16213E"));
        FrameLayout.LayoutParams hostParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 500);
        mHostContainer.setLayoutParams(hostParams);
        rootLayout.addView(mHostContainer);

        TextView hint = new TextView(this);
        hint.setText("↑ 宿主容器：Client 渲染内容将显示在此");
        hint.setTextColor(Color.parseColor("#666666"));
        hint.setTextSize(11);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 8, 0, 0);
        rootLayout.addView(hint);

        TextView infoText = new TextView(this);
        infoText.setText("\n新架构说明:\n" +
                "• Server 绑定 Client 的 Service\n" +
                "• Server 传递 HostToken 给 Client\n" +
                "• Client 通过回调返回 SurfacePackage\n" +
                "• Server 渲染 Client 的内容");
        infoText.setTextColor(Color.parseColor("#666666"));
        infoText.setTextSize(11);
        rootLayout.addView(infoText);

        setContentView(rootLayout);
    }

    private void setupHostManager() {
        mHostManager = HostManager.getInstance(this);
        mHostManager.setCallback(new HostManager.HostCallback() {
            @Override
            public void onTokenReady(IBinder token) {
                updateStatus("HostToken 已就绪");
                // 通知 ConnectionManager Token 准备好了
                if (mConnectionManager != null) {
                    mConnectionManager.onHostTokenReady(token);
                }
            }

            @Override
            public void onWindowCountChanged(int count) {
                updateStatus("窗口数: " + count);
            }
        });
        mHostManager.setTokenCallback(token -> {
            // Token 准备好后通知 ConnectionManager
            if (mConnectionManager != null) {
                mConnectionManager.onHostTokenReady(token);
            }
        });
        mHostManager.setContainer(mHostContainer);
    }

    private void setupConnectionManager() {
        mConnectionManager = ServerConnectionManager.getInstance(this);
        mConnectionManager.addCallback(new ServerConnectionManager.ConnectionCallback() {
            @Override
            public void onClientConnected() {
                updateStatus("Client 已连接，可请求渲染");
            }

            @Override
            public void onClientDisconnected() {
                updateStatus("Client 已断开");
            }

            @Override
            public void onWindowCreated(String windowId) {
                Toast.makeText(HostActivity.this, "SurfacePackage 已收到: " + windowId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWindowDestroyed(String windowId) {
                updateStatus("窗口已销毁: " + windowId);
            }

            @Override
            public void onError(String error) {
                updateStatus("错误: " + error);
                Toast.makeText(HostActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        // 连接 Client 的 Service
        mConnectionManager.connectClient();
    }

    private void requestCreateWindow() {
        if (!mConnectionManager.isClientConnected()) {
            updateStatus("Client 未连接");
            return;
        }

        try {
            int width = Integer.parseInt(mWidthEdit.getText().toString());
            int height = Integer.parseInt(mHeightEdit.getText().toString());

            String windowId = mConnectionManager.requestCreateWindow(width, height);
            if (windowId != null) {
                updateStatus("已请求创建窗口: " + windowId);
            }

        } catch (NumberFormatException e) {
            updateStatus("错误: 请输入有效数字");
        }
    }

    private void clearAllWindows() {
        if (mHostManager != null) {
            mHostManager.clearAllWindows();
        }
    }

    private void disconnectClient() {
        if (mConnectionManager != null) {
            mConnectionManager.disconnectClient();
            updateStatus("已断开 Client");
        }
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> mStatusText.setText("状态: " + status));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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
        if (mConnectionManager != null) {
            mConnectionManager.disconnectClient();
        }
        if (mHostManager != null) {
            mHostManager.release();
        }
    }
}


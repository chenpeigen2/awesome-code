package com.example.client;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 客户端Activity
 */
public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";

    private ClientManager mClientManager;
    private TextView mStatusText;
    private EditText mPosXEdit;
    private EditText mPosYEdit;
    private EditText mWidthEdit;
    private EditText mHeightEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
        setupClientManager();
    }

    private void setupUI() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#0F0F23"));
        rootLayout.setPadding(32, 32, 32, 32);

        TextView titleText = new TextView(this);
        titleText.setText("跨进程渲染 - 客户端");
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(22);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 8);
        rootLayout.addView(titleText);

        TextView processText = new TextView(this);
        processText.setText("PID: " + android.os.Process.myPid());
        processText.setTextColor(Color.parseColor("#FF6B6B"));
        processText.setTextSize(12);
        processText.setGravity(Gravity.CENTER);
        processText.setPadding(0, 0, 0, 16);
        rootLayout.addView(processText);

        mStatusText = new TextView(this);
        mStatusText.setText("状态: 连接服务中...");
        mStatusText.setTextColor(Color.parseColor("#AAAAAA"));
        mStatusText.setTextSize(14);
        mStatusText.setPadding(0, 0, 0, 16);
        rootLayout.addView(mStatusText);

        TextView configTitle = new TextView(this);
        configTitle.setText("渲染位置配置:");
        configTitle.setTextColor(Color.WHITE);
        configTitle.setTextSize(14);
        configTitle.setPadding(0, 0, 0, 8);
        rootLayout.addView(configTitle);

        LinearLayout posLayout = new LinearLayout(this);
        posLayout.setOrientation(LinearLayout.HORIZONTAL);
        posLayout.setPadding(0, 0, 0, 8);

        TextView posXLabel = new TextView(this);
        posXLabel.setText("X:");
        posXLabel.setTextColor(Color.WHITE);
        posLayout.addView(posXLabel);

        mPosXEdit = new EditText(this);
        mPosXEdit.setText("50");
        mPosXEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mPosXEdit.setBackgroundColor(Color.parseColor("#2A2A4A"));
        mPosXEdit.setTextColor(Color.WHITE);
        mPosXEdit.setPadding(16, 8, 16, 8);
        posLayout.addView(mPosXEdit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView posYLabel = new TextView(this);
        posYLabel.setText(" Y:");
        posYLabel.setTextColor(Color.WHITE);
        posLayout.addView(posYLabel);

        mPosYEdit = new EditText(this);
        mPosYEdit.setText("50");
        mPosYEdit.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        mPosYEdit.setBackgroundColor(Color.parseColor("#2A2A4A"));
        mPosYEdit.setTextColor(Color.WHITE);
        mPosYEdit.setPadding(16, 8, 16, 8);
        posLayout.addView(mPosYEdit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(posLayout);

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

        Button showButton = new Button(this);
        showButton.setText("发送SurfacePackage到服务端");
        showButton.setBackgroundColor(Color.parseColor("#4ECDC4"));
        showButton.setTextColor(Color.WHITE);
        showButton.setOnClickListener(v -> showWindow());
        rootLayout.addView(showButton);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 8, 0, 0);

        Button hideLastButton = new Button(this);
        hideLastButton.setText("隐藏最后窗口");
        hideLastButton.setBackgroundColor(Color.parseColor("#666666"));
        hideLastButton.setTextColor(Color.WHITE);
        hideLastButton.setOnClickListener(v -> hideLastWindow());
        buttonLayout.addView(hideLastButton, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Button hideAllButton = new Button(this);
        hideAllButton.setText("隐藏所有窗口");
        hideAllButton.setBackgroundColor(Color.parseColor("#FF6B6B"));
        hideAllButton.setTextColor(Color.WHITE);
        hideAllButton.setOnClickListener(v -> hideAllWindows());
        buttonLayout.addView(hideAllButton, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        rootLayout.addView(buttonLayout);

        TextView infoText = new TextView(this);
        infoText.setText("\n说明:\n" +
                "• 点击按钮将SurfacePackage发送到服务端\n" +
                "• 服务端会在指定位置显示渲染内容\n" +
                "• 可自定义X/Y坐标和宽高");
        infoText.setTextColor(Color.parseColor("#666666"));
        infoText.setTextSize(11);
        rootLayout.addView(infoText);

        setContentView(rootLayout);
    }

    private void setupClientManager() {
        mClientManager = ClientManager.getInstance(this);
        mClientManager.setWindowOffset(5);
        mClientManager.setCallback(new ClientManager.ClientCallback() {
            @Override
            public void onServiceConnected() {
                updateStatus("已连接服务端，可以渲染");
            }

            @Override
            public void onServiceDisconnected() {
                updateStatus("服务端已断开");
            }

            @Override
            public void onWindowCreated(String windowId, int windowCount) {
                updateStatus("已发送窗口 #" + windowCount);
                Toast.makeText(ClientActivity.this, "SurfacePackage已发送", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWindowRemoved(String windowId, int windowCount) {
                updateStatus("窗口数: " + windowCount);
            }

            @Override
            public void onError(String error) {
                updateStatus("错误: " + error);
            }
        });
        mClientManager.connectService();
    }

    private void showWindow() {
        if (!mClientManager.isServiceConnected()) {
            updateStatus("服务未连接");
            return;
        }

        try {
            int x = Integer.parseInt(mPosXEdit.getText().toString());
            int y = Integer.parseInt(mPosYEdit.getText().toString());
            int width = Integer.parseInt(mWidthEdit.getText().toString());
            int height = Integer.parseInt(mHeightEdit.getText().toString());

            DemoRenderView demoView = new DemoRenderView(this);
            demoView.setLabel("窗口 #" + (mClientManager.getWindowCount() + 1));

            mClientManager.showWindow(demoView, width, height, x, y);

        } catch (NumberFormatException e) {
            updateStatus("错误: 请输入有效数字");
        }
    }

    private void hideLastWindow() {
        if (!mClientManager.hideLastWindow()) {
            updateStatus("没有窗口可隐藏");
        }
    }

    private void hideAllWindows() {
        mClientManager.hideAllWindows();
        updateStatus("已隐藏所有窗口");
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> mStatusText.setText("状态: " + status));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mClientManager.disconnectService();
    }
}

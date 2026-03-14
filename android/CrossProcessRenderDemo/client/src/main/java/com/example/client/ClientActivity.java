package com.example.client;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 客户端Activity（新架构）
 * 
 * 新架构说明：
 * - Client 提供 ClientRenderService
 * - Server 绑定此 Service
 * - Server 传递 HostToken
 * - Client 创建 SurfacePackage 并通过回调返回
 * 
 * 使用方式：
 * 1. 启动 Client 应用（此 Activity）
 * 2. 启动 Server 应用
 * 3. Server 会自动绑定 Client 的 Service
 * 4. Server 点击按钮请求渲染
 */
public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
    }

    private void setupUI() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.parseColor("#0F0F23"));
        rootLayout.setPadding(32, 32, 32, 32);

        TextView titleText = new TextView(this);
        titleText.setText("跨进程渲染 - 客户端（新架构）");
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

        TextView statusText = new TextView(this);
        statusText.setText("状态: 已就绪，等待服务端连接...");
        statusText.setTextColor(Color.parseColor("#4ECDC4"));
        statusText.setTextSize(14);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 0, 0, 16);
        rootLayout.addView(statusText);

        TextView infoTitle = new TextView(this);
        infoTitle.setText("新架构说明:");
        infoTitle.setTextColor(Color.WHITE);
        infoTitle.setTextSize(16);
        infoTitle.setPadding(0, 16, 0, 8);
        rootLayout.addView(infoTitle);

        TextView infoText = new TextView(this);
        infoText.setText("• Client 定义 ClientRenderService\n" +
                "• Server 绑定 Client 的 Service\n" +
                "• Server 传递 HostToken 给 Client\n" +
                "• Client 创建 SurfacePackage\n" +
                "• Client 通过回调返回给 Server\n" +
                "• Server 渲染 Client 的内容");
        infoText.setTextColor(Color.parseColor("#AAAAAA"));
        infoText.setTextSize(13);
        infoText.setPadding(16, 0, 0, 16);
        rootLayout.addView(infoText);

        TextView usageTitle = new TextView(this);
        usageTitle.setText("使用步骤:");
        usageTitle.setTextColor(Color.WHITE);
        usageTitle.setTextSize(16);
        usageTitle.setPadding(0, 16, 0, 8);
        rootLayout.addView(usageTitle);

        TextView usageText = new TextView(this);
        usageText.setText("1. 保持此页面打开\n" +
                "2. 打开服务端应用\n" +
                "3. 服务端会自动连接此客户端\n" +
                "4. 在服务端点击按钮请求渲染\n" +
                "5. 查看服务端容器中的渲染内容");
        usageText.setTextColor(Color.parseColor("#AAAAAA"));
        usageText.setTextSize(13);
        usageText.setPadding(16, 0, 0, 16);
        rootLayout.addView(usageText);

        Button hintButton = new Button(this);
        hintButton.setText("提示：此应用提供渲染服务，无需操作");
        hintButton.setBackgroundColor(Color.parseColor("#666666"));
        hintButton.setTextColor(Color.WHITE);
        hintButton.setClickable(false);
        rootLayout.addView(hintButton);

        setContentView(rootLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}


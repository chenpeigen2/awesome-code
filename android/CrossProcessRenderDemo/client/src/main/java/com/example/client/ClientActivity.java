package com.example.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceControlViewHost;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.common.IRenderService;
import com.example.common.SurfacePackageWrapper;
import com.example.common.WindowConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端Activity
 */
public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";

    private IRenderService mRenderService;
    private TextView mStatusText;
    private EditText mPosXEdit;
    private EditText mPosYEdit;
    private EditText mWidthEdit;
    private EditText mHeightEdit;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mServiceConnected = false;
    
    private final List<String> mWindowIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
        bindRenderService();
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

    private void bindRenderService() {
        Intent intent = new Intent("com.example.server.RENDER_SERVICE");
        intent.setPackage("com.example.server");
        
        Intent explicitIntent = createExplicitIntent(intent);
        if (explicitIntent == null) {
            updateStatus("错误: 找不到服务端应用");
            return;
        }

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: " + name);
                mRenderService = IRenderService.Stub.asInterface(service);
                mServiceConnected = true;
                updateStatus("已连接服务端，可以渲染");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
                mRenderService = null;
                mServiceConnected = false;
                updateStatus("服务端已断开");
            }
        };

        boolean result = bindService(explicitIntent, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bindService result: " + result);
    }

    private Intent createExplicitIntent(Intent implicitIntent) {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        
        if (resolveInfo == null || resolveInfo.isEmpty()) {
            Log.w(TAG, "No services found");
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

    private void showWindow() {
        if (!mServiceConnected || mRenderService == null) {
            updateStatus("服务未连接");
            return;
        }

        try {
            IBinder hostToken = mRenderService.getHostToken();
            if (hostToken == null) {
                updateStatus("错误: HostToken为空");
                return;
            }

            int x = Integer.parseInt(mPosXEdit.getText().toString());
            int y = Integer.parseInt(mPosYEdit.getText().toString());
            int width = Integer.parseInt(mWidthEdit.getText().toString());
            int height = Integer.parseInt(mHeightEdit.getText().toString());

            SurfaceControlViewHost scvh = new SurfaceControlViewHost(
                    this, getWindowManager().getDefaultDisplay(), hostToken);

            DemoRenderView demoView = new DemoRenderView(this);
            demoView.setLabel("窗口 #" + (mWindowIds.size() + 1));
            scvh.setView(demoView, width, height);

            SurfaceControlViewHost.SurfacePackage surfacePackage = scvh.getSurfacePackage();

            String windowId = "window_" + System.currentTimeMillis();
            WindowConfig config = new WindowConfig(width, height, x, y, Gravity.TOP | Gravity.LEFT);

            mRenderService.showWindow(windowId, config, new SurfacePackageWrapper(surfacePackage));
            mWindowIds.add(windowId);

            updateStatus("已发送窗口 #" + mWindowIds.size());
            Toast.makeText(this, "SurfacePackage已发送", Toast.LENGTH_SHORT).show();

        } catch (RemoteException e) {
            Log.e(TAG, "showWindow: RemoteException", e);
            updateStatus("错误: " + e.getMessage());
        } catch (NumberFormatException e) {
            updateStatus("错误: 请输入有效数字");
        }
    }

    private void hideLastWindow() {
        if (!mServiceConnected || mRenderService == null) {
            return;
        }
        if (mWindowIds.isEmpty()) {
            updateStatus("没有窗口可隐藏");
            return;
        }
        try {
            String lastId = mWindowIds.remove(mWindowIds.size() - 1);
            mRenderService.hideWindow(lastId);
            updateStatus("已隐藏窗口，剩余: " + mWindowIds.size());
        } catch (RemoteException e) {
            Log.e(TAG, "hideLastWindow: RemoteException", e);
        }
    }

    private void hideAllWindows() {
        if (!mServiceConnected || mRenderService == null) {
            return;
        }
        try {
            for (String windowId : mWindowIds) {
                mRenderService.hideWindow(windowId);
            }
            mWindowIds.clear();
            updateStatus("已隐藏所有窗口");
        } catch (RemoteException e) {
            Log.e(TAG, "hideAllWindows: RemoteException", e);
        }
    }

    private void updateStatus(String status) {
        mHandler.post(() -> mStatusText.setText("状态: " + status));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}

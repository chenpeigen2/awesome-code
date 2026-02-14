package com.example.server;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.common.WindowConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端宿主Activity
 * 
 * 职责：
 * 1. 提供宿主容器 FrameLayout
 * 2. 管理SurfacePackage的显示
 * 3. 支持自定义渲染位置
 */
public class HostActivity extends AppCompatActivity {
    private static final String TAG = "HostActivity";

    private FrameLayout mHostContainer;
    private TextView mStatusText;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    
    private final Map<String, SurfaceView> mWindows = new HashMap<>();
    private IBinder mHostToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: PID=" + android.os.Process.myPid());

        setupUI();
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
        mStatusText.setText("状态: 等待客户端连接...");
        mStatusText.setTextColor(Color.parseColor("#AAAAAA"));
        mStatusText.setTextSize(14);
        mStatusText.setPadding(0, 0, 0, 16);
        rootLayout.addView(mStatusText);

        Button clearButton = new Button(this);
        clearButton.setText("清除所有窗口");
        clearButton.setBackgroundColor(Color.parseColor("#666666"));
        clearButton.setTextColor(Color.WHITE);
        clearButton.setOnClickListener(v -> clearAllWindows());
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

        mHostContainer.post(() -> {
            mHostToken = getHostTokenFromView(mHostContainer);
            Log.d(TAG, "HostToken: " + mHostToken);
            RenderService.setHostActivity(HostActivity.this);
            updateStatus("就绪 - Token已获取");
        });
    }

    /**
     * 获取HostToken
     */
    public IBinder getHostToken() {
        return mHostToken;
    }

    /**
     * 通过反射获取View的HostToken
     */
    private IBinder getHostTokenFromView(android.view.View view) {
        try {
            Method getViewRootImpl = android.view.View.class.getDeclaredMethod("getViewRootImpl");
            getViewRootImpl.setAccessible(true);
            Object viewRootImpl = getViewRootImpl.invoke(view);
            if (viewRootImpl != null) {
                Method getSurfaceControl = viewRootImpl.getClass().getDeclaredMethod("getSurfaceControl");
                getSurfaceControl.setAccessible(true);
                Object surfaceControl = getSurfaceControl.invoke(viewRootImpl);
                if (surfaceControl != null) {
                    Method getHandle = surfaceControl.getClass().getDeclaredMethod("getHandle");
                    getHandle.setAccessible(true);
                    return (IBinder) getHandle.invoke(surfaceControl);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getHostTokenFromView failed", e);
        }
        return null;
    }

    /**
     * 获取DisplayId
     */
    public int getDisplayId() {
        return mHostContainer.getDisplay() != null ? 
               mHostContainer.getDisplay().getDisplayId() : 0;
    }

    /**
     * 显示窗口
     */
    public void showWindow(String windowId, WindowConfig config, 
                          SurfaceControlViewHost.SurfacePackage surfacePackage) {
        mHandler.post(() -> {
            if (mWindows.containsKey(windowId)) {
                hideWindow(windowId);
            }

            SurfaceView surfaceView = new SurfaceView(this);
            surfaceView.setZOrderOnTop(true);
            surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (surfacePackage != null) {
                        surfaceView.setChildSurfacePackage(surfacePackage);
                        Log.d(TAG, "surfaceCreated: SurfacePackage attached");
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.d(TAG, "surfaceDestroyed");
                }
            });

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    config.width, config.height, config.gravity);
            lp.leftMargin = config.x;
            lp.topMargin = config.y;

            mHostContainer.addView(surfaceView, lp);
            mWindows.put(windowId, surfaceView);

            Log.d(TAG, "showWindow: " + windowId + ", total=" + mWindows.size());
            updateStatus("窗口数: " + mWindows.size());
        });
    }

    /**
     * 隐藏窗口
     */
    public void hideWindow(String windowId) {
        mHandler.post(() -> {
            SurfaceView surfaceView = mWindows.remove(windowId);
            if (surfaceView != null && surfaceView.getParent() == mHostContainer) {
                mHostContainer.removeView(surfaceView);
            }
            updateStatus("窗口数: " + mWindows.size());
        });
    }

    /**
     * 更新窗口位置
     */
    public void updateWindowPosition(String windowId, int x, int y) {
        mHandler.post(() -> {
            SurfaceView surfaceView = mWindows.get(windowId);
            if (surfaceView != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                lp.leftMargin = x;
                lp.topMargin = y;
                surfaceView.setLayoutParams(lp);
            }
        });
    }

    /**
     * 更新窗口尺寸
     */
    public void updateWindowSize(String windowId, int width, int height) {
        mHandler.post(() -> {
            SurfaceView surfaceView = mWindows.get(windowId);
            if (surfaceView != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                lp.width = width;
                lp.height = height;
                surfaceView.setLayoutParams(lp);
            }
        });
    }

    private void clearAllWindows() {
        mHandler.post(() -> {
            for (SurfaceView surfaceView : mWindows.values()) {
                if (surfaceView.getParent() == mHostContainer) {
                    mHostContainer.removeView(surfaceView);
                }
            }
            mWindows.clear();
            updateStatus("已清除所有窗口");
        });
    }

    private void updateStatus(String status) {
        mHandler.post(() -> mStatusText.setText("状态: " + status));
    }
}

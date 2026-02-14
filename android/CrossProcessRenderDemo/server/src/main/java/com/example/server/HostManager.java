package com.example.server;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.example.common.WindowConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 宿主管理器
 * 
 * 独立于Activity，管理HostToken和窗口显示
 * 可以在任何容器中使用（Activity、悬浮窗等）
 */
public class HostManager {
    private static final String TAG = "HostManager";

    private static HostManager sInstance;

    private final Context mContext;
    private final Handler mHandler;
    private FrameLayout mContainer;
    private SurfaceView mTokenSurfaceView;
    private IBinder mHostToken;
    private final Map<String, SurfaceView> mWindows = new HashMap<>();
    private HostCallback mCallback;

    public interface HostCallback {
        void onTokenReady(IBinder token);
        void onWindowCountChanged(int count);
    }

    private HostManager(Context context) {
        mContext = context.getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static synchronized HostManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HostManager(context);
        }
        return sInstance;
    }

    /**
     * 设置容器
     * 必须在UI线程调用
     */
    public void setContainer(FrameLayout container) {
        mContainer = container;
        setupTokenSurfaceView();
    }

    /**
     * 设置回调
     */
    public void setCallback(HostCallback callback) {
        mCallback = callback;
    }

    /**
     * 获取HostToken
     */
    public IBinder getHostToken() {
        return mHostToken;
    }

    /**
     * 获取DisplayId
     */
    public int getDisplayId() {
        if (mContainer == null) {
            return 0;
        }
        return mContainer.getDisplay() != null ? 
               mContainer.getDisplay().getDisplayId() : 0;
    }

    /**
     * 显示窗口
     */
    public void showWindow(String windowId, WindowConfig config, 
                          SurfaceControlViewHost.SurfacePackage surfacePackage) {
        mHandler.post(() -> {
            if (mContainer == null) {
                Log.w(TAG, "Container is null");
                return;
            }

            if (mWindows.containsKey(windowId)) {
                hideWindow(windowId);
            }

            SurfaceView surfaceView = new SurfaceView(mContext);
            surfaceView.setZOrderOnTop(true);
            surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (surfacePackage != null) {
                        surfaceView.setChildSurfacePackage(surfacePackage);
                        Log.d(TAG, "surfaceCreated: SurfacePackage attached for " + windowId);
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.d(TAG, "surfaceDestroyed: " + windowId);
                }
            });

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    config.width, config.height, config.gravity);
            lp.leftMargin = config.x;
            lp.topMargin = config.y;

            mContainer.addView(surfaceView, lp);
            mWindows.put(windowId, surfaceView);

            Log.d(TAG, "showWindow: " + windowId + ", total=" + mWindows.size());
            notifyWindowCountChanged();
        });
    }

    /**
     * 隐藏窗口
     */
    public void hideWindow(String windowId) {
        mHandler.post(() -> {
            if (mContainer == null) return;

            SurfaceView surfaceView = mWindows.remove(windowId);
            if (surfaceView != null && surfaceView.getParent() == mContainer) {
                mContainer.removeView(surfaceView);
            }
            Log.d(TAG, "hideWindow: " + windowId + ", total=" + mWindows.size());
            notifyWindowCountChanged();
        });
    }

    /**
     * 更新窗口位置
     */
    public void updateWindowPosition(String windowId, int x, int y) {
        mHandler.post(() -> {
            SurfaceView surfaceView = mWindows.get(windowId);
            if (surfaceView != null && surfaceView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
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
            if (surfaceView != null && surfaceView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                lp.width = width;
                lp.height = height;
                surfaceView.setLayoutParams(lp);
            }
        });
    }

    /**
     * 清除所有窗口
     */
    public void clearAllWindows() {
        mHandler.post(() -> {
            if (mContainer == null) return;

            for (SurfaceView surfaceView : mWindows.values()) {
                if (surfaceView.getParent() == mContainer) {
                    mContainer.removeView(surfaceView);
                }
            }
            mWindows.clear();
            notifyWindowCountChanged();
        });
    }

    /**
     * 释放资源
     */
    public void release() {
        clearAllWindows();
        if (mContainer != null && mTokenSurfaceView != null && mTokenSurfaceView.getParent() == mContainer) {
            mContainer.removeView(mTokenSurfaceView);
        }
        mTokenSurfaceView = null;
        mHostToken = null;
        mContainer = null;
        mCallback = null;
    }

    private void setupTokenSurfaceView() {
        if (mContainer == null) return;

        mTokenSurfaceView = new SurfaceView(mContext);
        mTokenSurfaceView.setZOrderOnTop(true);
        mTokenSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mTokenSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mHostToken = mTokenSurfaceView.getHostToken();
                Log.d(TAG, "HostToken: " + mHostToken);
                if (mCallback != null) {
                    mCallback.onTokenReady(mHostToken);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "Token SurfaceView destroyed");
                mHostToken = null;
            }
        });

        FrameLayout.LayoutParams tokenParams = new FrameLayout.LayoutParams(1, 1);
        mContainer.addView(mTokenSurfaceView, tokenParams);
    }

    private void notifyWindowCountChanged() {
        if (mCallback != null) {
            mCallback.onWindowCountChanged(mWindows.size());
        }
    }
}

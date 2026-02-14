package com.example.common;

import android.view.SurfaceControlViewHost;
import com.example.common.WindowConfig;

/**
 * 跨进程渲染服务接口
 */
interface IRenderService {
    /**
     * 获取宿主窗口Token
     */
    IBinder getHostToken();
    
    /**
     * 获取Display ID
     */
    int getDisplayId();
    
    /**
     * 显示窗口
     */
    void showWindow(String windowId, in WindowConfig config, in SurfaceControlViewHost.SurfacePackage surfacePackage);
    
    /**
     * 隐藏窗口
     */
    void hideWindow(String windowId);
    
    /**
     * 更新窗口位置
     */
    void updateWindowPosition(String windowId, int x, int y);
    
    /**
     * 更新窗口尺寸
     */
    void updateWindowSize(String windowId, int width, int height);
}

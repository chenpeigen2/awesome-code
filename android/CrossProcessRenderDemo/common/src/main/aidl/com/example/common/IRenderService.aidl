package com.example.common;

import android.view.SurfaceControlViewHost.SurfacePackage;
import com.example.common.WindowConfig;

/**
 * 旧版接口（已废弃，保留兼容性）
 * 
 * 新架构使用 IClientService + IClientCallback
 * Server 绑定 Client 的 Service，通过回调传递 HostToken 和接收 SurfacePackage
 */
interface IRenderService {
    IBinder getHostToken();
    
    int getDisplayId();
    
    void showWindow(String windowId, in WindowConfig config, in SurfacePackage surfacePackage);
    
    void hideWindow(String windowId);
    
    void updateWindowPosition(String windowId, int x, int y);
    
    void updateWindowSize(String windowId, int width, int height);
}

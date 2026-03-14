package com.example.common;

import android.view.SurfaceControlViewHost.SurfacePackage;

/**
 * Server 端实现的回调接口
 * 
 * Server 实现此接口，用于接收 Client 返回的 SurfacePackage
 */
interface IClientCallback {
    /**
     * Client 创建 SurfacePackage 后调用此方法返回给 Server
     */
    void onSurfacePackageReady(String windowId, in SurfacePackage surfacePackage);
    
    /**
     * Client 销毁 SurfacePackage 后通知 Server
     */
    void onSurfacePackageDestroyed(String windowId);
    
    /**
     * 错误回调
     */
    void onError(String windowId, String error);
}

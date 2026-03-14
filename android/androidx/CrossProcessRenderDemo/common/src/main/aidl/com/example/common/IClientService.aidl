package com.example.common;

import com.example.common.IClientCallback;

/**
 * Client 端定义的 Service 接口
 * 
 * Server 绑定此 Service 后：
 * 1. registerHost 注册回调
 * 2. setHostToken 传递 HostToken
 * 3. createSurfacePackage 请求创建 SurfacePackage
 * 4. Client 通过 IClientCallback 返回 SurfacePackage
 */
interface IClientService {
    /**
     * Server 注册回调，用于接收 SurfacePackage
     */
    void registerHost(IClientCallback callback);
    
    /**
     * Server 取消注册
     */
    void unregisterHost(IClientCallback callback);
    
    /**
     * Server 传递 HostToken 给 Client
     * Client 保存后可用于创建 SurfacePackage
     */
    void setHostToken(IBinder hostToken, int displayId);
    
    /**
     * Server 请求 Client 创建 SurfacePackage
     * Client 创建后通过 IClientCallback.onSurfacePackageReady 返回
     */
    void createSurfacePackage(String windowId, int width, int height);
    
    /**
     * Server 请求销毁窗口
     */
    void destroySurfacePackage(String windowId);
}

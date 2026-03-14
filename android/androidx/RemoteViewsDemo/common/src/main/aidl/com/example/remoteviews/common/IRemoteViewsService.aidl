package com.example.remoteviews.common;

import com.example.remoteviews.common.RemoteViewInfo;
import com.example.remoteviews.common.IRemoteViewsCallback;

interface IRemoteViewsService {
    RemoteViewInfo getRemoteViewInfo(String viewId);
    
    void updateText(String viewId, String text);
    
    void updateImage(String viewId, in byte[] imageData);
    
    void setViewVisibility(String viewId, int visibility);
    
    void registerCallback(IRemoteViewsCallback callback);
    
    void unregisterCallback(IRemoteViewsCallback callback);
}

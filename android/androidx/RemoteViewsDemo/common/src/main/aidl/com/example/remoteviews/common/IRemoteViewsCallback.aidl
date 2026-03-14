package com.example.remoteviews.common;

import com.example.remoteviews.common.RemoteViewInfo;

interface IRemoteViewsCallback {
    void onRemoteViewsUpdated(in RemoteViewInfo info);
    
    void onViewClicked(String viewId, String action);
    
    void onServiceReady();
}

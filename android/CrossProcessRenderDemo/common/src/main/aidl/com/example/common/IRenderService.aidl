package com.example.common;

import com.example.common.WindowConfig;
import com.example.common.SurfacePackageWrapper;

interface IRenderService {
    IBinder getHostToken();
    
    int getDisplayId();
    
    void showWindow(String windowId, in WindowConfig config, in SurfacePackageWrapper surfacePackageWrapper);
    
    void hideWindow(String windowId);
    
    void updateWindowPosition(String windowId, int x, int y);
    
    void updateWindowSize(String windowId, int width, int height);
}

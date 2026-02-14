package com.example.common;

import android.view.SurfaceControlViewHost.SurfacePackage;
import com.example.common.WindowConfig;

interface IRenderService {
    IBinder getHostToken();
    
    int getDisplayId();
    
    void showWindow(String windowId, in WindowConfig config, in SurfacePackage surfacePackage);
    
    void hideWindow(String windowId);
    
    void updateWindowPosition(String windowId, int x, int y);
    
    void updateWindowSize(String windowId, int width, int height);
}

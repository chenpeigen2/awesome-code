package com.example.ipc;

import android.view.SurfaceControlViewHost.SurfacePackage;

interface ISurfacePackageReceiver {
    void onSurfacePackageReady(in SurfacePackage surfacePackage);
}
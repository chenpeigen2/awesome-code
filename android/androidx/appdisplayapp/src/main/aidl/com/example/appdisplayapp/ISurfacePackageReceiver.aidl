package com.example.appdisplayapp;

import android.view.SurfaceControlViewHost.SurfacePackage;

interface ISurfacePackageReceiver {
    void onSurfacePackageReady(in SurfacePackage surfacePackage);
}
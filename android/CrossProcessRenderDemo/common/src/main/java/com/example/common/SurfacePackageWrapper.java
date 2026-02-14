package com.example.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.SurfaceControlViewHost;

public class SurfacePackageWrapper implements Parcelable {
    private SurfaceControlViewHost.SurfacePackage mSurfacePackage;

    public SurfacePackageWrapper(SurfaceControlViewHost.SurfacePackage surfacePackage) {
        mSurfacePackage = surfacePackage;
    }

    protected SurfacePackageWrapper(Parcel in) {
        mSurfacePackage = in.readParcelable(SurfaceControlViewHost.SurfacePackage.class.getClassLoader());
    }

    public SurfaceControlViewHost.SurfacePackage getSurfacePackage() {
        return mSurfacePackage;
    }

    public static final Creator<SurfacePackageWrapper> CREATOR = new Creator<SurfacePackageWrapper>() {
        @Override
        public SurfacePackageWrapper createFromParcel(Parcel in) {
            return new SurfacePackageWrapper(in);
        }

        @Override
        public SurfacePackageWrapper[] newArray(int size) {
            return new SurfacePackageWrapper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSurfacePackage, flags);
    }
}

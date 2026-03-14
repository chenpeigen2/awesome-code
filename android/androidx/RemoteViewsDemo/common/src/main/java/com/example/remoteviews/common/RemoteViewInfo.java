package com.example.remoteviews.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.RemoteViews;

public class RemoteViewInfo implements Parcelable {
    private String mViewId;
    private RemoteViews mRemoteViews;
    private String mDescription;
    private long mTimestamp;

    public RemoteViewInfo() {
        mTimestamp = System.currentTimeMillis();
    }

    public RemoteViewInfo(String viewId, RemoteViews remoteViews, String description) {
        this.mViewId = viewId;
        this.mRemoteViews = remoteViews;
        this.mDescription = description;
        this.mTimestamp = System.currentTimeMillis();
    }

    protected RemoteViewInfo(Parcel in) {
        mViewId = in.readString();
        mRemoteViews = in.readParcelable(RemoteViews.class.getClassLoader());
        mDescription = in.readString();
        mTimestamp = in.readLong();
    }

    public static final Creator<RemoteViewInfo> CREATOR = new Creator<RemoteViewInfo>() {
        @Override
        public RemoteViewInfo createFromParcel(Parcel in) {
            return new RemoteViewInfo(in);
        }

        @Override
        public RemoteViewInfo[] newArray(int size) {
            return new RemoteViewInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mViewId);
        dest.writeParcelable(mRemoteViews, flags);
        dest.writeString(mDescription);
        dest.writeLong(mTimestamp);
    }

    public String getViewId() {
        return mViewId;
    }

    public void setViewId(String viewId) {
        this.mViewId = viewId;
    }

    public RemoteViews getRemoteViews() {
        return mRemoteViews;
    }

    public void setRemoteViews(RemoteViews remoteViews) {
        this.mRemoteViews = remoteViews;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RemoteViewInfo{" +
                "mViewId='" + mViewId + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mTimestamp=" + mTimestamp +
                '}';
    }
}

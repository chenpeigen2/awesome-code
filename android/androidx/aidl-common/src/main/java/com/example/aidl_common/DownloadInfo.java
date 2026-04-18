package com.example.aidl_common;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadInfo implements Parcelable {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_PAUSED = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_FAILED = 4;
    public static final int STATUS_CANCELLED = 5;

    public int id;
    public String url;
    public String fileName;
    public long totalSize;
    public long downloadedSize;
    public int status;

    public DownloadInfo() {}

    public DownloadInfo(int id, String url, String fileName, long totalSize, long downloadedSize, int status) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.totalSize = totalSize;
        this.downloadedSize = downloadedSize;
        this.status = status;
    }

    protected DownloadInfo(Parcel in) {
        id = in.readInt();
        url = in.readString();
        fileName = in.readString();
        totalSize = in.readLong();
        downloadedSize = in.readLong();
        status = in.readInt();
    }

    public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel in) {
            return new DownloadInfo(in);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(fileName);
        dest.writeLong(totalSize);
        dest.writeLong(downloadedSize);
        dest.writeInt(status);
    }
}

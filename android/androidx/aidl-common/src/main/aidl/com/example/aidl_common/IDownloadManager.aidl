package com.example.aidl_common;

import com.example.aidl_common.IDownloadCallback;
import com.example.aidl_common.DownloadInfo;

interface IDownloadManager {
    int add(int a, int b);

    oneway void startDownload(String url, String fileName);

    void downloadWithCallback(String url, String fileName, IDownloadCallback callback);

    void registerCallback(IDownloadCallback callback);
    void unregisterCallback(IDownloadCallback callback);

    boolean cancelDownload(int downloadId);

    List<DownloadInfo> getActiveDownloads();
}

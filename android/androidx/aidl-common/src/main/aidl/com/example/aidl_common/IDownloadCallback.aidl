package com.example.aidl_common;

interface IDownloadCallback {
    void onProgress(int downloadId, int progress);
    void onComplete(int downloadId, String filePath, long fileSize);
    void onFailure(int downloadId, int errorCode, String message);
}

package com.example.android.mypreloaddata.services;

public interface LoadDataCallback {
    void onPreLoad();
    void onProgressUpdate(long progress);
    void onLoadSuccess();
    void onLoadFailed();
    void onLoadCancel();
}

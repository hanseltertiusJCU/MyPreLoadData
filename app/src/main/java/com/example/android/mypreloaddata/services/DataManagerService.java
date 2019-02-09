package com.example.android.mypreloaddata.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.mypreloaddata.database.MahasiswaHelper;
import com.example.android.mypreloaddata.pref.AppPreference;

public class DataManagerService extends Service {
    // Initialize variable yg akan diperlukan
    public static final int PREPARATION_MESSAGE = 0;
    public static final int UPDATE_MESSAGE = 1;
    public static final int SUCCESS_MESSAGE = 2;
    public static final int FAILED_MESSAGE = 3;
    public static final int CANCEL_MESSAGE = 4;
    public static final String ACTIVITY_HANDLER = "activity_handler";

    private String TAG = DataManagerService.class.getSimpleName();
    private LoadDataAsync loadData;
    private Messenger mActivityMessenger; // Sebagai jembatan untuk menghubungkan service dengan activity


    public DataManagerService() {
    }

    // Kode brikut brguna untuk melakukan pemanggilan LoadDataAsync di DataManagerService,
    // sehingga kita menerapkan service pada applikasi
    @Override
    public void onCreate() {
        super.onCreate();

        // Memperbaharui mahasiswaHelper dan appPreference lalu memperbaharui LoadDataAsync
        MahasiswaHelper mahasiswaHelper = MahasiswaHelper.getInstance(getApplicationContext());
        AppPreference appPreference = new AppPreference(getApplicationContext());

        loadData = new LoadDataAsync(mahasiswaHelper, appPreference, myCallback, getResources());

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadData.cancel(true);
        Log.d(TAG, "onDestroy: ");
    }

    // Method tsb dijalankan ketika service terikat dengan Activity pemanggil
    @Override
    public IBinder onBind(Intent intent) {
        mActivityMessenger = intent.getParcelableExtra(ACTIVITY_HANDLER);

        loadData.execute(); // Menjalankan loadData
        return mActivityMessenger.getBinder(); // service mengirim pesan ke Activity scr tidak langsung dengan method tsb
    }

    // Method tsb dijalankan ketika Activity pemanggil melepaskan ikatan kelas DataManagerService
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        loadData.cancel(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    // Memanggil LoadDataCallback untuk mengirim pesan ke Activity terkait (activity pemanggil)
    LoadDataCallback myCallback = new LoadDataCallback() {
        @Override
        public void onPreLoad() {
            // Ketika onPreExecute() berjalan atau persiapan sebelum proses terjadi
            Message message = Message.obtain(null, PREPARATION_MESSAGE); // Mengirim pesan ke Messenger
            try{
                mActivityMessenger.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onProgressUpdate(long progress) {
            // Ketika onProgressUpdate() berjalan akan mengirim progress dari proses tersebut
            try{
                Message message = Message.obtain(null, UPDATE_MESSAGE); // Mengirim pesan ke Messenger dengan bantuan bundle
                Bundle bundle = new Bundle();
                bundle.putLong("KEY_PROGRESS", progress);
                message.setData(bundle);
                mActivityMessenger.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadSuccess() {
            // Ketika onPostExecute() mendapat hasil true atau sukses
            Message message = Message.obtain(null, SUCCESS_MESSAGE);
            try{
                mActivityMessenger.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadFailed() {
            // Ketika onPostExecute() mendapat hasil false atau gagal
            Message message = Message.obtain(null, FAILED_MESSAGE);
            try{
                mActivityMessenger.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadCancel(){
            // Ketika onLoadCancel() user melakukan pembatalan pada proses load data
            Message message = Message.obtain(null, CANCEL_MESSAGE);
            try{
                mActivityMessenger.send(message);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }
    };
}

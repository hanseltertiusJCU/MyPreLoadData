package com.example.android.mypreloaddata;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.mypreloaddata.services.DataManagerService;

import java.lang.ref.WeakReference;

import static com.example.android.mypreloaddata.services.DataManagerService.CANCEL_MESSAGE;
import static com.example.android.mypreloaddata.services.DataManagerService.FAILED_MESSAGE;
import static com.example.android.mypreloaddata.services.DataManagerService.PREPARATION_MESSAGE;
import static com.example.android.mypreloaddata.services.DataManagerService.SUCCESS_MESSAGE;
import static com.example.android.mypreloaddata.services.DataManagerService.UPDATE_MESSAGE;

public class MainActivity extends AppCompatActivity implements HandlerCallback {

    ProgressBar progressBar;
    Messenger mActivityMessenger;

    Messenger mBoundService;
    boolean mServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);

        Intent mBoundServiceIntent = new Intent(MainActivity.this, DataManagerService.class);
        mActivityMessenger = new Messenger(new IncomingHandler(this));
        mBoundServiceIntent.putExtra(DataManagerService.ACTIVITY_HANDLER, mActivityMessenger);

        // Ikat DataManagerService ke Activity
        bindService(mBoundServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    // Untuk menjalankan koneksi antara Service dengan Activity
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = new Messenger(service);
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    // Callback method dari interface HandlerCallback
    @Override
    public void preparation() {
        Toast.makeText(this, "MEMULAI MEMUAT DATA", Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateProgress(long progress) {
        Log.e("PROGRESS", "updateProgress: " + progress);
        progressBar.setProgress((int) progress);
    }

    @Override
    public void loadSuccess() {
        Toast.makeText(this, "BERHASIL", Toast.LENGTH_LONG).show();
        startActivity(new Intent(MainActivity.this, MahasiswaActivity.class)); // Mendapat pesan yg menandakan bahwa data berhasil dimuat lalu dikirim ke MahasiswaActivity
        finish();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(this, "GAGAL", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoadCancel() {
       finish();
    }

    // Class tsb berguna untuk menerima data dari service lalu dikirimkan ke HandlerCallback
    public static class IncomingHandler extends Handler{

        WeakReference<HandlerCallback> weakCallback;

        // Handler berguna utk mengolah data bedasarkan message dari {@link DataManagerService}
        IncomingHandler(HandlerCallback callback) {
            weakCallback = new WeakReference<>(callback);
        }

        // Method tsb berguna untuk membuat
        // weak reference yg mempunyai input parameter HandlerCallback mengirim hasil sesuai dengan
        // message yg ada
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PREPARATION_MESSAGE:
                    weakCallback.get().preparation();
                    break;
                case UPDATE_MESSAGE:
                    Bundle bundle = msg.getData();
                    long progress = bundle.getLong("KEY_PROGRESS");
                    weakCallback.get().updateProgress(progress);
                    break;
                case SUCCESS_MESSAGE:
                    weakCallback.get().loadSuccess();
                    break;
                case FAILED_MESSAGE:
                    weakCallback.get().loadFailed();
                    break;
                case CANCEL_MESSAGE:
                    weakCallback.get().onLoadCancel();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Lepas DataManagerService dari Activity
        unbindService(mServiceConnection);
    }
}

interface HandlerCallback{
    void preparation();
    void updateProgress(long progress);
    void loadSuccess();
    void loadFailed();
    void onLoadCancel();
}

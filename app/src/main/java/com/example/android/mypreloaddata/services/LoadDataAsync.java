package com.example.android.mypreloaddata.services;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.mypreloaddata.R;
import com.example.android.mypreloaddata.database.MahasiswaHelper;
import com.example.android.mypreloaddata.model.MahasiswaModel;
import com.example.android.mypreloaddata.pref.AppPreference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Class ini berguna untuk mengolah data mahasiswa ke dalam database
public class LoadDataAsync extends AsyncTask<Void, Integer, Boolean> {

    // Initiate variable yg akan dipakai
    private final String TAG = LoadDataAsync.class.getSimpleName();
    private MahasiswaHelper mahasiswaHelper;
    private AppPreference appPreference;
    private WeakReference<LoadDataCallback> weakCallback; // WeakReference in this case, berguna untuk menghubungkan antara AsyncTask dengan Service
    private WeakReference<Resources> weakResources;
    double progress;
    double maxProgress = 100;

    // Initiate constructor untuk set value
    LoadDataAsync(MahasiswaHelper mahasiswaHelper, AppPreference appPreference, LoadDataCallback callback, Resources resources){
        this.mahasiswaHelper = mahasiswaHelper;
        this.appPreference = appPreference;
        this.weakCallback = new WeakReference<>(callback); // Kedua weak reference tsb akan memperbarui sesuai dengan input dari kelas LoadDataAsync
        this.weakResources = new WeakReference<>(resources);
    }

    // Method tsb berguna untuk mengelola file raw dari data_mahasiswa
    // lalu tiap baris dari data akan di parse
    private ArrayList<MahasiswaModel> preLoadRaw(){
        ArrayList<MahasiswaModel> mahasiswaModels = new ArrayList<>();
        String line;
        BufferedReader reader;
        try {
            Resources res = weakResources.get();
            InputStream raw_dict = res.openRawResource(R.raw.data_mahasiswa);

            reader = new BufferedReader(new InputStreamReader(raw_dict));
            do{
                // Membaca teks dari tiap baris
                line = reader.readLine();
                // Membagi teks bedasarkan "\t" atau tab
                String[] splitstr = line.split("\t");

                MahasiswaModel mahasiswaModel;

                // Nilai nama dan id digunakan untuk membuat object MahasiswaModel
                mahasiswaModel = new MahasiswaModel(splitstr[0], splitstr[1]);
                mahasiswaModels.add(mahasiswaModel);
            } while (line != null); // While condition true ketika linenya masih ada data
        } catch (Exception e){
            e.printStackTrace();
        }

        return mahasiswaModels;
    }

    // Perintah sebelum melakukan proses dari kelas LoadDataAsync
    @Override
    protected void onPreExecute() {
        Log.e(TAG, "onPreExecute");
        weakCallback.get().onPreLoad();
    }

    // Perintah untuk update progress yg berpengaruh terhadap progress bar
    @Override
    protected void onProgressUpdate(Integer... values) {
        weakCallback.get().onProgressUpdate(values[0]);
    }

    // Perintah untuk melakukan proses dari kelas LoadDataAsync
    @Override
    protected Boolean doInBackground(Void... voids) {
        // Dapatin nilai dari appPreference, lalu cek apakah datanya udh d simpen atau belum
        Boolean firstRun = appPreference.getFirstRun();
        if(firstRun){
            // Jika firstRun bernilai true, maka akan melakukan perintah insert ke dalam database
            ArrayList<MahasiswaModel> mahasiswaModels = preLoadRaw();

            mahasiswaHelper.open();

            progress = 30;
            publishProgress((int) progress);
            Double progressMaxInsert = 80.0;
            // Progress diff itu merepresentasikan progress memasukkan data per model,
            // ex: jika data per model ada 5 = (80 - 30) / 5 = 10%
            Double progressDiff = (progressMaxInsert - progress) / mahasiswaModels.size();

            boolean isInsertSuccess;
            // Code ini merepresentasikan process transaction
            try {
                // Proses dimana bulk insert yang melibatkan preload data dimulai
                mahasiswaHelper.beginTransaction(); // Memanggil code beginTransaction ke dalam {@link MahasiswaHelper}
                for(MahasiswaModel model: mahasiswaModels){
                    // Cek ketika datanya cancel untuk di load,
                    // jika iya maka datanya tidak perlu di
                    // insert ke database dari kelas MahasiswaHelper
                    if(isCancelled()){
                        break;
                    } else { // Setiap proses memasukkan data selesai, maka progress bar
                        // diperbaharui dengan memanggil method publishProgress
                        mahasiswaHelper.insertTransaction(model);
                        progress += progressDiff;
                        publishProgress((int) progress);
                    }
                }

                // Cek ketika datanya cancel untuk di load,
                // jika iya maka app menandakan bahwa appnya itu pertama kali dijalankan
                if(isCancelled()){
                    isInsertSuccess = false;
                    appPreference.setFirstRun(true);
                    weakCallback.get().onLoadCancel();
                } else {
                    mahasiswaHelper.setTransactionSuccess();
                    isInsertSuccess = true;
                    // Kode ini dijalankan setelah proses berhasil
                    appPreference.setFirstRun(false);
                }

            } catch (Exception e){
                Log.e(TAG, "doInBackground: Exception");
                isInsertSuccess = false;
            } finally { // Baris ini digunakan ketika prosesnya itu sudah selesai
                mahasiswaHelper.endTransaction();
            }

            mahasiswaHelper.close();

            publishProgress((int) maxProgress);

            return isInsertSuccess;
        } else {
            // Jika firstRun bernilai false, maka hanya menjalankan progress
            try{
                synchronized (this){
                    this.wait(2000);
                    publishProgress(50);

                    this.wait(2000);
                    publishProgress((int) maxProgress);

                    return true;
                }
            } catch (Exception e){
                return false;
            }
        }
    }

    // Perintah setelah melakukan proses dari kelas LoadDataAsync
    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            weakCallback.get().onLoadSuccess();
        } else {
            weakCallback.get().onLoadFailed();
        }
    }
}

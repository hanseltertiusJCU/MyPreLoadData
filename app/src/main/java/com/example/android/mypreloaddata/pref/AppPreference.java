package com.example.android.mypreloaddata.pref;

import android.content.Context;
import android.content.SharedPreferences;

// Class SharedPreferences ini berguna untuk mengetahui bahwa appnya itu
// pertama kali dijalankan atau tidak, agar app tidak load data ke database everytime
public class AppPreference {
    private static final String PREFS_NAME = "MahasiswaPref";
    private static final String APP_FIRST_RUN = "app_first_run";
    private SharedPreferences prefs;

    // Buat object SharedPreferences
    public AppPreference(Context context){
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Simpen data ke SharedPreferences, method ini berguna utk mengganti nilai
    // dengan key app_first_run dan
    // dipanggil stlh proses insert ke database berhasil
    public void setFirstRun(Boolean input) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(APP_FIRST_RUN, input);
        editor.apply();
    }

    // Baca data dari SharedPreferences, method ini berguna untuk mengambil nilai dari
    // key app_first_run
    public boolean getFirstRun(){
        return prefs.getBoolean(APP_FIRST_RUN, true);
    }
}

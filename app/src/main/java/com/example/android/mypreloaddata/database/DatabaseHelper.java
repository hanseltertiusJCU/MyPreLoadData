package com.example.android.mypreloaddata.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.example.android.mypreloaddata.database.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.android.mypreloaddata.database.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.android.mypreloaddata.database.DatabaseContract.TABLE_NAME;

// Mengimplementasikan DDL
public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "dbmahasiswa";

    private static final int DATABASE_VERSION = 1;

    private static String CREATE_TABLE_MAHASISWA = "CREATE TABLE " + TABLE_NAME +
            "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NAMA + " TEXT NOT NULL, " +
            NIM + " TEXT NOT NULL" +
            ");";

    DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MAHASISWA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

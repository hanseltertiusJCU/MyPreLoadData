package com.example.android.mypreloaddata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.android.mypreloaddata.model.MahasiswaModel;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.android.mypreloaddata.database.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.android.mypreloaddata.database.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.android.mypreloaddata.database.DatabaseContract.TABLE_NAME;

// Class tsb berguna untuk membuat database pembantu untuk mempermudah penggunaan database mahasiswa (mengimplementasikan DML)
public class MahasiswaHelper {
    private DatabaseHelper databaseHelper;
    private static MahasiswaHelper INSTANCE;

    private SQLiteDatabase database;

    public MahasiswaHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static MahasiswaHelper getInstance(Context context){
        if(INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if(INSTANCE == null){
                    INSTANCE = new MahasiswaHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    // Membuka koneksi dari database
    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    // Menutup koneksi dari database
    public void close(){
        databaseHelper.close();

        if(database.isOpen())
            database.close();
    }

    // Method tsb berguna untuk membaca data dari database mahasiswa
    public ArrayList<MahasiswaModel> getAllData(){
        Cursor cursor = database.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                _ID + " ASC",
                null
        );
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList = new ArrayList<>();
        MahasiswaModel mahasiswaModel;
        if(cursor.getCount() > 0){
            do{
                // Initialize MahasiswaModel object by calling MahasiswaModel class
                mahasiswaModel = new MahasiswaModel();
                // Set values
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                // Add a MahasiswaModel object into ArrayList
                arrayList.add(mahasiswaModel);
                // Move to next row
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    // Melakukan pencarian data bedasarkan nama (seperti retrieve search results)
    public ArrayList<MahasiswaModel> getDataByName(String nama){
        Cursor cursor = database.query(TABLE_NAME,
                null,
                NAMA + " LIKE ?",
                new String[]{nama},
                null,
                null,
                _ID + " ASC",
                null);
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList = new ArrayList<>();
        MahasiswaModel mahasiswaModel;
        if(cursor.getCount() > 0){
            do{
                mahasiswaModel = new MahasiswaModel();
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                arrayList.add(mahasiswaModel);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    // Method tsb berguna untuk menulis data ke database mahasiswa
    public long insert(MahasiswaModel mahasiswaModel){
        ContentValues initialValues = new ContentValues();
        initialValues.put(NAMA, mahasiswaModel.getName());
        initialValues.put(NIM, mahasiswaModel.getNim());
        return database.insert(TABLE_NAME,
                null,
                initialValues
        );
    }

    // Tambahkan beberapa method agar insert data bersifat transactional
    public void beginTransaction(){
        database.beginTransaction(); // Database siap untuk menerima transaction
    }

    // Method tsb menandakan bahwa semua data berhasil dimasukkan
    public void setTransactionSuccess(){
        database.setTransactionSuccessful(); // Method tsb untuk memastikan
    }

    // Method tsb menunjukkan bahwa transaction selesai
    public void endTransaction(){
        database.endTransaction();
    }

    public void insertTransaction(MahasiswaModel mahasiswaModel){
        // Insert data ke Database scr looping
        String sql = "INSERT INTO " + TABLE_NAME + " (" + NAMA +  ", "
                + NIM + ") VALUES (?, ?)";
        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, mahasiswaModel.getName());
        stmt.bindString(2, mahasiswaModel.getNim());
        stmt.execute();
        stmt.clearBindings();
    }
}

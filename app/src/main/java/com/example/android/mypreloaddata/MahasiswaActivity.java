package com.example.android.mypreloaddata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.mypreloaddata.adapter.MahasiswaAdapter;
import com.example.android.mypreloaddata.database.MahasiswaHelper;
import com.example.android.mypreloaddata.model.MahasiswaModel;

import java.util.ArrayList;

public class MahasiswaActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MahasiswaAdapter mahasiswaAdapter;
    MahasiswaHelper mahasiswaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);
        recyclerView = findViewById(R.id.recyclerview);

        // Access MahasiswaHelper (class yg implement DML) dan MahasiswaAdapter (layout -> view)
        mahasiswaHelper = new MahasiswaHelper(this);
        mahasiswaAdapter = new MahasiswaAdapter();

        // Set recyclerView untuk menampung data LinearLayout dan set Adapter ke recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mahasiswaAdapter);

        // Buka koneksi ke SQL untuk membaca data
        mahasiswaHelper.open();
        ArrayList<MahasiswaModel> mahasiswaModels = mahasiswaHelper.getAllData();
        // Tutup koneksi dari SQL
        mahasiswaHelper.close();

        // Set data dari adapter bedasarkan data dari SQL (mahasiswaModels)
        mahasiswaAdapter.setData(mahasiswaModels);
    }
}

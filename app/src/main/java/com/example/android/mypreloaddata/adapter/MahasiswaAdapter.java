package com.example.android.mypreloaddata.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mypreloaddata.R;
import com.example.android.mypreloaddata.model.MahasiswaModel;

import java.util.ArrayList;

// Class ini berguna untuk menerapkan data ke layout
public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaHolder> {

    private ArrayList<MahasiswaModel> listMahasiswa = new ArrayList<>();

    public MahasiswaAdapter() {
    }

    public void setData(ArrayList<MahasiswaModel> listMahasiswa){
        // Delete listMahasiswa di MahasiswaAdapter jika isi dari parameter listMahasiswa itu ada datanya
        if(listMahasiswa.size() > 0){
            this.listMahasiswa.clear();
        }
        // Add semua data ke listMahasiswa di MahasiswaAdapter dengan input dari data listMahasiswa
        this.listMahasiswa.addAll(listMahasiswa);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MahasiswaHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // Inflate layout xml to view, which is then used for ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa_row, parent, false);
        return new MahasiswaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaHolder holder, int position) {
        // Set data dari value di MahasiswaModel
        holder.textViewNim.setText(listMahasiswa.get(position).getNim());
        holder.textViewNama.setText(listMahasiswa.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public class MahasiswaHolder extends RecyclerView.ViewHolder{
        // Initiate View
        private TextView textViewNim;
        private TextView textViewNama;
        public MahasiswaHolder(@NonNull View itemView) {
            super(itemView);
            // Assign View
            textViewNim = itemView.findViewById(R.id.txt_nim);
            textViewNama = itemView.findViewById(R.id.txt_nama);
        }
    }
}

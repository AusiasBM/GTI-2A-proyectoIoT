package com.example.androidthings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterDatos extends RecyclerView.Adapter<AdapterDatos.ViewHolderDatos> {

    List<Taquilla> listTaquillas;

    public AdapterDatos(List<Taquilla> listTaquillas) {
        this.listTaquillas = listTaquillas;
    }

    @NonNull
    @Override // Este método asocia el adaptador con la vista item_list
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        return new ViewHolderDatos(view);
    }

    @Override // Se encarga de la comunicación entre nuestro adaptador y la clase ViewHolderDatos
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(listTaquillas.get(position));
    }

    @Override // retorna el tamaño de la lista
    public int getItemCount() {
        return listTaquillas.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView numTaquilla;
        TextView tvPatineteTaquilla;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            numTaquilla = itemView.findViewById(R.id.tvNumTaquilla);
            tvPatineteTaquilla= itemView.findViewById(R.id.tvPatineteTaquilla);
        }

        public void asignarDatos(Taquilla taquilla) {

            numTaquilla.setText(taquilla.getId() + "");
            if (taquilla.getPatinNuestro()){
                tvPatineteTaquilla.setText("Patinete");
            }else{
                tvPatineteTaquilla.setText("Taquilla");
            }
        }
    }

}

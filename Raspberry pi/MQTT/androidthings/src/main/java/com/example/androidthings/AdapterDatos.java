package com.example.androidthings;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class AdapterDatos extends RecyclerView.Adapter<AdapterDatos.ViewHolderDatos> {

    List<Taquilla> listTaquillas;
    protected View.OnClickListener onClickListener;

    public AdapterDatos(List<Taquilla> listTaquillas) {
        this.listTaquillas = listTaquillas;
    }

    @NonNull
    @Override // Este método asocia el adaptador con la vista item_list
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolderDatos(view);
    }

    @Override // Se encarga de la comunicación entre nuestro adaptador y la clase ViewHolderDatos
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.asignarDatos(listTaquillas.get(position));
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
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
            tvPatineteTaquilla = itemView.findViewById(R.id.tvPatineteTaquilla);
        }

        public void asignarDatos(final Taquilla taquilla) {

            numTaquilla.setText(taquilla.getId() + "");
            if (taquilla.getPatinNuestro()){
                tvPatineteTaquilla.setText("Patinete");
            }else{
                tvPatineteTaquilla.setText("Taquilla");
            }

        }
    }

}

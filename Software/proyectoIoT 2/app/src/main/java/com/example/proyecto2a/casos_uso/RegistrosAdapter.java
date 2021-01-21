package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Registros;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.InfoRegistros;
import com.example.proyecto2a.presentacion.InfoUsuario;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class RegistrosAdapter extends FirestoreRecyclerAdapter<Registros, RegistrosAdapter.ViewHolder> {

    FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Activity activity;
    Registros registros= new Registros();

    public RegistrosAdapter(@NonNull FirestoreRecyclerOptions<Registros> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Registros model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = model.getFechaInicioAlquiler() + "";

        Log.d("fecha", model.toString());

        holder.coste.setText(String.format("%.2f", model.getImporteAlquiler())+"€");
        holder.fecha.setText(model.getDate(model.getFechaInicioAlquiler(),"dd/MM/yyyy HH:mm:ss"));
        holder.vInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoRegistros.class);
                intent.putExtra("registrosID", id);
                activity.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registros_item_list, parent, false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView fecha, coste;
        View vInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.Fecha);
            coste = itemView.findViewById(R.id.Coste);
            vInfo = itemView.findViewById(R.id.vInfoHistorial);
        }
    }
}

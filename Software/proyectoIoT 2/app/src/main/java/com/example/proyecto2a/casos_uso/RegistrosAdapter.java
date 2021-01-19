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
    //Saco la id de cada usuario
    //String idUsuario = firebaseFirestore.collection("usuarios").getId();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RegistrosAdapter(@NonNull FirestoreRecyclerOptions<Registros> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Registros model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.estant.setText("Estant: " + model.getEstant());
        holder.taquilla.setText("Taquilla: " + model.getTaquilla());
        holder.tipoAlquiler.setText(model.getTipoAlquiler());
        holder.fecha.setText(model.getDate(model.getFechaInicoAlquiler(), "dd/MM/yyyy HH:mm:ss"));


        if (model.getTipoAlquiler() != null) {
            if (model.getTipoAlquiler().equals("taquilla")) {
                holder.ubicacion.setText(model.getUbicacion());
                holder.ubicacionFin.setVisibility(View.GONE);
            } else {
                holder.ubicacion.setText(model.getUbicacionInicio());
                holder.ubicacionFin.setText(model.getUbicacionFinal());
            }

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registros_item_list, parent, false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView estant, taquilla, tipoAlquiler, fecha, ubicacion, ubicacionFin;
        ImageView logo;
        View vInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            estant = itemView.findViewById(R.id.tvEstant);
            taquilla = itemView.findViewById(R.id.tvTaquilla);
            tipoAlquiler = itemView.findViewById(R.id.tipoAlquiler);
            fecha = itemView.findViewById(R.id.Fecha);
            ubicacion = itemView.findViewById(R.id.Ubicacion);
            ubicacionFin = itemView.findViewById(R.id.UbicacionFin);
            logo = itemView.findViewById(R.id.logo);
            vInfo = (View) itemView.findViewById(R.id.vInfoRegistro);
        }
    }
}

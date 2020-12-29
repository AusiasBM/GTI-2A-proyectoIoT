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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto2a.R;
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

public class UsuariosAdapter extends FirestoreRecyclerAdapter<Usuario, UsuariosAdapter.ViewHolder> {

    FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Activity activity;
    //Saco la id de cada usuario
    //String idUsuario = firebaseFirestore.collection("usuarios").getId();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsuariosAdapter(@NonNull FirestoreRecyclerOptions<Usuario> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Usuario model) {
        //holder.fotoPerfil.setImageResource(Integer.parseInt(model.getFoto()));
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        Glide.with(activity).load(model.getFoto()).into(holder.fotoPerfil);
        if (!model.getNombre().equals("")){
            holder.nombre.setText(model.getNombre());
        } else {
            holder.nombre.setText(R.string.textNoNombre);
        }

        holder.correo.setText(model.getCorreo());

        holder.vInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoUsuario.class);
                intent.putExtra("usuarioID", id);
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usuarios_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, correo;
        ImageView fotoPerfil;
        View vInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvNombreUsuario);
            correo = itemView.findViewById(R.id.tvCorreoUsuario);
            fotoPerfil = itemView.findViewById(R.id.ivUsuario);
            vInfo = (View) itemView.findViewById(R.id.vInfoUsuario);
        }
    }
}

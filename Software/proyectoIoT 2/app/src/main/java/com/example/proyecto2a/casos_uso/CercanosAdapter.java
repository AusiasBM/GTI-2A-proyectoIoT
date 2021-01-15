package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.example.proyecto2a.presentacion.StantsCercanos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class CercanosAdapter extends FirestoreRecyclerAdapter<Stant, CercanosAdapter.ViewHolder> {

    Activity activity;
    private String isUser;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CercanosAdapter(@NonNull FirestoreRecyclerOptions<Stant> options, Activity activity, String idUser) {
        super(options);
        this.activity = activity;
        this.isUser = idUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Stant model) {

        DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.nombreStant.setText(model.getUbicacion());
        holder.clcercano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MenuDialogActivity.class);
                intent.putExtra("idUser", isUser);
                intent.putExtra("nombre", model.getUbicacion());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cercanos_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreStant;
        ConstraintLayout clcercano;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreStant = itemView.findViewById(R.id.tvNombreStant);
            clcercano = itemView.findViewById(R.id.clcercano);

        }

    }
}

package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.presentacion.InfoStant;
import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Stant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class StantsAdapter extends FirestoreRecyclerAdapter<Stant, StantsAdapter.ViewHolder> {

    Activity activity;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StantsAdapter(@NonNull FirestoreRecyclerOptions<Stant> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Stant model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.ubicacion.setText(model.getUbicacion());
        holder.vInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoStant.class);
                intent.putExtra("stantID", id);
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stants_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ubicacion;
        View vInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ubicacion = itemView.findViewById(R.id.tvUbicacionStant);
            vInfo = itemView.findViewById(R.id.vInfoStant);
        }
    }
}

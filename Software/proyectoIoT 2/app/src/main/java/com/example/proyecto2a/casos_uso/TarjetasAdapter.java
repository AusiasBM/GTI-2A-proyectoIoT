package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Tarjeta;
import com.example.proyecto2a.presentacion.InfoTarjeta;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TarjetasAdapter extends FirestoreRecyclerAdapter<Tarjeta, TarjetasAdapter.TarjetasViewHolder> {

    Activity activity;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    FirebaseFirestore firebaseFirestore;
    public TarjetasAdapter(@NonNull FirestoreRecyclerOptions<Tarjeta> options, Activity activity) {
        super(options);
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull TarjetasViewHolder holder, int position, @NonNull Tarjeta model) {

        //id del elemento a usar
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.tvNumTarj.setText(String.valueOf(model.getNumTarjeta()));

        holder.vInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoTarjeta.class);
                intent.putExtra("tarjetaID", id);
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public TarjetasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjetas_item_list, parent, false);
        return new TarjetasViewHolder(view);
    }

    public static class TarjetasViewHolder extends RecyclerView.ViewHolder{

        //private FirebaseAuth firebaseAuth;
        TextView tvNumTarj;
        View vInfo;

        public TarjetasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumTarj = itemView.findViewById(R.id.tvNumeroTarjetaInfo);
            vInfo = itemView.findViewById(R.id.vInfo);
            //firebaseAuth = FirebaseAuth.getInstance();
            //firebaseAuth.getUid();
        }
    }
}
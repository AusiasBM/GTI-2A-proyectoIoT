package com.example.androidthings;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.androidthings.LoginActivity.user;
import static com.example.androidthings.MainActivity.taquillas;

public class AdapterDatos extends RecyclerView.Adapter<AdapterDatos.ViewHolderDatos> {

    List<Taquilla> listTaquillas;
    protected View.OnClickListener onClickListener;
    DocumentReference docRef;
    FirebaseFirestore db;

    public AdapterDatos(List<Taquilla> listTaquillas, FirebaseFirestore db) {

        this.db = db;
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
    public void onBindViewHolder(@NonNull final ViewHolderDatos holder, int position) {
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
        CardView cardTaquilla;
        Switch swCarga;
        LinearLayout item;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            numTaquilla = itemView.findViewById(R.id.tvNumTaquilla);
            tvPatineteTaquilla = itemView.findViewById(R.id.tvPatineteTaquilla);
            cardTaquilla = itemView.findViewById(R.id.cardTaquilla);
            swCarga = (Switch) itemView.findViewById(R.id.swCarga);
            item = itemView.findViewById(R.id.item);

        }

        public void asignarDatos(final Taquilla taquilla) {
            docRef = db.collection("estaciones/" + 0 + "/taquillas").document(taquilla.id + "");
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("Escucha", "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("Escucha", "Current data: " + snapshot.getData());
                        if (Boolean.parseBoolean(snapshot.getData().get("alquilada").toString())){
                            cardTaquilla.setCardBackgroundColor(Color.GRAY);
                            //item.setClickable(false);
                            taquilla.setAlquilada(true);

                            if (snapshot.getData().get("idUsuario").equals(user.getuId())){
                                // Poner la foto del usuario donde está el patín
                            }
                        }else{
                            taquilla.setAlquilada(false);
                            cardTaquilla.setCardBackgroundColor(Color.argb(255, 69, 114, 188));
                            item.setClickable(true);
                        }
                    } else {
                        Log.d("Escucha", "Current data: null");
                    }

                    taquilla.setIdUsuario(snapshot.getData().get("idUsuario").toString());
                }
            });
            if (taquilla.alquilada){
                cardTaquilla.setCardBackgroundColor(Color.GRAY);
            }
            numTaquilla.setText(taquilla.getId() + "");
            if (taquilla.getPatinNuestro()){
                tvPatineteTaquilla.setText("Patinete");
            }else{
                tvPatineteTaquilla.setText("Taquilla");
            }

        }
    }

}

package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Taquilla;
import com.example.proyecto2a.presentacion.InfoStant;
import com.example.proyecto2a.presentacion.StantsActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaquillasRecyclerAdatper extends FirestoreRecyclerAdapter<Taquilla, TaquillasRecyclerAdatper.ViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Activity activity;
    private FirebaseFirestore firebaseFirestore;
    Stant stant = new Stant();
    public TaquillasRecyclerAdatper(@NonNull FirestoreRecyclerOptions<Taquilla> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Taquilla model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String stantID = stant.getuID();
        final String id = documentSnapshot.getId();
        firebaseFirestore = FirebaseFirestore.getInstance();
        holder.tvIdTaquilla.setText("Taquilla nº" + model.getId());
        if (model.isAlquilada()){
            holder.ivEstado.setImageResource(R.drawable.taquilla_alquilada);
        }
        else if (model.isReservada()){
            holder.ivEstado.setImageResource(R.drawable.taquilla_reservada);
        }
        else {
            holder.ivEstado.setImageResource(R.drawable.taquilla_disponible);
        }
        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  final AlertDialog.Builder alert =new AlertDialog.Builder(activity);
                alert.setMessage(R.string.preguntaFoto);
                alert.setTitle(R.string.eliminarFoto);


                alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog=alert.create();
                dialog.show();*/
                //
                final LayoutInflater inflater = LayoutInflater.from(activity);
                final View view = inflater.inflate(R.layout.dialog_eliminar_taquilla,null);
                Button acceptButton= view.findViewById(R.id.btn_si);
                final Button cancelButton = view.findViewById(R.id.btn_no);
                acceptButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("estaciones").document(model.getEstant()).collection("taquillas").document(model.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, R.string.taquilla_eliminada_correctamente, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, R.string.fallo_al_eliminar_taquilla, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                final AlertDialog alertDialog=new AlertDialog.Builder(activity)
                        .setView(view)
                        .create();
                alertDialog.show();
                cancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taquillas_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvIdTaquilla;
        ImageView ivEstado;
        Button eliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIdTaquilla = itemView.findViewById(R.id.tvIdTaquilla);
            ivEstado = itemView.findViewById(R.id.ivEstadoTaquilla);
            eliminar = itemView.findViewById(R.id.btEliminarTaquilla);

        }
    }
}

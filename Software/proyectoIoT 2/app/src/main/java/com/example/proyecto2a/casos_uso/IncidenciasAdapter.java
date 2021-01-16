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

import com.bumptech.glide.Glide;
import com.example.proyecto2a.presentacion.IncidenciasActivity;
import com.example.proyecto2a.presentacion.InfoFoto;
import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Incidencia;
import com.example.proyecto2a.presentacion.InfoPago;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class IncidenciasAdapter extends FirestoreRecyclerAdapter<Incidencia, IncidenciasAdapter.ViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Activity activity;
    public IncidenciasAdapter(@NonNull FirestoreRecyclerOptions<Incidencia> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Incidencia model) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        final long tiempo = model.getTiempo();
        final String url = model.getUrl();

        firebaseFirestore = FirebaseFirestore.getInstance();

        holder.fechaFoto.setText(model.getDate(model.getTiempo(),"dd/MM/yyyy HH:mm:ss"));
        Glide.with(activity)
                .load(model.getUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.foto);
        holder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, InfoFoto.class);
                intent.putExtra("incidenciaID", id);
                intent.putExtra("incidenciaTiempo", tiempo);
                intent.putExtra("incidenciaURL", url);
                activity.startActivity(intent);
            }
        });

        holder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* final AlertDialog.Builder alert =new AlertDialog.Builder(activity);
                alert.setMessage(R.string.preguntaFoto);
                alert.setTitle(R.string.eliminarFoto);
                alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("imagenesSeguridad").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, R.string.fotoEliminada, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, R.string.fotoNoEliminada, Toast.LENGTH_SHORT).show();
                            }
                        });
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
                final View view = inflater.inflate(R.layout.dialog_eliminar_incidencias,null);
                Button acceptButton= view.findViewById(R.id.btn_si);
                final Button cancelButton = view.findViewById(R.id.btn_no);
                acceptButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("imagenesSeguridad").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(activity, R.string.fotoEliminada, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, R.string.fotoNoEliminada, Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incidencias_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fechaFoto;
        ImageView foto;
        Button eliminar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ivAlarma);
            fechaFoto = itemView.findViewById(R.id.tvFechaIncidencia);
            eliminar =  itemView.findViewById(R.id.btEliminarRv);
        }
    }
}

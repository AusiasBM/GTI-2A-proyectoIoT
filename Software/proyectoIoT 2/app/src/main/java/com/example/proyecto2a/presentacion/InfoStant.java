package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.TaquillasRecyclerAdatper;
import com.example.proyecto2a.datos.Stants;
import com.example.proyecto2a.datos.Taquillas;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Taquilla;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InfoStant extends AppCompatActivity {

    private String stantID;
    private String taquillaID;
    private FirebaseFirestore firebaseFirestore;
    private TextView tvUbicacion, tvPosicion;
    private Button btEliminar, btAñadir;
    private ImageView ivVolver;
    Taquilla taquilla = new Taquilla();
    Taquillas taquillas = new Taquillas();
    Stant stant = new Stant();
    Stants stants = new Stants();
    int contador = 1;

    RecyclerView recyclerViewNuestro, recyclerViewPropio;
    TaquillasRecyclerAdatper adapterNuestro, adapterPropio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_stant);
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacioStant_info);
        btEliminar = (Button) findViewById(R.id.btEliminarStant);
        ivVolver = (ImageView) findViewById(R.id.ivBackInfoStant);
        btAñadir = findViewById(R.id.btAnadirTaquilla);

        stantID = getIntent().getStringExtra("stantID");
        taquillaID = getIntent().getStringExtra("taquillaID");

        recyclerViewNuestro = findViewById(R.id.rvTaquillas);
        recyclerViewNuestro.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPropio = findViewById(R.id.rvTaquillasSinPatin);
        recyclerViewPropio.setLayoutManager(new LinearLayoutManager(this));

        firebaseFirestore = FirebaseFirestore.getInstance();

        Query queryNuestro = firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas")
                .whereEqualTo("patinNuestro", true);

        Query queryPropio = firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas")
                .whereEqualTo("patinNuestro", false);

        FirestoreRecyclerOptions<Taquilla> firestoreRecyclerOptionsNuestro = new FirestoreRecyclerOptions.Builder<Taquilla>()
                .setQuery(queryNuestro, Taquilla.class).build();
        FirestoreRecyclerOptions<Taquilla> firestoreRecyclerOptionsPropio = new FirestoreRecyclerOptions.Builder<Taquilla>()
                .setQuery(queryPropio, Taquilla.class).build();
        adapterNuestro = new TaquillasRecyclerAdatper(firestoreRecyclerOptionsNuestro, this);
        adapterPropio = new TaquillasRecyclerAdatper(firestoreRecyclerOptionsPropio, this);
        adapterNuestro.notifyDataSetChanged();
        adapterPropio.notifyDataSetChanged();
        recyclerViewNuestro.setAdapter(adapterNuestro);
        recyclerViewPropio.setAdapter(adapterPropio);

        verValoresStants();

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoStant.this, StantsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* firebaseFirestore.collection("estaciones").document(stantID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InfoStant.this, R.string.estacionEliminada, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InfoStant.this, StantsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InfoStant.this, R.string.estacionNoEliminada, Toast.LENGTH_SHORT).show();
                    }
                });*/
                final LayoutInflater inflater = LayoutInflater.from(InfoStant.this);
                final View view = inflater.inflate(R.layout.dialog_eliminar_estacion,null);
                Button acceptButton= view.findViewById(R.id.btn_si);
                final Button cancelButton = view.findViewById(R.id.btn_no);
                acceptButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        firebaseFirestore.collection("estaciones").document(stantID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InfoStant.this, R.string.estacionEliminada, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InfoStant.this, StantsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoStant.this, R.string.estacionNoEliminada, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                final AlertDialog alertDialog=new AlertDialog.Builder(InfoStant.this)
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

    public void verValoresStants(){
        firebaseFirestore.collection("estaciones").document(stantID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String ubicacionStant = documentSnapshot.getString("ubicacion");
                            tvUbicacion.setText(ubicacionStant);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterNuestro.startListening();
        adapterPropio.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterNuestro.stopListening();
        adapterPropio.stopListening();
    }


    public void anadirTaquillaPatinNuestro(View view){
        try{
            taquilla.setPatinNuestro(true);
            taquilla.setAlquilada(false);
            taquilla.setCargaPatinete(true);
            taquilla.setEstant(stantID);
            taquilla.setId(contador+"");
            taquilla.setIdUsuario("");
            taquilla.setOcupada(false);
            taquilla.setPuertaAbierta(false);
            taquilla.setReservada(false);
            taquilla.setEstacionFinal(true);
            firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas").document((String.valueOf(contador))).set(taquilla).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    contadorTaquillas();
                }
            });
            //taquillas.actualizarTaquilla(taquilla);
            Toast.makeText(this, "Creada taquilla con patín de la empresa", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error al añadir taquilla", Toast.LENGTH_SHORT).show();
        }
    }

    public void anadirTaquillaPatinPropio(View view){
        try{
            taquilla.setPatinNuestro(false);
            taquilla.setAlquilada(false);
            taquilla.setCargaPatinete(false);
            taquilla.setEstant(stantID);
            taquilla.setId(contador+"");
            taquilla.setIdUsuario("");
            taquilla.setOcupada(false);
            taquilla.setPuertaAbierta(false);
            taquilla.setReservada(false);
            taquilla.setEstacionFinal(true);
            firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas").document((String.valueOf(contador))).set(taquilla).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    contadorTaquillas();
                }
            });
            //taquillas.actualizarTaquilla(taquilla);
            Toast.makeText(this, "Creada taquilla con patín propio", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error al añadir taquilla", Toast.LENGTH_SHORT).show();
        }
    }

    public void contadorTaquillas(){
        firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                contador = 1;
                for(QueryDocumentSnapshot documentSnaphots: queryDocumentSnapshots){
                        contador = contador + 1;
                }
            }
        });
    }
}
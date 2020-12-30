package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Stants;
import com.example.proyecto2a.datos.Taquillas;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Taquilla;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class InfoStant extends AppCompatActivity {

    private String stantID;
    private String taquillaID;
    private FirebaseFirestore firebaseFirestore;
    private TextView tvUbicacion, tvPosicion;
    private Button btEliminar;
    private ImageView ivVolver;
    Taquilla taquilla = new Taquilla();
    Stant stant = new Stant();
    Stants stants = new Stants();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_stant);

        tvPosicion = (TextView) findViewById(R.id.tv_posicion);
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacioStant_info);
        btEliminar = (Button) findViewById(R.id.btEliminarStant);
        ivVolver = (ImageView) findViewById(R.id.ivBackInfoStant);

        stantID = getIntent().getStringExtra("stantID");
        taquillaID = getIntent().getStringExtra("taquillaID");

        firebaseFirestore = FirebaseFirestore.getInstance();

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
    }

    public void verValoresStants(){
        firebaseFirestore.collection("estaciones").document(stantID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            GeoPoint posicionStant = documentSnapshot.getGeoPoint("pos");
                            String ubicacionStant = documentSnapshot.getString("ubicacion");
                            tvPosicion.setText(posicionStant + "");
                            tvUbicacion.setText(ubicacionStant);
                        }
                    }
                });
    }

    /*
    public void anadirTaquillaPatinNuestro(View view){
        try{
            taquilla.setPatinNuestro(true);
            //taquillas.actualizarTaquilla(taquilla);
            Toast.makeText(this, "Creada taquilla con patín de la empresa", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error al añadir taquilla", Toast.LENGTH_SHORT).show();
        }
    }

    public void anadirTaquillaPatinPropio(View view){
        try{
            taquilla.setPatinNuestro(false);
            //taquillas.actualizarTaquilla(taquilla);
            Toast.makeText(this, "Creada taquilla con patín propio", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error al añadir taquilla", Toast.LENGTH_SHORT).show();
        }
    }

    public void eliminarTaquilla(View view){
        firebaseFirestore.collection("estaciones").document(stantID).collection("taquillas").document(taquillaID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(InfoStant.this, "Taquilla eliminada correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InfoStant.this, StantsActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoStant.this, "La taquilla no se pudo eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }
     */
}
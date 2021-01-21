package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Registros;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoRegistros extends AppCompatActivity {

    private String registrosID;

    private ImageView ivVolver;
    private TextView fecha;
    private TextView tipoAlquiler;
    private TextView coste;
    private TextView duracion;
    private TextView ubicacion, etUbicacionFinal, tvUbicacionFinal;
    FirebaseFirestore firebaseFirestore;

    Registros registros = new Registros();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_registros);
        ivVolver = (ImageView) findViewById(R.id.ivBackRegistros);
        fecha = findViewById(R.id.tvFecha_info);
        tipoAlquiler = findViewById(R.id.tv_Alquiler_info);
        ubicacion = findViewById(R.id.tvUbicacion);
        coste = findViewById(R.id.tvCoste);
        duracion = findViewById(R.id.tvDuracion);
        etUbicacionFinal= findViewById(R.id.ubicacionFinal);
        tvUbicacionFinal= findViewById(R.id.tvUbicacionFinal);

        firebaseFirestore = FirebaseFirestore.getInstance();

        registrosID = getIntent().getStringExtra("registrosID");


        verValores();

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void verValores() {
        firebaseFirestore.collection("registrosAlquiler").document(registrosID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            String tipoAlquilerfb = documentSnapshot.getString("tipoAlquiler");
                            tipoAlquiler.setText(tipoAlquilerfb + "");

                            String ubicacionTaquilla = documentSnapshot.getString("ubicacion");
                            String ubicacionInicio = documentSnapshot.getString("ubicacionInicio");
                            String ubicacionFinal = documentSnapshot.getString("ubicacionFinal");
                            if (tipoAlquilerfb.equals("taquilla")) {
                                ubicacion.setText(ubicacionTaquilla);
                                tvUbicacionFinal.setVisibility(View.GONE);
                                etUbicacionFinal.setVisibility(View.GONE);
                            } else {
                                ubicacion.setText(ubicacionInicio);
                                etUbicacionFinal.setText(ubicacionFinal);

                            }
                            long fechaAlquiler = documentSnapshot.getLong("fechaInicioAlquiler");
                            fecha.setText(registros.getDate(fechaAlquiler,"dd/MM/yyyy HH:mm:ss"));

                            double costeAlquiler = documentSnapshot.getDouble("importeAlquiler");
                            coste.setText(String.format("%.2f", costeAlquiler) +"€");

                            double duracionAlquiler = documentSnapshot.getDouble("tiempoAlquilada");
                            if (duracionAlquiler >= 3600000) {
                                duracion.setText(String.format("%.0f", duracionAlquiler/3600000) + "h");
                            } else if (duracionAlquiler >= 60000){
                                duracion.setText(String.format("%.0f",duracionAlquiler/60000) + "min");
                            }else if (duracionAlquiler < 60000 && duracionAlquiler >= 1000){
                                    duracion.setText("<1min");
                            }else if (duracionAlquiler == 0){
                                duracion.setText("<1s");
                            }
                        }
                    }
                });
    }
}
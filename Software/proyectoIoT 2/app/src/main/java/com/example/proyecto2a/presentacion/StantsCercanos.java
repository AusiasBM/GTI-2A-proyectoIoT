package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.CercanosAdapter;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StantsCercanos extends AppCompatActivity implements LocationListener {

    ImageView volver;
    RecyclerView recyclerView;
    CercanosAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    private String idUser;
    private double latUsu = 0;
    private double longUsu = 0;
    int FlagUbicacion = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stants_cercanos);
        volver = findViewById(R.id.ivBackCercano);

        idUser = getIntent().getStringExtra("idUser");

        recyclerView = findViewById(R.id.recyclerViewCercanos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Active la ubicación para usar esta funcionalidad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Query quey = firebaseFirestore.collection("estaciones");
        FirestoreRecyclerOptions<Stant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Stant>().setQuery(quey, Stant.class).build();
        adapter = new CercanosAdapter(firestoreRecyclerOptions, this, idUser, latUsu, longUsu);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StantsCercanos.this, ResActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //  Receptor broadcast
    public class ReceptorOperacion extends BroadcastReceiver {
        public static final String ACTION_RESP= "com.example.exempleexam20192.LATITUD_LONGITUD";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (FlagUbicacion ==0){
                Double latUsu = intent.getDoubleExtra("latitud", 0.0);
                Double longUsu = intent.getDoubleExtra("longitud", 0.0);
                Query quey = firebaseFirestore.collection("estaciones");
                FirestoreRecyclerOptions<Stant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Stant>().setQuery(quey, Stant.class).build();
                adapter = new CercanosAdapter(firestoreRecyclerOptions, StantsCercanos.this, idUser, latUsu, longUsu);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                FlagUbicacion ++;
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        try {
            startService(new Intent(this,
                    ServicioLocalizacion.class));
            adapter.startListening();
        }catch (Exception ex){
            Toast.makeText(this, "Active la ubicación para usar esta funcionalidad", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
            adapter.stopListening();
            stopService(new Intent(this,
                    ServicioLocalizacion.class));
        }catch (Exception ex){
            Toast.makeText(this, "Active la ubicación para usar esta funcionalidad", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
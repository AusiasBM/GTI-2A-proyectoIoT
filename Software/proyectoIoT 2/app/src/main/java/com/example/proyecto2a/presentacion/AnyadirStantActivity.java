package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Stants;
import com.example.proyecto2a.modelo.Stant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

public class AnyadirStantActivity extends AppCompatActivity {

    private Stant stant;
    private Stants stants;
    private EditText etLatitud, etUbicacion, etLongitud ;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anyadir_stant);
        etLatitud= findViewById(R.id.etLat);
        etLongitud = findViewById(R.id.etLon);
        etUbicacion = findViewById(R.id.etUbicacion);

        stant = new Stant();
        stants = new Stants();
    }

    public void returnStant(View view){
        actualizarPerfilStant();
        Intent intent=new Intent(this, StantsActivity.class);
        startActivity(intent);
    }

    public void guardarStant(View view){
        if (etUbicacion.getText().toString().isEmpty() || etLatitud.getText().toString().isEmpty() || etLongitud.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.rellenarCampos, Toast.LENGTH_SHORT).show();
        } else {
            actualizarPerfilStant();
        }
    }

    public void actualizarPerfilStant(){
        try{
            stant.setUbicacion(etUbicacion.getText().toString());

            double latitud = Double.parseDouble(etLatitud.getText().toString());
            double longitud = Double.parseDouble(etLongitud.getText().toString());

            GeoPoint pos = new GeoPoint(latitud, longitud);
            stant.setPos(pos);

            stants.actualizarStant(stant);
            Toast.makeText(this, R.string.estacionOk, Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, StantsActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, R.string.errorModificar, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        actualizarPerfilStant();
        Intent intent = new Intent(this, StantsActivity.class);
        startActivity(intent);
        finish();
    }
}
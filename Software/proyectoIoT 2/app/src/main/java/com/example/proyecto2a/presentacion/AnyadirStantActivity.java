package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Stants;
import com.example.proyecto2a.modelo.Stant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class AnyadirStantActivity extends AppCompatActivity {

    private Stant stant;
    private Stants stants;
    private EditText etLatitudLongitud, etUbicacion;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anyadir_stant);
        etLatitudLongitud = findViewById(R.id.etLatLong);
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
        if (etUbicacion.getText().toString().isEmpty() || etLatitudLongitud.getText().toString().isEmpty()){
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            actualizarPerfilStant();
        }
    }

    public void actualizarPerfilStant(){
        try{
            stant.setuId(firebaseAuth.getUid());
            stant.setUbicacion(etUbicacion.getText().toString());

            GeoPoint pos = (GeoPoint)etLatitudLongitud.getText();
            stant.setPos(pos);

            stants.actualizarStant(stant);
            Toast.makeText(this, "Estación introducida correctamente", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, StantsActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show();
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
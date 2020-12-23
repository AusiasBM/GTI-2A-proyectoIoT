package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Stants;
import com.example.proyecto2a.modelo.Stant;

public class AnyadirStantActivity extends AppCompatActivity {

    private Stant stant;
    private Stants stants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anyadir_stant);
    }
    public void returnStant(View view){
        //actualizarPerfilTarjeta();
        Intent intent=new Intent(this, StantsActivity.class);
        startActivity(intent);
    }
    public void guardarStant(View view){
        Toast.makeText(this, ".", Toast.LENGTH_SHORT).show();
    }
}
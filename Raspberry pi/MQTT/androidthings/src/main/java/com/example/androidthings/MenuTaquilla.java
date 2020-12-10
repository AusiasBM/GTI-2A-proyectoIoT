package com.example.androidthings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import static com.example.androidthings.MainActivity.abrirCerradura;
import static com.example.androidthings.MainActivity.apagarEncenderCarga;
import static com.example.androidthings.MainActivity.sonoff;
import static com.example.androidthings.MainActivity.taquillas;

public class MenuTaquilla extends AppCompatActivity {

    TextView tvTituloTaquilla;
    Switch swCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_taquilla);
        tvTituloTaquilla = findViewById(R.id.tvTituloTaquilla);
        swCarga = findViewById(R.id.swCarga);

        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("pos", 0);

        if (taquillas.get(pos).patineteNuestro){
            tvTituloTaquilla.setText("Patinete " + taquillas.get(pos).id);
        }else{
            tvTituloTaquilla.setText("Taquilla " + taquillas.get(pos).id);
        }

        if (taquillas.get(pos).cargarPatinete){
            swCarga.setChecked(true);
        }

    }

    public void abrirPuerta(View v){
        abrirCerradura(v);
    }

    public void cargar(View v){
        apagarEncenderCarga();
    }

    public void cerrar(View v){
        this.finish();
    }
}
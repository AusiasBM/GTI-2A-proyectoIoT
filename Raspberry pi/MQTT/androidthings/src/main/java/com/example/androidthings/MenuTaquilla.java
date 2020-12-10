package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.androidthings.MainActivity.enviarMensaje;
import static com.example.androidthings.MainActivity.sonoff;
import static com.example.androidthings.MainActivity.taquillas;

import com.example.comun.Mqtt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.androidthings.MainActivity.enviarMensaje;

public class MenuTaquilla extends AppCompatActivity {

    Button bt;
    TextView tvTituloTaquilla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_taquilla);
        bt = findViewById(R.id.btPuerta);
        tvTituloTaquilla = findViewById(R.id.tvTituloTaquilla);

        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("pos", 0);

<<<<<<< HEAD
        if (taquillas.get(pos).patineteNuestro){
            tvTituloTaquilla.setText("Patinete " + taquillas.get(pos).id);
        }else{
            tvTituloTaquilla.setText("Taquilla " + taquillas.get(pos).id);
        }

    }

    public void abrirPuerta(View v){
        enviarMensaje(v);
    }
=======
        bt.setText("Abrir taquilla " + pos);
>>>>>>> develop

    public void cargar(View v){
        sonoff("ON");
    }

    public void abrirTaquilla(View v){
        enviarMensaje(null);
    }
}
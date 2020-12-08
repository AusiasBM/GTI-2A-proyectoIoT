package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comun.Mqtt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.androidthings.MainActivity.enviarMensaje;

public class MenuTaquilla extends AppCompatActivity {

    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_taquilla);
        bt = findViewById(R.id.bt);

        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("pos", 0);

        bt.setText("Abrir taquilla " + pos);

    }

    public void abrirTaquilla(View v){
        enviarMensaje(null);
    }
}
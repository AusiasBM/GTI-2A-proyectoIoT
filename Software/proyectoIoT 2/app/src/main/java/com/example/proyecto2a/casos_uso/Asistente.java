package com.example.proyecto2a.casos_uso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;

public class Asistente extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente);

    }

    public void llamarTel(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:962849337"));
        startActivity(intent);
    }

    public void mandarCorreo(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.asistenteProblema);
        intent.putExtra(Intent.EXTRA_TEXT, R.string.asistenteEscribir);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"asistente@gmail.com"});
        startActivity(intent);
    }
}

package com.example.proyecto2a.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.Asistente;

public class Ayuda extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ayuda_menu);
    }

    public void returnRes(View view){
        Intent intent=new Intent(this, ResActivity.class);
        startActivity(intent);
    }
    public void faq(View view){
        Intent intent=new Intent(this, faq.class);
        startActivity(intent);
    }
    public void politica(View view){
        Intent intent=new Intent(this, politica.class);
        startActivity(intent);
    }
    public void terminos(View view){
        Intent intent=new Intent(this, terminos.class);
        startActivity(intent);
    }

    public void lanzarContactos(View view) {
        Intent i = new Intent(this, Asistente.class);
        startActivity(i);
    }

}

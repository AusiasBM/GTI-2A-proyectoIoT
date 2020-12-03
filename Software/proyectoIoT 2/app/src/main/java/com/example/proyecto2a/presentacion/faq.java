package com.example.proyecto2a.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;

public class faq extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.faq);
    }
    public void returnAyuda(View view){
        Intent intent=new Intent(this, Ayuda.class);
        startActivity(intent);
    }
}
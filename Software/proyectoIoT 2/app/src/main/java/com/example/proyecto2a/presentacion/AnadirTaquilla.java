package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.proyecto2a.R;

public class AnadirTaquilla extends AppCompatActivity {

    ImageView ivVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_taquilla);
        ivVolver = findViewById(R.id.ivBackAddTaquilla);

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AnadirTaquilla.this, InfoStant.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
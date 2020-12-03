package com.example.androidthings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuTaquilla extends AppCompatActivity {

    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_taquilla);
        bt = findViewById(R.id.bt);

        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("pos", 0);

        bt.setText("Cerrar taquilla " + pos);

    }

    public void cerrar(View v){
        this.finish();
    }
}
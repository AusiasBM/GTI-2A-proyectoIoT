package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.SignIn;
import com.example.proyecto2a.casos_uso.SignUp;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void SignUp(View view){
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }
    public void SignIn(View view){
        Intent intent = new Intent(this, SignIn.class);
        intent.putExtra(SignIn.metodo, "nada");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){

    }


}
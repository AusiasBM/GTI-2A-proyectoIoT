package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.SignIn;
import com.example.proyecto2a.casos_uso.SignUp;
import com.example.proyecto2a.datos.Mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

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
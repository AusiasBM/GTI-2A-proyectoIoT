package com.example.proyecto2a.presentacion;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MensajeActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mensaje_verif);
    }

    public void confirmar(View view){

    }
}

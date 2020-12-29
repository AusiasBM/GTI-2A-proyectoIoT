package com.example.proyecto2a.presentacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.Asistente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MensajeActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;
    public String estant;
    public String ide;
    public String id;
  //  Context context;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mensaje_verif);
      //  context = getContext();
    }

    public void cerrar(View view){
        finish();
    }
    public void mensajeVerif(View view) {
       // String nombre = (String) textViewNombre.getText();
         id = getIntent().getExtras().getString("id");
         ide = getIntent().getExtras().getString("ide");
         estant = getIntent().getExtras().getString("estant");

        DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
        taq.update("idUsuario", ide).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ocupada", "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ocupada", "Error updating document", e);
                    }
                });
        taq.update("alquilada", true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ocupada", "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ocupada", "Error updating document", e);
                    }
                });
        //Crear la notificació
        notificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this, CANAL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Taquilla reservada")
                        .setContentText("Has reservado una taquilla");
        //Llançar l'aplicació des de la notificació
        PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, ResActivity.class), 0);
        notificacion.setContentIntent(intencionPendiente);
        //Para lanzar la notificación
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
        finish();
    }
}

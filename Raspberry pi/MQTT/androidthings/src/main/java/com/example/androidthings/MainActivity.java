package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comun.Mqtt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.comun.Mqtt.qos;
import static com.example.comun.Mqtt.topicRoot;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 * <p>
 * PeripheralManager manager = PeripheralManager.getInstance();
 * try {
 * Gpio gpio = manager.openGpio("BCM6");
 * gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * gpio.setValue(true);
 * } catch (IOException e) {
 * Log.e(TAG, "Unable to access GPIO");
 * }
 * <p>
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
public class MainActivity extends AppCompatActivity implements MqttCallback {

    FirebaseFirestore db;
    public static MqttClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        try {
            client = new MqttClient(Mqtt.broker, Mqtt.clientId, new
                    MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setWill(topicRoot+"WillTopic", "App desconectada".getBytes(),Mqtt.qos, false);
        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // Nos suscribimos al topic rfid
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"rfid");
            client.subscribe(topicRoot+"rfid", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        // Nos suscribimos al topic magnetico
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"magnetico");
            client.subscribe(topicRoot+"magnetico", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
    }

    // Se ejecuta cuando se pierde la conexión
    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    // Se ejecuta cuando se publica algo en los topics subscritos
    @Override
    public void messageArrived(String topic, MqttMessage message) throws
            Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);

        if(topic.equals(topicRoot+"rfid")){
            topicRfid(payload);
        }

        if(topic.equals(topicRoot+"magnetico")){
            topicMagnetico(payload);
        }

    }

    private void topicMagnetico(final String payload) {

        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Actualiza el estado de la puerta en fireStore
                            if (payload.equals("cerraduraAbierta")){
                                db.collection("estaciones/0/taquillas/")
                                        .document("0")
                                        .update("puertaAbierta", true);
                            }else if (payload.equals("cerraduraCerrada")){
                                db.collection("estaciones/0/taquillas/")
                                        .document("0")
                                        .update("puertaAbierta", false);
                            }

                        } else {
                            Log.w(Mqtt.TAG, "Error getting documents.", task.getException());
                        }

                    }
                });
    }

    public void topicRfid(final String payload){

        final boolean[] puertaAbierta = {false};

        final ArrayList<Map<String, Object>> usuarios = new ArrayList<>();

        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(Mqtt.TAG, document.getId() + " => " + document.getData());
                                Log.d("llave pasada", document.getData().get("llave").toString());
                                usuarios.add(document.getData());
                                if (document.getData().get("llave").toString().equals(payload)){
                                    puertaAbierta[0] = true;
                                    break;
                                }
                            }
                        } else {
                            Log.w(Mqtt.TAG, "Error getting documents.", task.getException());
                        }
                        if (puertaAbierta[0]){
                            enviarMensaje(null);
                        }
                    }
                });

    }

    // Se ejecuta cuando completa la entrega
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }


    // Abre la cerradura ( publica en el topic cerradura )
    public void enviarMensaje(View view){
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "cerradura ON");
            MqttMessage message = new MqttMessage("cerradura ON".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(Mqtt.topicRoot+"cerradura", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }


}

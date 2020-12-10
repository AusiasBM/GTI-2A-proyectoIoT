package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.comun.Imagen;
import com.example.comun.Mqtt;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static MqttClient client = null;
    List<Taquilla> taquillas = new ArrayList<>();
    RecyclerView recycler;

    //Càmara
    private DoorbellCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private Handler temporizadorHandler = new Handler();


    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //Subscipción topic alarma
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"alarma");
            client.subscribe(topicRoot+"alarma", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }



        //Subscipción topic sonoff
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot+"cmnd");
            client.subscribe(topicRoot+"cerradura/POWER", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        recycler = findViewById(R.id.recyclerId);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)); // Lista de tipo vertical

        getTaquillasEstant(0); // Llenamos el array


        //Camara
        //Creamos handlers y les asociamos un hilo para la cámara
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

    }

    public void getTaquillasEstant(int estant){

        db.collection("estaciones/" + estant + "/taquillas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(Mqtt.TAG, document.getId() + " => " + document.getData());
                                taquillas.add(new Taquilla(
                                        Integer.parseInt(document.getData().get("id").toString()),
                                        Boolean.parseBoolean(document.getData().get("cargaPatinete").toString()),
                                        document.getData().get("idUsuario").toString(),
                                        Boolean.parseBoolean(document.getData().get("ocupada").toString()),
                                        Boolean.parseBoolean(document.getData().get("patinNuestro").toString()),
                                        Boolean.parseBoolean(document.getData().get("puertaAbierta").toString())
                                ));
                            }

                            AdapterDatos adapter = new AdapterDatos(taquillas);

                            adapter.setOnItemClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int pos = recycler.getChildAdapterPosition(v);
                                    Intent i = new Intent(v.getContext(), MenuTaquilla.class);
                                    i.putExtra("pos", pos);
                                    startActivity(i);
                                }
                            });

                            recycler.setAdapter(adapter);
                        } else {
                            Log.w(Mqtt.TAG, "Error getting documents.", task.getException());
                        }


                    }
                });

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
        if (topic.equals(topicRoot+"alarma")){

            Log.i(Mqtt.TAG, "Publicando mensaje: " + "ALARMA ON");
            //El Código para programar la cámara ha sido recogido en una clase
            mCamera = DoorbellCamera.getInstance();
            mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);
            temporizadorHandler.postDelayed(tomaFoto, 3 * 1000); //llamamos en 3 seg.
        }

        if(topic.equals(topicRoot+"POWER")){
            sonoff(payload);
        }

    }

    private void sonoff(final String payload) {
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("keloke", payload);
                            // Actualiza el estado de la puerta en fireStore
                            if (payload.equals("ON")){
                                db.collection("estaciones/0/taquillas/")
                                        .document("0")
                                        .update("cargaPatinete", true);
                            }else if (payload.equals("OFF")){
                                db.collection("estaciones/0/taquillas/")
                                        .document("0")
                                        .update("cargaPatinete", false);
                            }

                        } else {
                            Log.w(Mqtt.TAG, "Error getting documents.", task.getException());
                        }

                    }
                });

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
    public static void enviarMensaje(View view){
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


    //CODI CAMARA


    //Mètodes per a la càmara
    private Runnable tomaFoto = new Runnable() {
        @Override public void run() {
            mCamera.takePicture();
            //temporizadorHandler.postDelayed(tomaFoto, 60 * 1000);
            //Programamos siguiente llamada dentro de 60 segundos
        }
    };
    private ImageReader.OnImageAvailableListener
            mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();
                    onPictureTaken(imageBytes);
                }
            };

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
                    String nombreFichero = UUID.randomUUID().toString();
                    subirBytes(imageBytes, "imagenesSeguridad/"+nombreFichero);
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    ImageView imageView = findViewById(R.id.imageView2);
                    imageView.setImageBitmap(bitmap);
                }
            });
        }
    }
    private void subirBytes(final byte[] bytes, String referencia) {
        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();

        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putBytes(bytes);
        Task<Uri> urlTask = uploadTask.continueWithTask(new
                Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override public Task<Uri> then(@NonNull
                                                            Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) throw task.getException();
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("Almacenamiento", "URL: " + downloadUri.toString());
                    registrarImagen("Subida por R.P.", downloadUri.toString());
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo bytes");
                }
            }
        });
    }

    static void registrarImagen(String titulo, String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Imagen imagen = new Imagen(titulo, url);
        db.collection("imagenesSeguridad").document().set(imagen);
    }
}

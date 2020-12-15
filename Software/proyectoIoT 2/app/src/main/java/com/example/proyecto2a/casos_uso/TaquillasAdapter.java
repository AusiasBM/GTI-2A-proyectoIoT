package com.example.proyecto2a.casos_uso;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Mqtt;
import com.example.proyecto2a.presentacion.MainActivity;

import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.example.proyecto2a.presentacion.ResActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static android.content.Context.NOTIFICATION_SERVICE;

public class TaquillasAdapter extends FirestoreRecyclerAdapter<Taquilla, TaquillasAdapter.Viewholder> implements View.OnClickListener {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TaquillasAdapter(@NonNull FirestoreRecyclerOptions<Taquilla> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Taquilla taquilla) {

        holder.setOnclickListeners(taquilla.getEstant(), taquilla.getId(), taquilla.isCargaPatinete(), taquilla.isAlquilada());
        if (taquilla.isPatinNuestro()) {
            holder.textViewNombre.setText("Patinete " + taquilla.getId());

        } else {
            holder.textViewNombre.setText("Taquilla " + taquilla.getId());

        }

        if (taquilla.isAlquilada()) {
            holder.boton.setVisibility(View.GONE);
            holder.botonAlquila.setVisibility(View.VISIBLE);
            holder.botoncancelares.setVisibility(View.VISIBLE);

        }

        if (taquilla.isOcupada()) {
            holder.boton.setVisibility(View.GONE);
            holder.botonAlquila.setVisibility(View.GONE);
            holder.botoncancelares.setVisibility(View.GONE);
            holder.boton2.setVisibility(View.VISIBLE);
            holder.botoncancela.setVisibility(View.VISIBLE);
            holder.enchufe.setVisibility(View.VISIBLE);
            if (taquilla.isCargaPatinete()) {
                holder.enchufe.setImageResource(R.drawable.enchufe);
            } else {
                holder.enchufe.setImageResource(R.drawable.enchufe_no);
            }

        } else {
            holder.boton2.setVisibility(View.GONE);
            holder.botoncancela.setVisibility(View.GONE);
            holder.enchufe.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onClick(View v) {

    }


    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FirebaseAuth firebaseAuth;
        private FirebaseAuth.AuthStateListener firebaseAuthListener;
        private FirebaseUser user;
        public String estant;
        public String id;
        public boolean carga;
        public boolean alquilada;
        public String ide;

        private NotificationManager notificationManager;
        static final String CANAL_ID = "mi_canal";
        static final int NOTIFICACION_ID = 1;

        public FirebaseFirestore db = FirebaseFirestore.getInstance();
        public MqttClient client = null;
        TextView textViewNombre;
        Button boton;
        Button boton2;
        Button botonAlquila;
        Button botoncancela;
        Button botoncancelares;
        ImageView enchufe;
        Context context;

        public Viewholder(@NonNull View itemView) {

            super(itemView);
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        ide = "kk";
                    } else {
                        ide = user.getUid();
                    }
                }
            };
            firebaseAuth.addAuthStateListener(firebaseAuthListener);
            context = itemView.getContext();
            textViewNombre = itemView.findViewById(R.id.nombre);
            boton = itemView.findViewById(R.id.bt_reserva);
            boton2 = itemView.findViewById(R.id.bt_abrir);
            botonAlquila = itemView.findViewById(R.id.bt_alquila);
            botoncancela = itemView.findViewById(R.id.buttonCan);
            botoncancelares = itemView.findViewById(R.id.buttonCanReserva);
            enchufe = itemView.findViewById(R.id.imagenchufe);

        }

        public void setOnclickListeners(String estant, String id, boolean carga, boolean alquilada) {
            this.estant = estant;
            this.carga = carga;
            this.id = id;
            this.alquilada = alquilada;
            MenuDialogActivity m = new MenuDialogActivity();
            boton.setOnClickListener(this);
            boton2.setOnClickListener(this);
            botoncancela.setOnClickListener(this);
            botoncancelares.setOnClickListener(this);
            enchufe.setOnClickListener(this);
            botonAlquila.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_reserva:
                    //Intent intent = new Intent(context, MensajeActivity.class);
                    //context.startActivity(intent);
                    String nombre = (String) textViewNombre.getText();
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
                            context.getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(
                                CANAL_ID, "Mis Notificaciones",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationChannel.setDescription("Descripcion del canal");
                        notificationManager.createNotificationChannel(notificationChannel);
                    }
                    NotificationCompat.Builder notificacion =
                            new NotificationCompat.Builder(context, CANAL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Taquilla reservada")
                                    .setContentText("Has reservado una taquilla");
                    //Llançar l'aplicació des de la notificació
                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                            context, 0, new Intent(context, ResActivity.class), 0);
                    notificacion.setContentIntent(intencionPendiente);
                    //Para lanzar la notificación
                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                    break;
                case R.id.bt_abrir:
                    abreTaquilla();
                    break;
                case R.id.imagenchufe:
                    enchufa(v);
                    break;
                case R.id.buttonCan:
                    DocumentReference taki = db.collection("estaciones").document(estant).collection("taquillas").document(id);
                    taki.update("idUsuario", "").addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    taki.update("ocupada", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    taki.update("alquilada", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    break;
                case R.id.bt_alquila:

                    DocumentReference taquilla = db.collection("estaciones").document(estant).collection("taquillas").document(id);
                    taquilla.update("idUsuario", ide).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    taquilla.update("ocupada", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    break;
                case R.id.buttonCanReserva:
                    DocumentReference document = db.collection("estaciones").document(estant).collection("taquillas").document(id);
                    document.update("idUsuario", "").addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    document.update("alquilada", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    break;
            }
        }

        public void abreTaquilla() {

            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }

            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }

            try {
                Log.i(Mqtt.TAG, "Publicando mensaje: " + "cerradura ON");
                MqttMessage message = new MqttMessage("cerradura ON".getBytes());
                message.setQos(Mqtt.qos);
                message.setRetained(false);
                client.publish(Mqtt.topicRoot + "cerradura", message);

            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al publicar.", e);

            }
        }

        public void enchufa(View v) {

            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }

            try {
                Log.i(Mqtt.TAG, "Publicando mensaje: " + "power OFF");
                //MqttMessage message = new MqttMessage("toggle".getBytes());
                Log.i(Mqtt.TAG, "Publicando mensaje: " + "power Toggle");
                MqttMessage message = new MqttMessage("TOGGLE".getBytes());
                message.setQos(Mqtt.qos);
                message.setRetained(false);
                client.publish(Mqtt.topicRoot + "cerradura/cmnd/power", message);
            } catch (MqttException e) {
                e.printStackTrace();
            }

/*
            if (carga){

                try {


                    DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
                    taq.update("cargaPatinete", false).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ocupada", "DocumentSnapshot successfully updated!");
                        }
                    });
                    Toast.makeText(v.getContext(), "Carga apagada", Toast.LENGTH_SHORT);




                } catch (Exception e) {
                    Log.e(Mqtt.TAG, "Error al publicar.", e);
                    Toast.makeText(v.getContext(), "Problema al cargar", Toast.LENGTH_SHORT);
                }
            }else {

                try {





                    DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
                    taq.update("cargaPatinete", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ocupada", "DocumentSnapshot successfully updated!");
                        }
                    });
                    Toast.makeText(v.getContext(), "Patinete cargando", Toast.LENGTH_SHORT);




                } catch (Exception e) {
                    Log.e(Mqtt.TAG, "Error al publicar.", e);
                    Toast.makeText(v.getContext(), "Problema al cargar", Toast.LENGTH_SHORT);
                }
            }

        }*/

        }
    }

}

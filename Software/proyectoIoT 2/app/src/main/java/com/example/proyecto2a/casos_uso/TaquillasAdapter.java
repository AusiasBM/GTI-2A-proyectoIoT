package com.example.proyecto2a.casos_uso;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Mqtt;
import com.example.proyecto2a.presentacion.MainActivity;
import com.example.proyecto2a.presentacion.MensajeActivity;
import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

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

        holder.setOnclickListeners(taquilla.getEstant(), taquilla.getId());
        if (taquilla.isPatinNuestro()) {
            holder.textViewNombre.setText("Patinete " + taquilla.getId());

        } else {
            holder.textViewNombre.setText("Taquilla " + taquilla.getId());

        }

        if (taquilla.isOcupada()) {
            holder.boton.setVisibility(View.GONE);
            holder.boton2.setVisibility(View.VISIBLE);

        } else {
            holder.boton2.setVisibility(View.GONE);
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
        public String ide;

        public FirebaseFirestore db = FirebaseFirestore.getInstance();
        public MqttClient client = null;
        TextView textViewNombre;
        Button boton;
        Button boton2;
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
            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }
        }

        public void setOnclickListeners(String estant, String id) {
            this.estant = estant;
            this.id = id;
            MenuDialogActivity m = new MenuDialogActivity();
            boton.setOnClickListener(this);
            boton2.setOnClickListener(this);
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
                    taq.update("ocupada", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                case R.id.bt_abrir:
                    abreTaquilla();
                    break;
            }
        }

        public void abreTaquilla() {
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
    }

}

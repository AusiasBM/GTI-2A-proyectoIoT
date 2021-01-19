package com.example.proyecto2a.casos_uso;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Mqtt;
import com.example.proyecto2a.modelo.Alquiler;

import com.example.proyecto2a.modelo.DatosAlquiler;
import com.example.proyecto2a.modelo.Taquilla;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.example.proyecto2a.presentacion.ServicioReservaAlquilerTaquilla;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class TaquillasAdapter extends FirestoreRecyclerAdapter<Taquilla, TaquillasAdapter.Viewholder> implements View.OnClickListener {
    private String ubicacion;
    Activity activity;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TaquillasAdapter(@NonNull FirestoreRecyclerOptions<Taquilla> options, Activity activity, String ubicacion) {
        super(options);
        this.activity = activity;
        this.ubicacion = ubicacion;
    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Taquilla taquilla) {

        holder.setOnclickListeners(taquilla.getEstant(), taquilla.getId(), taquilla.isCargaPatinete(), taquilla.isReservada(), ubicacion);

        holder.textViewNombre.setText("Taquilla " + taquilla.getId());

        if (taquilla.isReservada()) {
            holder.boton.setVisibility(View.GONE);
            holder.botonAlquila.setVisibility(View.VISIBLE);
            holder.botoncancelares.setVisibility(View.VISIBLE);
        }

        if (taquilla.isAlquilada()) {
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
        public String ide;
        public boolean carga;
        public boolean alquilada;

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
        String correo;
        private Intent i;

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
                        correo = user.getEmail();
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
            i = new Intent(context, ServicioReservaAlquilerTaquilla.class);




        }

        public void setOnclickListeners(String estant, String id, boolean carga, boolean alquilada, String ubicacion) {
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

            i.putExtra("estant", estant);
            i.putExtra("id", id);
            i.putExtra("ubicacion", ubicacion);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_reserva:
                    comprovarTaquillaPatinReservado();
                    break;

                case R.id.bt_abrir:
                    abreTaquilla();
                    break;

                case R.id.imagenchufe:
                    enchufa(v);
                    break;

                case R.id.buttonCan:
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View view = inflater.inflate(R.layout.dialog_reservar_fin,null);
                Button acceptButton= view.findViewById(R.id.btn_si);
                final Button cancelButton = view.findViewById(R.id.btn_no);
                final android.app.AlertDialog alertDialog=new android.app.AlertDialog.Builder(context)
                        .setView(view)
                        .create();
                alertDialog.show();
                acceptButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Metodo que comprueba que la taquilla está cerrada (y sin nada dentro)
                        //Si la puerta está cerrada pasará al método finAlquiler() y al finContadorAlquiler()
                        //Sino, mostrará un AlertDialog diciendo que cierre la puerta
                        taquillaVaciaCerrada();
                        alertDialog.cancel();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                break;

                case R.id.bt_alquila:

                final LayoutInflater inf = LayoutInflater.from(context);
                final View view1 = inf.inflate(R.layout.dialog_reservar,null);
                Button accept= view1.findViewById(R.id.btn_si);
                final Button cancel = view1.findViewById(R.id.btn_no);
                final android.app.AlertDialog alertDialog1=new android.app.AlertDialog.Builder(context)
                        .setView(view1)
                        .create();
                alertDialog1.show();
                accept.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        i.putExtra("ide", ide);
                        i.putExtra("flagReserva", false);
                        context.startService(i);
                        alquilar();
                        alertDialog1.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        alertDialog1.cancel();
                    }
                });
                break;

                case R.id.buttonCanReserva:
                    context.stopService(i);
                    cancelarReserva();

                    break;
            }
        }




        //*****************************************************************************
        //Lógica de reservar
        //*****************************************************************************

        //Comprovar si el usuario ya tiene una taquilla o patín reservado. En caso negativo en los dos campos,
        // el usuario podrá reservar una taquilla.
        // Sirve para evitar que un usuario pueda reservar todas las taquillas.
        private void comprovarTaquillaPatinReservado(){

            //db.collection("usuarios").document(ide).update("reservaAlquiler", false);
            db.collection("usuarios").document(ide).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        boolean reservaAlquiler= task.getResult().getBoolean("reservaAlquiler");
                        boolean reservaAlquilerPatin= task.getResult().getBoolean("reservaAlquilerPatin");

                        if (reservaAlquiler == false && reservaAlquilerPatin == false){
                            //Reservamos la taquilla
                            reservar();

                            //Lanzamos el servicio en primer plano
                            i.putExtra("ide", ide);
                            i.putExtra("flagReserva", true);
                            context.startService(i);
                        }else{
                            //En caso de que ya haya reservado/alquilado algo, aparecerá un Alert informando de
                            // por qué no puede reservar
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Error al reservar la taquilla");
                            builder.setMessage("Ya tiene una taquilla o patín reservado/alquilado");
                            builder.setPositiveButton("Aceptar", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            });
        }

        private void reservar(){

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
            taq.update("reservada", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            db.collection("usuarios").document(ide).update("reservaAlquiler", true);

            //Enviar mensage MQTT a la taquilla para que inicie la cuenta de tiempo que puede estar reservada
            // Durante ese tiempo la taquilla estará esperando otro MQTT confirmando el alquiler o la cancelación de la resrva.
            // En caso de expirar el tiempo, enviará otro MQTT para que la taquilla quede liberada
            contadorTiempoReserva();
        }

        private void cancelarReserva(){
            //Parar el contador de tiempo de reserva de la taquilla
            pararContadorTiempoReserva();

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
            document.update("reservada", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            db.collection("usuarios").document(ide).update("reservaAlquiler", false);
            db.collection("usuarios").document(ide).update("datos", new DatosAlquiler());
        }


        //*******************************Mensages MQTT**********************************************
        public void contadorTiempoReserva(){
            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }
            try {
                MqttMessage message = new MqttMessage("inicio reserva".getBytes());
                message.setQos(Mqtt.qos);
                message.setRetained(false);
                client.publish(Mqtt.topicRoot + "tiempoReserva", message);
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al publicar.", e);
            }
        }

        public void pararContadorTiempoReserva(){
            try {
                Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
                client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                        new MemoryPersistence());
                client.connect();
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al conectar.", e);
            }
            try {
                MqttMessage message = new MqttMessage("parar".getBytes());
                message.setQos(Mqtt.qos);
                message.setRetained(false);
                client.publish(Mqtt.topicRoot + "tiempoReserva", message);
            } catch (MqttException e) {
                Log.e(Mqtt.TAG, "Error al publicar.", e);
            }
        }


        //*****************************************************************************
        //Lógica de alquilar
        //*****************************************************************************
        private void alquilar(){
            //Primero, parar el contador de tiempo de la taquilla
            pararContadorTiempoReserva();

            //Registrar alquiler de la taquilla
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
            taquilla.update("alquilada", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            //Inicio de la cuenta de tiempo de la taquilla alquilada
            inicioContadorAlquilerTaquilla();
        }

        //Contador del tiempo alquilada para después aplicar los precios
        private void inicioContadorAlquilerTaquilla(){
            //Inicio lógica de alquiler
            db.collection("estaciones").document(estant).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task){
                    if (task.isSuccessful()) {
                        String ubicacion = task.getResult().getString("ubicacion");
                        Alquiler a = new Alquiler(ide, correo, ubicacion, estant, id, false);
                        db.collection("registrosAlquiler").document(a.getFechaInicioAlquiler().toString()).set(a);
                    } else {
                        Log.e("Firestore", "Error al leer", task.getException());
                    }
                }
            });
        }

        //Finalizar el alquiler de la taquilla
        private void finAlquiler(){
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

            taki.update("reservada", false).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            db.collection("usuarios").document(ide).update("reservaAlquiler", false);
            db.collection("usuarios").document(ide).update("datos", new DatosAlquiler());

            //Fin de la cuenta de tiempo de la taquilla alquilada
            finContadorAlquiler();
        }

        //Finalizar contador del tiempo alquilada i calcular el importe
        private void finContadorAlquiler(){
            //Fin de la lógica de alquiler
            Query query = db.collection("registrosAlquiler").whereEqualTo("uId", ide)
                    .orderBy("fechaInicioAlquiler", Query.Direction.DESCENDING ). limit(1);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("Prova10", "");
                                //Obtenció de cada estació de su ubicación y su geoposición
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Prova20", "");
                                    Alquiler a = document.toObject(Alquiler.class);
                                    a.calcularImporteTotal();
                                    db.collection("registrosAlquiler").document(a.getFechaInicioAlquiler().toString()).set(a);
                                }
                            }
                        }
                    });
        }

        //Comprobar que la taquilla que va a dejar de estar alquilada esté cerrada y vacía
        private void taquillaVaciaCerrada(){

            DocumentReference document = db.collection("estaciones").document(estant).collection("taquillas").document(id);
            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task){
                    if (task.isSuccessful()) {
                        boolean puertaAbierta = task.getResult().getBoolean("puertaAbierta");
                        boolean ocupada = task.getResult().getBoolean("ocupada");
                        Log.d("puertaAbierta", puertaAbierta+"");
                        if (puertaAbierta==false && ocupada == false){
                            //Calcular el tiempo alquilada i el importe correspondiente
                            finAlquiler();

                            //Finalizar el servicio
                            context.stopService(i);
                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Error al finalizar el alquiler");
                            builder.setMessage("Por favor, comprueba que no haya nada dentro de la taquilla y la puerta esté cerrada.");
                            builder.setPositiveButton("Aceptar", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                }
            });
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
        }
    }

}

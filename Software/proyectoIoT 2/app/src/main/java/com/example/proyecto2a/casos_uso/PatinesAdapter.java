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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Mqtt;
import com.example.proyecto2a.modelo.AlquilerPatin;
import com.example.proyecto2a.modelo.AlquilerTaquilla;

import com.example.proyecto2a.modelo.DatosAlquiler;
import com.example.proyecto2a.modelo.Taquilla;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.example.proyecto2a.presentacion.ServicioReservaAlquilerPatinete;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;


public class PatinesAdapter extends FirestoreRecyclerAdapter<Taquilla, PatinesAdapter.Viewholder> implements View.OnClickListener {
    private String ubicacion;
    Activity activity;
    private String idUser;
    private Usuario usuario = new Usuario();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PatinesAdapter(@NonNull FirestoreRecyclerOptions<Taquilla> options, Activity activity, String ubicacion, String idUser) {
        super(options);
        this.activity = activity;
        this.ubicacion = ubicacion;
        this.idUser = idUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull final Viewholder holder, final int position, @NonNull final Taquilla taquilla) {

        holder.setOnclickListeners(taquilla.getEstant(), taquilla.getId(), taquilla.isCargaPatinete(), taquilla.isReservada(), ubicacion);

        holder.textViewNombre.setText("Patinete " + taquilla.getId());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef =  db.collection("usuarios").document(idUser);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    usuario.setReservaAlquilerPatin((Boolean) snapshot.getData().get("reservaAlquilerPatin"));
                    boolean flagReserva = ((Boolean) ((HashMap) snapshot.getData().get("datos")).get("flagReserva"));
                    Log.d("TAG", " 123: " + usuario.isReservaAlquilerPatin() + flagReserva);
                    visualizarBotones( holder,  position, taquilla, flagReserva, usuario.isReservaAlquilerPatin());
                } else {
                    Log.d("TAG", " 987: null");
                }
            }
        });

    }

    public void visualizarBotones(@NonNull Viewholder holder, int position,
                                  @NonNull Taquilla taquilla, boolean isReservado, boolean isAlquilado){
        if(isAlquilado == true){
            Log.d("a", "USUARIO " + idUser);
            //Patinete reservado
            if(!taquilla.isEstacionFinal()){
                if (taquilla.isReservada() && !taquilla.isAlquilada()) {
                    holder.reserva.setVisibility(View.GONE);
                    holder.botonAlquila.setVisibility(View.VISIBLE);
                    holder.abrir.setVisibility(View.GONE);
                    holder.botoncancelaReserva.setVisibility(View.VISIBLE);
                    holder.botoncancela.setVisibility(View.GONE);

                }else if ((taquilla.isReservada() && taquilla.isAlquilada())){
                    holder.reserva.setVisibility(View.GONE);
                    holder.botonAlquila.setVisibility(View.GONE);
                    holder.abrir.setVisibility(View.VISIBLE);
                    holder.botoncancela.setVisibility(View.VISIBLE);
                    holder.botoncancelaReserva.setVisibility(View.GONE);
                }
            }
            else{
                if(isReservado == true){
                    holder.reserva.setVisibility(View.GONE);
                    holder.botonAlquila.setVisibility(View.GONE);
                    holder.botoncancelaReserva.setVisibility(View.GONE);
                    holder.abrir.setVisibility(View.GONE);
                    holder.botoncancela.setVisibility(View.GONE);
                    holder.enchufe.setVisibility(View.GONE);
                }else{
                    holder.reserva.setVisibility(View.GONE);
                    holder.botonAlquila.setVisibility(View.GONE);
                    holder.botoncancelaReserva.setVisibility(View.GONE);
                    holder.abrir.setVisibility(View.VISIBLE);
                    holder.botoncancela.setVisibility(View.VISIBLE);
                    holder.enchufe.setVisibility(View.GONE);
                }

            }
        }else {
            //Para dejar patinete
            if (taquilla.isEstacionFinal() ) {
                holder.reserva.setVisibility(View.GONE);
                holder.botonAlquila.setVisibility(View.GONE);
                holder.botoncancelaReserva.setVisibility(View.GONE);
                holder.abrir.setVisibility(View.GONE);
                holder.botoncancela.setVisibility(View.GONE);
                holder.enchufe.setVisibility(View.GONE);
            }else{
                holder.reserva.setVisibility(View.VISIBLE);
                holder.botonAlquila.setVisibility(View.GONE);
                holder.botoncancelaReserva.setVisibility(View.GONE);
                holder.abrir.setVisibility(View.GONE);
                holder.botoncancela.setVisibility(View.GONE);
                holder.enchufe.setVisibility(View.GONE);
            }
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
        public String idUser;
        public boolean carga;
        public boolean alquilada;

        public FirebaseFirestore db = FirebaseFirestore.getInstance();
        public MqttClient client = null;
        TextView textViewNombre;
        Button reserva;
        Button abrir;
        Button botonAlquila;
        Button botoncancela;
        Button botoncancelaReserva;
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
                        idUser = "kk";
                    } else {
                        idUser = user.getUid();
                        correo = user.getEmail();
                    }
                }
            };

            firebaseAuth.addAuthStateListener(firebaseAuthListener);
            context = itemView.getContext();
            textViewNombre = itemView.findViewById(R.id.nombre);
            reserva = itemView.findViewById(R.id.bt_reserva);
            abrir = itemView.findViewById(R.id.bt_abrir);
            botonAlquila = itemView.findViewById(R.id.bt_alquila);
            botoncancela = itemView.findViewById(R.id.buttonCan);
            botoncancelaReserva = itemView.findViewById(R.id.buttonCanReserva);
            enchufe = itemView.findViewById(R.id.imagenchufe);
            i = new Intent(context, ServicioReservaAlquilerPatinete.class);




        }

        public void setOnclickListeners(String estant, String id, boolean carga, boolean alquilada, String ubicacion) {
            this.estant = estant;
            this.carga = carga;
            this.id = id;
            this.alquilada = alquilada;
            MenuDialogActivity m = new MenuDialogActivity();
            reserva.setOnClickListener(this);
            abrir.setOnClickListener(this);
            botoncancela.setOnClickListener(this);
            botoncancelaReserva.setOnClickListener(this);
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
                    db.collection("usuarios").document(idUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                boolean reservaAlquilerPatin= task.getResult().getBoolean("reservaAlquilerPatin");
                                HashMap reserva = (HashMap) task.getResult().get("datos");
                                boolean flagReserva = (boolean) reserva.get("flagReserva");
                                //Si tiene alquilado/reservado un patín y el flagReserva es false (tiene alquilado el patín)
                                if (reservaAlquilerPatin == true && flagReserva == false){
                                    abreTaquilla();
                                }else{
                                    //En caso de que ya haya reservado/alquilado algo, aparecerá un Alert informando de
                                    // por qué no puede reservar
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Error al abrir la taquilla");
                                    builder.setMessage("No puede abrir la taquilla porque no tiene un patín alquilado.");
                                    builder.setPositiveButton("Aceptar", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                }
                            }
                        }
                    });


                    break;

                case R.id.imagenchufe:
                    enchufa(v);
                    break;

                case R.id.buttonCan:
                    AlertDialog.Builder builderFin = new AlertDialog.Builder(context);
                    builderFin.setTitle("Fin alquiler de patinete");
                    builderFin.setMessage("¿Desea finalizar el alquiler del patinete?");

                    builderFin.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Metodo que comprueba que la taquilla está cerrada (y sin nada dentro)
                            //Si la puerta está cerrada pasará al método finAlquiler() y al finContadorAlquiler()
                            //Sino, mostrará un AlertDialog diciendo que cierre la puerta
                            taquillaOcupadaCerrada();

                        }
                    });
                    builderFin.setNegativeButton("No", null);

                    AlertDialog dialogFin = builderFin.create();
                    dialogFin.show();
                    break;

                case R.id.bt_alquila:
                    AlertDialog.Builder builderInicio = new AlertDialog.Builder(context);
                    builderInicio.setTitle("Inicio alquiler de patinete");
                    builderInicio.setMessage("¿Desea alquilar el patinete? Si acepta, dará comienzo al contador para " +
                            "después aplicar los cargos correspondientes");

                    builderInicio.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            i.putExtra("ide", idUser);
                            i.putExtra("flagReserva", false);
                            context.stopService(i);
                            context.startService(i);
                            alquilar();
                        }
                    });
                    builderInicio.setNegativeButton("No", null);

                    AlertDialog dialogInicio = builderInicio.create();
                    dialogInicio.show();
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
            db.collection("usuarios").document(idUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        boolean reservaAlquiler= task.getResult().getBoolean("reservaAlquiler");
                        boolean reservaAlquilerPatin= task.getResult().getBoolean("reservaAlquilerPatin");

                        if (reservaAlquiler == false && reservaAlquilerPatin == false){
                            //Reservamos la taquilla
                            reservar();

                            //Lanzamos el servicio en primer plano
                            i.putExtra("ide", idUser);
                            i.putExtra("flagReserva", true);
                            context.startService(i);
                        }else{
                            //En caso de que ya haya reservado/alquilado algo, aparecerá un Alert informando de
                            // por qué no puede reservar
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Error al reservar el patinete");
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
            taq.update("idUsuario", idUser);
            taq.update("reservada", true);

            db.collection("usuarios").document(idUser).update("reservaAlquilerPatin", true);

            //Enviar mensage MQTT a la taquilla para que inicie la cuenta de tiempo que puede estar reservada
            // Durante ese tiempo la taquilla estará esperando otro MQTT confirmando el alquiler o la cancelación de la resrva.
            // En caso de expirar el tiempo, enviará otro MQTT para que la taquilla quede liberada
            contadorTiempoReserva();
        }

        private void cancelarReserva(){
            //Parar el contador de tiempo de reserva de la taquilla
            pararContadorTiempoReserva();

            DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
            taq.update("idUsuario", "");
            taq.update("reservada", false);

            db.collection("usuarios").document(idUser).update("reservaAlquilerPatin", false);
            db.collection("usuarios").document(idUser).update("datos", new DatosAlquiler());
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
            DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
           // taq.update("reservada", false);
            taq.update("alquilada", true);

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
                        AlquilerPatin a = new AlquilerPatin(idUser, correo,  ubicacion);
                        db.collection("registrosAlquiler").document(String.valueOf(a.getFechaInicioAlquiler())).set(a);
                    } else {
                        Log.e("Firestore", "Error al leer", task.getException());
                    }
                }
            });
        }

        //Finalizar el alquiler de la taquilla
        private void finAlquiler(){
            DocumentReference taki = db.collection("estaciones").document(estant).collection("taquillas").document(id);
            taki.update("idUsuario", "");
            taki.update("alquilada", false);
            taki.update("reservada", false);

            db.collection("usuarios").document(idUser).update("reservaAlquilerPatin", false);
            db.collection("usuarios").document(idUser).update("datos", new DatosAlquiler());

            //Fin de la cuenta de tiempo de la taquilla alquilada
            finContadorAlquiler();
        }

        //Finalizar contador del tiempo alquilada i calcular el importe
        private void finContadorAlquiler(){
            //Fin de la lógica de alquiler
            Query query = db.collection("registrosAlquiler").whereEqualTo("uId", idUser)
                    .orderBy("fechaInicioAlquiler", Query.Direction.DESCENDING ). limit(1);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                //Obtenció de cada estació de su ubicación y su geoposición
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    AlquilerPatin a = document.toObject(AlquilerPatin.class);
                                    a.setUbicacionFinal(ubicacion);
                                    a.calcularImporte();
                                    db.collection("registrosAlquiler").document(String.valueOf(a.getFechaInicioAlquiler())).set(a);
                                }
                            }
                        }
                    });
        }

        //Comprobar que la taquilla que va a dejar de estar alquilada esté cerrada y vacía
        private void taquillaOcupadaCerrada(){

            final DocumentReference document = db.collection("estaciones").document(estant).collection("taquillas").document(id);
            document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task){
                    if (task.isSuccessful()) {
                        boolean puertaAbierta = task.getResult().getBoolean("puertaAbierta");
                        boolean ocupada = task.getResult().getBoolean("ocupada");
                        boolean estacionFinal = task.getResult().getBoolean("estacionFinal");
                        Log.d("puertaAbierta", puertaAbierta+"");
                        if (puertaAbierta==false && ocupada == true){

                            //Calcular el tiempo alquilada i el importe correspondiente
                            finAlquiler();

                            document.update("estacionFinal", false);

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

            //DocumentReference taq = db.collection("estaciones").document(estant).collection("taquillas").document(id);
            //taq.update("idUsuario", ide);
            //taq.update("reservada", true );
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

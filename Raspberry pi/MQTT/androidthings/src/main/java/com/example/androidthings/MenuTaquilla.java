package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;
import static com.example.androidthings.LoginActivity.user;
import static com.example.androidthings.MainActivity.abrirCerradura;
import static com.example.androidthings.MainActivity.apagarEncenderCarga;
import static com.example.androidthings.MainActivity.taquillas;
import static com.example.androidthings.MainActivity.db;

public class MenuTaquilla extends AppCompatActivity {

    TextView tvTituloTaquilla;
    Switch swCarga;
    Button btPuerta;
    DocumentReference docRef;
    int taquilla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_taquilla);
        tvTituloTaquilla = findViewById(R.id.tvTituloTaquilla);
        btPuerta = findViewById(R.id.btPuerta);
        swCarga = findViewById(R.id.swCarga);

        Bundle extras = getIntent().getExtras();
        int pos = extras.getInt("pos", 0);

        taquilla = pos;

        if (taquillas.get(pos).patineteNuestro){
            tvTituloTaquilla.setText("Patinete " + taquillas.get(pos).id);
        }else{
            tvTituloTaquilla.setText("Taquilla " + taquillas.get(pos).id);
        }

        if (taquillas.get(pos).cargarPatinete){
            swCarga.setChecked(true);
        }

        empezarBd();

        docRef = db.collection("estaciones/" + 0 + "/taquillas").document(taquillas.get(pos).id + "");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Escucha", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("Escucha", "Current data: " + snapshot.getData());
                    if (Boolean.parseBoolean(snapshot.getData().get("cargaPatinete").toString())){
                        taquillas.get(taquilla).setCargarPatinete(true);
                        swCarga.setChecked(true);
                    }else{
                        taquillas.get(taquilla).setCargarPatinete(false);
                        swCarga.setChecked(false);
                    }
                    taquillas.get(taquilla).setOcupada(Boolean.parseBoolean(snapshot.getData().get("ocupada").toString()));
                    taquillas.get(taquilla).setPuertaAbierta(Boolean.parseBoolean(snapshot.getData().get("puertaAbierta").toString()));
                    if (Boolean.parseBoolean(snapshot.getData().get("puertaAbierta").toString())){
                        btPuerta.setClickable(false);
                        btPuerta.setBackgroundColor(Color.GRAY);
                    }else{
                        btPuerta.setBackgroundColor(Color.argb(255, 69, 114, 188));
                        btPuerta.setClickable(true);
                    }

                    taquillas.get(taquilla).setIdUsuario(snapshot.getData().get("idUsuario").toString());
                } else {
                    Log.d("Escucha", "Current data: null menu taquilla");
                }
            }
        });

    }

    public void terminarAlquiler(View v){
        if (taquillas.get(taquilla).ocupada && taquillas.get(taquilla).isPuertaAbierta()){
            eleccionOcupada(v);
        }else{
            eleccion(v);
        }

    }

    public void eleccion(final View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finContadorAlquiler();
                terminarBd();
                cerrar(v);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("¿Está seguro de que quiere terminar su alquiler? Esta opción no tiene vuelta atrás.")
                .setTitle("Terminar alquiler");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finContadorAlquiler(){
        //Fin de la lógica de alquiler
        Query query = db.collection("registrosAlquiler").whereEqualTo("uId", user.getuId())
                .orderBy("fechaInicioAlquiler", Query.Direction.DESCENDING ). limit(1);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //Obtenció de cada estació de su ubicación y su geoposición
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                AlquilerTaquilla a = document.toObject(AlquilerTaquilla.class);
                                a.calcularImporteTotal();
                                db.collection("registrosAlquiler").document(a.getFechaInicioAlquiler().toString()).set(a);
                            }
                        }
                    }
                });
    }

    public void eleccionOcupada(final View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                terminarBd();
                cerrar(v);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage("Esta taquilla aun contiene elementos, por la seguridad de los mismos ¿podría comprobar si está vacía?")
                .setTitle("Perdona, se está dejando sus cosas!");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void empezarBd(){

        db.collection("usuarios/")
                .document(user.getuId() + "")
                .update("reservaAlquiler", true);

        Map<String, Object> flagReserva = new HashMap<>();
        flagReserva.put("flagReserva", false);
        flagReserva.put("ubicacionTaquilla", "UPV-EPSG");

        db.collection("usuarios/")
                .document(user.getuId() + "")
                .update("datos", flagReserva);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("alquilada", true);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("reservada", true);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("idUsuario", user.getuId());
        taquillas.get(taquilla).setAlquilada(true);
        taquillas.get(taquilla).setIdUsuario(user.getuId());
    }

    public void terminarBd(){

        db.collection("usuarios/")
                .document(user.getuId() + "")
                .update("reservaAlquiler", false);

        Map<String, Object> flagReserva = new HashMap<>();
        flagReserva.put("flagReserva", false);
        flagReserva.put("ubicacionTaquilla", "");

        db.collection("usuarios/")
                .document(user.getuId() + "")
                .update("datos", flagReserva);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("alquilada", false);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("reservada", false);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("idUsuario", "");

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("cargaPatinete", false);

        db.collection("estaciones/0/taquillas/")
                .document(taquilla + "")
                .update("ocupada", false);

        taquillas.get(taquilla).setAlquilada(false);
        taquillas.get(taquilla).setIdUsuario("");
    }

    public void abrirPuerta(View v){
        abrirCerradura(v);
    }

    public void cargar(View v){
        String mensaje = "";
        if (taquillas.get(taquilla).cargarPatinete){
            mensaje = "¿Estas seguro de apagar el cargador?\nTe podrías quedar tirado.";
        }else{
            mensaje = "¿Estas seguro de encender el cargador?\nEsta acción tiene un coste extra.";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                apagarEncenderCarga();
                if (swCarga.isChecked()){
                    taquillas.get(taquilla).setCargarPatinete(true);
                }else{
                    taquillas.get(taquilla).setCargarPatinete(false);
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (swCarga.isChecked()){
                    swCarga.setChecked(false);
                    taquillas.get(taquilla).setCargarPatinete(false);
                }else{
                    swCarga.setChecked(true);
                    taquillas.get(taquilla).setCargarPatinete(true);
                }
            }
        });
        builder.setMessage(mensaje)
                .setTitle("Cargador");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void cerrar(View v){
        this.finish();
    }
}
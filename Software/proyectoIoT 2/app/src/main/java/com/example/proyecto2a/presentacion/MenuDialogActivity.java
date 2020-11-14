package com.example.proyecto2a.presentacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static android.media.CamcorderProfile.get;
import static java.util.Objects.requireNonNull;

public class MenuDialogActivity<AddMember> extends AppCompatActivity {
    private TextView nombreLugar;
    private TextView patinesText;
    private TextView taquillasText;
    private Spinner spinner;
    private Spinner spinnerPats;
    double latitud;
    String id = "0";
    double longitud;
    int taquillasDisponibles;
    int PatinesDisponibles;
    int patinesDisponibles = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_dialog);
        nombreLugar = (TextView) findViewById(R.id.TituloLugar);
        patinesText = (TextView) findViewById(R.id.PatinesDispo);
        taquillasText = (TextView) findViewById(R.id.taquillasDispo);
        spinner = (Spinner) findViewById(R.id.spinnerTaquillas);
        spinnerPats = (Spinner) findViewById(R.id.spinner2);
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> arrayList2 = new ArrayList<>();
        taquillasDisponibles = 0;
        PatinesDisponibles = 0;
        String nombre = getIntent().getStringExtra("nombre");
        latitud = getIntent().getDoubleExtra("lat", 0);
        longitud = getIntent().getDoubleExtra("long", 0);
        LatLng pos = new LatLng(latitud, longitud);
        nombreLugar.setText(nombre);
        //Consulta a bbdd para cargar las estaciones
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estaciones").whereEqualTo("ubicacion", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = (String) document.getId();
                    }
                } else {
                    finish();
                }
            }
        });

        final ArrayList<Object>[] ListaTaquillas = new ArrayList[1];
        final ArrayList<Object>[] ListaPatines = new ArrayList[1];


        db.collection("estaciones").document(id).collection("taquillas").document("taquillasAlquiler").get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Prova de retorn", "" + ((HashMap) ((ArrayList<Object>) task.getResult().getData().get("taquillas")).get(0)).get("ocupada"));
                                    Log.e("Prova de retorn 22", "" + ((String) ((HashMap) ((ArrayList<Object>) task.getResult().getData().get("taquillas")).get(1)).get("idUsuario")).length());
                                    ListaTaquillas[0] = (ArrayList<Object>) task.getResult().getData().get("taquillas");
                                    for (int i = 0; i < ListaTaquillas[0].size(); i++) {
                                        if (((HashMap) ((ArrayList<Object>) task.getResult().getData().get("taquillas")).get(i)).get("ocupada").equals(false)) {
                                            taquillasDisponibles++;
                                            arrayList.add("Taquilla " + Integer.toString(i));
                                        }
                                    }

                                    taquillasText.setText(Integer.toString(taquillasDisponibles));

                                    //GeoPoint dato2 = task.getResult().getGeoPoint("pos");
                                    //ArrayList<Object> a = new ArrayList<Object>();
                                    // a = dato1.get("lista");
                                    //Log.d("Firestore", "" + ((ArrayList) dato1.get("lista")).size());
                                    //for (int i = 0; i < ((ArrayList) dato1.get("lista")).size();i++){
                                    //Log.d("PROVA" + i,""+ ((HashMap) ((ArrayList) dato1.get("lista")).get(i)).get("pos").getClass());
                                    // ubicacion = ((HashMap) ((ArrayList) dato1.get("lista")).get(i)).get("ubicacion").toString();


                                    // }


                                    //Informacion i= task.getResult().toObject(Informacion.class);
                                    //Log.d("PROVA",""+ a);
                                } else {
                                    Log.e("Firestore", "Error al leer", task.getException());
                                }
                            }

                        });//

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        db.collection("estaciones").document(id).collection("taquillas").document("taquillasAlquilerPatinete").get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    ListaPatines[0] = (ArrayList<Object>) task.getResult().getData().get("taquillas");
                                    for (int i = 0; i < ListaPatines[0].size(); i++) {
                                        if (((HashMap) ((ArrayList<Object>) task.getResult().getData().get("taquillas")).get(i)).get("ocupada").equals(false)) {
                                            patinesDisponibles++;
                                            arrayList2.add("Patinete " + Integer.toString(i));
                                        }
                                    }

                                    patinesText.setText(Integer.toString(patinesDisponibles));

                                } else {
                                    Log.e("Firestore", "Error al leer", task.getException());
                                }
                            }

                        });
        ArrayAdapter<String> arrayAdapterPats = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList2);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPats.setAdapter(arrayAdapterPats);


    }


    public void googleMaps(View view) {
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", latitud, longitud);
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        startActivity(intent);
    }

    public void cerrar(View view) {
        finish();
    }


}

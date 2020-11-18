package com.example.proyecto2a.presentacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.media.CamcorderProfile.get;
import static java.util.Objects.requireNonNull;

public class MenuDialogActivity<AddMember> extends AppCompatActivity {
    private TextView nombreLugar;
    private TextView patinesText;
    private TextView taquillasText;
    private TextView taquillaSel;
    private Spinner spinner;
    private Spinner spinnerPats;
    private Button botonAbre;
    private Button botonReserva;
    private Button botonAlquila;
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
        taquillaSel = (TextView) findViewById(R.id.textView6);
        botonReserva = findViewById(R.id.button2);
        botonReserva.setVisibility(View.VISIBLE);
        botonAlquila = findViewById(R.id.button);
        botonAlquila.setVisibility(View.VISIBLE);
        botonAbre = findViewById(R.id.button3);
        botonAbre.setVisibility(View.GONE);
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


        Query lista = db.collection("estaciones").document(id).collection("taquillas").whereEqualTo("ocupada", false).whereEqualTo("patinNuestro", false);
        final List<String> taquillas = new ArrayList<>();
        taquillas.add("Taquillas disponibles");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, taquillas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        lista.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = "Taquilla "+document.getId();
                        taquillas.add(subject);
                        taquillasDisponibles++;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        taquillasText.setText(Integer.toString(taquillasDisponibles));

        Query listaPatinetes = db.collection("estaciones").document(id).collection("taquillas").whereEqualTo("ocupada", false).whereEqualTo("patinNuestro", true);
        final List<String> patines = new ArrayList<>();
        patines.add("Patines disponibles");
        final ArrayAdapter<String> adaperPatines = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, patines);
        adaperPatines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPats.setAdapter(adaperPatines);
        listaPatinetes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = "Patinete "+document.getId();
                        patines.add(subject);
                        patinesDisponibles++;
                    }
                    adaperPatines.notifyDataSetChanged();
                }
            }
        });

        patinesText.setText(Integer.toString(patinesDisponibles));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
/*
                switch (position) {
                    case 0 :
                        int indzex = s1.getSelectedItemPosition();
                        powerfactorEditText.setVisibility(View.GONE);
                        final  EditText editText = (EditText)findViewById(R.id.voltageEditText);
                        final  EditText editText2 = (EditText)findViewById(R.id.ampEditText);

                    case 1:
                        int index = s1.getSelectedItemPosition();
                        powerfactorEditText.setVisibility(View.VISIBLE);
                        break;

                }
*/

            String opcion = spinner.getSelectedItem().toString();
            if (!opcion.equals("Taquillas disponibles")){
                taquillaSel(opcion);
            }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void taquillaSel(String opcion) {
        botonAbre.setVisibility(View.VISIBLE);
        botonReserva.setVisibility(View.GONE);
        taquillaSel.setText(opcion);
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

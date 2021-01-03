package com.example.proyecto2a.presentacion;

import android.app.FragmentManager;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Mqtt;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.media.CamcorderProfile.get;
import static java.util.Objects.requireNonNull;

public class MenuDialogActivity extends AppCompatActivity {
    private TextView nombreLugar;

    double latitud;
    public String id = "0";
    double longitud;

    public String idUser;
    public static MqttClient client = null;
    private String[] nombres = new String[]{"Taquillas","Patinetes"};

    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_dialog);

       //Consulta a bbdd para cargar las estaciones
        idUser = getIntent().getStringExtra("idUser");
        String nombre = getIntent().getStringExtra("nombre");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estaciones").whereEqualTo("ubicacion", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = document.getId();
                    }
                } else {
                    finish();
                }
            }
        });
        nombreLugar = findViewById(R.id.TituloLugar);

        latitud = getIntent().getDoubleExtra("lat", 0);
        longitud = getIntent().getDoubleExtra("long", 0);
        LatLng pos = new LatLng(latitud, longitud);
        nombreLugar.setText(nombre);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);
                    }
                }
        ).attach();
    }


    public class MiPagerAdapter extends FragmentStateAdapter {
        String nombre = getIntent().getStringExtra("nombre");
        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }
        @Override
        public int getItemCount() {
            return 2;
        }
        @Override @NonNull
        public Fragment createFragment(int position) {

            switch (position) {
                case 0: Fragment fr=new Tab1();
                    Bundle args = new Bundle();
                    args.putString("CID", nombre);
                    args.putString("idUser", idUser);
                    fr.setArguments(args);
                    return fr;
                case 1: Fragment frr=new Tab2();
                    Bundle arrgs = new Bundle();
                    arrgs.putString("CID", nombre);
                    arrgs.putString("idUser", idUser);
                    frr.setArguments(arrgs);
                    return frr;
            }
            return null;
        }
    }

}

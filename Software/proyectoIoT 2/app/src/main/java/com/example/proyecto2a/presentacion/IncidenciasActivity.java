package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.IncidenciasAdapter;
import com.example.proyecto2a.modelo.Incidencia;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class IncidenciasActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    IncidenciasAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    Incidencia incidencia= new Incidencia();
    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);
        ivBack = findViewById(R.id.ivBackIncidencias);
        recyclerView = findViewById(R.id.recyclerViewIncidencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("imagenesSeguridad").orderBy("tiempo", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Incidencia> firestoreRecyclerOptions= new FirestoreRecyclerOptions.Builder<Incidencia>().setQuery(query, Incidencia.class).build();

        adapter = new IncidenciasAdapter(firestoreRecyclerOptions, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncidenciasActivity.this, ResActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
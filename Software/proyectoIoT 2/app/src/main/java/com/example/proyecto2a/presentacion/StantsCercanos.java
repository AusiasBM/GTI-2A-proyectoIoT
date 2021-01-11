package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StantsCercanos extends AppCompatActivity {

    ImageView volver;
    Button abrirStant;
    RecyclerView recyclerView;
    CercanosAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    Usuario usuario = new Usuario();
    Stant stant = new Stant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stants_cercanos);
        volver = findViewById(R.id.ivBackCercano);

        recyclerView = findViewById(R.id.recyclerViewCercanos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query quey = firebaseFirestore.collection("estaciones");
        FirestoreRecyclerOptions<Stant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Stant>().setQuery(quey, Stant.class).build();
        adapter = new CercanosAdapter(firestoreRecyclerOptions);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StantsCercanos.this, ResActivity.class);
                startActivity(intent);
            }
        });
    }

    public void abrirEstacion(View view){
        Intent intent = new Intent(StantsCercanos.this, MenuDialogActivity.class);
        intent.putExtra("idUser", usuario.getuId());
        intent.putExtra("nombre", stant.getUbicacion());
        startActivity(intent);
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
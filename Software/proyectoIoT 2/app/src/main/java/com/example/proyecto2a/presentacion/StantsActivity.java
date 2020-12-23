package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.StantsAdapter;
import com.example.proyecto2a.casos_uso.UsuariosAdapter;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StantsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StantsAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stants);

        recyclerView = findViewById(R.id.recyclerViewStants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("estaciones");

        FirestoreRecyclerOptions<Stant> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Stant>().setQuery(query, Stant.class).build();

        adapter = new StantsAdapter(firestoreRecyclerOptions, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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

    public void volverStants(View view){
        Intent intent = new Intent(this, ResActivity.class);
        startActivity(intent);
    }

    public void añadirStant(View view){
        Toast.makeText(this, "·", Toast.LENGTH_SHORT).show();
    }
}
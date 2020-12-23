package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.UsuariosAdapter;
import com.example.proyecto2a.modelo.Usuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class UsuariosActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UsuariosAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        recyclerView = findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();
        Query query = firebaseFirestore.collection("usuarios");



        FirestoreRecyclerOptions<Usuario> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Usuario>().setQuery(query, Usuario.class).build();

        adapter = new UsuariosAdapter(firestoreRecyclerOptions, this);
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

    public void VolverUsuarios(View view){
        Intent intent = new Intent(this, ResActivity.class);
        startActivity(intent);
    }
}
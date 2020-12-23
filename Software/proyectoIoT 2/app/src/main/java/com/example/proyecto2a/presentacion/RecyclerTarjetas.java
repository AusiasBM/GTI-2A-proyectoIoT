package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.TarjetasAdapter;
import com.example.proyecto2a.modelo.Tarjeta;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RecyclerTarjetas extends AppCompatActivity {

    TextView tvFecha, tvPropietario, tvInfoFecha, tvInfoPropietario;
    View vInfo;
    private ImageView ivVolver;

    RecyclerView rv;
    TarjetasAdapter adapter;
    FirebaseFirestore tFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_tarjetas);
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        ivVolver = (ImageView) findViewById(R.id.ivBackAdd);

        rv.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();

        Query query = tFirestore.collection("tarjetas").whereEqualTo("uID", firebaseAuth.getUid());

        FirestoreRecyclerOptions<Tarjeta> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Tarjeta>().setQuery(query, Tarjeta.class).build();

        adapter = new TarjetasAdapter(firestoreRecyclerOptions, this);
        adapter.notifyDataSetChanged();
        rv.setAdapter(adapter);

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecyclerTarjetas.this, ResActivity.class);
                startActivity(intent);
                finish();
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

    public void añadirTarjeta(View view){
        Intent intent = new Intent(this, InfoPago.class);
        startActivity(intent);
        finish();
    }
}
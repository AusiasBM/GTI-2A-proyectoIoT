package com.example.proyecto2a.presentacion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.Taquilla;
import com.example.proyecto2a.casos_uso.TaquillasAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Tab2 extends Fragment {RecyclerView recyclerView;
    RecyclerView recyclerViewReservas;
    TaquillasAdapter nAdapter;
    TaquillasAdapter mAdapter;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    public String id = "1";
    public String nombre;
    public String idUsuario;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.tab2, container, false);
        Bundle bundle = getArguments();
        nombre = bundle.getString("CID");
        idUsuario = bundle.getString("idUser");
        recyclerView = v.findViewById(R.id.recycler_view_taquillas);
        recyclerView.setLayoutManager(new LinearLayoutManager( this.getContext()));
        //recyclerView.setHasFixedSize(true);
        recyclerViewReservas = v.findViewById(R.id.recycler_view_taquillas_reservadas);
        recyclerViewReservas.setLayoutManager(new LinearLayoutManager( this.getContext()));
        //recyclerViewReservas.setHasFixedSize(true);
        setUpRecyclerView();
        return v;
    }


    public void setUpRecyclerView(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("estaciones").whereEqualTo("ubicacion", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = (String) document.getId();
                        Query lista = mFirestore.collection("estaciones").document(id).collection("taquillas").whereEqualTo("alquilada", true).whereEqualTo("idUsuario", idUsuario).whereEqualTo("patinNuestro", true);
                        FirestoreRecyclerOptions<Taquilla> options = new FirestoreRecyclerOptions.Builder<Taquilla>()
                                .setQuery(lista, Taquilla.class)
                                .build();
                        mAdapter = new TaquillasAdapter(options,getActivity());
                        mAdapter.notifyDataSetChanged();
                        recyclerViewReservas.setAdapter(mAdapter);
                        listenm();
                    }
                }
            }
        });

        db.collection("estaciones").whereEqualTo("ubicacion", nombre).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        id = (String) document.getId();
                        Query lista = mFirestore.collection("estaciones").document(id).collection("taquillas").whereEqualTo("alquilada", false).whereEqualTo("patinNuestro", true);

                        FirestoreRecyclerOptions<Taquilla> options = new FirestoreRecyclerOptions.Builder<Taquilla>()
                                .setQuery(lista, Taquilla.class)
                                .build();

                        nAdapter = new TaquillasAdapter(options,getActivity());

                        nAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(nAdapter);

                        listen();
                    }
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void listen(){
        nAdapter.startListening();
    }

    public void listenm(){
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        nAdapter.stopListening();
        mAdapter.stopListening();
    }
}
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
import com.example.proyecto2a.casos_uso.PatinesAdapter;
import com.example.proyecto2a.modelo.Taquilla;
import com.example.proyecto2a.casos_uso.TaquillasAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Tab2 extends Fragment {
    RecyclerView recyclerViewPatinesDisponibles;
    RecyclerView recyclerViewReservas;
    RecyclerView recyclerViewTaquillasDisponibles;
    PatinesAdapter nAdapter;
    PatinesAdapter mAdapter;
    PatinesAdapter oAdapter;
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
        recyclerViewPatinesDisponibles = v.findViewById(R.id.recycler_view_patines_disponibles);
        recyclerViewPatinesDisponibles.setLayoutManager(new LinearLayoutManager( this.getContext()));
        //recyclerView.setHasFixedSize(true);
        recyclerViewReservas = v.findViewById(R.id.recycler_view_patines_reservados);
        recyclerViewReservas.setLayoutManager(new LinearLayoutManager( this.getContext()));
        //recyclerViewReservas.setHasFixedSize(true);
        recyclerViewTaquillasDisponibles = v.findViewById(R.id.recycler_view_taquillas_disponibles);
        recyclerViewTaquillasDisponibles.setLayoutManager(new LinearLayoutManager( this.getContext()));
        setUpRecyclerView();
        return v;
    }


    public void setUpRecyclerView(){
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("estaciones").whereEqualTo("ubicacion", nombre).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                id = (String) document.getId();
                                Query lista = mFirestore.collection("estaciones").document(id).collection("taquillas")
                                        .whereEqualTo("reservada", true)
                                        .whereEqualTo("idUsuario", idUsuario)
                                        .whereEqualTo("patinNuestro", true)
                                        .whereEqualTo("estacionFinal", false);


                                FirestoreRecyclerOptions<Taquilla> options = new FirestoreRecyclerOptions.Builder<Taquilla>()
                                        .setQuery(lista, Taquilla.class)
                                        .build();
                                oAdapter = new PatinesAdapter(options,getActivity(), nombre,idUsuario);
                                oAdapter.notifyDataSetChanged();
                                recyclerViewReservas.setAdapter(oAdapter);
                                listeno();
                            }
                        }
                    }
                });
                            db.collection("estaciones").whereEqualTo("ubicacion", nombre).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            id = (String) document.getId();
                                            Query lista = mFirestore.collection("estaciones").document(id).collection("taquillas")
                                                    .whereEqualTo("reservada", false)
                                                    .whereEqualTo("idUsuario", "")
                                                    .whereEqualTo("patinNuestro", true)
                                                    .whereEqualTo("estacionFinal", false);


                                            FirestoreRecyclerOptions<Taquilla> options = new FirestoreRecyclerOptions.Builder<Taquilla>()
                                                    .setQuery(lista, Taquilla.class)
                                                    .build();
                                            mAdapter = new PatinesAdapter(options,getActivity(), nombre,idUsuario);
                                            mAdapter.notifyDataSetChanged();
                                            recyclerViewPatinesDisponibles.setAdapter(mAdapter);
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
                                            Query lista = mFirestore.collection("estaciones").document(id)
                                                    .collection("taquillas")
                                                    //.whereEqualTo("reservada", false)
                                                    .whereEqualTo("patinNuestro", true)
                                                    .whereEqualTo("estacionFinal", true);

                        FirestoreRecyclerOptions<Taquilla> options = new FirestoreRecyclerOptions.Builder<Taquilla>()
                                .setQuery(lista, Taquilla.class)
                                .build();

                        nAdapter = new PatinesAdapter(options,getActivity(), nombre,idUsuario);

                        nAdapter.notifyDataSetChanged();
                        recyclerViewTaquillasDisponibles.setAdapter(nAdapter);

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

    public void listeno(){
        oAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        nAdapter.stopListening();
        mAdapter.stopListening();
        oAdapter.stopListening();
    }
}
package com.example.proyecto2a.datos;

import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Tarjeta;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class Stants {
    private static CollectionReference stants;

    public Stants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        stants = db.collection("estaciones");
    }

    //Crear un stant nuevo
    public void actualizarStant(Stant stant) {
        stants.document(stant.getUbicacion()).set(stant);
    }
}

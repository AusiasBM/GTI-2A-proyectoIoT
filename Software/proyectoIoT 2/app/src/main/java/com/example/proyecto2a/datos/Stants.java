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

    public static CollectionReference getStants() {
        return stants;
    }

    //Crear un usuario nuevo
    public void guardarStant(String id, String ubicacion, GeoPoint pos) {
        Stant stant = new Stant();
        stant.setPos(pos);
        stant.setuId(id);
        stant.setUbicacion(ubicacion);
        stants.document(id).set(stant);
    }

    public void actualizarStant(Stant stant) {
        stants.document().set(stant);
    }
}

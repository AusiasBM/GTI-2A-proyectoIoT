package com.example.proyecto2a.datos;

import com.example.proyecto2a.modelo.Tarjeta;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Tarjetas {
    private static CollectionReference tarjetas;

    public Tarjetas(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tarjetas = db.collection("tarjetas");
    }

    public static CollectionReference getTarjetas() {
        return tarjetas;
    }

    //Crear un usuario nuevo
    public void guardarTarjeta(String nombrePropietario, String id, int numTarjeta) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setNombrePropietario(nombrePropietario);
        tarjeta.setuID(id);
        tarjeta.setNumTarjeta(numTarjeta);
        tarjetas.document(id).set(tarjeta);
    }

    public void actualizarTarjeta(Tarjeta tarjeta){
        tarjetas.document().set(tarjeta);
    }
}

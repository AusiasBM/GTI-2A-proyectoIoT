package com.example.proyecto2a.modelo;

import com.google.firebase.firestore.GeoPoint;

public class Stant {
    private String ubicacion, uID;
    private GeoPoint pos;

    public Stant() {
    }

    public Stant(String ubicacion, GeoPoint pos) {
        this.ubicacion = ubicacion;
        this.pos = pos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public GeoPoint getPos() {
        return pos;
    }

    public void setPos(GeoPoint pos) {
        this.pos = pos;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}

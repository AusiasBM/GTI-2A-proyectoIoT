package com.example.proyecto2a.modelo;

import com.google.firebase.firestore.GeoPoint;

public class Stant {
    private String ubicacion, uId;
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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}

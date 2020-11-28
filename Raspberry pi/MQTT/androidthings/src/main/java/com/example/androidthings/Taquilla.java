package com.example.androidthings;

public class Taquilla {

    int id;
    boolean cargarPatinete;
    String idUsuario;
    boolean ocupada;
    boolean patineteNuestro;
    boolean puertaAbierta;

    public Taquilla(int id, boolean cargarPatinete, String idUsuario, boolean ocupada, boolean patinNuestro, boolean puertaAbierta) {
        this.id = id;
        this.cargarPatinete = cargarPatinete;
        this.idUsuario = idUsuario;
        this.ocupada = ocupada;
        this.patineteNuestro = patinNuestro;
        this.puertaAbierta = puertaAbierta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCargarPatinete() {
        return cargarPatinete;
    }

    public void setCargarPatinete(boolean cargarPatinete) {
        this.cargarPatinete = cargarPatinete;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public boolean isPatinNuestro() {
        return patineteNuestro;
    }

    public void setPatinNuestro(boolean patinNuestro) {
        this.patineteNuestro = patinNuestro;
    }

    public boolean getPatinNuestro() {
        return this.patineteNuestro;
    }

    public boolean isPuertaAbierta() {
        return puertaAbierta;
    }

    public void setPuertaAbierta(boolean puertaAbierta) {
        this.puertaAbierta = puertaAbierta;
    }
}

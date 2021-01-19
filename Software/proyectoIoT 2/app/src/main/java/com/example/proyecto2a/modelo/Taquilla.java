package com.example.proyecto2a.modelo;

public class Taquilla {
    private String id;
    private String estant;
    private String idUsuario;
    private boolean cargaPatinete;
    private boolean puertaAbierta;
    private boolean patinNuestro;
    private boolean ocupada;
    private boolean alquilada;
    private boolean reservada;
    private  boolean estacionFinal;



    public Taquilla(){

    }

    public Taquilla(String id, String idUsuario, String estant, boolean cargaPatinete,
                    boolean puertaAbierta, boolean patinNuestro, boolean ocupada, boolean alquilada, boolean estacionFinal) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.cargaPatinete = cargaPatinete;
        this.puertaAbierta = puertaAbierta;
        this.patinNuestro = patinNuestro;
        this.ocupada = ocupada;
        this.estant = estant;
        this.alquilada = alquilada;
        this.estacionFinal = estacionFinal;
    }

    public String getEstant() {
        return estant;
    }

    public void setEstant(String estant) {
        this.estant = estant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isCargaPatinete() {
        return cargaPatinete;
    }

    public void setCargaPatinete(boolean cargaPatinete) {
        this.cargaPatinete = cargaPatinete;
    }

    public boolean isPuertaAbierta() {
        return puertaAbierta;
    }

    public void setPuertaAbierta(boolean puertaAbierta) {
        this.puertaAbierta = puertaAbierta;
    }

    public boolean isPatinNuestro() {
        return patinNuestro;
    }

    public void setPatinNuestro(boolean patinNuestro) {
        this.patinNuestro = patinNuestro;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public boolean isAlquilada() {
        return alquilada;
    }

    public void setAlquilada(boolean alquilada) {
        this.alquilada = alquilada;
    }

    public boolean isReservada() {
        return reservada;
    }

    public void setReservada(boolean reservada) {
        this.reservada = reservada;
    }

    public boolean isEstacionFinal() {
        return estacionFinal;
    }

    public void setEstacionFinal(boolean estacionFinal) {
        this.estacionFinal = estacionFinal;
    }
}

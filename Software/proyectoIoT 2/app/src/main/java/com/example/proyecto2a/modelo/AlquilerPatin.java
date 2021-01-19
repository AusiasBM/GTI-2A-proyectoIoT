package com.example.proyecto2a.modelo;

import static java.lang.System.currentTimeMillis;

public class AlquilerPatin {

    private String uId, correo, ubicacionInicio, ubicacionFinal, tipoAlquiler;
    private long fechaInicioAlquiler, tiempoAlquilada;
    private double importeAlquiler;

    public AlquilerPatin() {
    }

    public AlquilerPatin(String uId, String correo, String ubicacionInicio) {
        this.uId = uId;
        this.correo = correo;
        this.ubicacionInicio = ubicacionInicio;
        this.fechaInicioAlquiler = currentTimeMillis();
        this.tipoAlquiler = "patin";
    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUbicacionInicio() {
        return ubicacionInicio;
    }

    public void setUbicacionInicio(String ubicacionInicio) {
        this.ubicacionInicio = ubicacionInicio;
    }

    public String getUbicacionFinal() {
        return ubicacionFinal;
    }

    public void setUbicacionFinal(String ubicacionFinal) {
        this.ubicacionFinal = ubicacionFinal;
    }

    public long getFechaInicioAlquiler() {
        return fechaInicioAlquiler;
    }

    public void setFechaInicioAlquiler(long fechaInicioAlquiler) {
        this.fechaInicioAlquiler = fechaInicioAlquiler;
    }

    public long getTiempoAlquilada() {
        return tiempoAlquilada;
    }

    public void setTiempoAlquilada(long tiempoAlquilada) {
        this.tiempoAlquilada = tiempoAlquilada;
    }

    public double getImporteAlquiler() {
        return importeAlquiler;
    }

    public void setImporteAlquiler(double importeAlquiler) {
        this.importeAlquiler = importeAlquiler;
    }

    public String getTipoAlquiler() {
        return tipoAlquiler;
    }

    public void setTipoAlquiler(String tipoAlquiler) {
        this.tipoAlquiler = tipoAlquiler;
    }


    private void calcularTiempoAlquiler(){
        this.tiempoAlquilada = currentTimeMillis() - fechaInicioAlquiler;
    }

    public void calcularImporte() {
        calcularTiempoAlquiler();

        //El import mínim per alquilar un patinet serà d'1€, fins a 30 segons (per a fer la simulació)
        //Tarifa para alquiler de patin
        importeAlquiler = 1;

        //A partir de 30 segons es cobrarà 0.05€ per segon
        //Per a fer-ho en temps >  30 minuts, canviar a 1800000
        if (tiempoAlquilada > 30000) {
            //Calcular els segons que ha estat de més de 30 segons per aplicar la tarifa
            //Per a fer-ho en temps >  30 minuts, canviar a 1800000 i 60000
            long tiempoExtra = (tiempoAlquilada - 30000) / 1000;

            importeAlquiler += tiempoExtra * 0.05;

        }
    }
}
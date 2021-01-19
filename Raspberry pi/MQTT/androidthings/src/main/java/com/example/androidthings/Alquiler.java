package com.example.androidthings;

import static java.lang.System.currentTimeMillis;

public class Alquiler {

    private String uId, correo, ubicacion, estant, taquilla;
    private boolean patin;
    private long fechaInicioAlquiler, tiempoAlquilada;
    private double importeAlquiler;
    private double mWConsumidos;
    private double importeCarga;
    private double importeTotal;

    public Alquiler() {
    }

    public Alquiler(String uId, String correo, String ubicacion, String estant, String id, long fechaInicioAlquiler,
                    long tiempoAlquilada, double importeAlquiler, double importeCarga, double importeTotal) {
        this.uId = uId;
        this.correo = correo;
        this.ubicacion = ubicacion;
        this.estant = estant;
        this.taquilla = id;
        this.fechaInicioAlquiler = fechaInicioAlquiler;
        this.tiempoAlquilada = tiempoAlquilada;
        this.importeAlquiler = importeAlquiler;
        this.importeCarga = importeCarga;
        this.importeTotal = importeTotal;
        mWConsumidos = 0;

    }


    public Alquiler(String uId, String correo, String ubicacion, String estant, String id, boolean patin) {
        this.uId = uId;
        this.correo = correo;
        this.ubicacion = ubicacion;
        this.estant = estant;
        this.taquilla = id;
        fechaInicioAlquiler = currentTimeMillis();
        importeCarga = 0;
        mWConsumidos = 0;
        importeTotal = 0;

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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstant() {
        return estant;
    }

    public void setEstant(String estant) {
        this.estant = estant;
    }

    public String getTaquilla() {
        return taquilla;
    }

    public void setTaquilla(String id) {
        this.taquilla = id;
    }

    public Long getFechaInicioAlquiler() {
        return fechaInicioAlquiler;
    }

    public void setFechaInicioAlquiler(long fechaInicio) {
        this.fechaInicioAlquiler = fechaInicio;
    }

    public Long getTiempoAlquilada() {
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

    public double getImporteCarga() {
        return importeCarga;
    }

    public void setImporteCarga(double importeCarga) {
        this.importeCarga = importeCarga;
    }

    public double getmWConsumidos() {
        return mWConsumidos;
    }

    public void setmWConsumidos(double mWConsumidos) {
        this.mWConsumidos += mWConsumidos;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public boolean isPatin() {
        return patin;
    }

    public void setPatin(boolean patin) {
        this.patin = patin;
    }


    private void calcularTiempoAlquiler(){
        this.tiempoAlquilada = currentTimeMillis() - fechaInicioAlquiler;
    }

    private void calcularImporte(){
        calcularTiempoAlquiler();
        //El import mínim per alquilar una taquilla serà de 2€, fins a 1 minut (per a fer la simulació)
        //El import mínim per alquilar un patinet serà d'1€, fins a 30 segons (per a fer la simulació)

        //Tarifa para alquiler de taquilla
        if(patin == false){
            importeAlquiler = 2;

            //A partir d'1 minut es cobrarà 0.05€ per segon
            //Per a fer-ho en temps > 1 hora, canviar a 3600000
            if(tiempoAlquilada > 60000){
                //Calcular els segons que ha estat de més d'un 1minut per aplicar la tarifa
                //Per a fer-ho en temps > 1 hora, canviar a 3600000 i 60000
                long tiempoExtra = (tiempoAlquilada-60000)/1000;

                importeAlquiler += tiempoExtra * 0.05;
            }
        }else{
            //Tarifa para alquiler de patin
            importeAlquiler = 1;

            //A partir de 30 segons es cobrarà 0.05€ per segon
            //Per a fer-ho en temps >  30 minuts, canviar a 1800000
            if(tiempoAlquilada > 30000){
                //Calcular els segons que ha estat de més de 30 segons per aplicar la tarifa
                //Per a fer-ho en temps >  30 minuts, canviar a 1800000 i 60000
                long tiempoExtra = (tiempoAlquilada-30000)/1000;

                importeAlquiler += tiempoExtra * 0.05;
            }
        }

    }

    private void calcularImporteCarga() {
        importeCarga += mWConsumidos * 0.001;
    }

    public void calcularImporteTotal(){
        calcularImporte();
        calcularImporteCarga();

        importeTotal = importeCarga + importeAlquiler;
    }
}

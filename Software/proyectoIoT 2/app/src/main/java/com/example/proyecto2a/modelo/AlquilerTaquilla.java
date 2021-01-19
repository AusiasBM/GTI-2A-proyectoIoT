package com.example.proyecto2a.modelo;

import static java.lang.System.currentTimeMillis;

public class AlquilerTaquilla {

    private String uId, correo, ubicacion, estant, taquilla, tipoAlquiler;
    private long fechaInicioAlquiler, tiempoAlquilada;
    private double importeAlquiler;
    private double mWConsumidos, vatiosInicio;
    private double importeCarga;
    private double importeTotal;

    public AlquilerTaquilla() {
    }

    public AlquilerTaquilla(String uId, String correo, String ubicacion, String estant, String id, long fechaInicioAlquiler,
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
        tipoAlquiler = "taquilla";

    }


    public AlquilerTaquilla(String uId, String correo, String ubicacion, String estant, String id) {
        this.uId = uId;
        this.correo = correo;
        this.ubicacion = ubicacion;
        this.estant = estant;
        this.taquilla = id;
        fechaInicioAlquiler = currentTimeMillis();
        importeCarga = 0;
        mWConsumidos = 0;
        vatiosInicio = 0;
        importeTotal = 0;
        tipoAlquiler = "taquilla";

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

    public String getTipoAlquiler() {
        return tipoAlquiler;
    }

    public void setTipoAlquiler(String tipoAlquiler) {
        this.tipoAlquiler = tipoAlquiler;
    }

    public double getVatiosInicio() {
        return vatiosInicio;
    }

    public void setVatiosInicio(double vatiosInicio) {
        this.vatiosInicio = vatiosInicio;
    }

    private void calcularTiempoAlquiler(){
        this.tiempoAlquilada = currentTimeMillis() - fechaInicioAlquiler;
    }

    public void calcularVatios(double vatios){
        this.mWConsumidos += vatios - vatiosInicio;
    }

    private void calcularImporte(){
        calcularTiempoAlquiler();
        //El import mínim per alquilar una taquilla serà de 2€, fins a 1 minut (per a fer la simulació)

        //Tarifa para alquiler de taquilla
        importeAlquiler = 2;

        //A partir d'1 minut es cobrarà 0.05€ per segon
        //Per a fer-ho en temps > 1 hora, canviar a 3600000
        if(tiempoAlquilada > 60000) {
            //Calcular els segons que ha estat de més d'un 1minut per aplicar la tarifa
            //Per a fer-ho en temps > 1 hora, canviar a 3600000 i 60000
            long tiempoExtra = (tiempoAlquilada - 60000) / 1000;

            importeAlquiler += tiempoExtra * 0.05;
        }


    }

    //Per connectar el sonoff es cobrarà 0.1€ i cada Watt consumit (no són mW perque el sonoff va en W) costarà 0.05€
    private void calcularImporteCarga() {
        importeCarga += (mWConsumidos * 0.05) + 0.1;
    }

    public void calcularImporteTotal(){
        calcularImporte();
        calcularImporteCarga();

        importeTotal = importeCarga + importeAlquiler;
    }
}

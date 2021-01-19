package com.example.proyecto2a.modelo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Registros {
    private String correo;
    private String ubicacionFinal;
    private String ubicacionInicio;
    private String uId;

    private double importeAlquiler;
    private double importeCarga;
    private double importeTotal;
    private long tiempoAlquilada;
    private long fechaInicoAlquiler;

    private double mWConsumidos;
    private String tipoAlquiler;
    private String estant;
    private String taquilla;
    private String ubicacion;

    public Registros(String correo, String ubicacionFinal, String ubicacionInicio, String uId, double importeAlquiler, double importeCarga, double importeTotal, long tiempoAlquilada, long fechaInicoAlquiler, double mWConsumidos, String tipoAlquiler, String estant, String taquilla, String ubicacion) {
        this.correo = correo;
        this.ubicacionFinal = ubicacionFinal;
        this.ubicacionInicio = ubicacionInicio;
        this.uId = uId;
        this.importeAlquiler = importeAlquiler;
        this.importeCarga = importeCarga;
        this.importeTotal = importeTotal;
        this.tiempoAlquilada = tiempoAlquilada;
        this.fechaInicoAlquiler = fechaInicoAlquiler;
        this.mWConsumidos = mWConsumidos;
        this.tipoAlquiler = tipoAlquiler;
        this.estant = estant;
        this.taquilla = taquilla;
        this.ubicacion = ubicacion;
    }

    public Registros() {
    }

    public static String getDate(long milliSeconds, String dateFormat) {
// Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
// Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUbicacionFinal() {
        return ubicacionFinal;
    }

    public void setUbicacionFinal(String ubicacionFinal) {
        this.ubicacionFinal = ubicacionFinal;
    }

    public String getUbicacionInicio() {
        return ubicacionInicio;
    }

    public void setUbicacionInicio(String ubicacionInicio) {
        this.ubicacionInicio = ubicacionInicio;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public long getTiempoAlquilada() {
        return tiempoAlquilada;
    }

    public void setTiempoAlquilada(long tiempoAlquilada) {
        this.tiempoAlquilada = tiempoAlquilada;
    }

    public long getFechaInicoAlquiler() {
        return fechaInicoAlquiler;
    }

    public void setFechaInicoAlquiler(long fechaInicoAlquiler) {
        this.fechaInicoAlquiler = fechaInicoAlquiler;
    }

    public double getmWConsumidos() {
        return mWConsumidos;
    }

    public void setmWConsumidos(double mWConsumidos) {
        this.mWConsumidos = mWConsumidos;
    }

    public String getTipoAlquiler() {
        return tipoAlquiler;
    }

    public void setTipoAlquiler(String tipoAlquiler) {
        this.tipoAlquiler = tipoAlquiler;
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

    public void setTaquilla(String taquilla) {
        this.taquilla = taquilla;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
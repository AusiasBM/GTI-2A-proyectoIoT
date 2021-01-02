package com.example.proyecto2a.modelo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Incidencia {
    private String url;
    private long tiempo;

    public Incidencia(String url, long tiempo) {
        this.url = url;
        this.tiempo = tiempo;
    }

    public Incidencia() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "url='" + url + '\'' +
                ", tiempo=" + tiempo +
                '}';
    }
}

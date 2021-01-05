package com.example.proyecto2a.modelo;

public class DatosAlquiler {
    private String ubicacionTaquilla;
    private boolean flagReserva = false;

    public DatosAlquiler() {
    }

    public String getUbicacionTaquilla() {
        return ubicacionTaquilla;
    }

    public void setUbicacionTaquilla(String ubicacionTaquilla) {
        this.ubicacionTaquilla = ubicacionTaquilla;
    }

    public boolean isFlagReserva() {
        return flagReserva;
    }

    public void setFlagReserva(boolean flagReserva) {
        this.flagReserva = flagReserva;
    }

    @Override
    public String toString() {
        return "DatosAlquiler{" +
                "ubicacionTaquilla='" + ubicacionTaquilla + '\'' +
                ", flagReserva=" + flagReserva +
                '}';
    }
}

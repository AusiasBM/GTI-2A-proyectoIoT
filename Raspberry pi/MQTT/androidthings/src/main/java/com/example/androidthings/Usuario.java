package com.example.androidthings;

public class Usuario {

    int dni;
    boolean admin;
    String correo;
    String direccion;
    String foto;
    String nombre;
    int pin;
    String poblacion;
    int telefono;
    String uId;

    public Usuario(int dni, boolean admin, String correo, String direccion, String foto, String nombre, int pin, String poblacion, int telefono, String uId) {
        this.dni = dni;
        this.admin = admin;
        this.correo = correo;
        this.direccion = direccion;
        this.foto = foto;
        this.nombre = nombre;
        this.pin = pin;
        this.poblacion = poblacion;
        this.telefono = telefono;
        this.uId = uId;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}

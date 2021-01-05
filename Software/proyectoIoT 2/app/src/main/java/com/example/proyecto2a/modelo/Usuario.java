package com.example.proyecto2a.modelo;


import com.google.firebase.firestore.FirebaseFirestore;

public class Usuario {
    private String uId, nombre, correo, dirección, población, llave, foto;
    private int telefono,dni,pin;
    private boolean admin = false, reservaAlquiler = false, nuevo = true;
    private DatosAlquiler datos;

    //Constructores
    public Usuario() {
    }

    //Getters y Setters


    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getDirección() {
        return dirección;
    }

    public void setDirección(String dirección) {
        this.dirección = dirección;
    }

    public String getPoblación() {
        return población;
    }

    public void setPoblación(String población) {
        this.población = población;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isNuevo() {
        return nuevo;
    }

    public void setNuevo(boolean nuevo) {
        this.nuevo = nuevo;
    }



    @Override
    public String toString() {
        return "Usuario{" +
                "uId='" + uId + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", dirección='" + dirección + '\'' +
                ", población='" + población + '\'' +
                ", llave='" + llave + '\'' +
                ", foto='" + foto + '\'' +
                ", telefono=" + telefono +
                ", admin=" + admin +
                ", nuevo=" + nuevo +
                ", pin=" + pin +
                ", dni=" + dni +
                ", reservaAlquiler=" + reservaAlquiler +
                '}';
    }

    public boolean isReservaAlquiler() {
        return reservaAlquiler;
    }

    public void setReservaAlquiler(boolean reservaAlquiler) {
        this.reservaAlquiler = reservaAlquiler;
    }

    public DatosAlquiler getDatos() {
        return datos;
    }

    public void setDatos(DatosAlquiler datos) {
        this.datos = datos;
    }


}

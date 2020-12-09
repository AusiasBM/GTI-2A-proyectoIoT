package com.example.proyecto2a.datos;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.proyecto2a.modelo.Usuario;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.Callable;


public class Usuarios {
    private static CollectionReference usuarios;

    public Usuarios(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarios = db.collection("usuarios");
    }

    public static CollectionReference getUsuarios() {
        return usuarios;
    }

    //Crear un usuario nuevo
    public void guardarUsuario(String email, String id) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(email);
        usuario.setuId(id);
        usuarios.document(id).set(usuario);
    }

    public void actualizarUsuario(String id, Usuario usuario){
        usuarios.document(id).set(usuario);
    }

}
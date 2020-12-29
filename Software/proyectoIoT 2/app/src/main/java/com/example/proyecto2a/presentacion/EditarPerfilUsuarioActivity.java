package com.example.proyecto2a.presentacion;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Usuarios;
import com.example.proyecto2a.modelo.Tarjeta;
import com.example.proyecto2a.modelo.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

public class EditarPerfilUsuarioActivity extends AppCompatActivity {
    private String idUsuario;
    private Tarjeta tarjeta;
    private Usuario usuario;
    private EditText nombre;
    private EditText telefono;
    private EditText direccion;
    private EditText poblacion;
    private TextView correo;
    private FirebaseFirestore db;
    private static ProgressDialog progressDialog;
    private Usuarios usuarios;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private ImageView fotoPerfil;
    private static final int SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE = 0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_menu);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Revisando datos del usuario...");
        progressDialog.show();

        Bundle extra = getIntent().getExtras();
        idUsuario = extra.getString("id");
        nombre = findViewById(R.id.etNumTarjeta);
        telefono = findViewById(R.id.etTelefono);
        direccion = findViewById(R.id.etNombre);
        poblacion = findViewById(R.id.etPoblacion);
        correo = findViewById(R.id.tvCorreo);
        fotoPerfil = findViewById(R.id.imagenLogo);

        usuarios = new Usuarios();

        cargarUsuarioPerfil(idUsuario);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getUid();

        bajarFichero();
    }

    public void subirFoto(View view){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, 1234);
        }else{
            solicitarPermiso(Manifest.permission.READ_EXTERNAL_STORAGE, "Sin el permiso" +
                            " acceso a la galería.",
                    SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE, this);
        }

    }

    public void mostrarDatosUsuario(Usuario user){
        Log.d("PROVAAA URIIII", "" + user.getFoto());
        nombre.setText(user.getNombre());
        direccion.setText(user.getDirección());
        poblacion.setText(user.getPoblación());
        if (String.valueOf(user.getTelefono()).length() != 1){
            telefono.setText(String.valueOf(user.getTelefono()));
        }
        correo.setText(user.getCorreo());
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                subirFichero(data.getData(), "imagenes/"+idUsuario);
            }
        }

    }

    private void subirFichero(final Uri fichero, final String referencia) {
        final StorageReference ficheroRef = storageReference.child(referencia);
        UploadTask uploadTask = ficheroRef.putFile(fichero);
        Task<Uri> urlTask = uploadTask.continueWithTask(new
            Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override public Task<Uri> then(@NonNull
                    Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) throw task.getException();
                        return ficheroRef.getDownloadUrl();
                    }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    bajarFichero();
                    registrarImagen(downloadUri.toString());
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo fichero");
                }
            }
        });
    }
    void registrarImagen(String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usuario = db.collection("usuarios").document(idUsuario);
        usuario.update("foto", url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Foto", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Foto", "Error updating document", e);
                    }
                });
    }

    private void bajarFichero() {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);
        StorageReference ficheroRef = storageReference.child("imagenes/"+idUsuario);
        ficheroRef.getFile(localFile)
                .addOnSuccessListener(new
                  OnSuccessListener<FileDownloadTask.TaskSnapshot>(){
                      @Override
                      public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot){
                          Log.d("Almacenamiento", "Fichero bajado");
                          fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(path));
                      }
                 }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Almacenamiento", "ERROR: bajando fichero");
            }
        });
    }

    public void actualizarPerfilUsuario(){
        try{
            if(nombre.getText().toString().equals(null)){
                usuario.setNombre("");
            }else {
                usuario.setNombre(nombre.getText().toString());
            }

            if (telefono.getText().toString().length() != 9 && telefono.getText().toString().length() != 0) {
                usuario.setTelefono(0);
                Toast.makeText(this, "Número de teléfono incorrecto", Toast.LENGTH_SHORT).show();
            } else if(telefono.getText().toString().length() == 0){
                usuario.setTelefono(0);
            } else {
                usuario.setTelefono(Integer.parseInt(telefono.getText().toString()));
            }

            if(direccion.getText().toString().length() == 0){
                usuario.setDirección("");
            }else {
                usuario.setDirección(direccion.getText().toString());
            }

            if(poblacion.getText().toString().length() == 0){
                usuario.setPoblación("");
            }else {
                usuario.setPoblación(poblacion.getText().toString());
            }
            usuarios.actualizarUsuario(idUsuario, usuario);
        }catch (Exception e){
        }
    }

    public void volverHome(View view){
        actualizarPerfilUsuario();
        Intent intent=new Intent(this, ResActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        actualizarPerfilUsuario();
        super.onStop();
    }

    public void cargarUsuarioPerfil(String idUsuario){
        usuarios.getUsuarios().document(idUsuario).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            usuario = task.getResult().toObject(Usuario.class);
                            mostrarDatosUsuario(usuario);
                        } else {
                            Log.e("Firebase", "Error al leer", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        actualizarPerfilUsuario();
        Intent intent = new Intent(this, ResActivity.class);
        startActivity(intent);
    }

    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitud de permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }}).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

}





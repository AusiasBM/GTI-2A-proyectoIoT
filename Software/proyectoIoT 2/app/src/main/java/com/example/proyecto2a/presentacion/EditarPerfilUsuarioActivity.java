package com.example.proyecto2a.presentacion;

import android.app.Activity;
import android.app.ProgressDialog;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_menu);

        //Diálogo de carga mientras se ponen los dats en los editText
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

        //Cargar el usuario y poner los datos en su perfil
        cargarUsuarioPerfil(idUsuario);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getUid();

        bajarFichero();
        /*
        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.example_img);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        ImageView imageView = (ImageView) findViewById(R.id.imagenPerfil);

        imageView.setImageDrawable(roundedDrawable);*/

        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction)

        //Foto de usuario

       //*/


    }

    public void subirFoto(View view){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
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

    private void subirFichero(Uri fichero, String referencia) {
        StorageReference ficheroRef = storageReference.child(referencia);
        ficheroRef.putFile(fichero)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Almacenamiento", "Fichero subido");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Almacenamiento", "ERROR: subiendo fichero");
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
                          bajarFichero();
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
            //Los if comprueban que los campos estén llenos o sinó se encarga de que los campos
            // que se actualizan en la bbdd contengan una cadena vacía para que, en caso de no
            //completar todos los campos (se deje alguno vacío) se puedan guardar el resto en Firestore
            if(nombre.getText().toString().equals(null)){
                usuario.setNombre("");
            }else {
                usuario.setNombre(nombre.getText().toString());
            }

            //En caso de que se introduzca un número de teléfono de menos o mas cifras de las que tocan, enviar un toast
            //avisando del error
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

            //Llamar al método actualizarUsuarios de la clase Usuarios
            usuarios.actualizarUsuario(idUsuario, usuario);
            //Toast.makeText(this, "Guardados los cambios", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
           // Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show();
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


    //Cargar el usuario del perfil
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

}





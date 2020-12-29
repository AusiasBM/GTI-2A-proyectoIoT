package com.example.proyecto2a.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.UsuariosAdapter;
import com.example.proyecto2a.modelo.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoUsuario extends AppCompatActivity {

    private ImageView ivVolver, fotoPerfil;
    private TextView direccion, telefono, nombre, correo;
    FirebaseFirestore firebaseFirestore;
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_usuario);
        ivVolver = (ImageView) findViewById(R.id.ivBackUsuario);
        direccion = findViewById(R.id.tvDireccion_info);
        nombre = findViewById(R.id.tvNombreUsuario_info);
        telefono = findViewById(R.id.tv_telefono_info);
        correo = findViewById(R.id.tvCorreoUsuario_info);
        fotoPerfil = findViewById(R.id.ivUsuario_info);

        firebaseFirestore = FirebaseFirestore.getInstance();

        usuarioID = getIntent().getStringExtra("usuarioID");

        verValores();

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoUsuario.this, UsuariosActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void verValores() {
        firebaseFirestore.collection("usuarios").document(usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            String nombreUsuario = documentSnapshot.getString("nombre");
                            String correoUsuario = documentSnapshot.getString("correo");
                            long telefonoUsuario = documentSnapshot.getLong("telefono");
                            String direccionUsuario = documentSnapshot.getString("dirección");
                            String poblacion = documentSnapshot.getString("población");

                            correo.setText(correoUsuario + "");

                            if (Integer.parseInt(String.valueOf(telefonoUsuario)) != 0){
                                telefono.setText(telefonoUsuario + "");
                            } else {
                                telefono.setText(R.string.textNoTelefono);
                            }

                            if (!nombreUsuario.equals("")){
                                nombre.setText(nombreUsuario + "");
                            } else {
                                telefono.setText(R.string.textNoNombre);
                            }

                            if (!direccionUsuario.equals("")){
                                direccion.setText(direccionUsuario + ", " + poblacion);
                            } else {
                                direccion.setText(R.string.textNoDireccion);
                            }

                        }

                    }
                });
    }
}
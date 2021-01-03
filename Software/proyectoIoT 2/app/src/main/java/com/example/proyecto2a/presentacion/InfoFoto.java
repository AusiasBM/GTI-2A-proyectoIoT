package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Incidencia;
import com.example.proyecto2a.modelo.Stant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;

public class InfoFoto extends AppCompatActivity {

    ImageView ivFoto, ivBack;
    private FirebaseFirestore firebaseFirestore;
    Button btEliminar, btAlertar;
    private String incidenciaID, incidenciaURL;
    private long incidenciaTiempo;
    Stant stant = new Stant();
    Incidencia incidencia = new Incidencia();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_foto);
        btAlertar = findViewById(R.id.ivAlertaInfoFoto);
        btEliminar = findViewById(R.id.ivEliminarInfoFoto);
        ivBack = findViewById(R.id.ivBackFoto);
        ivFoto = findViewById(R.id.ivFotoInfo);
        incidenciaID = getIntent().getStringExtra("incidenciaID");
        incidenciaURL = getIntent().getStringExtra("incidenciaURL");
        incidenciaTiempo = getIntent().getLongExtra("incidenciaTiempo", incidenciaTiempo);

        firebaseFirestore = FirebaseFirestore.getInstance();

        verValores();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoFoto.this, IncidenciasActivity.class);
                startActivity(intent);
            }
        });

        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert =new AlertDialog.Builder(InfoFoto.this);
                alert.setMessage(R.string.preguntaFoto);
                alert.setTitle(R.string.eliminarFoto);
                alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("imagenesSeguridad").document(incidenciaID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InfoFoto.this, R.string.fotoEliminada, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InfoFoto.this, IncidenciasActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoFoto.this, R.string.fotoNoEliminada, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog=alert.create();
                dialog.show();
            }
        });

        btAlertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { "incidencias@policia.es" });
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Incidencia TRICOOPARK");
                /*emailIntent.putExtra(Intent.EXTRA_TEXT, "Datos de la incidencia:");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Ubicación: Stant Univeritat Politécnica de València - ESPG");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Fecha:" + incidencia.getDate(incidenciaTiempo,"dd/MM/yyyy HH:mm:ss"));
                 */
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Datos de la incidencia: \n \n" +
                        "Ubicación stant: Universitat Politècnica de València - ESPG. \n \n" +
                        "Fecha: " + incidencia.getDate(incidenciaTiempo,"dd/MM/yyyy HH:mm:ss") + ".\n \n" +
                        "URL: "+ incidenciaURL);
                startActivity(emailIntent);
            }
        });
    }

    private void verValores() {
        firebaseFirestore.collection("imagenesSeguridad").document(incidenciaID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Glide.with(InfoFoto.this)
                                    .load(documentSnapshot.getString("url"))
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .into(ivFoto);
                    }
                });
    }
}
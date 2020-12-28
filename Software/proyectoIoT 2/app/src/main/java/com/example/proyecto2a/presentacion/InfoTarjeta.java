package com.example.proyecto2a.presentacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoTarjeta extends AppCompatActivity {

    private String tarjetaID;
    private FirebaseFirestore firebaseFirestore;
    private TextView tvFecha, tvPropietario, tvNum;
    private Button btEliminar;
    private ImageView ivVolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tarjeta);

        tvNum = (TextView) findViewById(R.id.tvNumeroTarjetaInfo);
        tvFecha = (TextView) findViewById(R.id.tv_fecha);
        tvPropietario = (TextView) findViewById(R.id.tv_propietario);
        btEliminar = (Button) findViewById(R.id.btEliminar);
        ivVolver = (ImageView) findViewById(R.id.ivBackInfo);

        tarjetaID = getIntent().getStringExtra("tarjetaID");

        firebaseFirestore = FirebaseFirestore.getInstance();

        verValores();

        ivVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoTarjeta.this, RecyclerTarjetas.class);
                startActivity(intent);
                finish();
            }
        });
        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder alert =new AlertDialog.Builder(InfoTarjeta.this);
                alert.setMessage("¿Estas seguro de que quieres eliminar esta tarjeta?");
                alert.setTitle("Eliminar tarjeta");
                alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("tarjetas").document(tarjetaID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(InfoTarjeta.this, "Tarjeta eliminada correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InfoTarjeta.this, RecyclerTarjetas.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoTarjeta.this, "La tarjeta no se pudo eliminar", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog=alert.create();
                dialog.show();



            }
        });
    }

    public void verValores(){
        firebaseFirestore.collection("tarjetas").document(tarjetaID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    long num = documentSnapshot.getLong("numTarjeta");
                    String nombrePropietario = documentSnapshot.getString("nombrePropietario");
                    String apellidoPropietario = documentSnapshot.getString("apellidoPropietario");
                    long mes = documentSnapshot.getLong("mes");
                    long año = documentSnapshot.getLong("año");

                    tvNum.setText(num + "");
                    tvPropietario.setText(apellidoPropietario + ", " + nombrePropietario);
                    tvFecha.setText(mes + "/20" + año);
                }

            }
        });
    }
}
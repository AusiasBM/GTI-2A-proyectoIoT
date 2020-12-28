package com.example.proyecto2a.presentacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Tarjetas;
import com.example.proyecto2a.datos.Usuarios;
import com.example.proyecto2a.modelo.Tarjeta;
import com.example.proyecto2a.modelo.Usuario;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoPago extends AppCompatActivity {

    private String idUsuario;
    private Tarjeta tarjeta;
    private Usuario usuario;
    private FirebaseFirestore db;
    private static ProgressDialog progressDialog;
    private Tarjetas tarjetas;
    private Usuarios usuarios;
    private FirebaseAuth firebaseAuth;

    private Button btn;
    private CheckBox cb;
    private EditText num, mes, año, cvv, name, apellido;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_pago);
        btn = (Button) findViewById(R.id.btnAddTarget);
        cb = (CheckBox) findViewById(R.id.cbAcepto);
        num = (EditText) findViewById(R.id.etNumTarjeta);
        mes = (EditText) findViewById(R.id.etMes);
        año = (EditText) findViewById(R.id.etAño);
        cvv = (EditText) findViewById(R.id.etCVV);
        name = (EditText) findViewById(R.id.etNombre);
        apellido = (EditText) findViewById(R.id.etApellido);

        //Bundle extra = getIntent().getExtras();
        //idUsuario = extra.getString("idTarj");
        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getUid();
        Log.d("hola", firebaseAuth.getUid());

        tarjetas = new Tarjetas();
        usuarios = new Usuarios();
        tarjeta = new Tarjeta();

        //Cargar el usuario y poner los datos en su perfil


        //-----habilitarBoton-----
        cb.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cb.isChecked()){
                            btn.setEnabled(true);
                        } else {
                            btn.setEnabled(false);
                        }
                    }
                }
        );
    }
    public void returnAyuda(View view){
        actualizarPerfilTarjeta();
        Intent intent=new Intent(this, RecyclerTarjetas.class);
        startActivity(intent);
    }
    public void guardarTarjeta(View view){
        final AlertDialog.Builder alert =new AlertDialog.Builder(InfoPago.this);
        alert.setMessage("¿Estas seguro de que quieres añadir esta tarjeta?");
        alert.setTitle("Añadir tarjeta");
        alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("hi", num.getText().equals("") + "");
                if (num.getText().toString().isEmpty() || mes.getText().toString().isEmpty()
                        || año.getText().toString().isEmpty()
                        || cvv.getText().toString().isEmpty() || name.getText().toString().isEmpty()
                        || apellido.getText().toString().isEmpty()){

                } else {
                    actualizarPerfilTarjeta();
                }
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

    public void actualizarPerfilTarjeta(){
        try{
            tarjeta.setuID(firebaseAuth.getUid());
            tarjeta.setNumTarjeta(Integer.parseInt(num.getText().toString()));

            if (mes.getText().toString().length() > 2 || Integer.parseInt(mes.getText().toString()) > 12 || Integer.parseInt(mes.getText().toString()) <= 0 ){
                Toast.makeText(this, "Mes incorrecto", Toast.LENGTH_SHORT).show();
            } else {
                tarjeta.setMes(Integer.parseInt(mes.getText().toString()));
            }
            tarjeta.setAño(Integer.parseInt(año.getText().toString()));
            if (cvv.getText().toString().length() != 3 ) {
                Toast.makeText(this, "CVV incorrecto", Toast.LENGTH_SHORT).show();
            } else {
                tarjeta.setCvv(Integer.parseInt(cvv.getText().toString()));
            }
            tarjeta.setNombrePropietario(name.getText().toString());
            tarjeta.setApellidoPropietario(apellido.getText().toString());

            tarjetas.actualizarTarjeta(tarjeta);
            Toast.makeText(this, "Tarjeta introducida correctamente", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this, RecyclerTarjetas.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        actualizarPerfilTarjeta();
        Intent intent = new Intent(this, RecyclerTarjetas.class);
        startActivity(intent);
        finish();
    }
}

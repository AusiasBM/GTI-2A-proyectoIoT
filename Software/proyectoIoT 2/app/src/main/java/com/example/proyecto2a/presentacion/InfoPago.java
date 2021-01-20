package com.example.proyecto2a.presentacion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Tarjetas;
import com.example.proyecto2a.datos.Usuarios;
import com.example.proyecto2a.modelo.Tarjeta;
import com.example.proyecto2a.modelo.Usuario;
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getUid();
        Log.d("hola", firebaseAuth.getUid());

        tarjetas = new Tarjetas();
        usuarios = new Usuarios();
        tarjeta = new Tarjeta();

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
        Intent intent=new Intent(this, RecyclerTarjetas.class);
        startActivity(intent);
    }
    public void guardarTarjeta(View h){
        final LayoutInflater inflater = LayoutInflater.from(InfoPago.this);
        final View view = inflater.inflate(R.layout.custom_dialog_anyadir_tarjeta,null);
        Button acceptButton= view.findViewById(R.id.btn_si);
        final Button cancelButton = view.findViewById(R.id.btn_no);
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
        final AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    public void actualizarPerfilTarjeta(){
        try{
            tarjeta.setuID(firebaseAuth.getUid());
            tarjeta.setNombrePropietario(name.getText().toString());
            tarjeta.setApellidoPropietario(apellido.getText().toString());
            tarjeta.setAño(Integer.parseInt(año.getText().toString()));
            if (num.getText().toString().length() == 8){
                tarjeta.setNumTarjeta(Integer.parseInt(num.getText().toString()));
                if (mes.getText().toString().length() > 2 || Integer.parseInt(mes.getText().toString()) > 12 || Integer.parseInt(mes.getText().toString()) <= 0 ){
                    Toast.makeText(this, R.string.mesIncorrecto, Toast.LENGTH_SHORT).show();
                } else {
                    tarjeta.setMes(Integer.parseInt(mes.getText().toString()));
                    if (cvv.getText().toString().length() != 3 ) {
                        Toast.makeText(this, R.string.cvvIncorrecto, Toast.LENGTH_SHORT).show();
                    } else {
                        tarjeta.setCvv(Integer.parseInt(cvv.getText().toString()));
                        tarjetas.actualizarTarjeta(tarjeta);
                        Toast.makeText(this, R.string.tarjetaIntroducida, Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(this, RecyclerTarjetas.class);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(this, R.string.tarjetaIncorrecta, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, R.string.errorModificar, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RecyclerTarjetas.class);
        startActivity(intent);
        finish();
    }
}

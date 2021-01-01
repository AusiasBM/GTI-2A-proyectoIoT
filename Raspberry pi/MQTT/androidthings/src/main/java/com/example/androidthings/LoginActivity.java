package com.example.androidthings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText edTxDNI;
    EditText edTxPin;
    static Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void info(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage("Para acceder a un stand primero tienes que estar registrado en TRICOOPARK y haber configurado en la aplicación tu DNI y tu pin en el apartado del menú \"acceder a stands.")
                .setTitle("Información Stand");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void entrar(View v){

        edTxDNI = findViewById(R.id.edTxDNI);
        edTxPin = findViewById(R.id.edTxPin);
        try {
            db.collection("usuarios")
                    .whereEqualTo("DNI", Integer.parseInt(edTxDNI.getText().toString()))
                    .whereEqualTo("pin", Integer.parseInt(edTxPin.getText().toString()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Resultado consulta de entrada", document.getId() + " => " + document.getData());

                                    user = new Usuario(
                                            Integer.parseInt(document.getData().get("DNI").toString()),
                                            Boolean.parseBoolean(document.getData().get("admin").toString()),
                                            document.getData().get("correo").toString(),
                                            document.getData().get("dirección").toString(),
                                            document.getData().get("foto").toString(),
                                            document.getData().get("nombre").toString(),
                                            Integer.parseInt(document.getData().get("pin").toString()),
                                            document.getData().get("población").toString(),
                                            Integer.parseInt(document.getData().get("telefono").toString()),
                                            document.getData().get("uId").toString()
                                    );

                                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(i);
                                    edTxDNI.setText("");
                                    edTxPin.setText("");
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Toast aviso = Toast.makeText(this, "Te falta el DNI o el Pin", Toast.LENGTH_LONG);
            aviso.show();
        }


    }
}
package com.example.proyecto2a.casos_uso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Usuarios;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.InfoActivity;
import com.example.proyecto2a.presentacion.MainActivity;
import com.example.proyecto2a.presentacion.ResActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignUp extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    private EditText TextEmail;
    private EditText TextPassword, TextPassConf;
    private Button btnRegistrar;
    private TextView tv_registro;
    public static final int SIGN_IN_CODE = 777;

    public ProgressDialog progressDialog;


    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Referenciamos los views
        TextEmail = (EditText) findViewById(R.id.et_em_up);
        TextPassword = (EditText) findViewById(R.id.et_pass_up);
        TextPassConf = (EditText) findViewById(R.id.et_pass_verify);
        btnRegistrar = (Button) findViewById(R.id.bt_sign_up);
        tv_registro = (TextView) findViewById(R.id.tv_Registrarse);
        progressDialog = new ProgressDialog(this);

        //----------------------------------------------------
        //Código para ir a la página Inicio Sesion mediante TextView
        tv_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                intent.putExtra(SignIn.metodo, "verif");
                intent.putExtra(SignIn.email, "");
                intent.putExtra(SignIn.pass, "");
                startActivity(intent);
                finish();
            }
        });


        //----------------------------------------------------

        //----------------------------------------------------
        //Inicio sesion con Google
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton = findViewById(R.id.btn_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });


        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goRes();
                }
            }
        };
        //attaching listener to button
        btnRegistrar.setOnClickListener((View.OnClickListener) this);
    }


    public void onClick(View view) {

        //Invocamos al método:
        registrarUsuario();

    }

    private void registrarUsuario() {

        //Obtenemos el email y la contraseña desde las cajas de texto
        final String email = TextEmail.getText().toString();
        final String password = TextPassword.getText().toString();
        String confirm = TextPassConf.getText().toString();

        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            //Abrir teclado
            TextEmail.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(TextEmail, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            //Abrir teclado
            TextPassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(TextPassword, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        if (!confirm.equals(password)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            //Abrir teclado
            TextPassword.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(TextPassword, InputMethodManager.SHOW_IMPLICIT);
            return;
        }


        this.progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Usuario registrado, revise su email para la verificación", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignUp.this, SignIn.class);
                                        intent.putExtra(SignIn.metodo, "verif");
                                        intent.putExtra(SignIn.email, email);
                                        intent.putExtra(SignIn.pass, password);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else {

                            Toast.makeText(SignUp.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            Toast.makeText(this, "No se pudo iniciar Sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "No se pudo Autenticar con Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    goRes();
                }
            }
        });
    }

    private void goRes() {
        //Creación de un objeto FirebaseUser
        final FirebaseUser usuario = firebaseAuth.getInstance().getCurrentUser();

        Log.d("PROVA DE UID", "" + usuario.getUid());
        //Creación de un nuevo documento (id = uId de firebaseAuth)en la bbdd de Firestore con el correo
        final Usuarios usuarios = new Usuarios();
        usuarios.getUsuarios().document(usuario.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists() == false){
                            Log.d("PROVA DE REGISTRE TRUE", "" + task.getResult().exists());
                            usuarios.guardarUsuario(usuario.getEmail(), usuario.getUid());
                        }else{
                            Usuario usr = task.getResult().toObject(Usuario.class);
                            Log.d("PROVA DE REGISTRE FALSE", "" + usuario.getUid());
                        }
                    }
                });
        //Abrir la actividad ResActivity
        Intent intent = new Intent(this, ResActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void lanzarInfo(View view) {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }

}
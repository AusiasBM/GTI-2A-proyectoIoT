package com.example.proyecto2a.casos_uso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2a.R;
import com.example.proyecto2a.datos.Usuarios;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static String email ="email";
    public static String metodo="metodo";
    public static String pass="contra";
    //defining view objects
    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnLogin;
    private SignInButton signInButton;
    private TextView tv_registro;
    private ProgressDialog progressDialog;
    public static final int SIGN_IN_CODE = 777;
    private GoogleApiClient googleApiClient;
    private boolean esVisible;
    private ImageView visible;


    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        TextEmail = (EditText) findViewById(R.id.et_nombre_signIn);
        TextPassword = (EditText) findViewById(R.id.et_pass_signIn);
        tv_registro = (TextView) findViewById(R.id.tv_Registrarse);
        btnLogin = (Button) findViewById(R.id.bt_sign_in);
        visible = (ImageView) findViewById(R.id.iv_ver);

        progressDialog = new ProgressDialog(this);

        //----------------------------------------------------
        //Código para ir a la página Registro mediante TextView
        tv_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });


        //----------------------------------------------------

        //asociamos un oyente al evento clic del botón
        btnLogin.setOnClickListener((View.OnClickListener) this);

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
        signInButton = findViewById(R.id.signInButton);
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
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if (user!=null){
                    goRes();
                }
            }
        };
        String method = "";
        method = getIntent().getStringExtra(metodo);
        assert method != null;
        if (!method.equals("nada")){
            String user = getIntent().getStringExtra(email);
            String contraseña = getIntent().getStringExtra(pass);
            TextEmail.setText(user);
            TextPassword.setText(contraseña);
        }
    }

    public void SignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void loguearUsuario() {

        //Obtenemos el email y la contraseña desde las cajas de texto
        final String email = TextEmail.getText().toString();
        String password = TextPassword.getText().toString();

        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {//(precio.equals(""))
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            //Abrir teclado
            TextEmail.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(TextEmail, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            //Abrir teclado
            TextPassword.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(TextPassword, InputMethodManager.SHOW_IMPLICIT);
            return;
        }


        progressDialog.setMessage("Realizando consulta en linea...");
        progressDialog.show();

        //loguear usuario
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                goRes();
                                //int pos = email.indexOf("@");
                                //String user = email.substring(0, pos);
                                //Toast.makeText(SignIn.this, "Bienvenido: " + TextEmail.getText(), Toast.LENGTH_LONG).show();
                                //Intent intencion = new Intent(getApplication(), ResActivity.class);
                                //intencion.putExtra(ResActivity.user, user);
                                //intencion.putExtra(ResActivity.metodo, "email");
                                //startActivity(intencion);
                            }else {
                                Toast.makeText(SignIn.this, "Por favor verifique su email" , Toast.LENGTH_LONG).show();

                            }



                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(SignIn.this, "Ese usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignIn.this, "Email o contraseña incorrectos ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });


    }

    @Override
    public void onClick(View v) {
        //Invocamos al método:
        loguearUsuario();
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
                }else {
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
                            Log.d("PROVA DE REGISTRE FALSE", "" + usuario.getUid());
                        }
                    }
        });



        //Abrir la actividad ResActivity
        Intent intent = new Intent(this, ResActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void mostrarOcultarPass(View view){
       if(esVisible){
               TextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
               visible.setImageResource(R.drawable.invisible);
               esVisible = false;
           } else{
               TextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
               visible.setImageResource(R.drawable.visible);
               esVisible = true;
       }
    }
}
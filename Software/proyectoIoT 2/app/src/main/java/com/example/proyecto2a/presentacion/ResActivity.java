package com.example.proyecto2a.presentacion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.Asistente;
import com.example.proyecto2a.modelo.DatosAlquiler;
import com.example.proyecto2a.modelo.Usuario;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import static android.view.Gravity.END;

public class ResActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {


    final private int REQUEST_CODE_ASK_PERMISSION = 111;

    public static final String metodo = "metodo";
    private static String method = "sin iniciar";
    TextView txtUser;
    private ImageButton btn_accRapidoTaquilla;
    FloatingActionButton cercano;

    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser user;
    private String TAG = "Exception";
    private int totalEstaciones;

    private String[] nombresEstaciones = new String[100];
    private double[] lats = new double[100];
    private double[] longs = new double[100];
    private DrawerLayout drawerLayout;

    private LocationManager manejador;
    private Location mejorLocaliz;

    Usuario usuario = new Usuario();


    //--------


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        cercano = findViewById(R.id.faBCercano);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    goMain();
                } else {
                    setUserData(user);
                }
            }
        };

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Carga del fragment del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Obtención de la colección "estaciones" en la base datos
        db.collection("estaciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Obtenció de cada estació de su ubicación y su geoposición
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                nombresEstaciones[i] = (String) document.getData().get("ubicacion");
                                GeoPoint p = (GeoPoint) document.getData().get("pos");
                                longs[i] = p.getLongitude();
                                lats[i] = p.getLatitude();
                                i++;
                            }
                            totalEstaciones = i;
                            onMapReady(mMap);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        solicitarPermisos();
        //Tutorial

        db.collection("usuarios").document(firebaseAuth.getUid()).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    usuario = task.getResult().toObject(Usuario.class);

                                    boolean esNuevo = task.getResult().toObject(Usuario.class).isNuevo();
                                    if (esNuevo){
                                        lanzaTutorial();
                                    }

                                    menuTipoUsuario(usuario);

                                }else {
                                    Log.d("Error usuario", "");
                                }
                            }
                        }
                );

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(firebaseAuth.getUid()).get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DatosAlquiler d = new DatosAlquiler();

                                    //En caso de que el usuario tenga algo alquilado o reservado poner visible el botón
                                    // de acceso rápido a la taquilla para no tener que buscar la estación.
                                    if(task.getResult().getBoolean("reservaAlquiler") == true){
                                        d = (task.getResult().get("datos", DatosAlquiler.class));
                                        visibilidadBtnAccRapido(d);
                                    }else {
                                        btn_accRapidoTaquilla.setVisibility(View.GONE);
                                    }

                                    final DatosAlquiler finalD = d;
                                    btn_accRapidoTaquilla.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            lanzarTaquillaAlquilada(finalD.getUbicacionTaquilla(), user.getUid());
                                        }
                                    });


                                }
                            }
                        }
                );

    }

    public void lanzaTutorial(){
        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra("id", firebaseAuth.getUid());
        startActivity(intent);
    }

    private void visibilidadBtnAccRapido(DatosAlquiler d){
        btn_accRapidoTaquilla.setVisibility(View.VISIBLE);

        //Si está reservada poner el fondo en naranja, y si está alquilada poner en verde
        if(d.isFlagReserva() == true){
            btn_accRapidoTaquilla.setBackground(ContextCompat.getDrawable(this, R.drawable.acc_rapido_reservada));
        }else{
            btn_accRapidoTaquilla.setBackground(ContextCompat.getDrawable(this, R.drawable.acc_rapido_alquilada));
        }
    }

    private void menuTipoUsuario(Usuario user){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            //Comprobación tipo usuario
            Log.d("usr", user.toString());
            if (user.isAdmin()){
                navigationView.getMenu().findItem(R.id.nav_users).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_incidencias).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
            }
            else {
                navigationView.getMenu().findItem(R.id.nav_gallery).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_pays).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_asistencia).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_ayuda).setVisible(true);
            }

            prepararDrawer(navigationView);
            // Seleccionar item por defecto
            seleccionarItem(navigationView.getMenu().getItem(0));
        }
        agregarToolbar();
    }

    private void agregarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.menu);
            ab.setDisplayHomeAsUpEnabled(true);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

            setSupportActionBar(toolbar);
            mTitle.setText(toolbar.getTitle());

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            //Formula para convertir las medidas dp a px
            final float scale = ResActivity.this.getResources().getDisplayMetrics().density;

            //Saber los píxeles equivalentes a 36 dp en cada dispositivo
            int pixels = (int) (36 * scale + 0.5f);
            //Saber los píxeles equivalentes a 8 dp en cada dispositivo
            int pixels2 = (int) (8 * scale + 0.5f);

            //Dimensionar, posicionar en el toolbar y darle un marginLeft al botón de acceso rápido a la taquilla alquilada
            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(pixels, pixels, END);
            lp.setMargins(0, 0, pixels2, 0);
            btn_accRapidoTaquilla = findViewById(R.id.btn_taqResAl);
            btn_accRapidoTaquilla.setVisibility(View.GONE);
            btn_accRapidoTaquilla.setLayoutParams(lp);
            ab.setCustomView(btn_accRapidoTaquilla, lp);



            //ab.setLogo(R.drawable.logo);
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void lanzarTaquillaAlquilada(String ubicacion, String idUser){
        Intent i = new Intent(this, MenuDialogActivity.class);
        i.putExtra("idUser", idUser);
        i.putExtra("nombre", ubicacion);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void logOut() {
        /*
        final AlertDialog.Builder alert =new AlertDialog.Builder(this);
        alert.setMessage(R.string.preguntaCerrarSesion);
        alert.setTitle(R.string.cerrarSesion);
        alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Cerrar el servicio de alquiler de taquilla
                Intent stopIntent = new Intent(ResActivity.this, ServicioReservaAlquilerTaquilla.class);
                stopIntent.setAction("terminar");
                startService(stopIntent);

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            goMain();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.cerrarSesionError, Toast.LENGTH_SHORT).show();
                        }
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
        */
        //
        //

        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.custom_dialog,null);
        Button acceptButton= view.findViewById(R.id.btn_accp);
        final Button cancelButton = view.findViewById(R.id.btn_cancel);
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Cerrar el servicio de alquiler de taquilla
                Intent stopIntent = new Intent(ResActivity.this, ServicioReservaAlquilerTaquilla.class);
                stopIntent.setAction("terminar");
                startService(stopIntent);

                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            goMain();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.cerrarSesionError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        String nombre;
        LatLng[] posiciones = new LatLng[totalEstaciones];
        for (int i = 0; i < totalEstaciones; i++) {
            posiciones[i] = new LatLng(lats[i], longs[i]);
        }
        // Add a marker in Sydney and move the camera
        for (int i = 0; i < totalEstaciones; i++) {
            mMap.addMarker(new MarkerOptions().position(posiciones[i]).title(nombresEstaciones[i]).icon(BitmapDescriptorFactory.
                    fromResource(R.drawable.icon_pat)).anchor(0.5f, 1f));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            nombre = nombresEstaciones[i];
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posiciones[0], 13));
            final String finalNombre = nombre;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                lanzaMenuDialog(marker.getTitle(), marker.getPosition());
                return false;
            }
        });

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void lanzaAsistente() {
        Intent i = new Intent(this, Asistente.class);
        startActivity(i);
    }

    public void lanzarPerfil() {
        Intent intent = new Intent(this, EditarPerfilUsuarioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //Pasar el id del usuario para modificar el perfil en la base de datos de firestore
        intent.putExtra("id", user.getUid());
        startActivity(intent);

    }

    public void lanzaMenuDialog(String nom, LatLng pos) {
        Intent i = new Intent(this, MenuDialogActivity.class);
        i.putExtra("nombre", nom);
        double longitud = pos.longitude;
        double latitud = pos.latitude;
        i.putExtra("idUser", user.getUid());
        i.putExtra("lat", latitud);
        i.putExtra("long", longitud);
        startActivity(i);
    }


    private void setUserData(FirebaseUser user) {
        //txtUser.setText("¡Bienvenido " + "\n" + user.getDisplayName() + "!");
        if (Objects.equals(user.getDisplayName(), "")) {
            goMain();
        }
        if (user.getDisplayName() == null) {
            //txtUser.setText("¡Bienvenido " + "\n" + user.getEmail() + "!");
        }
    }

    private void prepararDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    private void seleccionarItem(MenuItem itemDrawer) {

        switch (itemDrawer.getItemId()) {
            case R.id.nav_gallery:
                lanzarPerfil();
                break;
            case R.id.nav_home:
                break;
            case R.id.nav_pays:
                lanzarPago();
                break;
            case R.id.nav_asistencia:
                lanzaAsistente();
                break;
            case R.id.nav_ayuda:
                lanzarAyuda();
                break;
            case R.id.nav_slideshow:
                logOut();
                break;
            case R.id.nav_users:
                lanzarUsuarios();
                break;
            case R.id.nav_incidencias:
                lanzarIncidencias();
                break;
            case R.id.nav_settings:
                lanzarConfiguracion();
                break;
        }

    }

    private void lanzarConfiguracion() {
        Intent i = new Intent(this, StantsActivity.class);
        startActivity(i);
    }

    private void lanzarUsuarios() {
        Intent i = new Intent(this, UsuariosActivity.class);
        startActivity(i);
    }

    private void lanzarIncidencias() {
        Intent i = new Intent(this, IncidenciasActivity.class);
        startActivity(i);
    }

    private void lanzarPago() {
        Intent i = new Intent(this, RecyclerTarjetas.class);
        startActivity(i);
    }

    public void lanzarAyuda() {
        Intent i = new Intent(this, Ayuda.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    public void onMapClick(LatLng latLng) {

    }

    private void solicitarPermisos() {
        int permisoUbicacion = ActivityCompat.checkSelfPermission(ResActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permisoUbicacion != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSION);
            }
        }
    }

    public void abrirCercano(View view){
        Intent i = new Intent(this, StantsCercanos.class);
        i.putExtra("idUser", usuario.getuId());
        startActivity(i);
    }
}


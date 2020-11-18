package com.example.proyecto2a.presentacion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.Asistente;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ResActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {


    public static final String metodo = "metodo";
    private static String method = "sin iniciar";
    TextView txtUser;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        txtUser = (TextView) findViewById(R.id.textser);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            prepararDrawer(navigationView);
            // Seleccionar item por defecto
            seleccionarItem(navigationView.getMenu().getItem(0));
        }
        agregarToolbar();
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        //Carga del fragment del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Consulta a bbdd para cargar las estaciones
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
            //ab.setLogo(R.drawable.logo);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void logOut(View view) {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goMain();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void logOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goMain();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revoke(View view) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goMain();
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesion", Toast.LENGTH_SHORT).show();
                }
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
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void lanzaAsistente() {
        Intent i = new Intent(this, Asistente.class);
        startActivity(i);
    }
    public void lanzarPerfil(){
        Intent intent=new Intent(this, EditarPerfilUsuarioActivity.class);
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
        i.putExtra("lat", latitud);
        i.putExtra("long", longitud);
        startActivity(i);
    }


    private void setUserData (FirebaseUser user){
        //txtUser.setText("¡Bienvenido " + "\n" + user.getDisplayName() + "!");
        if (Objects.equals(user.getDisplayName(), "")){
            goMain();
        }if (user.getDisplayName()==null){
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
                // Fragmento para la sección Cuenta
                break;
            case R.id.nav_slideshow:
                logOut();
                break;
            case R.id.nav_asistencia:
                lanzaAsistente();
                break;

        }

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
}


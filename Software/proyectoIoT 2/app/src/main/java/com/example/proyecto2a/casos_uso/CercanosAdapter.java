package com.example.proyecto2a.casos_uso;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2a.R;
import com.example.proyecto2a.modelo.Stant;
import com.example.proyecto2a.modelo.Usuario;
import com.example.proyecto2a.presentacion.MenuDialogActivity;
import com.example.proyecto2a.presentacion.StantsCercanos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CercanosAdapter extends FirestoreRecyclerAdapter<Stant, CercanosAdapter.ViewHolder>{

    Activity activity;
    private String isUser;
    private double latitud = 0;
    private double longitud = 0;

    private GoogleMap mMap;
    private Stant[] stantsCercania = new Stant[100];


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CercanosAdapter(@NonNull FirestoreRecyclerOptions<Stant> options, Activity activity, String idUser, double latitud, double longitud) {
        super(options);
        this.activity = activity;
        this.isUser = idUser;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    private void actualizarUbicacion(Location location) {
        try{
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
            }
        } catch (Exception err){

        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            actualizarUbicacion(location);
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Stant model) {

        DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.nombreStant.setText(model.getUbicacion());
        holder.clcercano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MenuDialogActivity.class);
                intent.putExtra("idUser", isUser);
                intent.putExtra("nombre", model.getUbicacion());
                activity.startActivity(intent);
            }
        });

        //Posición del stant
        Location locationA = new Location("punto A");
        locationA.setLatitude(model.getPos().getLatitude());
        locationA.setLongitude(model.getPos().getLongitude());

        //Posicion usuario
        Location locationB = new Location("punto B");
        miUbicacion();
        locationB.setLatitude(latitud);
        locationB.setLongitude(longitud);

        //Sacar distancia
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(locationA.getLatitude() - locationB.getLatitude());
        double dLng = Math.toRadians(locationA.getLongitude() - locationB.getLongitude());
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(locationA.getLatitude())) * Math.cos(Math.toRadians(locationB.getLatitude()));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double dist = radioTierra * va2;

        //Mostrar distancia
        Log.d("TAGdistancia", locationB + "," + locationA + ", " + dist);

        if (latitud == 0 && longitud == 0){
            holder.distanica.setText("...");
        } else {
            if (dist > 1){
                holder.distanica.setText(String.format("%.2f", dist)+"km");
            } else {
                holder.distanica.setText(String.format("%.0f", dist*1000)+"m");
            }
        }



    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cercanos_item_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreStant;
        ConstraintLayout clcercano;
        TextView distanica;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreStant = itemView.findViewById(R.id.tvNombreStant);
            clcercano = itemView.findViewById(R.id.clcercano);
            distanica = itemView.findViewById(R.id.tvDistancia);
        }

    }
}

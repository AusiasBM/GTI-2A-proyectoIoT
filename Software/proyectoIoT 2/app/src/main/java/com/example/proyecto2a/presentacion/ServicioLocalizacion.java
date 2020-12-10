package com.example.proyecto2a.presentacion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.proyecto2a.R;

//Per a Localització            Per a Sensors del mòbil
public class ServicioLocalizacion extends Service implements LocationListener {


    //Per a localització
    private static final long CINCO_SEGUNDOS = 5000;
    private LocationManager locationManager;
    private Location location;
    private Criteria criterio;


    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    @Override
    public void onCreate() {
        Toast.makeText(this, "Servicio creado",
                Toast.LENGTH_SHORT).show();

        //Per a localització
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        //Crear la notificació
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Mis Notificaciones",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Descripcion del canal");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificacion =
                new NotificationCompat.Builder(this, CANAL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Servicio Música")
                        .setContentText("Notificación de servicio activo.");
        //Llançar l'aplicació des de la notificació
        PendingIntent intencionPendiente = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), 0);
        notificacion.setContentIntent(intencionPendiente);


        //Servici en primer pla (DECLARAR EN EL MANIFEST)
        startForeground(NOTIFICACION_ID, notificacion.build());

        Toast.makeText(this, "Servicio arrancado " + idArranque,
                Toast.LENGTH_SHORT).show();


        //LOCALITZACIÓ
        //Per a actualitzar la posició cada 5 segons, distància minima per a mostra el canvi de 0 metres

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criterio, true), CINCO_SEGUNDOS, 0, this);

        return START_STICKY;
    }

    //Accions per a donar per acabat el servici
    @Override public void onDestroy() {
        Toast.makeText(this,"Servicio detenido",
                Toast.LENGTH_SHORT).show();
        notificationManager.cancel(NOTIFICACION_ID);
        locationManager.removeUpdates(this);

    }


    @Override public IBinder onBind(Intent intencion) {
        return null;
    }


    private void actualizaMejorLocaliz(Location localiz) {
        location = localiz;
        Log.d("Localización: ", "" + location.getLatitude() + " " + location.getLongitude());
        //Crear una intenció amb el setAcTion creat per mí i declarat al manifest, i enviar-ho broadcast
        Intent i = new Intent();
        i.setAction("com.example.exempleexam20192.LATITUD_LONGITUD");
        i.putExtra("latitud", location.getLatitude());
        i.putExtra("longitud", location.getLongitude());
        sendBroadcast(i);
    }


    @Override
    public void onLocationChanged(@NonNull Location localizacion) {
        actualizaMejorLocaliz(localizacion);
    }

}
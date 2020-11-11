package org.example.comun;

import com.apple.eawt.Application;

public class Mqtt extends Application {
    public static final String TAG = "MQTT";
    public static final String topicRoot="ausiasbm/practica/";//Reemplaza jtomas
    public static final int qos = 1;
    public static final String broker = "tcp://mqtt.eclipse.org:1883";
    public static final String clientId = "ausiasPrueba";
}
package com.example.androidthings;

public class Mqtt {
    public static final String TAG = "MQTT";
    public static final String topicRoot="arduino/";
    //Reemplaza equipo por el nombre de tu equipo de prácticas
    public static final int qos = 1;
    public static final String broker = "tcp://broker.hivemq.com:1883";
    public static final String clientId = "raspberry";
    //Reemplaza ClientId con un valor diferente
}
package com.example.uartandroidarduino;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.JSONException;

import static android.content.ContentValues.TAG;

public class ArduinoUart extends Activity {

    ArrayList<String> llaves = new ArrayList<>();
    private UartDevice uart;
    private JSONObject json;
    public ArduinoUart(String nombre, int baudios) {
        try {
            uart = PeripheralManager.getInstance().openUartDevice(nombre);
            uart.setBaudrate(baudios);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
            uart.registerUartDeviceCallback(callback); //listener per quan el ESP32 envia informació
            llaves.add("8eec5fd3");
            llaves.add("7134d8");
            llaves.add("44d4922ee6480");
        } catch (IOException e) {
            Log.w(TAG, "Error iniciando UART", e);
        }
    }


    public void escribir(String s) {
        try {
            int escritos = uart.write(s.getBytes(), s.length());
            Log.d(TAG, escritos + " bytes escritos en UART");
        } catch (IOException e) {
            Log.w(TAG, "Error al escribir en UART", e);
        }
    }
    public String leer() {
        String s = "";
        int len;
        final int maxCount = 8; // Máximo de datos leídos cada vez
        byte[] buffer = new byte[maxCount];
        try {
            do {
                len = uart.read(buffer, buffer.length);
                for (int i=0; i<len; i++) {
                    s += (char)buffer[i];
                }
            } while(len>0);
        } catch (IOException e) {
            Log.w(TAG, "Error al leer de UART", e);
        }
        return s;
    }

    public void cerrar() {
        if (uart != null) {
            try {
                uart.close();
                uart = null;
            } catch (IOException e) {
                Log.w(TAG, "Error cerrando UART", e);
            }
        }
    }
    static public List<String> disponibles() {
        return
                PeripheralManager.getInstance().getUartDeviceList();
    }


    public UartDeviceCallback callback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uartDevice) {
            String msn = leer();
            String codigoRecibido = "";
            boolean existe = false;
            Log.d(TAG, "Recibido de Arduino: " + msn);
            try {
                json = new JSONObject(msn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                codigoRecibido = (String) json.get("tarjeta");
                Log.d(TAG, "Usuario: " + codigoRecibido);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < llaves.size(); i++) {
                if (llaves.get(i).equals(codigoRecibido)){
                    Log.d(TAG, "Mandado a Arduino: 1");
                    escribir("1");
                    existe = true;
                }
            }

            if (!existe){
                Log.d(TAG, "Mandado a Arduino: 0");
                escribir("0");
            }

            return true;
        }
    };
}

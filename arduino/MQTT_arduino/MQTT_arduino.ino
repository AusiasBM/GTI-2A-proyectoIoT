
#include <ArduinoMqttClient.h>
#include <WiFi.h>

#include "HX711.h"
#include <SPI.h>
#include <MFRC522.h>
#include "Tilt.h";

#define RST_PIN 22 //Pin 9 para el reset del RC522 no es necesario conctarlo
#define SS_PIN 21 //Pin 10 para el SS (SDA) del RC522
#define pinSensorMagnetico 27 //Pin 27 para el sensor magnético
#define TILT_PIN 4 //pin 4 para el sensor tilt
#define DOUT_PIN 5 //Pin 5 para DT del sensor de peso
#define SCK_PIN 18 //Pin 18 para SCK del sensor de peso

#define VALOR_MEDIO_ALERTA 0.5 //Valor del Tilt para enviar una alerta
#define SIZE_BUFFER 18;
#define MAX_SIZE_BLOCK 16;


Tilt tilt; //Definición de la clase Tilt
struct PatinGuardado{
  bool guardado = false;
  double peso = 0;
};
PatinGuardado patin;

MFRC522 mfrc522(SS_PIN, RST_PIN); ///Creamos el objeto para el RC522
MFRC522::StatusCode status; //variable to get card status

//Servo myservo;  // crea el objeto servo
 
int pos = 0;    // posicion del servo

//char ssid[] = "Team_2-1";        // your network SSID (name)
//char pass[] = "Team_2-1";    // your network password (use for WPA, or use as key for WEP)
char ssid[] = "FERCATEL_ACA6";        // your network SSID (name)
char pass[] = "74649518"; 

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const int cerradura = 14;

const char broker[]    = "broker.hivemq.com";
int        port        = 1883;
const char willTopic[] = "arduino/will";
const char cerraduraTopic[]   = "arduino/cerradura";
const char RFIDTopic[]  = "arduino/rfid";
const char magneticoTopic[]  = "arduino/magnetico";
const char alarmaTopic[]  = "arduino/alarma";
const char pesoTopic[] = "arduino/peso";

const long interval = 5000;
unsigned long previousMillis = 0;

int count = 0;
HX711 balanza;
void setup() {
  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  SPI.begin(); //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos el MFRC522 - Cuando pone PCD se refiere al modulo lector
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  //myservo.attach(13);  // vincula el servo al pin digital 13

  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }

  Serial.println("You're connected to the network");
  Serial.println();

  String willPayload = "oh no!";
  bool willRetain = true;
  int willQos = 1;

  mqttClient.beginWill(willTopic, willPayload.length(), willRetain, willQos);
  mqttClient.print(willPayload);
  mqttClient.endWill();

  Serial.print("Conectando al MQTT broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("La conexion a fallado! Error code = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }

  Serial.println("Estas conectado al MQTT broker!");
  Serial.println();

  // set the message receive callback
  mqttClient.onMessage(onMqttMessage);

  Serial.print("Suscribiendote al topic: ");
  Serial.println(cerraduraTopic);
  Serial.println(alarmaTopic);

  // subscribe to a topic
  // the second parameter set's the QoS of the subscription,
  // the the library supports subscribing at QoS 0, 1, or 2
  int subscribeQos = 1;

  mqttClient.subscribe(cerraduraTopic, subscribeQos);
  mqttClient.subscribe(alarmaTopic, subscribeQos);
  mqttClient.subscribe(pesoTopic, subscribeQos);
  
  // topics can be unsubscribed using:
  // mqttClient.unsubscribe(cerraduraTopic);

  Serial.print("Waiting for messages on topic: ");
  Serial.println(cerraduraTopic);
  Serial.println(alarmaTopic);

  pinMode(cerradura, OUTPUT);
  digitalWrite(cerradura, HIGH);

  //Sensor magnético
  pinMode(pinSensorMagnetico, INPUT_PULLUP); //DEFINE PIN COMO ENTRADA / "_PULLUP" PARA ACTIVAR EL RESISTOR INTERNO
  //DE ARDUINO PARA GARANTIRAE QUE NO EXISTA VARIACIÓN ENTRE 0 (LOW) y 1 (HIGH)

  //Sensor Peso
  balanza.begin(DOUT_PIN, SCK_PIN);
  Serial.print("Lectura del valor del ADC:  ");
  Serial.println(balanza.read());
  Serial.println("No ponga ningun  objeto sobre la balanza");
  Serial.println("Destarando...");
  Serial.println("...");
  balanza.set_scale(-190480); // Establecemos la escala
  balanza.tare(20);  //El peso actual es considerado Tara.

  //Configuración del pin y objeto Tilt
  pinMode(TILT_PIN , INPUT);  //Configurar el pin TILT_PIN como input
  digitalWrite(TILT_PIN , HIGH);  //activamos la resistencia interna PULL UP 
  tilt = Tilt(TILT_PIN);  //Declaración de un objeto de la clase Tilt 
}

byte ActualUID[7]; //almacenará el código del Tag leído

bool estadoAnteriorCerradura = false;//Variable para evitar envios constantes cuando la cerradura está cerrada

void loop() {
  bool retained = false;
  int qos = 1;
  bool dup = false;
  String payload = "";
  mqttClient.poll();

  //Sensor magnético
  if (digitalRead(pinSensorMagnetico) == HIGH){//SI LA LECTURA DEL PIN ES HIGH...
    if (estadoAnteriorCerradura == false){
      payload = "cerraduraAbierta";
      mqttClient.beginMessage(magneticoTopic, payload.length(), retained, qos, dup);
      mqttClient.print(payload);
      mqttClient.endMessage();
      estadoAnteriorCerradura = true;
    }
  }
  else{ //SEÑAL, FALSA
     if(estadoAnteriorCerradura == true && digitalRead(pinSensorMagnetico) == LOW){
      payload = "cerraduraCerrada";
      mqttClient.beginMessage(magneticoTopic, payload.length(), retained, qos, dup);
      mqttClient.print(payload);
      mqttClient.endMessage();
      estadoAnteriorCerradura = false;
     }
  }
  
   
  if ( mfrc522.PICC_IsNewCardPresent())
  {

    payload = "";
    //Seleccionamos una tarjeta
    if ( mfrc522.PICC_ReadCardSerial())
    {
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        ActualUID[i]=mfrc522.uid.uidByte[i];
        payload += String(ActualUID[i], HEX);
      }

        Serial.print("Sending message to topic: ");
        Serial.println(RFIDTopic);
        Serial.println(payload);

        delay(2000); // Este delay es para cuando el usuario esté poniendo la tarjeta en el lector solo se envie una vez.
    
        mqttClient.beginMessage(RFIDTopic, payload.length(), retained, qos, dup);
        mqttClient.print(payload);
        mqttClient.endMessage();
    
        Serial.println();
    }
  }

  //Sensor peso
  patin.peso = balanza.get_units(20);
  
  Serial.print(patin.peso);
  if (patin.peso>=0.2 && patin.guardado == false){
    payload = "Patin guardado";
    mqttClient.beginMessage(pesoTopic, payload.length(), retained, qos, dup);
    mqttClient.print(payload);
    mqttClient.endMessage();
    patin.guardado = true;
  } else if(patin.peso<0.2 && patin.guardado == true){
    payload = "Patin no guardado";
    mqttClient.beginMessage(pesoTopic, payload.length(), retained, qos, dup);
    mqttClient.print(payload);
    mqttClient.endMessage();
    patin.guardado == false;
  }

  //Sensor Tilt
  double resTilt = tilt.media();
  Serial.print("Prova de resultat de mitjana"); 
  Serial.println(resTilt);
  if (resTilt > VALOR_MEDIO_ALERTA){
    Serial.println("Alerta Alerta!!");
    payload = "Alerta!";
    mqttClient.beginMessage(alarmaTopic, payload.length(), retained, qos, dup);
    mqttClient.print(payload);
    mqttClient.endMessage();
  }
}

void onMqttMessage(int messageSize) {
  String cadena = "";
  // we received a message, print out the topic and contents
  Serial.print("Received a message with topic '");
  Serial.print(mqttClient.messageTopic());
  Serial.print("', duplicate = ");
  Serial.print(mqttClient.messageDup() ? "true" : "false");
  Serial.print(", QoS = ");
  Serial.print(mqttClient.messageQoS());
  Serial.print(", retained = ");
  Serial.print(mqttClient.messageRetain() ? "true" : "false");
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");

  // use the Stream interface to print the contents
  while (mqttClient.available()) {
    cadena += (char)mqttClient.read();
  }
  Serial.println(cadena);

  if(cadena == "cerradura ON"){
    Serial.println(cadena);
    digitalWrite(cerradura, LOW);
    delay(1000);
    digitalWrite(cerradura, HIGH);
  }
  Serial.println();

  Serial.println();


}

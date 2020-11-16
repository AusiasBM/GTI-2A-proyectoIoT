
#include <ArduinoMqttClient.h>
#include <WiFi.h>

#include <SPI.h>
#include <MFRC522.h>
#define RST_PIN 22 //Pin 9 para el reset del RC522 no es necesario conctarlo
#define SS_PIN 21 //Pin 10 para el SS (SDA) del RC522
#define SIZE_BUFFER 18;
#define MAX_SIZE_BLOCK 16;
MFRC522 mfrc522(SS_PIN, RST_PIN); ///Creamos el objeto para el RC522
MFRC522::StatusCode status; //variable to get card status


char ssid[] = "TP-Link_701E";        // your network SSID (name)
char pass[] = "28333401";    // your network password (use for WPA, or use as key for WEP)


WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const int cerradura = 14;

const char broker[]    = "mqtt.eclipse.org";
int        port        = 1883;
const char willTopic[] = "arduino/will";
const char inTopic[]   = "arduino/in";
const char outTopic[]  = "arduino/out";

const long interval = 5000;
unsigned long previousMillis = 0;

int count = 0;

void setup() {
  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  SPI.begin(); //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos el MFRC522 - Cuando pone PCD se refiere al modulo lector
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

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

  // You can provide a unique client ID, if not set the library uses Arduin-millis()
  // Each client must have a unique client ID
  // mqttClient.setId("clientId");

  // You can provide a username and password for authentication
  // mqttClient.setUsernamePassword("username", "password");

  // By default the library connects with the "clean session" flag set,
  // you can disable this behaviour by using
  // mqttClient.setCleanSession(false);

  // set a will message, used by the broker when the connection dies unexpectantly
  // you must know the size of the message before hand, and it must be set before connecting
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
  Serial.println(inTopic);
  Serial.println();

  // subscribe to a topic
  // the second parameter set's the QoS of the subscription,
  // the the library supports subscribing at QoS 0, 1, or 2
  int subscribeQos = 1;

  mqttClient.subscribe(inTopic, subscribeQos);

  // topics can be unsubscribed using:
  // mqttClient.unsubscribe(inTopic);

  Serial.print("Waiting for messages on topic: ");
  Serial.println(inTopic);
  Serial.println();

  pinMode(cerradura, OUTPUT);
  digitalWrite(cerradura, LOW);
}

byte ActualUID[7]; //almacenará el código del Tag leído

void loop() {
  mqttClient.poll();

  if ( mfrc522.PICC_IsNewCardPresent())
  {

    //Seleccionamos una tarjeta
    if ( mfrc522.PICC_ReadCardSerial())
    {
      
      String payload = "";
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        //M5.Lcd.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        //M5.Lcd.print(mfrc522.uid.uidByte[i], HEX);
        ActualUID[i]=mfrc522.uid.uidByte[i];
        payload += String(ActualUID[i], HEX);
      }

        Serial.print("Sending message to topic: ");
        Serial.println(outTopic);
        Serial.println(payload);
   
        bool retained = false;
        int qos = 1;
        bool dup = false;
    
        mqttClient.beginMessage(outTopic, payload.length(), retained, qos, dup);
        mqttClient.print(payload);
        mqttClient.endMessage();
    
        Serial.println();
    }
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

  if(cadena == "ON"){
    digitalWrite(cerradura, HIGH);
    delay(1000);
    digitalWrite(cerradura, LOW);
  }
  
  Serial.println();

  Serial.println();


}

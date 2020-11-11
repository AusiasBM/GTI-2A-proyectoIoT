#define BLANCO 0XFFFF
#define NEGRO 0
#define ROJO 0xF800
#define VERDE 0x07E0
#define AZUL 0x001F
#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>
#define RST_PIN 2 //Pin 9 para el reset del RC522 no es necesario conctarlo
#define SS_PIN 21 //Pin 10 para el SS (SDA) del RC522
MFRC522 mfrc522(SS_PIN, RST_PIN); ///Creamos el objeto para el RC522
MFRC522::StatusCode status; //variable to get card status

const uint8_t cerradura = 25;

void setup() {
 
  M5.begin();
  Serial.begin(115200);
  SPI.begin(); //Iniciamos el Bus SPI
  mfrc522.PCD_Init(); // Iniciamos el MFRC522 - Cuando pone PCD se refiere al modulo lector
  M5.Lcd.setTextSize(2);
  M5.Lcd.setCursor(90, 10);
  M5.Lcd.setTextColor(BLANCO);
  M5.Lcd.println("BIENVENIDO");
  
  pinMode(cerradura, OUTPUT);
  digitalWrite(cerradura, LOW);

}

byte ActualUID[7]; //almacenará el código del Tag leído
String tarjeta;
bool respuesta;

void loop() {
  if ( mfrc522.PICC_IsNewCardPresent())
  {
    //Seleccionamos una tarjeta
    if ( mfrc522.PICC_ReadCardSerial())
    {
      tarjeta = "";
      M5.Lcd.setTextSize(2);
      M5.Lcd.setCursor(0, 30);
      M5.Lcd.fillScreen(NEGRO); // Pone la pantalla toda en negro (Limpia la pantalla)
      M5.Lcd.setTextColor(AZUL);
      //M5.Lcd.print(F("CLIENTE:"));

      // Enviamos serialemente su UID
      // lo podemos descomentar para saber el ID de la tarjeta RFID
      for (byte i = 0; i < mfrc522.uid.size; i++) {
        //M5.Lcd.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        //M5.Lcd.print(mfrc522.uid.uidByte[i], HEX);
        ActualUID[i]=mfrc522.uid.uidByte[i];
        tarjeta += String(ActualUID[i], HEX);
      }

      // Envia el código de la tarjeta a la RP
      Serial.println("{\"tarjeta\":" + tarjeta + "}"); 

      respuesta = false;

      do{

         if (Serial.available() > 0) {
            char command = (char) Serial.read(); 

            switch (command) {
              case '0':  
                M5.Lcd.setTextColor(ROJO);
                M5.Lcd.println("NO REGISTRADO");
                M5.Lcd.setTextSize(3);
                M5.Lcd.setCursor(10, 90);
                M5.Lcd.setTextColor(ROJO);
                M5.Lcd.println("INTENTAR OTRA VEZ");
                M5.Lcd.setTextSize(2); 
                break;
              case '1':
                M5.Lcd.setTextColor(VERDE);
                M5.Lcd.setTextSize(3);
                M5.Lcd.setCursor(20, 90);
                M5.Lcd.setTextColor(VERDE);
                M5.Lcd.println("TAQUILLA ABIERTA");
                M5.Lcd.setTextSize(2);
                digitalWrite(cerradura, HIGH);
            }
            
            respuesta = true; 
         }
        
      }while( !respuesta );

      // Terminamos la lectura de la tarjeta tarjeta actual
      mfrc522.PICC_HaltA();
      M5.Lcd.setCursor(50, 150);
      M5.Lcd.setTextColor(BLANCO);
      M5.Lcd.println("SIGUIENTE CLIENTE");
      delay(1000);
      digitalWrite(cerradura, LOW);

    }
  }
}

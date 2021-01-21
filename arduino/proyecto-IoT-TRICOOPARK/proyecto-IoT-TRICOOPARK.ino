#include <ArduinoMqttClient.h>
#include <WiFi.h>

#include "HX711.h"
#include "driver\timer.h"

#define pinSensorMagnetico 27 //Pin 27 para el sensor magnético
#define TILT_PIN 4 //pn 4 para el sensor tilt
#define DOUT_PIN 5 //Pin 5 para DT del sensor de peso
#define SCK_PIN 18 //Pin 18 para SCK del sensor de peso
#define cerradura 14

#define VALOR_MEDIO_ALERTA 0.30 //Valor del Tilt para enviar una alerta
#define MAX_SIZE_BLOCK 16;

#define QOS 1

#define TIMER_DIVIDER 80
#define TIMER_SCALE (TIMER_BASE_CLK / TIMER_DIVIDER)
#define TIMER_INERVAL_SEC 30
#define ESP_INTR_FLAG_TIMER0 ESP_INTR_FLAG_LEVEL3


//====================================================================================
//      TIMER PARA TEMPORIZADOR DE RESERVA
timer_group_t timer_group = TIMER_GROUP_0;
timer_idx_t timer_idx = TIMER_0;


//====================================================================================
//      SEMÁFOROS
SemaphoreHandle_t xSemaphoreAlarma = NULL;
SemaphoreHandle_t xSemaphoreMagnetico = NULL;
SemaphoreHandle_t xSemaphorePeso = NULL;
SemaphoreHandle_t xSemaphoreTiempo = NULL;
SemaphoreHandle_t xMutex;


//====================================================================================
//      OBJETOS Y STRUCTS

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);
HX711 balanza;

struct PatinGuardado{
  bool guardado = false;
  double peso = 0;
};
PatinGuardado patin;


//====================================================================================
//        WIFI

char ssid[] = "Team_2-1";        // your network SSID (name)
char pass[] = "Team_2-1";    // your network password (use for WPA, or use as key for WEP)


//====================================================================================
//       MQTT

const char broker[]    = "broker.hivemq.com";
uint16_t        port        = 1883;
const char willTopic[] = "arduino/will";
const char cerraduraTopic[]   = "arduino/cerradura";
const char RFIDTopic[]  = "arduino/rfid";
const char magneticoTopic[]  = "arduino/magnetico";
const char alarmaTopic[]  = "arduino/alarma";
const char pesoTopic[] = "arduino/peso";
const char reservaTopic[] = "arduino/tiempoReserva";
String payload = "";
boolean retained = false;
boolean dupa = false;


//Función para reconectar con el broker de MQTT en caso de perder la conexión
void reconectar(){
  if (!mqttClient.connect(broker, port)) {
      Serial.print("La conexion Ha fallado! Error code = ");
      Serial.println(mqttClient.connectError());
      while (1);
  }
 
  mqttClient.subscribe(cerraduraTopic, QOS);
  mqttClient.subscribe(alarmaTopic, QOS);
  mqttClient.subscribe(reservaTopic, QOS);
   
}

//====================================================================================
//        SENSOR MAGNETICO

//Rutina atención a interrupción cerradura
//La interrupción se producirá cada vez que el sensor magnético cambie de estado
void IRAM_ATTR ISR_MAGNETICO(){
  xSemaphoreGiveFromISR(xSemaphoreMagnetico, NULL);
}

//Variable para evitar envios constantes cuando la cerradura está cerrada
bool estadoAnteriorCerradura = false;

//Tarea para enviar MQTT si la 
void T_magnetico(void *pvParameter){
  while(1){
    if(xSemaphoreTake(xSemaphoreMagnetico, portMAX_DELAY)== pdTRUE){
       if(!mqttClient.connected()) {
          reconectar();
       }
      if (digitalRead(pinSensorMagnetico) == HIGH){  
        if (estadoAnteriorCerradura == false){
          payload = "cerraduraAbierta";
          mqttClient.beginMessage(magneticoTopic, payload.length(), retained, QOS, dupa);
          mqttClient.print(payload);
          mqttClient.endMessage();
          estadoAnteriorCerradura = true;
  
          //Mientras la puerta esté abierta que compruebe el peso 
          xSemaphoreGive(xSemaphorePeso);
        }  
      }else{
        if(estadoAnteriorCerradura == true && digitalRead(pinSensorMagnetico) == LOW){
          payload = "cerraduraCerrada";
          mqttClient.beginMessage(magneticoTopic, payload.length(), retained, QOS, dupa);
          mqttClient.print(payload);
          mqttClient.endMessage();
          estadoAnteriorCerradura = false;
        }
      }
    }
  }
}


//====================================================================================
//      PESO

void T_peso(void *pvParameter){
  while(1){
    if(xSemaphoreTake(xSemaphorePeso, portMAX_DELAY) == pdTRUE){
      //Mientras la cerradura esté abierta comprobará el peso
      while(estadoAnteriorCerradura == true){
         //Sensor peso
        patin.peso = balanza.get_units(10),0;

        if(!mqttClient.connected()) {
          reconectar();
       }
        Serial.print("Peso: ");
        Serial.println(patin.peso);
        //patin.peso *= (-1);
        if (patin.peso>=0.3 && patin.guardado == false){

            payload = "Patin guardado";
            mqttClient.beginMessage(pesoTopic, payload.length(), retained, QOS, dupa);
            mqttClient.print(payload);
            mqttClient.endMessage();
            patin.guardado = true;

          
        } else if(patin.peso<0.3 && patin.guardado == true){

              payload = "Patin no guardado";
              mqttClient.beginMessage(pesoTopic, payload.length(), retained, QOS, dupa);
              mqttClient.print(payload);
              mqttClient.endMessage();
              patin.guardado = false;

        }
        vTaskDelay(50/portTICK_RATE_MS);
      }
    } 
    Serial.println("T_peso");
  }
}




//======================================================================================
//       ALARMA
uint8_t count = 0;
uint8_t medidas[100];
boolean golpe = false;


void T_tilt(void *pvParameter){
  double media;
  while(1){
    
   
    uint8_t medida = digitalRead(TILT_PIN);
    medidas[count] = medida;
    media = mediaTilt();
 
    if(media > VALOR_MEDIO_ALERTA && golpe == false){
      golpe = true;
      xSemaphoreGive(xSemaphoreAlarma);
    }else if(media <= VALOR_MEDIO_ALERTA && golpe == true){
      golpe = false;
    }
    
    if(count == 99){
      count = 0;
    }else{
      count++;
    }
    vTaskDelay(10/portTICK_RATE_MS);
  } 
}

double mediaTilt(){
  uint8_t suma = 0;
  for(int i = 0; i < 100; i++){
    suma += medidas[i];
  }
  //Serial.println(suma/100.0);
  return suma/100.0;
}

void T_alarma(void *pvParameter){
  while(1){
    if (xSemaphoreTake(xSemaphoreAlarma, portMAX_DELAY) == pdTRUE){
      if (xSemaphoreTake( xMutex, portMAX_DELAY )==pdTRUE){
        if(!mqttClient.connected()) {
          reconectar();
       }
        Serial.println("Alerta Alerta!!");       
        payload = "Alerta!";
        mqttClient.beginMessage(alarmaTopic, payload.length(), retained, QOS, dupa);
        mqttClient.print(payload);
        mqttClient.endMessage();
        
      }
      xSemaphoreGive(xMutex);     
    }
  }
}

//======================================================================================
//       TIMER

void IRAM_ATTR ISR_Timer0(void* arg){
      
      xSemaphoreGiveFromISR(xSemaphoreTiempo, NULL);
      TIMERG0.int_clr_timers.t0 = 1;
      timer_set_alarm(TIMER_GROUP_0, TIMER_0, TIMER_ALARM_EN);
      timer_pause(timer_group, timer_idx);
}

void T_finReserva(void*pvParameter){
  while(1){
    if(xSemaphoreTake(xSemaphoreTiempo, portMAX_DELAY) == pdTRUE){
      if (xSemaphoreTake( xMutex, portMAX_DELAY )==pdTRUE){
        if(!mqttClient.connected()) {
          reconectar();
        }
        payload = "parar";
        mqttClient.beginMessage(reservaTopic, payload.length(), retained, QOS, dupa);
        mqttClient.print(payload);
        mqttClient.endMessage();
        Serial.println("Parar");
      }
      xSemaphoreGive(xMutex);  
    }
  }
}
//======================================================================================
//      
void T_escuchando(void*pvParameter){
  while(1){
    mqttClient.poll();
    
    vTaskDelay(10/portTICK_RATE_MS);
  }
}


void setup() {
  Serial.begin(9600);

  //========================================================================================
  //Configuración del wifi
  
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print("NO CONECTADO");
    delay(5000);
  }

  Serial.println("You're connected to the network");
  Serial.println();


  //========================================================================================
  //Configuración MQTT
  
  bool willRetain = true;
  String willPayload = "oh no!";
  mqttClient.beginWill(willTopic, willPayload.length(), willRetain, QOS);
  mqttClient.print(willPayload);
  mqttClient.endWill();
  
  Serial.print("Conectando al MQTT broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("La conexion Ha fallado! Error code = ");
    Serial.println(mqttClient.connectError());
    while (1);
  }

  Serial.println("Estas conectado al MQTT broker!");
  Serial.println();

  mqttClient.onMessage(onMqttMessage);
 
  mqttClient.subscribe(cerraduraTopic, QOS);
  mqttClient.subscribe(alarmaTopic, QOS);
  mqttClient.subscribe(reservaTopic, QOS);

  //=========================================================
  //Configuración pines e interrupciones
  pinMode(cerradura, OUTPUT);
  digitalWrite(cerradura, HIGH);
  
  pinMode(TILT_PIN , INPUT);  //Configurar el pin TILT_PIN como input
  digitalWrite(TILT_PIN , HIGH);
  
  pinMode(pinSensorMagnetico, INPUT_PULLUP);

  attachInterrupt(digitalPinToInterrupt(pinSensorMagnetico), ISR_MAGNETICO, CHANGE);

  balanza.begin(DOUT_PIN, SCK_PIN);
  Serial.print("Lectura del valor del ADC:  ");
  Serial.println(balanza.read());
  Serial.println("No ponga ningun  objeto sobre la balanza");
  Serial.println("Destarando...");
  Serial.println("...");
  balanza.set_scale(192440.36); // Establecemos la escala
  balanza.tare(20);  //El peso actual es considerado Tara.
//608695.652
  //=========================================================
  //Semáforos
  xSemaphoreAlarma = xSemaphoreCreateBinary();
  xSemaphoreMagnetico = xSemaphoreCreateBinary();
  xSemaphorePeso = xSemaphoreCreateBinary();
  xSemaphoreTiempo = xSemaphoreCreateBinary();
  xMutex = xSemaphoreCreateMutex();

  //=========================================================
  //Tareas
  xTaskCreate(&T_tilt, "T_tilt", 1024*4, NULL, 2, NULL);
  xTaskCreate(&T_alarma, "T_alarma", 1024*8, NULL, 2, NULL);
  xTaskCreate(&T_magnetico, "T_magnetico", 1024*8, NULL, 2, NULL);
  xTaskCreate(&T_peso, "T_peso", 1024*8, NULL, 2, NULL);
  xTaskCreate(&T_escuchando, "T_escuchando", 1024*4, NULL, 2, NULL);
  xTaskCreate(&T_finReserva,"T_finReserva", 1024*8, NULL, 2, NULL);

  //=========================================================
  //Timer
  timer_config_t config_timer;
  config_timer.alarm_en = 1;
  config_timer.auto_reload = 1;
  config_timer.counter_dir = TIMER_COUNT_UP;
  config_timer.divider = (uint16_t)TIMER_DIVIDER;
  config_timer.intr_type = TIMER_INTR_LEVEL;
  config_timer.counter_en = TIMER_PAUSE;

  timer_init(timer_group, timer_idx, &config_timer);
  timer_pause(timer_group, timer_idx);
  timer_set_counter_value(timer_group, timer_idx, 0X00000000ULL);
  timer_set_alarm_value(timer_group, timer_idx, TIMER_INERVAL_SEC*TIMER_SCALE);// (10 segons)
  timer_enable_intr(timer_group, timer_idx);
  timer_isr_register(timer_group, timer_idx, ISR_Timer0, (void*) timer_idx, ESP_INTR_FLAG_TIMER0, NULL);
}

void loop() {
  // put your main code here, to run repeatedly:

}

//Callback donde recibe mensages MQTT
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
    abrirCerradura();
  }

  if(cadena == "inicio reserva"){
    timer_start(timer_group, timer_idx);
    Serial.println("RESERVADA!!");
  }else if(cadena == "parar"){
    TIMERG0.int_clr_timers.t0 = 1;
    timer_set_alarm(TIMER_GROUP_0, TIMER_0, TIMER_ALARM_EN);
    timer_pause(timer_group, timer_idx);
    Serial.println("DESRESERVADA!!");
  }
  
  Serial.println();
  Serial.println();
}


void abrirCerradura(){
  Serial.println("Inicio");
  digitalWrite(cerradura, LOW);
  delay(1000);
  digitalWrite(cerradura, HIGH);
  Serial.println("Fin");
}

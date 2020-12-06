// ---------------------------------------------------
//
// Tilt.cpp
//
// ---------------------------------------------------
#include "Tilt.h"


//Constructor por defecto
Tilt::Tilt(){};


//Constructor
Tilt::Tilt(int digitalPinV)
  : digitalPin(digitalPinV)
{}

//Método privado para coger medida del sensor (0 o 1) y guardarla en la lista medidas
void Tilt::medir(){
  int medida;
 
  for(int i = 0; i < 100; i++){
    medida = digitalRead((*this).digitalPin);
    (*this).medidas[i] = medida;
    delay(5);
  } 
}


//Método para medir la media de las últimas 100 medidas
double Tilt::media(){
  medir();
  int suma = 0;
  
  for(int i = 0; i < 100; i++){
    suma += (*this).medidas[i];
  }
  Serial.println(suma);
  Serial.println(suma/100.0);
  return suma/100.0;
}

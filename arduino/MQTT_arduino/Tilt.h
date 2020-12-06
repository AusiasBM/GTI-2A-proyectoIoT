//-------------
//
//  tilt.h 
//
//--------------

#ifndef TILT_H
#define TILT_H

#include <Arduino.h>

class Tilt{
  private:
          int digitalPin; //Número del pin digital a utilizar
          int medidas[200];
          int medida;

          void medir();
         
  public:
          Tilt();
          Tilt(int);
          double media();  
};

#endif

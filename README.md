# proyectoIoT

Conexiones RELE

	VCC --> 5V
	IN1 --> 14
	GND

Conexiones RFID

	SDA 	-->	21
	SCK 	-->	18
	MOSI 	-->	23
	MISO 	-->	19
	3.3V
	GND

BROKER

	Topic de arduino --> arduino/	

	Topic rfid 	--> arduino/rfid ( envia el número de la tarjeta leida )
	Topic cerradura --> arduino/cerradura ("cerradura ON" enviar para abrir la cerradura)



	
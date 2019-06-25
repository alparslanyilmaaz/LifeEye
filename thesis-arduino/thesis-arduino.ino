#include <Servo.h>

Servo motor;

int deger;
int derece;

const int trig = 2;
const int echo = 3;

int sure = 0;
int mesafe = 0;

float gecmis = 0;
float simdiki = 0;
float holder = 0;

void calculate(){
  holder = holder + 3000;
  digitalWrite(trig, HIGH);
  delayMicroseconds(1000);
  digitalWrite(trig, LOW);
  sure = pulseIn(echo,HIGH);
  mesafe = (sure/2)/29.1;
  Serial.println(mesafe);
}
void setup() {
  motor.attach(7);
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  Serial.begin(9600);
}

void loop() {
  simdiki = millis();
  deger = analogRead(A0);
  derece = map(deger, 0, 1023,0, 180);
  motor.write(derece);

  if(holder == simdiki){
    calculate();
  }
}

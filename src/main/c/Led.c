#include <wiringPi.h>
#include <jni.h>
#include <stdio.h>
#include "Led.h"

#define LEDPin 0

JNIEXPORT void JNICALL Java_com_covisint_platform_device_pi_jni_Led_on
  (JNIEnv *env, jobject obj) 
{
  wiringPiSetup();
  pinMode(LEDPin, OUTPUT);
  digitalWrite(LEDPin, LOW);
}

JNIEXPORT void JNICALL Java_com_covisint_platform_device_pi_jni_Led_off
  (JNIEnv *env, jobject obj)
{
  wiringPiSetup();
  pinMode(LEDPin, OUTPUT);
  digitalWrite(LEDPin, HIGH);
}

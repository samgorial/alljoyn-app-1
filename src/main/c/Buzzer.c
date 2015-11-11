#include <wiringPi.h>
#include <jni.h>
#include <stdio.h>
#include <softTone.h>
#include "Buzzer.h"

#define BuzPin    0

JNIEXPORT void JNICALL Java_com_covisint_platform_device_pi_jni_Buzzer_on
  (JNIEnv *env, jobject obj) 
{
wiringPiSetup();
softToneCreate(BuzPin);
while(1) {
  softToneWrite(BuzPin, 248);
  delay(9999);
}
}

JNIEXPORT void JNICALL Java_com_covisint_platform_device_pi_jni_Buzzer_off
  (JNIEnv *env, jobject obj)
{
wiringPiSetup();
softToneCreate(BuzPin);
softToneStop(BuzPin);
}

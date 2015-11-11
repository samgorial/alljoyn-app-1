#include <wiringPi.h>
#include <jni.h>
#include <softPwm.h>
#include <stdio.h>
#include "StatusLight.h"

#define uchar unsigned char

#define LedPinRed    0
#define LedPinGreen  1
#define LedPinBlue   2

void ledInit(void)
{
	softPwmCreate(LedPinRed,  0, 100);
	softPwmCreate(LedPinGreen,0, 100);
	softPwmCreate(LedPinBlue, 0, 100);
}

void ledColorSet(uchar r_val, uchar g_val, uchar b_val)
{
	softPwmWrite(LedPinRed,   r_val);
	softPwmWrite(LedPinGreen, g_val);
	softPwmWrite(LedPinBlue,  b_val);
}

JNIEXPORT void JNICALL Java_com_covisint_platform_device_pi_jni_StatusLight_setColor
  (JNIEnv *env, jobject obj, jint r, jint g, jint b)
{
  wiringPiSetup();
  ledInit();
  ledColorSet((unsigned char)r, (unsigned char)g, (unsigned char)b);
}

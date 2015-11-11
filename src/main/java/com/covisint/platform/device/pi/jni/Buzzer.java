package com.covisint.platform.device.pi.jni;

public class Buzzer {

	static {
		System.loadLibrary("buzzer");
	}

	public native void on();

	public native void off();

}

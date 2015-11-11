package com.covisint.platform.device.pi.jni;

public class Led {

	static {
		System.loadLibrary("led");
	}

	public native void on();

	public native void off();

}

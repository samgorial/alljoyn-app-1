package com.covisint.platform.device.pi.jni;

public class Thermometer {

	static {
		System.loadLibrary("thermometer");
	}

	public native double temperature();

}

package com.covisint.platform.device.pi.jni;

public class StatusLight {

	static {
		System.loadLibrary("status");
	}

	public native void setColor(int r, int g, int b);

}

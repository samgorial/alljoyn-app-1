package com.covisint.platform.device.demo;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(announced = "true")
public interface DemoInterface extends BusObject {

	@BusProperty
	double getInternalTemp() throws BusException;

	@BusProperty
	int getBuzzerState() throws BusException;

	@BusProperty
	String getLedColor() throws BusException;

	@BusMethod
	void setTargetTemp(double targetTemp) throws BusException;

	@BusMethod
	void turnOnBuzzer() throws BusException;

	@BusMethod
	void turnOffBuzzer() throws BusException;

	@BusSignal
	void internalTempChanged(double temp) throws BusException;

	@BusSignal
	String ledColorChanged(String newColor) throws BusException;

}
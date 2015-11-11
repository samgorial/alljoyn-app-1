package com.covisint.platform.device.pi;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "com.covisint.platform.devices.public", announced = "true")
public interface PiBusInterface extends BusObject {

	@BusProperty
	public double getInternalTemp() throws BusException;

	@BusProperty
	public int getBuzzerState() throws BusException;

	@BusProperty
	public int getLedState() throws BusException;

	@BusMethod
	public String ping(String text) throws BusException;

	@BusMethod
	public void setTargetTemp(double targetTemp) throws BusException;

	@BusMethod
	public void turnOnBuzzer() throws BusException;

	@BusMethod
	public void turnOffBuzzer() throws BusException;

	@BusMethod
	public void turnOnLight() throws BusException;

	@BusMethod
	public void turnOffLight() throws BusException;

	@BusSignal
	public void internalTempChanged(double temp) throws BusException;

	@BusSignal
	public void lightTurnedOn() throws BusException;

	@BusSignal
	public void lightTurnedOff() throws BusException;

	@BusSignal
	public void buzzerTurnedOn() throws BusException;

	@BusSignal
	public void buzzerTurnedOff() throws BusException;

}
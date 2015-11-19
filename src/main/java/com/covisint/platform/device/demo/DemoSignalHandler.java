package com.covisint.platform.device.demo;

import org.alljoyn.bus.annotation.BusSignalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DemoSignalHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DemoSignalHandler.class);

	public static final DemoSignalHandler INSTANCE = new DemoSignalHandler();

	private DemoSignalHandler() {
	}

	@BusSignalHandler(iface = "com.covisint.platform.device.demo.DemoInterface", signal = "internalTempChanged")
	public void internalTempChanged(double temp) {
		LOG.info("Remote Pi: internal temperature changed to {}", temp);
	}

	@BusSignalHandler(iface = "com.covisint.platform.device.demo.DemoInterface", signal = "ledColorChanged")
	public String ledColorChanged(String newColor) {
		LOG.info("Remote Pi: LED color changed to {}", newColor);
		return newColor;
	}

}
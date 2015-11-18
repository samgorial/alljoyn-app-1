package com.covisint.platform.device.demo;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements InitializingBean {

	static {
		System.loadLibrary("alljoyn_java");
	}

	public static final String APP_NAME = "Demo_AJ_Application";

	public static final short CONTACT_PORT = 42;

	@Autowired
	private DemoInterface service;

	public void afterPropertiesSet() throws Exception {
		doAboutAnnouncement();
	}

	private void doAboutAnnouncement() {

		BusAttachment bus = new BusAttachment(APP_NAME, BusAttachment.RemoteMessage.Receive);

		Status status = bus.registerBusObject(service, "/demo/1");

		if (status != Status.OK) {
			System.err.println("Could not register bus object: " + status.toString());
			return;
		}

		status = bus.connect();

		if (status != Status.OK) {
			System.err.println("Could not connect to bus: " + status.toString());
			return;
		}

		System.out.println("Bus connection successful on " + System.getProperty("org.alljoyn.bus.address"));

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = false;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		status = bus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {

			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				System.out.println("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}

			public void sessionJoined(short sessionPort, int id, String joiner) {
				System.out.println(
						String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
			}
		});

		if (status != Status.OK) {
			System.err.println("Could not bind session: " + status.toString());
			return;
		}

		AboutObj aboutObj = new AboutObj(bus);
		status = aboutObj.announce(contactPort.value, new AboutData());

		if (status != Status.OK) {
			System.out.println("Announce failed " + status.toString());
			return;
		}

		for (;;) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

package com.covisint.platform.device.demo;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
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

	public static final short CONTACT_PORT = 52;

	@Autowired
	private DemoInterface service;

	@Autowired
	private AboutDataListener aboutDataListener;

	public static final Map<DemoInterface, List<SignalEmitter>> SIGNAL_EMITTERS = new IdentityHashMap<>();

	private static BusAttachment bus;
	
	private static AboutObj aboutObj;
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public void afterPropertiesSet() throws Exception {
		executor.submit(new Runnable() {
			
			public void run() {
				doAboutAnnouncement();
			}
			
		});
		
		System.out.println("Leaving after properties set.");
	}

	private void doAboutAnnouncement() {

		bus = new BusAttachment(APP_NAME, BusAttachment.RemoteMessage.Receive);

		Status status = bus.registerBusObject(service, "/demo/1");

		if (status != Status.OK) {
			System.err.println("Could not register bus object: " + status.toString());
			return;
		}

		bus.registerBusListener(new BusListener());

		status = bus.connect();

		if (status != Status.OK) {
			System.err.println("Could not connect to bus: " + status.toString());
			return;
		}

		System.out.println("Bus connection successful on " + System.getProperty("org.alljoyn.bus.address"));

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts opts = new SessionOpts();
		opts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		opts.isMultipoint = false;
		opts.proximity = SessionOpts.PROXIMITY_ANY;
		opts.transports = SessionOpts.TRANSPORT_ANY;

		status = bus.bindSessionPort(contactPort, opts, new SessionPortListener() {

			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				System.out.println("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}

			public void sessionJoined(short sessionPort, int id, String joiner) {

				SignalEmitter emitter = new SignalEmitter(service, joiner, id, SignalEmitter.GlobalBroadcast.On);

				System.out.println("Joiner " + joiner + " joined session " + id + " on port " + sessionPort
						+ ".  Bound signal emitter to this application's service.");

				List<SignalEmitter> emitters = SIGNAL_EMITTERS.get(service);

				if (emitters == null) {
					emitters = new ArrayList<>();
					SIGNAL_EMITTERS.put(service, emitters);
				}

				emitters.add(emitter);

			}
		});

		if (status != Status.OK) {
			System.err.println("Could not bind session: " + status.toString());
			return;
		}

		aboutObj = new AboutObj(bus);
		status = aboutObj.announce(contactPort.value, aboutDataListener);

		if (status != Status.OK) {
			System.out.println("Announce failed " + status.toString());
			return;
		}
		
		System.out.println("Announce successful");

		for (;;) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}

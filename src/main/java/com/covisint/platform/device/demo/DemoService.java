package com.covisint.platform.device.demo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.SignalEmitter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.covisint.platform.device.pi.jni.Buzzer;
import com.covisint.platform.device.pi.jni.StatusLight;

@Component("bus")
public class DemoService implements DemoInterface, InitializingBean {

	private static Executor executor = Executors.newFixedThreadPool(10);

	@Autowired
	private InternalState state;

	private Timer timer = new Timer();

	private static boolean cooldownPhase;

	private TimerTask raiseTemperature = new TimerTask() {

		public void run() {

			for (;;) {

				try {
					System.out.println("Timer task sleeping for 5 seconds...");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (cooldownPhase) {
					System.out.println("In cooldown phase.  Timer task not operational.");
					continue;
				}

				if (state.temperature < 100) {
					System.out.println("Temp < 100, increasing by 1...");
					state.temperature += 1;
				} else {
					System.out.println("Max temp reached, not increasing.");
				}

				updateStatusLight();

			}

		}
	};

	public void afterPropertiesSet() throws Exception {
		System.out.println("Scheduling raise temp timer task");
		timer.schedule(raiseTemperature, 0);
		updateStatusLight();
		System.out.println("Completed BusService initialization");
	}

	private void updateStatusLight() {
		StatusLight light = new StatusLight();
		InternalState.RGB newColor;
		switch ((int) state.temperature) {
		case 100:
		case 99:
		case 98:
		case 97:
			newColor = new InternalState.RGB(0, 255, 255);
			break;
		case 96:
		case 95:
		case 94:
		case 93:
			newColor = new InternalState.RGB(0, 210, 255);
			break;
		case 92:
		case 91:
		case 90:
		case 89:
			newColor = new InternalState.RGB(0, 85, 255);
			break;
		case 88:
		case 87:
		case 86:
		case 85:
			newColor = new InternalState.RGB(0, 50, 255);
			break;
		case 84:
		case 83:
		case 82:
		case 81:
		case 80:
			newColor = new InternalState.RGB(0, 0, 255);
			break;
		default:
			newColor = new InternalState.RGB(255, 0, 255);
		}

		if (!state.ledColor.toString().equals(newColor.toString())) {
			try {
				System.out.println("Led color changing from " + state.ledColor + " to " + newColor);
				ledColorChanged();
			} catch (BusException e) {
				e.printStackTrace();
			}
		}

		light.setColor(newColor.r, newColor.g, newColor.b);
		state.ledColor = newColor;
	}

	public double internalTempChanged() throws BusException {
		System.out.println("Internal temperature changed to " + state.temperature);
		List<SignalEmitter> emitters = Application.SIGNAL_EMITTERS.get(this);
		if (emitters != null) {
			for (SignalEmitter emitter : emitters) {
				emitter.getInterface(DemoInterface.class).internalTempChanged();
				System.out.println("Emitted signal 'internalTempChanged'");
			}
		}
		return state.temperature;
	}

	public String ledColorChanged() throws BusException {
		String newColor = state.ledColor.toString();
		System.out.println("Led color changed: " + newColor);
		List<SignalEmitter> emitters = Application.SIGNAL_EMITTERS.get(this);
		if (emitters != null) {
			for (SignalEmitter emitter : emitters) {
				emitter.getInterface(DemoInterface.class).ledColorChanged();
				System.out.println("Emitted signal 'ledColorChanged'");
			}
		}
		return newColor;
	}

	public double getInternalTemp() throws BusException {
		System.out.println("Internal temperature was queried: " + state.temperature);
		return state.temperature;
	}

	public int getBuzzerState() throws BusException {
		System.out.println("Buzzer state was queried: " + state.buzzerOn);
		return state.buzzerOn ? 1 : 0;
	}

	public String getLedColor() throws BusException {
		System.out.println("LED state was queried: " + state.ledColor);
		return state.ledColor.toString();
	}

	public void setTargetTemp(double targetTemp) throws BusException {
		System.out.println("Setting target temp to " + targetTemp);

		if (targetTemp < state.temperature) {

			cooldownPhase = true;

			turnOnBuzzer();

			while (targetTemp < state.temperature) {

				state.temperature -= 1;

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				updateStatusLight();

			}

			turnOffBuzzer();

			cooldownPhase = false;
		}

		state.temperature = targetTemp;
		internalTempChanged();
	}

	public void turnOffBuzzer() throws BusException {
		System.out.println("Turning off buzzer.");
		new Buzzer().off();
		System.out.println("Turned off (physical) buzzer.");
		state.buzzerOn = false;
	}

	public void turnOnBuzzer() throws BusException {
		System.out.println("Turning on buzzer.");

		executor.execute(new Runnable() {

			public void run() {
				new Buzzer().on();
			}
		});

		System.out.println("Turned on (physical) buzzer.");
		state.buzzerOn = true;
	}

}
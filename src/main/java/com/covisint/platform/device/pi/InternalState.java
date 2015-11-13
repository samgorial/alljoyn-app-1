package com.covisint.platform.device.pi;

import org.springframework.stereotype.Component;

@Component
public class InternalState {

	public double temperature;
	
	public boolean buzzerOn;
	
	public RGB ledColor = new RGB(0, 255, 0);
	
	public static class RGB {
		public int r;
		public int g;
		public int b;
		
		public RGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		@Override
		public String toString() {
			return Integer.toHexString(r) + "," + Integer.toHexString(g) + "," + Integer.toHexString(b);
		}
	}
	
}

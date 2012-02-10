package de.fhb.thag.demo;

import java.util.HashMap;
import java.util.Map;

/**
 *  This is a demo flight with an emergency code. DemoFlight is a singleton.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class DemoFlight {
	
	private static DemoFlight instance;
	
	private DemoFlight() {
	}
	
	/**
	 * Returns an instance of the DemoFlight.
	 * @return an instance of DemoFlight
	 */
	public static DemoFlight getInstance() {
		if (instance == null) {
			instance = new DemoFlight();
		}
		return instance;
	}
	
	/**
	 * Creates demo flight informations.
	 * 
	 * @return The headers with demo flight informations.
	 */
	public Map<String, Object> getHeaders() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("FlightNumber", "1234");
		headers.put("Hexcode", "x1234");
		headers.put("Latidude", "1");
		headers.put("Longitude", "1");
		headers.put("Track", "123");
		headers.put("Altitude", "1");
		headers.put("Speed", "100");
		headers.put("Squawk", "7500");
		headers.put("RadarType", "1");
		headers.put("PlaneType", "1");
		headers.put("PlaneID", "1");
		headers.put("UnixTimestamp", "1");
		return headers;
	}

}

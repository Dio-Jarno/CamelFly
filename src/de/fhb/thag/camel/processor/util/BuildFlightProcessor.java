package de.fhb.thag.camel.processor.util;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.json.simple.JSONArray;

/**
 * A processor to build flight messages.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.2
 *
 */
public class BuildFlightProcessor implements Processor {

	private String flightCode;
	private JSONArray flightData;
	
	/**
	 * default constructor
	 * 
	 * @param flightCode - Unique code to identify a flight.
	 * @param flightData - Meta data of a flight.
	 */
	public BuildFlightProcessor(String flightCode, JSONArray flightData) {
		this.flightCode = flightCode;
		this.flightData = flightData;
	}
	
	/**
	 * Called when the processor is active. It builds a message with a flight inside.
	 * 
	 * @param exchange - Message with informations of a flight.
	 */
	@Override
	public void process(Exchange exchange) {
		exchange.setPattern(ExchangePattern.InOut);
        Message inMessage = exchange.getIn();
        inMessage.setHeader("FlightCode", flightCode);
		inMessage.setHeader("HexCode", flightData.get(0));
		inMessage.setHeader("Latitude", flightData.get(1));
		inMessage.setHeader("Longitude", flightData.get(2));
		inMessage.setHeader("Track", flightData.get(3));
		inMessage.setHeader("Altitude", flightData.get(4));
		inMessage.setHeader("Speed", flightData.get(5));
		inMessage.setHeader("Squawk", flightData.get(6));
		inMessage.setHeader("RadarType", flightData.get(7));
		inMessage.setHeader("PlaneType", flightData.get(8));
		inMessage.setHeader("PlaneID", flightData.get(9));
		inMessage.setHeader("UnixTimestamp", flightData.get(10));
	}

}

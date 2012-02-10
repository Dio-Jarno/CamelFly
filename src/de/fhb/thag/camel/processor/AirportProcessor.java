package de.fhb.thag.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.json.simple.JSONObject;

import de.fhb.thag.camel.processor.util.CamelFlyProcessor;
import de.fhb.thag.camel.processor.util.RequestProcessor;

/**
 * A processor to identify the target airport of a flight.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.3
 *
 */
public class AirportProcessor extends CamelFlyProcessor {
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public AirportProcessor(CamelContext context) {
		super(context);
	}

	/**
	 * Called when the processor is active. It identifies the airport of a flight and extracts the IATA code.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @throws Exception If the response string has no valid JSON code.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String flightCode = exchange.getIn().getHeader("FlightCode").toString();
		String hexCode = exchange.getIn().getHeader("HexCode").toString();
		String unixTimestamp = exchange.getIn().getHeader("UnixTimestamp").toString();
		ProducerTemplate template = context.createProducerTemplate();
		Exchange response = template.send("cxfrs://http://www.flightradar24.com/FlightDataService.php?callsign=" + flightCode + "&hex=" + hexCode + "&date=" + unixTimestamp, new RequestProcessor());
		JSONObject JSONObject = buildJSONObject(response);
		if (JSONObject.get("to") == null) {
			exchange.getIn().setHeader("IATACode", "N/A");
		} else {
			String IATACode = extractIATACode(JSONObject.get("to").toString());
			exchange.getIn().setHeader("IATACode", IATACode);
		}
	}
	
	/**
	 * Function to extract the IATA code.
	 * 
	 * @param content String with the IATA code inside.
	 * @return The IATA code or returns null if the content is not valid or null.
	 */
	private String extractIATACode(String content) {
		String IATACode = null;
		String tempString1 = content;
		try {
			String tempString2 = tempString1.split("<")[1];
			IATACode = tempString2.split(">")[1];
		} catch (Exception e) {
			return "N/A";
		}
		return IATACode;
	}

}


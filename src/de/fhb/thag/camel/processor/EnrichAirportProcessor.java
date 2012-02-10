package de.fhb.thag.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.json.simple.JSONObject;

import de.fhb.thag.camel.processor.util.CamelFlyProcessor;
import de.fhb.thag.camel.processor.util.RequestProcessor;

/**
 * A processor to enrich the flight message with airport informations.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class EnrichAirportProcessor extends CamelFlyProcessor {

	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public EnrichAirportProcessor(CamelContext context) {
		super(context);
	}

	/**
	 * Called when the processor is active. It enriches the flight message with airport informations.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @throws Exception If the response string has no valid JSON code.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String IATACode = exchange.getIn().getHeader("IATACode").toString();
		ProducerTemplate template = context.createProducerTemplate();
		Exchange request = template.send("cxfrs://http://avdata.geekpilot.net/airport/" + IATACode + ".json", new RequestProcessor());
		JSONObject JSONObject = buildJSONObject(request);
		if (JSONObject.get("iata_code") == null) {
		} else {
			enrichMessage(exchange, JSONObject);
		}
	}
	
	/**
	 * It enriches the flight message with the airport informations.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @param JSONObject - Meta data of a airport.
	 */
	private void enrichMessage(Exchange exchange, JSONObject JSONObject) {
		exchange.getIn().setHeader("Airport_ident", JSONObject.get("ident"));
		exchange.getIn().setHeader("Airport_gps_code", JSONObject.get("gps_code"));
		exchange.getIn().setHeader("Airport_local_code", JSONObject.get("local_code"));
		exchange.getIn().setHeader("Airport_name", JSONObject.get("name"));
		exchange.getIn().setHeader("Airport_latitude", JSONObject.get("latitude"));
		exchange.getIn().setHeader("Airport_longitude", JSONObject.get("longitude"));
		exchange.getIn().setHeader("Airport_elevation", JSONObject.get("elevation"));
	}

}


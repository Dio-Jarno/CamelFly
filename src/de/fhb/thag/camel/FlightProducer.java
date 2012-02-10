package de.fhb.thag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.fhb.thag.camel.processor.util.BuildFlightProcessor;
import de.fhb.thag.demo.DemoFlight;

/**
 * A producer for flights, which extracts the flight codes and data from a given JSON string.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.3
 *
 */
public class FlightProducer {
	
	private ProducerTemplate template;
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public FlightProducer(CamelContext context) {
		template = context.createProducerTemplate();
	}

	/**
	 * It creates a JSONObject and sends all flights by using the BuildFlightProcessor to "direct:allFlights".
	 * 
	 * @param content - A string with JSON code inside.
	 * @see BuildFlightProcessor
	 */
	public void parseFlights(String content) {
		// Demo flight with emergency code.
//		template.sendBodyAndHeaders("direct:allFlights", DemoFlight.getInstance().getHeaders(), DemoFlight.getInstance().getHeaders());
		
		try {
			JSONObject jObject_allFlights = (JSONObject) new JSONParser().parse(content);
			if (jObject_allFlights.size() > 0) {
				Object[] allFlightCodes = jObject_allFlights.keySet().toArray();
				Object[] allFlightDatas = jObject_allFlights.values().toArray();
				for (int i=0; i<allFlightCodes.length; i++) {
					template.send("direct:allFlights", new BuildFlightProcessor(allFlightCodes[i].toString(), (JSONArray)allFlightDatas[i]));
				}
			}
		} catch (Exception e) {
		}
	}

}


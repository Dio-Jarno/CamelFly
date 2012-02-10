package de.fhb.thag.camel.processor;

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;

import de.fhb.thag.camel.FlightProducer;
import de.fhb.thag.camel.processor.util.CamelFlyProcessor;
import de.fhb.thag.camel.processor.util.RequestProcessor;

/**
 * A processor to take flights from a radar.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.4
 *
 */
public class GetFlightRadarDataProcessor extends CamelFlyProcessor {

	private final String URL = "http://www.flightradar24.com/PlaneFeed.json";
	
	private FlightProducer producer;
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public GetFlightRadarDataProcessor(CamelContext context) {
		super(context);
		producer = new FlightProducer(context);
	}
	
	/**
	 * Called when the processor is active. It takes data from a flight radar and forwards to a parser.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @throws Exception If the response string has no valid JSON code.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		System.out.print("Fetch flight data... ");
		ProducerTemplate template = context.createProducerTemplate();
		Exchange request = template.send("cxfrs://" + URL, new RequestProcessor());
		ResponseImpl response = (ResponseImpl)(request.getOut().getBody());
		if (response != null) {
			System.out.println("Finished.");
			InputStream iStream = (InputStream) response.getEntity();
			String content = readStream(iStream);
			producer.parseFlights(content);
		} else {
			System.out.println("Error.");
		}
	}
	
}


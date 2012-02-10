package de.fhb.thag.camel.processor;

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;

import de.fhb.thag.camel.processor.util.CamelFlyProcessor;
import de.fhb.thag.camel.processor.util.RequestProcessor;

/**
 * A processor to get weather data.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class WeatherProcessor extends CamelFlyProcessor {

	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public WeatherProcessor(CamelContext context) {
		super(context);
	}
	
	/**
	 * Called when the processor is active. It takes weather data and enriches the message body.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @throws Exception If the response string is null or not valid.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String IATACode = exchange.getIn().getHeader("IATACode").toString();
		ProducerTemplate template = context.createProducerTemplate();
		Exchange request = template.send("cxfrs://http://weather.yahooapis.com/forecastrss?q=" + IATACode + "&u=c", new RequestProcessor());
		ResponseImpl response = (ResponseImpl)(request.getOut().getBody());
		InputStream iStream = (InputStream)response.getEntity();
		String content = readStream(iStream);
		exchange.getIn().setBody(content);
	}

}

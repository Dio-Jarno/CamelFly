package de.fhb.thag.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.json.simple.JSONObject;

import de.fhb.thag.camel.processor.util.CamelFlyProcessor;
import de.fhb.thag.camel.processor.util.RequestProcessor;

/**
 * A processor to identify the country, in which the flight is.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.2
 *
 */
public class CountryProcessor extends CamelFlyProcessor {
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public CountryProcessor(CamelContext context) {
		super(context);
	}

	/**
	 * Called when the processor is active. It identify the country of a flight and extracts the country code.
	 * 
	 * @param exchange - Message with flight data inside.
	 * @throws Exception If the response string has no valid JSON code.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String latitude = exchange.getIn().getHeader("Latitude").toString();
		String longitude = exchange.getIn().getHeader("Longitude").toString();
		ProducerTemplate template = context.createProducerTemplate();
		Exchange response = template.send("cxfrs://http://api.geonames.org/countryCodeJSON?lat=" + latitude + "&lng=" + longitude + "&username=fhb_thag", new RequestProcessor());
		JSONObject JSONObject = buildJSONObject(response);
		String countryCode = JSONObject.get("countryCode").toString();
		if (countryCode == null || countryCode.equals("")) {
			exchange.getIn().setHeader("CountryCode", "N/A");
		} else {
			exchange.getIn().setHeader("CountryCode", countryCode);
		}
	}

}

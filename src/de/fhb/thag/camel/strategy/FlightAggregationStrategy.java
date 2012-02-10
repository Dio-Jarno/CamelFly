package de.fhb.thag.camel.strategy;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * A aggregation strategy for two flight message.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class FlightAggregationStrategy implements AggregationStrategy {

	/**
	 * Called when the aggregation strategy is active. It aggregates two flight message to a new one.
	 * Note: One flight message has the airport informations inside. The other one has the weather data inside.
	 * 
	 * @param exchangeOld - First flight message with informations.
	 * @param exchangeNew - Second flight message with informations.
	 * @return The aggregate message with all flight informations and weather data.
	 */
	@Override
	public Exchange aggregate(Exchange exchangeOld, Exchange exchangeNew) {
		if (exchangeOld == null) {
			return exchangeNew;
		} 
		if (exchangeNew == null) {
			return exchangeOld;
		} 
		if (exchangeNew.getIn().getHeaders().size() > exchangeOld.getIn().getHeaders().size()) {
			String body = exchangeOld.getIn().getBody(String.class);
			exchangeNew.getIn().setBody(body);
			aggregateHeadersAndBody(exchangeNew);
			return exchangeNew;
		} else {
			String body = exchangeNew.getIn().getBody(String.class);
			exchangeOld.getIn().setBody(body);
			aggregateHeadersAndBody(exchangeOld);
			return exchangeOld;
		}
	}

	/**
	 * It aggregates the headers and the body of a message, so the message has all informations inside the body.
	 * 
	 * @param exchange - The message with all flight informations (headers) and weather data (body).
	 */
	private void aggregateHeadersAndBody(Exchange exchange) {
		String body = exchange.getIn().getBody(String.class);
		String itemHead = "";
		String itemBody = "";
		String itemFoot = "";
		Map<String, Object> headers;
		try {
			String [] bodyParts = body.split("<description>");
			itemHead = bodyParts[0] + "<description>" + bodyParts[1] + "<description>";
			itemFoot = "\n" + bodyParts[2];
		} catch (Exception e) {
		} finally {
			headers = exchange.getIn().getHeaders();
			headers.remove("breadcrumbid");
			for (int i=0; i<headers.size(); i++) {
				itemBody += headers.keySet().toArray()[i] + ": ";
				itemBody += headers.values().toArray()[i] + "&lt;br /&gt;\n";
			}
			exchange.getIn().setBody(itemHead + itemBody + itemFoot);
		}
	}

}

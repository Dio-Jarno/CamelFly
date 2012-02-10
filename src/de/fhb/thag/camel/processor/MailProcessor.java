package de.fhb.thag.camel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * A processor to build an e-mail.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.2
 *
 */
public class MailProcessor implements Processor {
	
//	private final String EMAIL = "diojarno@googlemail.com";
	private final String EMAIL = "si@thomas-preuss.de";
	
	private final int HIJACKING = 7500;
	private final int RADIO_FAILURE = 7600;
	private final int EMERGENCY = 7700;
	
	/**
	 * Called when the processor is active. It takes data from a flight radar and forwards to a parser.
	 * 
	 * @param exchange - Message with flight data inside.
	 */
	@Override
	public void process(Exchange exchange) {
		int squawk = Integer.valueOf((String) exchange.getIn().getHeader("Squawk"));
		switch (squawk) {
			case HIJACKING: setMetaData(exchange, "seven-five - man with a knife!");
							break;
			case RADIO_FAILURE: setMetaData(exchange, "seven-six - hear nix.");
								break;
			case EMERGENCY: setMetaData(exchange, "seven-seven - go to heaven");
							break;
			default: exchange.getIn().setHeader("Mail", Boolean.FALSE);
					 break;
		}
	}
	
	/**
	 * Function to set the meta data of an e-mail.
	 * 
	 * @param exchange - E-mail with flight data inside.
	 * @param subject - subject message
	 */
	private void setMetaData(Exchange exchange, String subject) {
		exchange.getIn().setHeader("subject", "Flight " + exchange.getIn().getHeader("FlightNumber") + " be careful: " + subject);
		exchange.getIn().setHeader("Mail", Boolean.TRUE);
		exchange.getIn().setHeader("to", EMAIL);
		exchange.getIn().setHeader("from", "CamelFly");
		System.out.println("E-Mail sent.");
	}
	
}


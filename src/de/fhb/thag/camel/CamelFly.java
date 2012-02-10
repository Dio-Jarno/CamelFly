package de.fhb.thag.camel;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.BrowsableEndpoint;

import de.fhb.thag.camel.processor.AirportProcessor;
import de.fhb.thag.camel.processor.CountryProcessor;
import de.fhb.thag.camel.processor.EnrichAirportProcessor;
import de.fhb.thag.camel.processor.GetFlightRadarDataProcessor;
import de.fhb.thag.camel.processor.MailProcessor;
import de.fhb.thag.camel.processor.WeatherProcessor;
import de.fhb.thag.camel.processor.util.RSSProcessor;
import de.fhb.thag.camel.processor.util.RestServiceRSS;
import de.fhb.thag.camel.strategy.FlightAggregationStrategy;

/**
 * This is the main class of this program. It builds the camel routes and provides for the program execution.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.5
 *
 */
public class CamelFly {
	
	private BrowsableEndpoint console;
	
	/**
	 * Default constructor, in which all camel routes are built.
	 * 
	 * @param context - default camel context
	 */
	public CamelFly(CamelContext context) {
		console = context.getEndpoint("browse:console", BrowsableEndpoint.class);
		try {
			context.addRoutes(new RouteBuilder() {
				public void configure() {
					// Create feed by using the temp rss files.
					from("cxfrs://http://localhost:9000?resourceClasses="+RestServiceRSS.class.getCanonicalName()).process(new RSSProcessor());
					
					// Fetch flights from the internet every ten minutes.
					from("timer://pollingTimer?fixedRate=true&delay=0&period=600000").process(new GetFlightRadarDataProcessor(this.getContext()));
					from("direct:allFlights").multicast().to("direct:squawkCheck", "direct:CountryCheck");
					
					// Check whether the flight has a emergency and if so sent an e-mail.
					from("direct:squawkCheck").process(new MailProcessor()).filter(header("Mail").isEqualTo(Boolean.TRUE)).to("direct:mail");
					from("direct:mail").to("smtps://smtp.googlemail.com?username=testfhb8889&password=rfvtgbzhn678");
					
					// Look up for flights in D-A-CH area and if so fetch the airport.
					from("direct:CountryCheck").process(new CountryProcessor(this.getContext())).filter(header("CountryCode").regex("DE|AT|CH")).to("direct:DACH");
					from("direct:DACH").process(new AirportProcessor(this.getContext())).multicast().parallelProcessing().to("seda:Airport","seda:Weather");
					
					// Fetch parallel airport informations and weather data and after that aggregate these data.
					from("seda:Airport").process(new EnrichAirportProcessor(this.getContext())).to("direct:aggregator");
					from("seda:Weather").process(new WeatherProcessor(this.getContext())).to("direct:aggregator");
					from("direct:aggregator").aggregate(header("FlightCode"), new FlightAggregationStrategy()).completionTimeout(2000).to("direct:DACH_FlightMessages");
					
					// Split flights from D-A-CH area in three separate country channels (i.g. for Germany, Austria and Switzerland).
					from("direct:DACH_FlightMessages").choice()
						.when(header("CountryCode").isEqualTo("DE")).setHeader("CamelFileName", simple("DE")).to("direct:feed")
						.when(header("CountryCode").isEqualTo("AT")).setHeader("CamelFileName", simple("AT")).to("direct:feed")
						.when(header("CountryCode").isEqualTo("CH")).setHeader("CamelFileName", simple("CH")).to("direct:feed");
					
					// Create for every country channel a temp rss file. TEST
					from("direct:feed").marshal().rss().transform().xpath("/rss/channel/item").to("file:rss?autoCreate=true&fileExist=Append");
				}
			});
			context.start();
			
		} catch (Exception e) {
			System.out.println("CamelFly causes an error. Please try to restart it.");
			e.printStackTrace();
		}
	}
	
	/**
	 * It prints out some program informations, i.g. all messages from "browse:console".
	 */
	public void getInfos() {
		List<Exchange> exchanges = console.getExchanges();
		System.out.println(exchanges.size() + " Flights received: ");
		System.out.println();
        for (Exchange exchange : exchanges) {
        	System.out.println("Message Headers: " + exchange.getIn().getHeaders());
        	System.out.println("Message Body: " + exchange.getIn().getBody().toString());
        }
	}
	
}

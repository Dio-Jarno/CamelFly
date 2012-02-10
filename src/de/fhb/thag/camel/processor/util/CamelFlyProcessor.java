package de.fhb.thag.camel.processor.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A abstract generalize processor as default.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public abstract class CamelFlyProcessor implements Processor {

	/** DefaultCamelContext */
	protected CamelContext context;
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public CamelFlyProcessor(CamelContext context) {
		this.context = context;
	}
	
	@Override
	public abstract void process(Exchange exchange) throws Exception;
	
	/**
	 * A method to read out the body of a message. It needs a input stream, which points to this body.
	 * 
	 * @param iStream The stream, which points to the body of a message.
	 * @return The string, which is inside the body.
	 * @throws IOException If the input stream could not read the data.
	 */
	protected String readStream(InputStream iStream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		int read;
		while ((read = iStream.read()) != -1) {
			buffer.append((char)read);
		}
		return buffer.toString();
	}
	
	/**
	 * A method to build a JSONObject from a given message with JSON code inside.
	 * 
	 * @param exchange - Message, which has JSON code inside.
	 * @return The JSONObject, which was build. Returns  null, if the message has no JSON code inside.
	 * @throws IOException If the input stream could not read the data inside the message.
	 * @throws ParseException If the JSONParser could not parse the JSON string, i.g. the JSON code is not valid.
	 */
	protected JSONObject buildJSONObject(Exchange exchange) throws IOException, ParseException {
		ResponseImpl response = (ResponseImpl)(exchange.getOut().getBody());
		InputStream iStream = (InputStream)response.getEntity();
		String content = readStream(iStream);
		if (content != null) {
			return (JSONObject)new JSONParser().parse(content);
		}
		return null;
	}

}

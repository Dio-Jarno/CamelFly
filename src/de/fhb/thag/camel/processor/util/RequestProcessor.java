package de.fhb.thag.camel.processor.util;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.message.CxfConstants;

/**
 * A processor for use as a request.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class RequestProcessor implements Processor {

	/**
	 * Called when the processor is active. It creates a GET-request for use cxf.
	 * 
	 * @param exchange - Request, which should be send to a server.
	 */
	@Override
	public void process(Exchange exchange) {
		exchange.setPattern(ExchangePattern.InOut);
        Message inMessage = exchange.getIn();
        inMessage.setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
        inMessage.setHeader(Exchange.HTTP_METHOD, "GET");
	}

}

package de.fhb.thag.camel.processor.util;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/rss/")
public class RestServiceRSS {

    public RestServiceRSS() {
    }

    @GET
	@Path("/DE")
	public String getDERss(){
		return null;
	}
	

	@GET
	@Path("/AT")
	public String getATRss(){
		return null;
	}
	

	@GET
	@Path("/CH")
	public String getCHRss(){
		return null;
	}
    
}

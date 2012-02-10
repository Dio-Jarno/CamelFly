package de.fhb.thag.camel.processor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * A processor to create the flights feed.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 0.1
 *
 */
public class RSSProcessor implements Processor {
	
	/**
	 * default constructor
	 * 
	 * @param context - default camel context
	 */
	public RSSProcessor() {
	}

	/**
	 * Called when the processor is active. It identifies the airport of a flight and extracts the IATA code.
	 * 
	 * @param exchange - New message, which represents an entry of the feed.
	 * @throws Exception If the feed cannot created.
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		String content = "";
		String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
						"<rss version=\"2.0\">\n"+
						"<channel>\n"+
						"<title>Flights RSS feed</title>\n"+
					    "<link>LINK</link>\n"+
					    "<description>DES</description>\n"+
					    "<language>SPRACHE</language>\n"+
					    "<copyright>COPY</copyright>\n"+
					    "<pubDate>DATE</pubDate>\n";
		try {
			String fileName = (String) exchange.getIn().getHeader("CamelHttpUri");
			fileName = fileName.substring(1, fileName.length());
			File file = new File(fileName);
			content = readFile(file);
		} catch (Exception e) {
			System.out.println("Cannnot read the file.");
		}
		String footer = "\n</channel>\n" +
						"</rss>";
		exchange.getOut().setBody(header + content + footer);
	}
	
	/**
	 * A method to read out the rss code of a temp file. It needs a input file, which has the code inside.
	 * 
	 * @param file - The file with the rss code inside.
	 * @return The rss code of the current feed.
	 * @throws IOException If the input stream could not read the data.
	 */
	private String readFile(File file) throws IOException {
		FileInputStream fiStream = new FileInputStream(file);
		byte[] buffer = new byte[65536];
		int read = 0;
		ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
		while ((read = fiStream.read(buffer)) > 0) {
			baoStream.write(buffer, 0, read);
		}
		return new String(baoStream.toByteArray());
	}
	
}

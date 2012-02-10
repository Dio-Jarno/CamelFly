package de.fhb.thag;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import de.fhb.thag.camel.CamelFly;

/**
 * Main class to start the program.
 * 
 * @author Arvid Grunenberg, Thomas Habiger
 * @version 1.0
 *
 */
public class CamelFlyStart {
	
	/**
	 * Method which is started first.
	 * 
	 * @param args - command-line arguments
	 */
	public static void main(String[] args) {
		CamelContext context = new DefaultCamelContext();
		System.out.println("CamelFly started.");

		@SuppressWarnings("unused")
		CamelFly camel;
		camel = new CamelFly(context);
	}

}

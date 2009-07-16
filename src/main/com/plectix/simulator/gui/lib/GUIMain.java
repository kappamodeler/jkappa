package com.plectix.simulator.gui.lib;

import java.util.TimeZone;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class GUIMain {

	public static void main(String[] args) {
	    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		// TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));	
		// TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
		
		// Initialize log4j
		PropertyConfigurator.configure(Settings.LOG4J_PROPERTIES_FILENAME);
		
		// Initialize Spring
		// Spring will create all the beans in the applicationContext.xml file, then call
		// the initialize() method (if any) on each.  That takes care of opening the main window, etc.
		new FileSystemXmlApplicationContext(Settings.SPRING_APPLICATION_CONTEXT_FILENAME);
	}
}

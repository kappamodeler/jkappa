package com.plectix.simulator.gui.lib;

import org.apache.log4j.Logger;



/**
 * Implement an exception handler for AWT event queues.  This class must
 * be initialized before the first exception is caught by an AWT event
 * thread.  This class acts as an "exception handler of last resort" so
 * that errors not caught and handled elsewhere will be displayed to the
 * user with at least a generic message.
 * 
 * cf. java.awt.EventDispatchThread#handleException(java.lang.Throwable)
 * 
 * @author ecemis
 */
public class AWTExceptionHandler {
	private static Logger log = Logger.getLogger(AWTExceptionHandler.class.getName());
	
	public AWTExceptionHandler() {
		super();
	}

	public void initialize() {
		System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
	}

	public void handle(Throwable t) {
		log.error("Exception in AWT event loop", t);
		Exception ex = (t instanceof Exception) ? (Exception) t : new Exception(t);
		PromptDialogs.getInstance().promptError("An application error occurred. If the error persists, try restarting the application.", ex);
	}
}

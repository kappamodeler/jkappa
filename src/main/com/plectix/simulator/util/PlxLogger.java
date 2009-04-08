package com.plectix.simulator.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/** 
 * This implementation wraps a log4j Logger. 
 * Subclass this in order to have your custom logging
*/
public class PlxLogger {
	
	private Logger logger = null;
	
	public PlxLogger(Class clazz) {
		 this.logger = Logger.getLogger(clazz);
	}

	public void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	public void debug(Object message) {
		logger.debug(message);
	}

	public void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	public void error(Object message) {
		logger.error(message);
	}

	public void fatal(Object message, Throwable t) {
		logger.fatal(message, t);
	}

	public void fatal(Object message) {
		logger.fatal(message);
	}

	public void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	public void info(Object message) {
		logger.info(message);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, message, t);
	}

	public void log(Priority priority, Object message) {
		logger.log(priority, message);
	}

	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	public void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}

	public void trace(Object message) {
		logger.trace(message);
	}

	public void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}

	public void warn(Object message) {
		logger.warn(message);
	}
 
}

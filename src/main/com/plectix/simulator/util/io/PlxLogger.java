package com.plectix.simulator.util.io;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/** 
 * This implementation wraps a log4j Logger. 
 * Subclass this in order to have your custom logging
*/
public final class PlxLogger {
	
	private final Logger logger;
	
	public PlxLogger(Class<?> clazz) {
		 this.logger = Logger.getLogger(clazz);
	}

	public final void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	public final void debug(Object message) {
		logger.debug(message);
	}

	public final void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	public final void error(Object message) {
		logger.error(message);
	}

	public final void fatal(Object message, Throwable t) {
		logger.fatal(message, t);
	}

	public final void fatal(Object message) {
		logger.fatal(message);
	}

	public final void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	public final void info(Object message) {
		logger.info(message);
	}

	public final boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public final boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	public final boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public final boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public final void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, message, t);
	}

	public final void log(Priority priority, Object message) {
		logger.log(priority, message);
	}

	public final void log(String callerFQCN, Priority level, Object message, Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	public final void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}

	public final void trace(Object message) {
		logger.trace(message);
	}

	public final void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}

	public final void warn(Object message) {
		logger.warn(message);
	}
 
}

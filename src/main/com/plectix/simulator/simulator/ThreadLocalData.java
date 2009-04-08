package com.plectix.simulator.simulator;

import com.plectix.simulator.util.NameDictionary;
import com.plectix.simulator.util.PlxLogger;

/**
 * This class hold data local to each <code>Thread</code> (i.e. Simulation). 
 * In other words, this class holds global variables for each Simulation. 
 * We should not abuse this class and keep its content very small (i.e. only few data fields).
 * 
 * @author ecemis
 */
public class ThreadLocalData {
	
	private static ThreadLocal<PlxLogger> plxLogger = null;

    private static final ThreadLocal<NameDictionary> nameDictionary = new ThreadLocal<NameDictionary> () {
            @Override 
            protected NameDictionary initialValue() {
                return new NameDictionary();
        }
    };

    public static final NameDictionary getNameDictionary() {
		return nameDictionary.get();
	}

	public static final void setLogger(final PlxLogger logger) {		
		plxLogger = new ThreadLocal<PlxLogger>() {
			@Override
			protected PlxLogger initialValue() {
				return logger;
			}
		};
	}

	public static final PlxLogger getLogger(Class clazz) {
		if (plxLogger == null) {
			return new PlxLogger(clazz);
		} 
		return plxLogger.get();
	}
}

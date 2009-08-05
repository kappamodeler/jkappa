package com.plectix.simulator.simulator;

import java.text.DecimalFormat;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.util.NameDictionary;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.util.TypeById;

/**
 * This class hold data local to each <code>Thread</code> (i.e. Simulation). 
 * In other words, this class holds global variables for each Simulation. 
 * We should not abuse this class and keep its content very small (i.e. only few data fields).
 * 
 * @author ecemis
 */
public class ThreadLocalData {
	
	private static ThreadLocal<PlxLogger> plxLogger = null;
	private static ThreadLocal<IRandom> random = new ThreadLocal<IRandom> () {
		@Override 
		protected IRandom initialValue() {
			return new CRandomJava(SimulationArguments.DEFAULT_SEED);
		}
	};

	/**
	 * "EMPTY" ConnectedComponent, contains no agents.
	 */
	private static ThreadLocal<CConnectedComponent> emptyConnectedComponent = new ThreadLocal<CConnectedComponent> () {
		@Override 
		protected CConnectedComponent initialValue() {
			return new CConnectedComponent();
		}
	};

	private static ThreadLocal<CInjection> emptyInjection = new ThreadLocal<CInjection> () {
		@Override 
		protected CInjection initialValue() {
			return new CInjection();
		}
	};
	
	private static final ThreadLocal<NameDictionary> nameDictionary = new ThreadLocal<NameDictionary> () {
		@Override 
		protected NameDictionary initialValue() {
			return new NameDictionary();
		}
	};

	private static final ThreadLocal<TypeById> typeById = new ThreadLocal<TypeById>(){
		@Override 
		protected TypeById initialValue() {
			return new TypeById();
		}
		
	};
	
	private static final ThreadLocal<DecimalFormat[]> decimalFormatters = new ThreadLocal<DecimalFormat[]>() {
		@Override 
		protected DecimalFormat[] initialValue() {
			return new DecimalFormat[] {
					new DecimalFormat("0"),
					new DecimalFormat("0.#"),
					new DecimalFormat("0.##"),
					new DecimalFormat("0.###"),
					new DecimalFormat("0.####"),
					new DecimalFormat("0.#####"),
					new DecimalFormat("0.######"),
					new DecimalFormat("0.#######"),
					new DecimalFormat("0.########"),
					new DecimalFormat("0.#########"),
					new DecimalFormat("0.##########"),
					new DecimalFormat("0.###########")
			};
		}
	};

    public static final NameDictionary getNameDictionary() {
		return nameDictionary.get();
    }

    public static final TypeById getTypeById(){
    	return typeById.get();
    }
    
    
    public static final DecimalFormat getDecimalFormat(int i) {
    	DecimalFormat[] decimalFormats = decimalFormatters.get();
    	if (i < 0) {
    		i = 0;
    	} else if (i >= decimalFormats.length) {
    		i = decimalFormats.length - 1;
    	}
    	return decimalFormats[i];
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

	public static IRandom getRandom() {
		return random.get();
	}
	
	public static CConnectedComponent getEmptyConnectedComponent(){
		return emptyConnectedComponent.get();
	}

	public static CInjection getEmptyInjection(){
		return emptyInjection.get();
	}
}

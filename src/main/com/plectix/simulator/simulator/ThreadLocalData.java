package com.plectix.simulator.simulator;

import java.text.DecimalFormat;

import com.plectix.simulator.interfaces.RandomInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.util.NameDictionary;
import com.plectix.simulator.util.io.PlxLogger;

/**
 * This class hold data local to each <code>Thread</code> (i.e. Simulation). 
 * In other words, this class holds global variables for each Simulation. 
 * We should not abuse this class and keep its content very small (i.e. only few data fields).
 * 
 * @author ecemis
 */
public final class ThreadLocalData {
	private static abstract class ThreadLocalContainer<E> extends ThreadLocal<E> {
		@Override
		protected abstract E initialValue();
		
		/*default*/ void reset() { 
			this.set(initialValue());
		}
	}
	
	private static ThreadLocalContainer<PlxLogger> plxLogger = null;
	private static final ThreadLocalContainer<RandomInterface> random = new ThreadLocalContainer<RandomInterface> () {
		@Override 
		protected RandomInterface initialValue() {
			return new DefaultRandom(SimulationArguments.DEFAULT_SEED);
		}
	};

	/**
	 * "EMPTY" ConnectedComponent, contains no agents.
	 */
	private static final ThreadLocal<ConnectedComponent> emptyConnectedComponent = new ThreadLocal<ConnectedComponent> () {
		@Override 
		protected ConnectedComponent initialValue() {
			return new ConnectedComponent();
		}
	};

	private static final ThreadLocal<Injection> emptyInjection = new ThreadLocal<Injection> () {
		@Override 
		protected Injection initialValue() {
			return new Injection();
		}
	};
	
	private static final ThreadLocalContainer<NameDictionary> nameDictionary = new ThreadLocalContainer<NameDictionary> () {
		@Override 
		protected NameDictionary initialValue() {
			return new NameDictionary();
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
					new DecimalFormat("0.###########"),
			};
		}
	};

	private static final ThreadLocal<DecimalFormat[]> exponentialDecimalFormatters = new ThreadLocal<DecimalFormat[]>() {
        @Override 
        protected DecimalFormat[] initialValue() {
            return new DecimalFormat[] {
                    new DecimalFormat("0.E0"),
                    new DecimalFormat("0.#E0"),
                    new DecimalFormat("0.##E0"),
                    new DecimalFormat("0.###E0"),
                    new DecimalFormat("0.####E0"),
                    new DecimalFormat("0.#####E0"),
                    new DecimalFormat("0.######E0"),
                    new DecimalFormat("0.#######E0"),
                    new DecimalFormat("0.########E0"),
                    new DecimalFormat("0.#########E0"),
                    new DecimalFormat("0.##########E0"),
                    new DecimalFormat("0.###########E0"),
                    new DecimalFormat("0.############E0"),
                    new DecimalFormat("0.#############E0"),
                    new DecimalFormat("0.##############E0"),
            };
        }
    };

    public static final NameDictionary getNameDictionary() {
		return nameDictionary.get();
    }

    public static final DecimalFormat getDecimalFormat(int i) {
        DecimalFormat[] decimalFormats = decimalFormatters.get();
        if (i < 0) {
            i = 0;
        } else if (i >= decimalFormats.length) {
            decimalFormats = exponentialDecimalFormatters.get();
            i = SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS-1;
             if (i >= decimalFormats.length) {
                 i = decimalFormats.length - 1;
             }
            return decimalFormats[i];  // 
        }
        return decimalFormats[i];
    }

    
	public static final void setLogger(final PlxLogger logger) {		
		plxLogger = new ThreadLocalContainer<PlxLogger>() {
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

	public static RandomInterface getRandom() {
		return random.get();
	}
	
	public static ConnectedComponent getEmptyConnectedComponent(){
		return emptyConnectedComponent.get();
	}

	public static Injection getEmptyInjection(){
		return emptyInjection.get();
	}
	
	public static final void reset() {
		plxLogger.reset();
		random.reset();
		nameDictionary .reset();
	}
}

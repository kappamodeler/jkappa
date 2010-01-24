package com.plectix.simulator.util;

/**
 * 
 * @author ecemis
 */
public final class ExponentialMovingAverage {
	private static final String NAME_PREFIX = "EMA-";
	// parameters:
	private String name;

	private int timePeriod = -1;
	private double percentage = -1.0;

	// values:
	private int itemCount = 0;
	private double currentEMA = 0.0;


	//*****************************************************************************
	/**
	 *
	 * @param timePeriod
	 */
	public ExponentialMovingAverage(int timePeriod) {
		super();
		reset(timePeriod);
	}

	//*****************************************************************************
	/**
	 *
	 * @param timePeriod
	 */
	private final void reset(int timePeriod) {
		if (timePeriod <= 0) {
			throw new RuntimeException("timePeriod " + timePeriod + " is not allowed!");
		}

		// parameters:
		name = NAME_PREFIX + timePeriod;
		this.timePeriod = timePeriod;
		this.percentage = 2.0 / (1.0+timePeriod);

		// values:
		itemCount = 0;
		currentEMA = 0.0;
	}

	//*****************************************************************************
	/**
	 *
	 * @param newValue
	 */
	public final double addValue(double newValue) {
		itemCount++;
		if (itemCount <= timePeriod) {
			// add new value:
			currentEMA += newValue;
			if (itemCount == timePeriod) {
				currentEMA = currentEMA/itemCount;
			}
		} else {
			currentEMA = currentEMA + percentage * (newValue - currentEMA);
		}
		return getCurrentValue();
	}

	//*****************************************************************************
	/**
	 * 
	 * @return the current value
	 */
	public final double getCurrentValue() {
		if (itemCount < timePeriod) {
			return currentEMA / itemCount;
		}
		return currentEMA;
	}

	//*****************************************************************************
	/**
	 *
	 * @return the time period
	 */
	final int getTimePeriod() {
		return timePeriod;
	}

	//*****************************************************************************
	/**
	 *
	 * @param timePeriod
	 */
	public final void setTimePeriod(int timePeriod) {
		this.timePeriod = timePeriod;
		reset(timePeriod);
	}


	//*****************************************************************************
	/*
	 *
	 */
	public static void main(String[] args) {
		ExponentialMovingAverage exponentialMovingAverage = new ExponentialMovingAverage(10);

		for (int i= 0; i< 4*exponentialMovingAverage.getTimePeriod(); i++) {
			if (i < exponentialMovingAverage.getTimePeriod()) {
				exponentialMovingAverage.addValue(5.0);
			} else {
				exponentialMovingAverage.addValue(15.0);
			}
			System.err.println(((i+1.0)/exponentialMovingAverage.getTimePeriod()) + "\t"
					+ (exponentialMovingAverage.getCurrentValue()));
		}
	}

}

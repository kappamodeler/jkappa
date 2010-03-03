package com.plectix.simulator.simulator.options;

import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.AGENTS_LIMIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.CLOCK_PRECISION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.EVENT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.INIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.ITERATION;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_CONSUMER_CLASSNAME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_INTERVAL;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.LIVE_DATA_POINTS;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.MAX_CLASHES;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.MONITOR_PEAK_MEMORY;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.OPERATION_MODE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.POINTS;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.RESCALE;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.SEED;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.TIME;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.WALL_CLOCK_TIME_LIMIT;
import static com.plectix.simulator.simulator.options.SimulatorParameterizedOption.XML_SESSION_NAME;

import java.util.HashMap;
import java.util.Map;

import com.plectix.simulator.simulationclasses.solution.OperationMode;

public class SimulatorArgumentsDefaultValues {
	public static final int NUMBER_OF_MILLISECONDS_IN_SECOND = 1000;
	public static final int NUMBER_OF_MILLISECONDS_IN_MINUTE = 60 * NUMBER_OF_MILLISECONDS_IN_SECOND;
	public static final int NUMBER_OF_MILLISECONDS_IN_HOUR = 60 * NUMBER_OF_MILLISECONDS_IN_MINUTE;
	public static final int NUMBER_OF_MILLISECONDS_IN_DAY = 24 * NUMBER_OF_MILLISECONDS_IN_HOUR;
	
	public static final int DEFAULT_SEED = -1;
	public static final long DEFAULT_MAX_CLASHES = 10000;
	public static final int DEFAULT_NUMBER_OF_POINTS = 1000;
	
	/** Maximum simulation time is 100 days */
	public static final long DEFAULT_WALL_CLOCK_TIME_LIMIT = 100L* NUMBER_OF_MILLISECONDS_IN_DAY;
	public static final long DEFAULT_MONITOR_PEAK_MEMORY = -1;
	public static final int DEFAULT_CLOCK_PRECISION = 60;
	public static final String DEFAULT_XML_SESSION_NAME = "simplx.xml";
	public static final int DEFAULT_AGENTS_LIMIT = 100;
	public static final int DEFAULT_LIVE_DATA_POINTS = 500;
	public static final String DEFAULT_LIVE_DATA_CONSUMER_CLASSNAME = "com.plectix.simulator.streaming.DensityDependantLiveDataConsumer";
	
	public static final Map<SimulatorParameterizedOption, SimulatorParameter<?>>
		DEFAULT_VALUES = new HashMap<SimulatorParameterizedOption, SimulatorParameter<?>>();
	
	static {
		DEFAULT_VALUES.put(XML_SESSION_NAME, new SimulatorParameter<String>(DEFAULT_XML_SESSION_NAME));
		DEFAULT_VALUES.put(INIT, new SimulatorParameter<Double>(0.0));
		DEFAULT_VALUES.put(POINTS, new SimulatorParameter<Integer>(-1));
		DEFAULT_VALUES.put(RESCALE, new SimulatorParameter<Double>(Double.NaN));
		DEFAULT_VALUES.put(SEED, new SimulatorParameter<Integer>(DEFAULT_SEED));
		DEFAULT_VALUES.put(MAX_CLASHES, new SimulatorParameter<Long>(DEFAULT_MAX_CLASHES));
		DEFAULT_VALUES.put(TIME, new SimulatorParameter<Double>(0.0));
		DEFAULT_VALUES.put(EVENT, new SimulatorParameter<Long>(-1L));
		DEFAULT_VALUES.put(ITERATION, new SimulatorParameter<Integer>(1));
		DEFAULT_VALUES.put(WALL_CLOCK_TIME_LIMIT, new SimulatorParameter<Long>(DEFAULT_WALL_CLOCK_TIME_LIMIT));
		DEFAULT_VALUES.put(MONITOR_PEAK_MEMORY, new SimulatorParameter<Long>(DEFAULT_MONITOR_PEAK_MEMORY));
		DEFAULT_VALUES.put(CLOCK_PRECISION, new SimulatorParameter<Integer>(DEFAULT_CLOCK_PRECISION));
		DEFAULT_VALUES.put(OPERATION_MODE, new SimulatorParameter<String>(OperationMode.DEFAULT.toString()));
		DEFAULT_VALUES.put(AGENTS_LIMIT, new SimulatorParameter<Integer>(DEFAULT_AGENTS_LIMIT));
		DEFAULT_VALUES.put(LIVE_DATA_INTERVAL, new SimulatorParameter<Integer>(-1));
		DEFAULT_VALUES.put(LIVE_DATA_POINTS, new SimulatorParameter<Integer>(DEFAULT_LIVE_DATA_POINTS));
		DEFAULT_VALUES.put(LIVE_DATA_CONSUMER_CLASSNAME, new SimulatorParameter<String>(DEFAULT_LIVE_DATA_CONSUMER_CLASSNAME));
	}
}

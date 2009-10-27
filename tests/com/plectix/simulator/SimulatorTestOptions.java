package com.plectix.simulator;

import java.util.ArrayList;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.SimulatorOption;

public final class SimulatorTestOptions {
	private final ArrayList<String> arguments = new ArrayList<String>();
	
	public final SimulatorCommandLine toCommandLine() throws ParseException {
		String[] argumentsArray = arguments.toArray(new String[arguments.size()]);
		arguments.clear();
		return this.createCommandLine(argumentsArray);
	}
	
	public static final SimulatorCommandLine defaultContactMapCommandLine(String filename) 
				throws ParseException {
		return defaultContactMapCommandLine(filename, false);
	}
	
	public static final SimulatorCommandLine defaultContactMapCommandLine(String filename, boolean influenceMap) 
				throws ParseException {
		return defaultContactMapOptions(filename, influenceMap).toCommandLine();
	}
	
	public static final SimulatorTestOptions defaultContactMapOptions(String filename) {
		return defaultContactMapOptions(filename, false);
	}
	
	public static final SimulatorTestOptions defaultContactMapOptions(String filename, boolean influenceMap) {
		SimulatorTestOptions options = new SimulatorTestOptions();
		options.append(SimulatorOption.SHORT_CONSOLE_OUTPUT);
		options.appendContactMap(filename);
		options.append(SimulatorOption.NO_DUMP_ITERATION_NUMBER);
		options.append(SimulatorOption.NO_DUMP_RULE_ITERATION);
		if (influenceMap) {
			options.append(SimulatorOption.BUILD_INFLUENCE_MAP);
		} else {
			options.append(SimulatorOption.NO_BUILD_INFLUENCE_MAP);			
		}
		options.append(SimulatorOption.NO_COMPUTE_QUALITATIVE_COMPRESSION);
		options.append(SimulatorOption.NO_COMPUTE_QUANTITATIVE_COMPRESSION);
		options.append(SimulatorOption.NO_ENUMERATE_COMPLEXES);
		return options;
	}
	
	public final void appendContactMap(String filename) {
		append(SimulatorOption.CONTACT_MAP);
		append(filename);
	}
	
	public final void appendFocus(String filename) {
		append(SimulatorOption.FOCUS_ON);
		append(filename);
	}
	
	public final void append(SimulatorOption option) {
		arguments.add("--" + option.getLongName());
	}
	
	private final void append(String option) {
		arguments.add(option);
	}
	
	private final SimulatorCommandLine createCommandLine(String[] args) throws ParseException {
		return new SimulatorCommandLine(SimulationUtils.changeArguments(args));
	}

	public void appendSimulation(String filename) {
		append(SimulatorOption.SIMULATIONFILE);
		append(filename);
	}

	public void appendQuantitativeCompression(String filename) {
		append(SimulatorOption.OUTPUT_QUANTITATIVE_COMPRESSION);
		append(filename);
	}
	
}
package com.plectix.simulator.simulator.options;

import org.apache.commons.cli.CommandLine;

public class CommandLineParametersManager {
	private final CommandLine commandLine;
	
	public CommandLineParametersManager(CommandLine commandLine) {
		this.commandLine = commandLine;
	}
	
	public final String retrieveParameterAsString(SimulatorParameterizedOption option) {
		return commandLine.getOptionValue(option.getLongName());
	}
		
	public final SimulatorParameter<? extends Number> retrieveNumberParameter(SimulatorParameterizedOption option) {
		String parameterAsString = this.retrieveParameterAsString(option);
		if (parameterAsString == null) {
			return null;
		}
		if (option.getParameterType().equals(Double.class)) {
			double doubleValue = Double.valueOf(parameterAsString);
			return new SimulatorParameter<Double>(doubleValue);
		} else if (option.getParameterType().equals(Integer.class)) {
			int integerValue = Integer.valueOf(parameterAsString);
			return new SimulatorParameter<Integer>(integerValue);
		} else if (option.getParameterType().equals(Long.class)) {
			long longValue = Long.valueOf(parameterAsString);
			return new SimulatorParameter<Long>(longValue);
		} else {
			return null;
		}
	}
	
	public final SimulatorParameter<?> retrievePositiveParameter(SimulatorParameterizedOption option) {
		SimulatorParameter<? extends Number> number = this.retrieveNumberParameter(option);
		if (number == null) {
			return null;
		}
		if (number.getValue().doubleValue() > 0) {
			return number;
		} else {
			throw new IllegalArgumentException(option.getLongName() + "expected to be a positive value");
		}
	}
	
	public final SimulatorParameter<?> retrieveParameter(SimulatorParameterizedOption option) {
		SimulatorParameter<?> number = this.retrieveNumberParameter(option);
		return (number != null) ? number : 
			new SimulatorParameter<String>(this.retrieveParameterAsString(option));
	}
	
	public final boolean hasOption(SimulatorOption option) {
		return commandLine.hasOption(option.getLongName());
	}
}

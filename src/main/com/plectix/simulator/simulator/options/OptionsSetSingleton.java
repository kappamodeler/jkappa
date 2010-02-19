package com.plectix.simulator.simulator.options;

import org.apache.commons.cli.Options;

public class OptionsSetSingleton {
	public static final Options allComandLineOptions;
	
	static {
		allComandLineOptions = new Options();
		for (SimulatorOption option : SimulatorFlagOption.values()) {
			addOption(option);
		}
		for (SimulatorOption option : SimulatorParameterizedOption.values()) {
			addOption(option);
		}
		// We need to add unused options either, forcing cmd parser to accept them 
		for (SimulatorOption option : SimulatorUnusedOption.values()) {
			addOption(option);
		}
	}
	
	public static final Options getInstance() {
		return allComandLineOptions;
	}
	
	private static final void addOption(SimulatorOption option) {
		if (option.getShortName() == null) {
			allComandLineOptions.addOption(option.getLongName(),
					option.hasArguments(), option.getDescription());
		} else {
			allComandLineOptions.addOption(option.getShortName(),
					option.getLongName(), option.hasArguments(),
					option.getDescription());
		}
	}
}

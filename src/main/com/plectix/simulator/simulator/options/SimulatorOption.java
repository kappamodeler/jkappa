package com.plectix.simulator.simulator.options;

public interface SimulatorOption {
	public String getShortName();
	
	public String getLongName();
	
	public String getDescription();
	
	public boolean hasArguments();
}

package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Agent;

public interface InjectionSettingStrategy {
	
	public void process(ConnectedComponentInterface component, Agent agent);
	
}

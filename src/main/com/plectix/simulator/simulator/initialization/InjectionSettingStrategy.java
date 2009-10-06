package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;

public interface InjectionSettingStrategy {
	
	public void process(ConnectedComponentInterface component, Agent agent);
	
}

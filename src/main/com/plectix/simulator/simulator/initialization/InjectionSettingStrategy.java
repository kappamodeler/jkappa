package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

public interface InjectionSettingStrategy {
	
	public void process(IConnectedComponent component, CAgent agent);
	
}

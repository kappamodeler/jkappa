package com.plectix.simulator.simulator.initialization;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;

/*package*/ interface InjectionSettingStrategy {
	public void process(IConnectedComponent component, IAgent agent);
}

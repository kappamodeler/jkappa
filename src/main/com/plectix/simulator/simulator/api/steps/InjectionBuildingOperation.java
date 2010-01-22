package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.initialization.InjectionsBuilder;

public class InjectionBuildingOperation extends AbstractOperation {

	public InjectionBuildingOperation() {
		super(OperationType.INJECTIONS);
	}
	
	public void perform(KappaSystem kappaSystem) {
		(new InjectionsBuilder(kappaSystem)).build();
	}

}

package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class SubviewsComputationOperation extends AbstractOperation<MainSubViews> {
	private final KappaSystem kappaSystem;
	
	public SubviewsComputationOperation(KappaSystem kappaSystem) {
		super(kappaSystem.getSimulationData(), OperationType.SUBVIEWS);
		this.kappaSystem = kappaSystem;
	}
	
	protected MainSubViews performDry() {
		MainSubViews subviews = new MainSubViews();
		subviews.build(kappaSystem.getSolution(), kappaSystem.getRules());
		return subviews;
	}

}

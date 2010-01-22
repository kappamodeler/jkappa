package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class SubviewsComputationOperation extends AbstractOperation {

	public SubviewsComputationOperation() {
		super(OperationType.SUBVIEWS);
	}
	
	public MainSubViews perform(KappaSystem kappaSystem) {
		MainSubViews subviews = new MainSubViews();
		subviews.build(kappaSystem.getSolution(), kappaSystem.getRules());
		return subviews;
	}

}

package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.StaticAnalysisException;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;

public class SubviewsComputationOperation extends AbstractOperation<AllSubViewsOfAllAgentsInterface> {
	private final KappaSystem kappaSystem;
	
	public SubviewsComputationOperation(KappaSystem kappaSystem) {
		super(kappaSystem.getSimulationData(), OperationType.SUBVIEWS);
		this.kappaSystem = kappaSystem;
	}
	
	protected AllSubViewsOfAllAgentsInterface performDry() throws StaticAnalysisException {
		AllSubViewsOfAllAgentsInterface subviews = new MainSubViews();
		subviews.build(kappaSystem.getSolution(), kappaSystem.getRules());
		kappaSystem.setSubviews(subviews);
		return subviews;
	}

	@Override
	protected boolean noNeedToPerform() {
		// TODO check if we can optimize this one
		return kappaSystem.getSubViews() != null;
	}

	@Override
	protected AllSubViewsOfAllAgentsInterface retrievePreparedResult() {
		return kappaSystem.getSubViews();
	}

}

package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.future.InfluenceMapWithFuture;
import com.plectix.simulator.staticanalysis.observables.Observables;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class InfluenceMapComputationOperation extends AbstractOperation<InfluenceMap> {
	private final SimulationData simulationData;
	
	public InfluenceMapComputationOperation(SimulationData simulationData) {
		super(simulationData, OperationType.INFLUENCE_MAP);
		this.simulationData = simulationData;
	}
	
	protected InfluenceMap performDry() {
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		
		InfluenceMap influenceMap = new InfluenceMapWithFuture();
		ContactMap contactMap = kappaSystem.getContactMap();
		Observables observables = kappaSystem.getObservables();
		AllSubViewsOfAllAgentsInterface subViews = kappaSystem.getSubViews();
		List<Rule> rules = kappaSystem.getRules();
		
		PlxTimer timer = new PlxTimer();
		simulationData.addInfo(InfoType.INFO,
				"--Abstracting influence map...");
		
		if (!contactMap.isInitialized()) {
			contactMap.fillContactMap(rules, subViews,
					simulationData.getKappaSystem());
		}
		influenceMap.initInfluenceMap(subViews.getAbstractRules(), observables,
				contactMap, subViews.getAgentNameToAgent());
		influenceMap.fillActivatedInhibitedRules(rules, kappaSystem,
				observables);
		SimulationClock.stopTimer(simulationData, InfoType.INFO, timer,
				"--Abstraction:");
		simulationData.addInfo(InfoType.INFO,
				"--Influence map computed");
		
		kappaSystem.setInfluenceMap(influenceMap);
		return influenceMap;
	}

	@Override
	protected boolean noNeedToPerform() {
		return simulationData.getKappaSystem().getInfluenceMap() != null;
	}

	@Override
	protected InfluenceMap retrievePreparedResult() {
		return simulationData.getKappaSystem().getInfluenceMap();
	}

}

package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationClock;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.AbstractOperation;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.influencemap.future.InfluenceMapWithFuture;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class InfluenceMapComputationOperation extends AbstractOperation {

	public InfluenceMapComputationOperation() {
		super(OperationType.INFLUENCE_MAP);
	}
	
	public InfluenceMap perform(SimulationData simulationData) {
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
		influenceMap.initInfluenceMap(subViews.getRules(), observables,
				contactMap, subViews.getAgentNameToAgent());
		influenceMap.fillActivatedInhibitedRules(rules, kappaSystem,
				observables);
		SimulationClock.stopTimer(simulationData, InfoType.INFO, timer,
				"--Abstraction:");
		simulationData.addInfo(InfoType.INFO,
				"--influence map computed");
		
		return influenceMap;
	}

}

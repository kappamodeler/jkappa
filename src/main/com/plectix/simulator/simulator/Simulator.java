package com.plectix.simulator.simulator;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILift;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class Simulator {
	private static final Logger LOGGER = Logger.getLogger(Simulator.class);

	private Model model;

	private double currentTime = 0.;

	public Simulator(Model model) {
		this.model = model;
		model.initialize();
	}

	public void run() {
		long clash = 0;
		CRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = new CProbabilityCalculation(
				model.getSimulationData().getRules());

		double time = 0.;
		
		model.getSimulationData().getObservables().calculateObs(currentTime);
		while (currentTime <= model.getSimulationData().getTimeLength()) {
			rule = ruleProbabilityCalculation.getRandomRule();

			if (rule == null) {
				LOGGER.info("end of simulation: there are no active rules");
				return;
			}
			if (LOGGER.isDebugEnabled()) LOGGER.debug("Rule: " + rule.getName());
			System.out.println("Rule: " + rule.getName());
			
			List<CInjection> injectionsList = rule.getSomeInjectionList();
			System.out.println("Time = " + currentTime);
			currentTime += ruleProbabilityCalculation.getTimeValue();

			if (!isClash(injectionsList)) {
				// negative update
				if (LOGGER.isDebugEnabled()) LOGGER.debug("negative update");

				rule.applyRule(injectionsList);
				for (CInjection injection : injectionsList) {
					for (CSite site : injection.getSiteList()) {
						site.removeInjectionsFromCCToSite(injection);
						site.getLift().clear();
					}
					injection.getConnectedComponent().getInjectionsList()
							.remove(injection);
				}
				model.getSimulationData().getObservables().PrintObsCount();

				// positive update
				if (LOGGER.isDebugEnabled()) LOGGER.debug("positive update");

		/*		for (CRule rules : model.getSimulationData().getRules()) {
					for (CConnectedComponent cc : rules.getLeftHandSide()) {
						cc.doPositiveUpdate(rule.getRightHandSide());
					}
				}
*/
				for (CRule rules : rule.getActivatedRule()) {
					for (CConnectedComponent cc : rules.getLeftHandSide()) {
						cc.doPositiveUpdate(rule.getRightHandSide());
					}
				}
				
				for (ObservablesConnectedComponent oCC : model
						.getSimulationData().getObservables()
						.getConnectedComponentList()) {
					oCC.doPositiveUpdate(rule.getRightHandSide());
				}

				model.getSimulationData().getObservables().calculateObs(
						currentTime);
				model.getSimulationData().getObservables().PrintObsCount();
			} else {
				if (LOGGER.isDebugEnabled()) LOGGER.debug("Clash");
				clash++;
			}
		}
		
		LOGGER.info("end of simulation: time");
	}

	public void outputData() {
		// TODO Auto-generated method stub

	}

	private boolean isClash(List<CInjection> injections) {
		if (injections.size() == 2) {
			for (CSite siteCC1 : injections.get(0).getSiteList())
				for (CSite siteCC2 : injections.get(1).getSiteList())
					if (siteCC1.getAgentLink().getId() == siteCC2
							.getAgentLink().getId())
						return true;
		}
		return false;
	}
}

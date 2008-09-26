package com.plectix.simulator.simulator;

import java.util.List;
import java.util.Random;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CProbabilityCalculation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.ILift;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class Simulator {

	private Model model;

	private double currentTime = 0.;

	public Simulator(Model model) {
		this.model = model;
		model.initialize();
	}

	public void run() {
		long clash = 0;
		CRule rule;
		CProbabilityCalculation ruleProbabilityCalculation = 
			new CProbabilityCalculation(model.getSimulationData().getRules());
		
		while (currentTime <= model.getSimulationData().getTimeLength()) {
			rule = ruleProbabilityCalculation.getRandomRule();
			if (rule==null){
				System.out.println("end of simulation");
				return;
			}
			List<CInjection> injectionsList = rule.getSomeInjectionList();
			currentTime += ruleProbabilityCalculation.getTimeValue();
			
			if (!isClash(injectionsList)) {
				// negative update

				// List<IAgent> newAgentList = model.getSimulationData()
				// .getSolution().apply(rule, inj);

				for (CInjection injection : injectionsList) {
					for (CSite site : injection.getSiteList()) {
						site.removeInjectionsFromCCToSite(injection);
						site.getLift().clear();
					}
					injection.getConnectedComponent().getInjectionsList()
							.remove(injection);
				}

				// positive update
			} else
				clash++;
		}
	}

	public void outputData() {
		// TODO Auto-generated method stub

	}

	private boolean isClash(List<CInjection> injections) {
		if (injections.size()==2 && injections.get(0)==injections.get(1))
			return true;
		return false;
	}
}

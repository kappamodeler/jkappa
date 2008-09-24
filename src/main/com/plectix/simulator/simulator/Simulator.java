package com.plectix.simulator.simulator;

import java.util.List;
import java.util.Random;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
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
		model.init();
	}

	public void run() {
		long clash = 0;
		CRule rule;

		while (currentTime <= model.getSimulationData().getTimeLength()
				|| model.getCommonActivity() != 0.0) {
			rule = getRandomRule();
			if (!isClash(rule)) {
				//currentTime += getRandomTime(model.getCommonActivity(), clash);

				List<CInjection> injectionsList = rule.getSomeInjectionList();

				// rule.recalcultateActivity();
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
				// for (IRule activRule : model.getActivationMap()
				// .getActivateRules(rule)) {
				// activRule.createInjection(newAgentList);// create
				// injection
				// if really
				// there is injection of (some) rule's components to
				// newAgentList,
				// which is root agents of new (after applying rule)
				// connected components
				// and update (or create, if component is new) lift for all
				// agents
				// from new connected component for rule wich has new
				// injection

			} else
				clash++;
		}
	}

	public void outputData() {
		// TODO Auto-generated method stub

	}

	private double equiprDistrRandValue() {
		return 1.;
	}

	public CRule getRandomRule() {
		double probability = equiprDistrRandValue();
		Random rand = new Random(model.getSimulationData().getRules().size());
		return model.getSimulationData().getRules().get(0);
//		return model.getSimulationData().getRules().get(rand.nextInt(3));
	}

	private boolean isClash(CRule rule) {
		// if clash return true
		return false;
	}

	private double getRandomTime(double activity, long clash) {
		return 1.0;
	}
}

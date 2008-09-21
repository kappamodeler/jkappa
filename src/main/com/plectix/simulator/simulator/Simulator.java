package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
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

	// current location of function
	public void removeRulesInjections(IState state, IAgent agent) {
		for (ILift.LiftElement liftElement : state.getLift()) {
			liftElement.getRule().removeInjection(liftElement.getInjection());
			liftElement.getRule().recalcultateActivity();
			for (ISite chSites : agent.getSites()) {
				chSites.getInternalState().removeLiftElement(
						new ILift.LiftElement(liftElement.getRule(), liftElement.getInjection()));
				chSites.getLinkState().removeLiftElement(
						new ILift.LiftElement(liftElement.getRule(), liftElement.getInjection()));
			}
		}
	}

	public void run() {
		long clash = 0;
		IRule rule;

		while (currentTime <= model.getSimulationData().getTimeLength()
				|| model.getCommonActivity() != 0.0) {
			rule = getRandomRule();
			if (!isClash(rule)) {
				currentTime += getRandomTime(model.getCommonActivity(), clash);
				IInjection inj = rule.getSomeInjection();
				// removing of injection from current rule
				rule.removeInjection(inj);
				rule.recalcultateActivity();
				// negative update

				List<IAgent> newAgentList = model.getSimulationData().getSolution()
						.apply(rule, inj);

				for (IAgent agent : inj.getAgents()) {
					IConnectedComponent cComp = model.getSimulationData()
							.getSolution().getConnectedComponent(agent);
					for (IAgent agentComp : cComp.getAgents()) {
						for (ISite chSites : agentComp.getSites()) {
							if (chSites.isChanged()) {
								CInternalState iState = chSites
										.getInternalState();
								if (iState.isChanged()) {
									removeRulesInjections(iState, agentComp);
									iState.setLift(null);
								}
								CLinkState lState = chSites.getLinkState();
								if (lState.isChanged()) {
									removeRulesInjections(lState, agentComp);
									lState.setLift(null);
								}
							}
						}

					}
				}

				// positive update
				for (IRule activRule : model.getActivationMap()
						.getActivateRules(rule)) {
					activRule.createInjection(newAgentList);// create injection
					// if realy
					// there is injection of (some) rule's components to
					// newAgentList,
					// which is root agents of new (after applying rule)
					// connected components
					// and update (or create, if component is new) lift for all
					// agents
					// from new connected component for rule wich has new
					// injection
				}
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

	private IRule getRandomRule() {
		double probability = equiprDistrRandValue();
		// get random rule
		System.out.println("Get random rule.");
		return null;
	}

	private boolean isClash(IRule rule) {
		// if clash return true
		return false;
	}

	private double getRandomTime(double activity, long clash) {
		return 1.0;
	}
}

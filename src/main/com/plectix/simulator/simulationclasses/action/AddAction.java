package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.TypeOfWire;
import com.plectix.simulator.staticanalysis.stories.storage.Event;
import com.plectix.simulator.staticanalysis.stories.storage.WireHashKey;

/**
 * Class implements "ADD" action type.
 * 
 * @author avokhmin
 * @see ActionType
 */
public class AddAction extends Action {
	private final Agent addedAgent;

	/**
	 * Constructor of CAddAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>->A(x)</code>, creates <code>ADD</code> action.<br>
	 * <code>rule</code> - rule "->A(x)";<br>
	 * <code>ccR</code> - connected component "A(x)" from right handSide;<br>
	 * <code>toAgent</code> - agent "A(x)" from right handSide;<br>
	 * other fields from extended {@link Action} - "null" ("fromAgent", "ccL").
	 * 
	 * @param rule
	 *            given rule
	 * @param addedAgent
	 *            given agent from right handSide rule
	 * @param rightHandSideComponent
	 *            given connected component, contains <b>toAgent</b>
	 */
	public AddAction(Rule rule, Agent addedAgent,
			ConnectedComponentInterface rightHandSideComponent) {
		super(rule, null, addedAgent, null, rightHandSideComponent);
		this.addedAgent = addedAgent;
		this.setType(ActionType.ADD);
		this.createBound();
	}

	@Override
	public final void doAction(RuleApplicationPoolInterface pool,
			Injection injection, ActionObserverInteface event,
			SimulationData simulationData) {

		Agent newlyCreatedAgent = new Agent(addedAgent.getName(),
				simulationData.getKappaSystem().generateNextAgentId());

		event.registerAgent(newlyCreatedAgent);
		event.addAtomicEvent(new WireHashKey(newlyCreatedAgent.getId(),
				TypeOfWire.AGENT), null, ActionOfAEvent.MODIFICATION,
				Event.AFTER_STATE);
		for (Site site : addedAgent.getSites()) {
			Site newlyCreatedSite = new Site(site.getName());
			newlyCreatedSite.setInternalState(new InternalState(site
					.getInternalState().getName()));
			newlyCreatedAgent.addSite(newlyCreatedSite);
			event.addSiteToEvent(newlyCreatedSite);
		}

		getRightCComponent().addAgentFromSolutionForRHS(newlyCreatedAgent);
		pool.addAgent(newlyCreatedAgent);

		getRule().registerAddedAgent(addedAgent, newlyCreatedAgent);
	}

	

	private final void createBound() {
		for (Site addedAgentSite : addedAgent.getSites()) {
			if (addedAgentSite.getLinkState().getConnectedSite() != null) {
				getRule().addAction(
						new BoundAction(getRule(), addedAgentSite,
								(addedAgentSite.getLinkState()
										.getConnectedSite()), null,
								getRightCComponent()));
			}
		}
	}
}

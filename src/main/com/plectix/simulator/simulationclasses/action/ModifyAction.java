package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.storage.Event;

/**
 * Class implements "MODIFY" action type.
 * @author avokhmin
 * @see ActionType
 */
public class ModifyAction extends Action {
	private final Site targetSite;
	private final String newInternalStateName;

	/**
	 * Constructor of CModifyAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x~q)->A(x~fi)</code>, creates <code>MODIFY</code> action.<br> 
	 * <code>siteFrom</code> - site "x" from agent "A" from left handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x~q)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x~fi)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x~q)->A(x~fi)".<br>
	 * 
	 * @param rule  given rule
	 * @param sourceSite given site from left handSide
	 * @param targetSite given site from right handSide
	 * @param leftHandSideComponent given connected component from left handSide
	 * @param rightHandSideComponent given connected component from right handSide
	 */
	public ModifyAction(Rule rule, Site sourceSite, Site targetSite,
			ConnectedComponentInterface leftHandSideComponent, ConnectedComponentInterface rightHandSideComponent) {
		super(rule, null, null, leftHandSideComponent, rightHandSideComponent);
		this.targetSite = targetSite;
		this.newInternalStateName = targetSite.getInternalState().getName();
		setActionApplicationSites(sourceSite, targetSite);
		setType(ActionType.MODIFY);
	}

	@Override
	public final void doAction(RuleApplicationPoolInterface pool, Injection injection,
			ActionObserverInteface event, SimulationData simulationData) {
		/**
		 * Done.
		 */
		int agentIdInCC = getAgentIdInCCBySideId(targetSite.getParentAgent());
		Agent agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);
		Site injectedSite = agentFromInSolution.getSiteByName(targetSite
				.getName());

		//event.setTypeById(simulationData.getStoriesAgentTypesStorage());
		event.modifyAddSite(injectedSite, Event.BEFORE_STATE);
		injectedSite.getInternalState().setName(newInternalStateName);
		injection.addToChangedSites(injectedSite);

		event.modifyAddSite( injectedSite, Event.AFTER_STATE);
	}


}

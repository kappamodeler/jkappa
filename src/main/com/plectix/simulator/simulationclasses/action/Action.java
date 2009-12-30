package com.plectix.simulator.simulationclasses.action;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/**
 * This class implements "atomic action".
 * 
 * @see ActionType
 */
public abstract class Action {
	private final Agent sourceAgent;
	private final Agent targetAgent;
	private final Rule rule;
	private final ConnectedComponentInterface rightHandSideComponent;
	private ConnectedComponentInterface leftHandSideComponent;
	private ActionType type;
	private Site targetSite = null;
	private Site sourceSite = null;

	/**
	 * Constructor. Creates atomic action, transforming chosen agent from left
	 * handside to the other one from right handside of the fixed rule
	 * 
	 * @param rule
	 *            fixed rule
	 * @param sourceAgent
	 *            agent from left handSide of the rule
	 * @param targetAgent
	 *            agent from right handSide of the rule
	 * @param leftHandSideComponent
	 *            given connected component, containing <b>fromAgent</b>
	 * @param rightHandSideComponent
	 *            given connected component, containing <b>toAgent</b>
	 */
	protected Action(Rule rule, Agent sourceAgent, Agent targetAgent,
			ConnectedComponentInterface leftHandSideComponent,
			ConnectedComponentInterface rightHandSideComponent) {
		this.sourceAgent = sourceAgent;
		this.targetAgent = targetAgent;
		this.rightHandSideComponent = rightHandSideComponent;
		this.leftHandSideComponent = leftHandSideComponent;
		this.rule = rule;
	}

	/**
	 * This method apply current action in fixed rule application pool.
	 * 
	 * @param pool
	 *            rule application pool, storage where we get changeable
	 *            substances from and where we put the result
	 * @param injection
	 *            injection, pointing to agents which we should take for
	 *            application.
	 * @param netNotation
	 *            network notation ("null" if it not "STORY" simulation mode).
	 * @param simulationData
	 *            simulation data
	 * @throws StoryStorageException 
	 */
	public abstract void doAction(RuleApplicationPoolInterface pool,
			Injection injection, ActionObserverInteface eventContainer,
			SimulationData simulationData) throws StoryStorageException;

	/**
	 * This method returns takes agent from left handside of the rule by it's
	 * image - agent from right handside, and then returns it's id in connected
	 * component
	 * 
	 * @param toAgentRight
	 *            given agent
	 * @return {@link Agent#getIdInConnectedComponent()}
	 */
	protected final int getAgentIdInCCBySideId(Agent toAgentRight) {
		// TODO May be should optimize?
		for (ConnectedComponentInterface cc : getRule().getLeftHandSide())
			for (Agent leftHandSideComponentAgent : cc.getAgents())
				if (leftHandSideComponentAgent.getIdInRuleHandside() == toAgentRight
						.getIdInRuleHandside()) {
					if (leftHandSideComponent == null)
						leftHandSideComponent = cc;
					return leftHandSideComponentAgent
							.getIdInConnectedComponent();
				}
		return -1;
	}


	/**
	 * This method analyses current action and creates new actions, if possible.
	 * 
	 * @return collection of new actions
	 */
	public final Collection<Action> createAtomicActions() {
		Set<Action> atomicActions = new LinkedHashSet<Action>();

		for (Site sourceSite : sourceAgent.getSites()) {
			Site targetSite = targetAgent.getSiteByName(sourceSite.getName());
			if (!sourceSite.getInternalState().hasSimilarName(targetSite.getInternalState())) {
				atomicActions.add(new ModifyAction(getRule(), sourceSite, targetSite,
						leftHandSideComponent,
						rightHandSideComponent));
				getRule().addInhibitedChangedSite(sourceSite, true, false);
			}

			if ((sourceSite.getLinkState().getStatusLink() == LinkStatus.FREE)
					&& (targetSite.getLinkState().getStatusLink() == LinkStatus.FREE))
				continue;

			if ((sourceSite.getLinkState().getStatusLink() != LinkStatus.FREE)
					&& (targetSite.getLinkState().getStatusLink() == LinkStatus.FREE)) {
				atomicActions.add(new BreakAction(getRule(), sourceSite, targetSite,
						leftHandSideComponent,
						rightHandSideComponent));
				getRule().addInhibitedChangedSite(sourceSite, false, true);
				continue;
			}

			if ((sourceSite.getLinkState().getStatusLink() == LinkStatus.FREE)
					&& (targetSite.getLinkState().getStatusLink() == LinkStatus.BOUND)) {
				atomicActions.add(new BoundAction(getRule(), targetSite, targetSite.getLinkState()
						.getConnectedSite(), leftHandSideComponent,
						rightHandSideComponent));
				getRule().addInhibitedChangedSite(sourceSite, false, true);
				continue;
			}

			Site siteConnectedToSiteFrom = sourceSite.getLinkState().getConnectedSite();
			Site siteConnectedToSiteTo = targetSite.getLinkState().getConnectedSite();
			if (siteConnectedToSiteFrom == null || siteConnectedToSiteTo == null)
				continue;
			if ((siteConnectedToSiteFrom.getParentAgent().getIdInRuleHandside() == siteConnectedToSiteTo
					.getParentAgent().getIdInRuleHandside())
					&& (siteConnectedToSiteFrom.equalz(siteConnectedToSiteTo)))
				continue;
			atomicActions.add(new BreakAction(getRule(), sourceSite, targetSite,
					leftHandSideComponent,
					rightHandSideComponent));
			atomicActions.add(new BoundAction(getRule(), targetSite, targetSite.getLinkState()
					.getConnectedSite(), leftHandSideComponent,
					rightHandSideComponent));
			getRule().addInhibitedChangedSite(sourceSite, false, true);
		}
		return atomicActions;
	}

	// =======================GETTERS AND SETTERS========================

	/**
	 * This method sets sites, which this action should be applied to.
	 * 
	 * @param source
	 *            site from the left handside of the rule
	 * @param target
	 *            site from the right handside of the rule
	 */
	protected final void setActionApplicationSites(Site source, Site target) {
		targetSite = target;
		sourceSite = source;
	}

	/**
	 * This method sets type of current action.
	 * 
	 * @param actionType
	 *            type to be set
	 * @see ActionType
	 */
	protected final void setType(ActionType actionType) {
		this.type = actionType;
	}

	/**
	 * This method returns site from the left handside of the rule, which this
	 * action should be applied to.
	 */
	public final Site getSourceSite() {
		return sourceSite;
	}

	/**
	 * This method returns site from the right handside of the rule, which this
	 * action should be applied to.
	 */
	public final Site getTargetSite() {
		return targetSite;
	}

	/**
	 * Util method, used for output only.
	 */
	public final Agent getSourceAgent() {
		return sourceAgent;
	}

	/**
	 * Util method, used for output only.
	 */
	public final Agent getTargetAgent() {
		return targetAgent;
	}

	/**
	 * This method returns connected component from right handSide rule, which
	 * this action should be applied to (may be "null").
	 */
	public final ConnectedComponentInterface getRightCComponent() {
		return rightHandSideComponent;
	}

	/**
	 * This method returns connected component from left handSide rule, which
	 * this action should be applied to (may be "null").
	 */
	public final ConnectedComponentInterface getLeftCComponent() {
		return leftHandSideComponent;
	}

	Rule getRule() {
		return rule;
	}

	public ActionType getType() {
		return type;
	}

}

package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.storage.Event;

/**
 * Class implements "BREAK" action type.
 * @author avokhmin
 * @see ActionType
 */
public class BreakAction extends Action {
	// These name are just as "first" and "second". In fact their 
	// order doesn't play any role 
	private final Site sourceSite;
	private final Site targetSite;
	private final Rule rule;

	/**
	 * Constructor of CBreakAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x!1),B(y!1)->A(x),B(y)</code>, creates 2 <code>BREAK</code> actions.<br> 
	 * <li>relative to site "x" from agent "A":<br>
	 * <code>siteFrom</code> - site "x" from agent "A" from left handSide.<br>
	 * <code>siteTo</code> - site "x" from agent "A" from right handSide.<br>
	 * <code>ccL</code> - connected component "A(x!1),B(y!1)" from left handSide.<br>
	 * <code>ccR</code> - connected component "A(x)" from right handSide.<br>
	 * <code>rule</code> - rule "A(x!1),B(y!1)->A(x),B(y)".<br>
	 * </li>
	 * 
	 * <li>relative to site "y" from agent "B":<br>
	 * similarly.
	 * </li>
	 * 
	 * @param rule  given rule
	 * @param siteFrom given site from left handSide
	 * @param siteTo given site from right handSide
	 * @param leftHandSideComponent given connected component from left handSide
	 * @param rightHandSideComponent given connected component from right handSide
	 */
	public BreakAction(Rule rule, Site siteFrom, Site siteTo,
			ConnectedComponentInterface leftHandSideComponent, ConnectedComponentInterface rightHandSideComponent) {
		super(rule, null, null, leftHandSideComponent, rightHandSideComponent);
		this.rule = rule;
		this.sourceSite = siteFrom;
		this.targetSite = siteTo;
		setActionApplicationSites(siteFrom, siteTo);
		setType(ActionType.BREAK);
	}

	@Override
	public final void doAction(RuleApplicationPoolInterface pool, Injection injection,
			ActionObserverInteface event, SimulationData simulationData) {
		Agent agentFromInSolution;
		int agentIdInCC = getAgentIdInCCBySideId(sourceSite.getParentAgent());
		agentFromInSolution = injection.getAgentFromImageById(agentIdInCC);

		Site injectedSite = agentFromInSolution.getSiteByName(sourceSite.getName());

		Site linkSite = injectedSite.getLinkState().getConnectedSite();
		event.breakAddToEvent(linkSite,Event.BEFORE_STATE);
		event.breakAddToEvent(injectedSite,Event.BEFORE_STATE);
		if ((sourceSite.getLinkState().getConnectedSite() == null) && (linkSite != null)) {
			linkSite.getLinkState().connectSite(null);
			linkSite.getLinkState().setStatusLink(LinkStatus.FREE);
			if (targetSite != null) {
				linkSite.setLinkIndex(-1);
			}
			injection.addToChangedSites(linkSite);
			getRightCComponent().addAgentFromSolutionForRHS(linkSite
					.getParentAgent());
		}

		injectedSite.getLinkState().connectSite(null);
		injectedSite.getLinkState().setStatusLink(LinkStatus.FREE);
		injection.addToChangedSites(injectedSite);

		// Break connection for rules such as A(x!_)->A(x)
		if (sourceSite.getLinkState().getConnectedSite() == null && linkSite != null) {
			addSiteToConnectedWithBroken(linkSite);
		}
		event.breakAddToEvent(linkSite,Event.AFTER_STATE);
		event.breakAddToEvent(injectedSite,Event.AFTER_STATE);
		///////////////////////////////////////////////
		agentFromInSolution.getSiteByName(sourceSite.getName()).setLinkIndex(-1);
	}



	private final void addSiteToConnectedWithBroken(Site checkedSite) {
		for (Site site : rule.getSitesConnectedWithBroken()) {
			if (site == checkedSite) {
				return;
			}
		}
		rule.addSiteConnectedWithBroken(checkedSite);
	}

}

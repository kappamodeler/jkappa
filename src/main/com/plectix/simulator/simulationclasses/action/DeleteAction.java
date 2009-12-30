package com.plectix.simulator.simulationclasses.action;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.LiftElement;
import com.plectix.simulator.simulationclasses.solution.RuleApplicationPoolInterface;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.staticanalysis.stories.ActionOfAEvent;
import com.plectix.simulator.staticanalysis.stories.storage.Event;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;

/**
 * Class implements "DELETE" action type.
 * 
 * @author avokhmin
 * @see ActionType
 */
public class DeleteAction extends Action {
	/**
	 * Constructor of CDeleteAction.<br>
	 * <br>
	 * Example:<br>
	 * <code>A(x)-></code>, creates <code>DELETE</code> action.<br>
	 * <code>fromAgent</code> - agent "A(x)" from left handSide;<br>
	 * <code>ccL</code> - connected component "A(x)" from left handSide.<br>
	 * <code>rule</code> - rule "A(x)->".<br>
	 * 
	 * @param rule
	 *            given rule
	 * @param sourceAgent
	 *            given agent from left handSide rule
	 * @param leftHandSideComponent
	 *            given connected component, contains <b>fromAgent</b>
	 */
	public DeleteAction(Rule rule, Agent sourceAgent,
			ConnectedComponentInterface leftHandSideComponent) {
		super(rule, sourceAgent, null, leftHandSideComponent, null);
		setType(ActionType.DELETE);
	}

	@Override
	public final void doAction(RuleApplicationPoolInterface pool,
			Injection injection, ActionObserverInteface event,
			SimulationData simulationData) throws StoryStorageException {

		Agent agent = injection.getAgentFromImageById(this.getSourceAgent()
				.getIdInConnectedComponent());
		event.registerAgent(agent);

		event.addToEvent(agent, ActionOfAEvent.TEST_AND_MODIFICATION,
				getSourceAgent());
		event.deleteAddNonFixedSites(agent);
		for (Site site : agent.getSites()) {
			checkAndRemoveSiteConnectedWithDeletedOne(site);
			Site solutionSite = (Site) site.getLinkState().getConnectedSite();

			if (solutionSite != null) {
				event.deleteAddToEvent(solutionSite,
						Event.BEFORE_STATE);
				checkAndAddSiteConnectedWithDeletedOne(solutionSite);
				solutionSite.getLinkState().connectSite(null);
				solutionSite.getLinkState().setStatusLink(LinkStatus.FREE);
				solutionSite.setLinkIndex(-1);

				event.deleteAddToEvent(solutionSite,
						Event.AFTER_STATE);
			}
		}

		for (LiftElement lift : agent.getDefaultSite().getLift()) {
			agent.getDefaultSite().clearIncomingInjections(lift.getInjection());
			lift.getInjection().getConnectedComponent().removeInjection(
					lift.getInjection());
		}

		for (Site site : agent.getSites()) {
			for (LiftElement lift : site.getLift()) {
				site.clearIncomingInjections(lift.getInjection());
				lift.getInjection().getConnectedComponent().removeInjection(
						lift.getInjection());
			}
			site.clearLifts();
			injection.removeSiteFromSitesList(site);
		}
		pool.removeAgent(agent);
	}


	private final void checkAndAddSiteConnectedWithDeletedOne(Site siteToCheck) {
		Rule rule = this.getRule();
		for (Site site : rule.getSitesConnectedWithDeleted()) {
			if (site == siteToCheck) {
				return;
			}
		}
		rule.addSiteConnectedWithDeleted(siteToCheck);
	}

	private final void checkAndRemoveSiteConnectedWithDeletedOne(
			Site siteToCheck) {
		Rule rule = this.getRule();
		int size = rule.getSitesConnectedWithDeleted().size();
		for (int i = 0; i < size; i++) {
			if (rule.getSiteConnectedWithDeleted(i) == siteToCheck) {
				rule.removeSiteConnectedWithDeleted(i);
				return;
			}
		}
	}
}

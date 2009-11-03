package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.injections.LiftElement;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

public class UpdatesPerformer {
	public static final List<Agent> doNegativeUpdateForDeletedAgents(
			Rule rule, List<Injection> injections) {
		List<Agent> freeAgents = new ArrayList<Agent>();
		for (Injection injection : injections) {
			for (Site checkedSite : rule.getSitesConnectedWithDeleted()) {
				if (!injection.checkSiteExistanceAmongChangedSites(checkedSite)) {

					Agent checkedAgent = checkedSite.getParentAgent();
					addToAgentList(freeAgents, checkedAgent);
					for (LiftElement lift : checkedAgent.getDefaultSite()
							.getLift()) {
						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedAgent.getDefaultSite().clearLifts();
					for (LiftElement lift : checkedSite.getLift()) {

						for (Site site : lift.getInjection().getSiteList()) {
							if (site != checkedSite)
								site.removeInjectionFromLift(lift
										.getInjection());
						}

						lift.getConnectedComponent().removeInjection(
								lift.getInjection());
					}
					checkedSite.clearLifts();
				}
			}
		}
		for (Site checkedSite : rule.getSitesConnectedWithBroken()) {
			Agent checkedAgent = checkedSite.getParentAgent();
			addToAgentList(freeAgents, checkedAgent);
		}
		return freeAgents;
	}
	
	private static final void addToAgentList(List<Agent> list, Agent agent) {
		if (agent.includedInCollection(list)) {
			return;
		}
		list.add(agent);
	}
	
	public static final void doNegativeUpdate(List<Injection> injections) {
		for (Injection injection : injections) {
			if (injection != ThreadLocalData.getEmptyInjection()) {
				for (Site site : injection.getChangedSites()) {
					site.getParentAgent().getDefaultSite()
							.clearIncomingInjections(injection);
					site.getParentAgent().getDefaultSite().clearLifts();
					site.clearIncomingInjections(injection);
					site.clearLifts();
				}
				if (injection.getChangedSites().size() != 0) {
					for (Site site : injection.getSiteList()) {
						if (!injection
								.checkSiteExistanceAmongChangedSites(site)) {
							site.removeInjectionFromLift(injection);
						}
					}
					injection.getConnectedComponent().removeInjection(injection);
				}
			}
		}
	}
}

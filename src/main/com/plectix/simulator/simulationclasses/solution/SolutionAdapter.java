package com.plectix.simulator.simulationclasses.solution;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.InternalState;
import com.plectix.simulator.staticanalysis.Link;
import com.plectix.simulator.staticanalysis.Site;

/**
 * This class implements basic solution functionality, that doesn't depend on operation modes 
 */
/*package*/ abstract class SolutionAdapter implements SolutionInterface {
	private final List<SolutionLine> solutionLines = new ArrayList<SolutionLine>();
	private final KappaSystem kappaSystem;

	/**
	 * Constructor which takes KappaSystem object as a parameter.
	 * This solution is the one contained in this KappaSystem.
	 * @param kappaSystem parent KappaSystem object
	 */
	SolutionAdapter(KappaSystem kappaSystem) {
		this.kappaSystem = kappaSystem;
	}
	
	//TODO check whether we use this feature or not
	@Override
	public final void checkSolutionLinesAndAdd(String line, long number) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLine sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setNumber(sl.getNumber() + number);
				return;
			}
		}
		solutionLines.add(new SolutionLine(line, number));
	}
	
	@Override
	public final List<SolutionLine> getSolutionLines() {
		return solutionLines;
	}
	
	@Override
	public final void clearSolutionLines() {
		solutionLines.clear();
	}
	
	//----------------------------CLONE METHODS--------------------------------------
	
	// TODO move this method to special util class
	@Override
	public final ConnectedComponentInterface cloneConnectedComponent(ConnectedComponentInterface component) {
		return new ConnectedComponent(cloneAgentsList(component.getAgents()));
	}

	// TODO move this method to special util class
	@Override
	public final List<Agent> cloneAgentsList(List<Agent> agentList) {
		List<Agent> newAgentsList = new ArrayList<Agent>();
		for (Agent agent : agentList) {
			Agent newAgent = new Agent(agent.getName(), kappaSystem.generateNextAgentId());
			newAgent.setIdInRuleSide(agent.getIdInRuleHandside());
			for (Site site : agent.getSites()) {
				Site newSite = new Site(site.getName(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new InternalState(site
						.getInternalState().getName()));
				// newSite.getInternalState().setName(
				// site.getInternalState().getName());
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (Site siteNew : newAgentsList.get(i).getSites()) {
				Link lsNew = siteNew.getLinkState();
				Link lsOld = agentList.get(i)
						.getSiteByName(siteNew.getName()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					Site siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < agentList.size(); j++) {
						if (agentList.get(j) == siteOldLink.getParentAgent())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByName(
							siteOldLink.getName()));
				}
			}
		}
		return newAgentsList;
	}
	
	@Override
	public final KappaSystem getKappaSystem() {
		return kappaSystem;
	}
}

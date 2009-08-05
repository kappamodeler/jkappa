package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This class implements basic solution functionality, that doesn't depend on operation modes 
 */
/*package*/ abstract class SolutionAdapter implements ISolution {
	private final List<SolutionLine> solutionLines = new ArrayList<SolutionLine>();
	private final KappaSystem mySystem;

	/**
	 * Constructor which takes KappaSystem object as a parameter.
	 * This solution is the one contained in this KappaSystem.
	 * @param system parent KappaSystem object
	 */
	SolutionAdapter(KappaSystem system) {
		mySystem = system;
	}
	
	//TODO check whether we use this feature or not
	@Override
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLine sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		solutionLines.add(new SolutionLine(line, count));
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
	public final IConnectedComponent cloneConnectedComponent(IConnectedComponent component) {
		return new CConnectedComponent(cloneAgentsList(component.getAgents()));
	}

	// TODO move this method to special util class
	@Override
	public final List<CAgent> cloneAgentsList(List<CAgent> agentList) {
		List<CAgent> newAgentsList = new ArrayList<CAgent>();
		for (CAgent agent : agentList) {
			CAgent newAgent = new CAgent(agent.getNameId(), mySystem.generateNextAgentId());
			newAgent.setIdInRuleSide(agent.getIdInRuleHandside());
			for (CSite site : agent.getSites()) {
				CSite newSite = new CSite(site.getNameId(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new CInternalState(site
						.getInternalState().getNameId()));
				// newSite.getInternalState().setNameId(
				// site.getInternalState().getNameId());
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (CSite siteNew : newAgentsList.get(i).getSites()) {
				CLink lsNew = siteNew.getLinkState();
				CLink lsOld = agentList.get(i)
						.getSiteByNameId(siteNew.getNameId()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					CSite siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < agentList.size(); j++) {
						if (agentList.get(j) == siteOldLink.getParentAgent())
							break;
					}
					int index = j;
					lsNew.connectSite(newAgentsList.get(index).getSiteByNameId(
							siteOldLink.getNameId()));
				}
			}
		}
		return newAgentsList;
	}
	
	@Override
	public final KappaSystem getKappaSystem() {
		return mySystem;
	}
}

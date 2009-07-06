package com.plectix.simulator.components.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.KappaSystem;

/*package*/ abstract class SolutionAdapter implements ISolution {
	private List<SolutionLines> solutionLines = new ArrayList<SolutionLines>();
	private final KappaSystem mySystem;

	public SolutionAdapter(KappaSystem system) {
		mySystem = system;
	}
	
	//TODO REMOVE
	public final void checkSolutionLinesAndAdd(String line, long count) {
		line = line.replaceAll("[ 	]", "");
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			line = line.substring(0, line.length() - 1);
		}
		for (SolutionLines sl : solutionLines) {
			if (sl.getLine().equals(line)) {
				sl.setCount(sl.getCount() + count);
				return;
			}
		}
		solutionLines.add(new SolutionLines(line, count));
	}
	
	public final List<SolutionLines> getSolutionLines() {
		return Collections.unmodifiableList(solutionLines);
	}
	
	public final void clearSolutionLines() {
		solutionLines.clear();
	}
	
	//----------------------------CLONE METHODS--------------------------------------
	
	public IConnectedComponent cloneConnectedComponent(IConnectedComponent component) {
		return new CConnectedComponent(cloneAgentsList(component.getAgents()));
	}
	
	public List<IConnectedComponent> cloneConnectedComponents(List<IConnectedComponent> components) {
		List<IConnectedComponent> cloned = new ArrayList<IConnectedComponent>();
		for (IConnectedComponent component : components) {
			cloned.add(cloneConnectedComponent(component));			
		}
		return cloned;
	}
	
	public List<CAgent> cloneAgentsList(List<CAgent> agentList) {
		List<CAgent> newAgentsList = new ArrayList<CAgent>();
		for (CAgent agent : agentList) {
			CAgent newAgent = new CAgent(agent.getNameId(), mySystem.generateNextAgentId());
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
						if (agentList.get(j) == siteOldLink.getAgentLink())
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
	
	public KappaSystem getKappaSystem() {
		return mySystem;
	}

//	public void applyRule(RuleApplicationPool pool) {
////		for (IConnectedComponent component : pool.getComponents()) {
////			addConnectedComponent(component);
////		}
//	}
	
//	public void applyRule(RuleApplicationPool pool) {
//		for (InfoAddAction action : pool.getAddActions()) {
//			addAgent(action.getAgent());
//		}
//		for (InfoDeleteAction action : pool.getDeleteActions()) {
//			removeAgent(action.getAgent());
//		}
//		for (InfoModifyAction action : pool.getModifyActions()) {
//			action.getSite().getInternalState().setNameId(action.getNewInternalStateNameId());
//		}
//		for (InfoBoundAction action : pool.getBoundActions()) {
//			agent(action.getAgent());
//		}
//		for (InfoBreakAction action : pool.getBreakActions()) {
//			agent(action.getAgent());
//		}
//	}
}

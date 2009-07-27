package com.plectix.simulator.rulecompression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.text.AbstractDocument.LeafElement;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLink;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractLinkState;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.base.AbstractAction;
import com.plectix.simulator.components.complex.subviews.base.SubViewsRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.parser.abstractmodel.AbstractLinkState;


/*
 * TODO see rule 
 * 
 * R(Y68~p!1),G(a!1,b),So(d) -> R(Y68~p!1),G(a!1,b!2),So(d!2)
 * 
 * actions before : 3 x default, 2 x bound
 * actions after : 2 x default, 1 x add, 1 x bound
 * 
 *  default one doesn't matter, but why others? 
 */
public class QualitativeCompressor {

	// TODO -> subViews
	// private IAllSubViewsOfAllAgents allSubViews;
	private CLocalViewsMain localviews;
	private CRule compressedRule;

	Map<Integer, CAgent> mapAfter;
	Map<Integer, CAgent> mapBefore;
	List<Integer> removedList;

	public QualitativeCompressor(CLocalViewsMain localviews) {
		this.localviews = localviews;
		this.compressedRule = null;
	}

	public boolean compress(CRule rule) {

		boolean answer = false;

		List<IConnectedComponent> listBefore = new LinkedList<IConnectedComponent>();
		List<IConnectedComponent> listAfter = new LinkedList<IConnectedComponent>();

		mapBefore = new LinkedHashMap<Integer, CAgent>();
		if (rule.getLeftHandSide() != null) {
			for (IConnectedComponent c : rule.getLeftHandSide()) {
				List<CAgent> oldLeftSide = new LinkedList<CAgent>(c.getAgents());
				List<CAgent> newLeftSide = clone(oldLeftSide);
				for (CAgent agent : newLeftSide) {
					mapBefore.put(agent.getIdInRuleHandside(), agent);
				}
				// listBefore.add(new CConnectedComponent(newLeftSide));

			}
		}

		mapAfter = new LinkedHashMap<Integer, CAgent>();
		if (rule.getRightHandSide() != null) {
			for (IConnectedComponent c : rule.getRightHandSide()) {
				List<CAgent> oldRightSide = new LinkedList<CAgent>(c
						.getAgents());
				List<CAgent> newRightSide = clone(oldRightSide);
				for (CAgent agent : newRightSide) {
					// agent.getIdInRuleHandside()
					mapAfter.put(agent.getIdInRuleHandside(), agent);
				}
				// listAfter.add(new CConnectedComponent(newRightSide));
			}
		}

		// clean up internal states
		answer = decreaseInternalStates() || answer;

		// delete unnecessary agents
		answer = deleteEmptyEnds() || answer;
		//
		// for(IConnectedComponent ic : listBefore){
		// System.out.println(((CConnectedComponent)(ic)).getHash());
		// }
		//
		// for(IConnectedComponent ic : listAfter){
		// System.out.println(((CConnectedComponent)(ic)).getHash());
		// }
		if (rule.getLeftHandSide() != null) {
			for (IConnectedComponent c : rule.getLeftHandSide()) {

				List<CAgent> newLeftSide = new LinkedList<CAgent>();
				for (CAgent agent : c.getAgents()) {
					int id = agent.getIdInRuleHandside();
					if (!removedList.contains(id)) {
						newLeftSide.add(mapBefore.get(id));
					}
				}
				if (!newLeftSide.isEmpty()) {
					sortBefore(newLeftSide);
					listBefore.add(new CConnectedComponent(newLeftSide));
				}
			}
		}
		if (rule.getRightHandSide() != null) {
			for (IConnectedComponent c : rule.getRightHandSide()) {
				List<CAgent> newRightSide = new LinkedList<CAgent>();
				for (CAgent agent : c.getAgents()) {
					int id = agent.getIdInRuleHandside();
					if (!removedList.contains(id)) {
						newRightSide.add(mapAfter.get(id));
					}
				}
				if (!newRightSide.isEmpty()) {
					sortAfter(newRightSide);
					listAfter.add(new CConnectedComponent(newRightSide));
				}
			}
		}

		// TODO ruleID
		compressedRule = new CRule(listBefore, listAfter, rule.getName()
				+ "_compressed", rule.getRate(), 0, false);
		return answer;

	}

	private void sortAfter(List<CAgent> newRightSide) {
		int i = 0;
		for (CAgent agent : newRightSide) {
			if (mapBefore.get(agent.getIdInRuleHandside()) == null) {
				continue;
			}
			i++;
		}
		for (CAgent agent : newRightSide) {
			if (mapBefore.get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	private void sortBefore(List<CAgent> newLeftSide) {
		int i = 0;
		for (CAgent agent : newLeftSide) {
			if (mapAfter.get(agent.getIdInRuleHandside()) == null)
				continue;

			agent.setIdInConnectedComponent(i);
			mapAfter.get(agent.getIdInRuleHandside())
					.setIdInConnectedComponent(i);
			i++;
		}
		for (CAgent agent : newLeftSide) {
			if (mapAfter.get(agent.getIdInRuleHandside()) == null) {
				agent.setIdInConnectedComponent(i);
				i++;
			}

		}

	}

	private boolean decreaseInternalStates() {
		boolean changed = false;

		for (int idInRule : mapBefore.keySet()) {
			changed = decreaseInternalStatesAndFreeLinkStateToWild(mapBefore
					.get(idInRule), mapAfter.get(idInRule))
					|| changed;

		}

		return changed;
	}

	private boolean deleteEmptyEnds() {
		removedList = new LinkedList<Integer>();
		boolean removed = false;
		Stack<CAgent> stack = new Stack<CAgent>();
		stack.addAll(mapBefore.values());

		while (!stack.isEmpty()) {
			CAgent agent = stack.pop();
			// may be trouble with empty agents TODO
			if (agent.getSites().size() > 1 || agent.getSites().size() == 0) {
				continue;
			}
			int siteId = agent.getSites().iterator().next().getNameId();

			CSite site = agent.getSiteByNameId(siteId);

			if (!(site.getInternalState().isRankRoot())) {
				continue;
			}
			if (site.getLinkState().getStatusLink() != CLinkStatus.BOUND) {
				continue;
			}

			CSite connectedSite = site.getLinkState().getConnectedSite();
			if (connectedSite != null) {
				if (decreaseLinkSite(site)) {
					stack.add(connectedSite.getAgentLink());
					removed = true;
				}
			}

		}

		return removed;
	}

	private boolean decreaseLinkSite(CSite site) {
		CSite connectedSite = site.getLinkState().getConnectedSite();
		CAgent agent = connectedSite.getAgentLink();
		CAbstractAgent aAgent = clone(agent);
		int size = localviews.getCountOfCoherentAgent(aAgent);

		aAgent.getSite(connectedSite.getNameId()).getLinkState().setSemiLink();
		if (size == localviews.getCountOfCoherentAgent(aAgent)) {
			connectedSite.getLinkState().setSemiLink();

			CLink linkState = mapAfter.get(agent.getIdInRuleHandside())
					.getSiteByNameId(connectedSite.getNameId()).getLinkState();

			if (linkState.getStatusLink() != CLinkStatus.FREE) {
				linkState.setSemiLink();
			}

			Integer removedId = site.getAgentLink().getIdInRuleHandside();
			mapBefore.remove(removedId);
			mapAfter.remove(removedId);
			removedList.add(removedId);
			return true;
		}

		return false;
	}

	private CAbstractAgent clone(CAgent agent) {
		if (agent == null) {
			return null;
		}
		CAbstractAgent newAgent = new CAbstractAgent(agent);
		for (CSite s : agent.getSites()) {
			CAbstractSite newSite = new CAbstractSite(s, newAgent);
			newAgent.addSite(newSite);
		}
		return newAgent;
	}

	public CRule getCompressedRule() {
		return compressedRule;
	}

	private boolean decreaseInternalStatesAndFreeLinkStateToWild(
			CAgent agentBefore, CAgent agentAfter) {
		boolean decreased = false;
		if (agentBefore == null) {
			return decreased;
		}
		AbstractAction action = new AbstractAction(clone(agentBefore),
				clone(agentAfter));
		CAbstractAgent aAgent = action.getLeftHandSideAgent();
		int size = localviews.getCountOfCoherentAgent(aAgent);

		for (CAbstractSite aSite : action.getTestedSites()) {
			decreased = decreaseInternalState(aAgent, agentBefore
					.getSiteByNameId(aSite.getNameId()), size)
					|| decreased;
			decreased = decreaseLinkState(aAgent, agentBefore
					.getSiteByNameId(aSite.getNameId()), true, size)
					|| decreased;

		}

		if (action.getModificatedSites() != null) {
			for (CAbstractSite aSite : action.getModificatedSites()) {
				decreased = decreaseInternalState(aAgent, agentBefore
						.getSiteByNameId(aSite.getNameId()), size)
						|| decreased;
				decreased = decreaseLinkState(aAgent, agentBefore
						.getSiteByNameId(aSite.getNameId()), false, size)
						|| decreased;
			}
		}
		return decreased;
	}

	private boolean decreaseLinkState(CAbstractAgent aAgent, CSite site,
			boolean tested, int sizeOfProper) {
		// TODO optimize

		CAbstractLinkState oldLinkState = new CAbstractLinkState(aAgent
				.getSite(site.getNameId()).getLinkState());

		CAgent dualAgent = mapAfter.get(site.getAgentLink()
				.getIdInRuleHandside());
		if (oldLinkState.getStatusLink() == CLinkStatus.FREE) {
			aAgent.getSite(site.getNameId()).getLinkState().setWildLinkState();
			if (sizeOfProper == localviews.getCountOfCoherentAgent(aAgent)) {
				site.getLinkState().setWildLinkState();
				if (tested && site.getInternalState().getNameId() == -1) {
					site.getAgentLink().removeSite(site.getNameId());
					if (dualAgent != null) {
						dualAgent.removeSite(site.getNameId());
					}
				}
				return true;
			}
			aAgent.getSite(site.getNameId()).setLinkState(oldLinkState);
			return false;
		} else {
			if (oldLinkState.getStatusLink() == CLinkStatus.BOUND) {
				return false;
			} else {
				if (tested && site.getInternalState().getNameId() == -1) {
					// site.getAgentLink().getSites().remove(site);
					site.getAgentLink().removeSite(site.getNameId());
					dualAgent.removeSite(site.getNameId());
					return true;
				}

				return false;
			}

		}

	}

	private boolean decreaseInternalState(CAbstractAgent aAgent, CSite site,
			int sizeOfProper) {
		// TODO optimize
		int oldInternal = aAgent.getSite(site.getNameId()).getInternalState()
				.getNameId();
		aAgent.getSite(site.getNameId()).getInternalState().setNameId(-1);
		if (sizeOfProper == localviews.getCountOfCoherentAgent(aAgent)) {
			site.getInternalState().setNameId(-1);
			return true;
		}
		aAgent.getSite(site.getNameId()).getInternalState().setNameId(
				oldInternal);
		return false;
	}

	private ArrayList<CAgent> clone(List<CAgent> instance) {
		ArrayList<CAgent> newAgentsList = new ArrayList<CAgent>();
		for (CAgent agent : instance) {
			CAgent newAgent = new CAgent(agent.getNameId(), agent.getId());
			newAgent.setIdInConnectedComponent(agent
					.getIdInConnectedComponent());
			newAgent.setIdInRuleSide(agent.getIdInRuleHandside());
			for (CSite site : agent.getSites()) {
				CSite newSite = new CSite(site.getNameId(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new CInternalState(site
						.getInternalState().getNameId()));
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (CSite siteNew : newAgentsList.get(i).getSites()) {
				CLink lsNew = siteNew.getLinkState();
				CLink lsOld = instance.get(i).getSiteByNameId(
						siteNew.getNameId()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getConnectedSite() != null) {
					CSite siteOldLink = lsOld.getConnectedSite();
					int j = 0;
					for (; j < instance.size(); j++) {
						if (instance.get(j) == siteOldLink.getAgentLink())
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

}

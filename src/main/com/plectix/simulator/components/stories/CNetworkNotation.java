package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInjection;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IAgentLink;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.INetworkNotation;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.interfaces.IStoriesSiteStates;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;

public class CNetworkNotation implements INetworkNotation {
	public enum NetworkNotationMode {
		TEST, TEST_OR_MODIFY, MODIFY, NONE;
	}

	public enum IntersectionType {
		NO_INTERSECTION, PART_INTERSECTION, FULL_INTERSECTION;
	}

	private boolean leaf;
	private int step;
	private final int ruleID;
	private Simulator simulator;
	private Map<Long, AgentSites> changesOfAllUsedSites;
	private Map<Long, AgentSites> changedAgentsFromSolution;
	private Map<Long, AgentSitesFromRules> usedAgentsFromRules;
	List<String> agentsNotation;
	List<List<Long>> introCC;

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	public Map<Long, AgentSites> getChangesOfAllUsedSites() {
		return changesOfAllUsedSites;
	}

	public Map<Long, AgentSites> getChangedAgentsFromSolution() {
		return changedAgentsFromSolution;
	}

	public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
		return usedAgentsFromRules;
	}

	public List<String> getAgentsNotation() {
		return agentsNotation;
	}

	@Override
	public String toString() {
		String st = "ruleName="
				+ simulator.getSimulationData().getRulesByID(ruleID).getName()
				+ " ";
		st += "usedAgentsFromRules=" + usedAgentsFromRules.keySet().toString()
				+ " ";
		st += "changedAgentsFromSolution="
				+ changedAgentsFromSolution.keySet().toString() + " ";
		st += "changesOfAllUsedSites="
				+ changesOfAllUsedSites.keySet().toString() + " ";
		st += "step=" + step + " ";
		st += "agentsNotation=" + agentsNotation.toString() + " ";

		// return super.toString();
		return st;
	}

	public CNetworkNotation(Simulator simulator, int step, IRule rule,
			List<IInjection> injectionsList, SimulationData data) {
		this.simulator = simulator;
		this.step = step;
		// this.rule = rule;
		this.ruleID = rule.getRuleID();
		leaf = false;
		this.changedAgentsFromSolution = new HashMap<Long, AgentSites>();
		this.changesOfAllUsedSites = new HashMap<Long, AgentSites>();
		this.usedAgentsFromRules = new HashMap<Long, AgentSitesFromRules>();
		this.agentsNotation = new ArrayList<String>();
		this.introCC = new ArrayList<List<Long>>();
		createAgentsNotation(injectionsList, data);
	}

	public final void changeIntroCCAndAgentNotation(int indexToDel,
			List<Long> agentsList, String str) {
		this.introCC.remove(indexToDel);
		this.introCC.add(agentsList);
		this.getAgentsNotation().remove(indexToDel);
		this.getAgentsNotation().add(str);
	}

	public final void changeIntroCC(Long agentToDelete, Long agent) {
		for (List<Long> agentIDsListint : this.introCC) {
			int index = agentIDsListint.indexOf(agentToDelete);
			if (index >= 0) {
				agentIDsListint.remove(index);
				agentIDsListint.add(agent);
				return;
			}
		}
	}

	private final void createAgentsNotation(List<IInjection> injectionsList,
			SimulationData data) {

		ISolution solution = data.getSolution();

		for (IInjection inj : injectionsList) {
			if (inj != CInjection.EMPTY_INJECTION) {
				IConnectedComponent cc = solution.getConnectedComponent(inj
						.getAgentLinkList().get(0).getAgentTo());
				agentsNotation.add(SimulationUtils.printPartRule(cc,
						new int[] { 0 }, data.isOcamlStyleObsName()));
				List<Long> agentIDsList = new ArrayList<Long>();

				for (IAgent agent : cc.getAgents()) {
					agentIDsList.add(agent.getId());
				}
				introCC.add(agentIDsList);

			}
		}

	}
	
	public List<List<Long>> getIntroCC() {
		return introCC;
	}

	public final void checkLinkForNetworkNotation(StateType index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, -1, -1),
					index, true);
		else
			this.addToAgents(site, new CStoriesSiteStates(index, ((CAgent) site
					.getLinkState().getSite().getAgentLink()).getHash(),
					((CSite) site.getLinkState().getSite()).getNameId()),
					index, true);
	}

	public final boolean changedSitesContains(ISite site) {
		IAgent agent = site.getAgentLink();
		AgentSites as = changesOfAllUsedSites.get(agent.getId());
		if (as != null) {
			IStoriesSiteStates sss = as.getSites().get(site.getNameId());
			if (sss != null)
				return true;
		}
		return false;
	}

	public final void checkLinkToUsedSites(StateType index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index, false);
		else
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), ((CAgent) site
					.getLinkState().getSite().getAgentLink()).getHash(),
					((CSite) site.getLinkState().getSite()).getNameId()),
					index, false);
	}

	public final void checkLinkForNetworkNotationDel(StateType index,
			ISite site, boolean toChangedAgents) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index,
					toChangedAgents);
		else
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), ((CAgent) site
					.getLinkState().getSite().getAgentLink()).getHash(),
					((CSite) site.getLinkState().getSite()).getNameId()),
					index, toChangedAgents);
	}

	public final void addToAgents(ISite site, IStoriesSiteStates siteStates,
			StateType index, boolean toChangedAgents) {

		Map<Long, AgentSites> map;
		if (toChangedAgents)
			map = changedAgentsFromSolution;
		else
			map = changesOfAllUsedSites;
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = map.get(key);
			if (as == null) {
				as = new AgentSites(site.getAgentLink());
				map.put(key, as);
			}
			as.addToSites(site.getNameId(), siteStates, index);
		}
	}

	public final void addToAgentsFromRules(ISite site,
			NetworkNotationMode agentMode,
			NetworkNotationMode internalStateMode,
			NetworkNotationMode linkStateMode) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode, site.getAgentLink());
				usedAgentsFromRules.put(key, aSFR);
			}
			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode, site.getAgentLink().getNameId());
		}
	}

	public final void addFixedSitesFromRules(ISite site,
			NetworkNotationMode agentMode, boolean internalState,
			boolean linkState) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode, site.getAgentLink());
				usedAgentsFromRules.put(key, aSFR);
			}
			NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;
			NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;

			if (internalState == true)
				internalStateMode = NetworkNotationMode.TEST;
			if (linkState == true)
				linkStateMode = NetworkNotationMode.TEST;

			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode, site.getAgentLink().getNameId());
		}
	}

	public final IRule getRule() {
		return simulator.getSimulationData().getRulesByID(ruleID);
	}

	public final CNetworkNotation isNotOpposite(
			List<CNetworkNotation> networkNotationList) {
		for (int i = networkNotationList.size() - 1; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			switch (isIntersects(nn, true)) {
			case FULL_INTERSECTION:
				networkNotationList.remove(i);
				return nn;
			case PART_INTERSECTION:
				return null;
			}
		}
		return null;
	}

	public final boolean isOpposite(CNetworkNotation networkNotation) {
		switch (isIntersects(networkNotation, true)) {
		case FULL_INTERSECTION:
			return false;
		case PART_INTERSECTION:
			return true;
		}
		return true;
	}

	public final IntersectionType isIntersects(CNetworkNotation nn) {
		Iterator<Long> iterator = this.changedAgentsFromSolution.keySet()
				.iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Long key = iterator.next();

			if (nn.changedAgentsFromSolution.containsKey(key)) {
				IntersectionType it = checkSites(key, nn);

				if (it == IntersectionType.FULL_INTERSECTION)
					fullCounter++;
				// if (it != IntersectionType.NO_INTERSECTION)
				counter++;
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.size())
				&& (this.changedAgentsFromSolution.size() == nn.changedAgentsFromSolution
						.size()))
			return IntersectionType.FULL_INTERSECTION;

		if (counter > 0)
			return IntersectionType.PART_INTERSECTION;

		return IntersectionType.NO_INTERSECTION;
	}

	public final IntersectionType isIntersects(CNetworkNotation nn,
			boolean isAllUsedSites) {
		Map<Long, AgentSites> mapThis;
		Map<Long, AgentSites> mapCheck;

		if (isAllUsedSites) {
			mapThis = this.changesOfAllUsedSites;
			mapCheck = nn.changesOfAllUsedSites;
		} else {
			mapThis = this.changedAgentsFromSolution;
			mapCheck = nn.changedAgentsFromSolution;
		}
		Iterator<Long> iterator = mapThis.keySet().iterator();

		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Long key = iterator.next();

			if (mapCheck.containsKey(key)) {
				IntersectionType it = checkSites(key, mapThis, mapCheck);
				// IntersectionType it = checkSites(key, nn);
				if (it == IntersectionType.FULL_INTERSECTION)
					fullCounter++;
				// if ((!isAllUsedSites && it !=
				// IntersectionType.NO_INTERSECTION) || isAllUsedSites)
				if (it != IntersectionType.NO_INTERSECTION)
					counter++;
			}
		}

		if ((fullCounter == mapThis.size())
				&& (mapThis.size() == mapCheck.size()))
			return IntersectionType.FULL_INTERSECTION;

		if (counter > 0)
			return IntersectionType.PART_INTERSECTION;

		return IntersectionType.NO_INTERSECTION;
	}

	private final IntersectionType checkSites(long key,
			Map<Long, AgentSites> mapThis, Map<Long, AgentSites> mapCheck) {
		Iterator<Integer> iterator = mapThis.get(key).getSites().keySet()
				.iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Integer keySite = iterator.next();
			if (mapCheck.get(key).getSites().containsKey(keySite)) {
				counter++;

				if (CStoriesSiteStates.isEqual(mapThis.get(key).getSites().get(
						keySite).getAfterState(), mapCheck.get(key).getSites()
						.get(keySite).getBeforeState())) {
					fullCounter++;
				}
			}
		}

		if ((fullCounter == mapThis.get(key).getSites().size())
				&& (mapThis.get(key).getSites().size() == mapCheck.get(key)
						.getSites().size()))
			return IntersectionType.FULL_INTERSECTION;

		if (counter > 0)
			return IntersectionType.PART_INTERSECTION;

		return IntersectionType.NO_INTERSECTION;
	}

	private final IntersectionType checkSites(long key, CNetworkNotation nn) {
		Iterator<Integer> iterator = this.changedAgentsFromSolution.get(key)
				.getSites().keySet().iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Integer keySite = iterator.next();
			if (nn.changedAgentsFromSolution.get(key).getSites().containsKey(
					keySite)) {
				counter++;

				if (CStoriesSiteStates.isEqual(this.changedAgentsFromSolution
						.get(key).getSites().get(keySite).getAfterState(),
						nn.changedAgentsFromSolution.get(key).getSites().get(
								keySite).getBeforeState())) {
					fullCounter++;
				}
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.get(key).getSites()
				.size())
				&& (this.changedAgentsFromSolution.get(key).getSites().size() == nn.changedAgentsFromSolution
						.get(key).getSites().size()))
			return IntersectionType.FULL_INTERSECTION;

		if (counter > 0)
			return IntersectionType.PART_INTERSECTION;

		return IntersectionType.NO_INTERSECTION;
	}

	public final boolean isEqualsNetworkNotation(CNetworkNotation checkNN,
			Long agentIDToDelete, Long checkAgentID) {
		if (this.changedAgentsFromSolution.size() != checkNN
				.getChangedAgentsFromSolution().size())
			return false;
		Iterator<Long> agentIterator = this.changedAgentsFromSolution.keySet()
				.iterator();
		while (agentIterator.hasNext()) {
			Long agentID = agentIterator.next();

			AgentSites as = this.changedAgentsFromSolution.get(agentIDToDelete);
			AgentSites asToCheck = null;
			if (agentID.equals(agentIDToDelete)) {
				asToCheck = checkNN.getChangedAgentsFromSolution().get(
						checkAgentID);
			} else {
				asToCheck = checkNN.getChangedAgentsFromSolution().get(agentID);
			}

			if (asToCheck == null)
				return false;
			if (!as.isEqualsAgentSites(asToCheck))
				return false;

		}

		return true;
	}
}

package com.plectix.simulator.components.stories;

import java.util.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SolutionUtils;
import com.plectix.simulator.components.stories.CStoriesSiteStates.StateType;
import com.plectix.simulator.components.CAgentLink;
import com.plectix.simulator.interfaces.IConnectedComponent;

import com.plectix.simulator.interfaces.INetworkNotation;

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

	private int step;
	private int ruleID;
	private Simulator simulator;
	private Map<Long, AgentSites> changesOfAllUsedSites;
	private Map<Long, AgentSitesFromRules> usedAgentsFromRules;
	private Set<Long> addedAgentsID;
	private List<String> agentsNotation;
	private List<Map<Long, List<Integer>>> introCCMap;

	public Simulator getSimulator() {
		return simulator;
	}

	public Set<Long> getAddedAgentsID() {
		return addedAgentsID;
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

	public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
		return usedAgentsFromRules;
	}

	public List<String> getAgentsNotation() {
		return agentsNotation;
	}

	@Override
	public String toString() {
		String st = "ruleName="
				+ simulator.getSimulationData().getKappaSystem().getRuleByID(
						ruleID).getName() + " ";
		st += "usedAgentsFromRules=" + usedAgentsFromRules.keySet().toString()
				+ " ";
		st += "changesOfAllUsedSites="
				+ changesOfAllUsedSites.keySet().toString() + " ";
		st += "step=" + step + " ";
		st += "agentsNotation=" + agentsNotation.toString() + " ";

		return st;
	}

	private void initParameters(Simulator simulator, int step, Integer ruleID) {
		this.simulator = simulator;
		this.step = step;
		this.ruleID = ruleID;
		this.changesOfAllUsedSites = new HashMap<Long, AgentSites>();
		this.usedAgentsFromRules = new HashMap<Long, AgentSitesFromRules>();
		this.agentsNotation = new ArrayList<String>();
		this.introCCMap = new ArrayList<Map<Long, List<Integer>>>();
	}

	// TODO: Make sure that CNetworkNotation works with long event number, not integer
	public CNetworkNotation(Simulator simulator, int step, int ruleID) {
		initParameters(simulator, step, ruleID);
	}

	// TODO: Make sure that CNetworkNotation works with long event number, not integer
	public CNetworkNotation(Simulator simulator, int step, CRule rule,
			List<CInjection> injectionsList, SimulationData data) {
		initParameters(simulator, step, rule.getRuleID());
		createAgentsNotation(injectionsList, data, rule);
	}

	public void fillAddedAgentsID(SimulationData data) {
		CRule rule = data.getKappaSystem().getRuleByID(ruleID);
		this.addedAgentsID = rule.getAgentsAddedID();
	}

	private void fillIntroMap(CInjection inj, IConnectedComponent ccFromSolution) {
		IConnectedComponent ccFromRule = inj.getConnectedComponent();
		Map<Long, List<Integer>> currentMap = new HashMap<Long, List<Integer>>();

		for (CAgentLink agentLink : inj.getAgentLinkList()) {
			CAgent agent = agentLink.getAgentTo();
			long key = agent.getId();
			CAgent agentFromRule = ccFromRule.getAgents().get(
					agentLink.getIdAgentFrom());

			List<Integer> currentList = new ArrayList<Integer>();
			currentMap.put(key, currentList);

			for (CSite site : agentFromRule.getSites()) {
				currentList.add(site.getNameId());
			}
		}

		ISolution solution = simulator.getSimulationData().getKappaSystem()
				.getSolution();
		List<CAgent> newAgentsList = solution.cloneAgentsList(ccFromSolution.getAgents());

		CAgent mainAgent = null;

		for (int i = 0; i < ccFromSolution.getAgents().size(); i++) {
			CAgent oldAgent = ccFromSolution.getAgents().get(i);
			List<Integer> sites = currentMap.get(oldAgent.getId());
			CAgent newAgent = newAgentsList.get(i);
			if (sites != null) {
				mainAgent = newAgent;

				for (CSite site : newAgent.getSites()) {
					if (!sites.contains(site.getNameId())) {
						CSite connectionSite = site.getLinkState().getConnectedSite();
						if (connectionSite != null) {
							site.getLinkState().setFree();
							connectionSite.getLinkState().setFree();
						}
					}
				}

			}
		}

		IConnectedComponent cc = SolutionUtils.getConnectedComponent(mainAgent);
		agentsNotation.add(SimulationUtils.printPartRule(cc, new int[] { 0 },
				simulator.getSimulationData().isOcamlStyleObsName()));
		introCCMap.add(currentMap);
	}

	public List<Map<Long, List<Integer>>> getIntroCCMap() {
		return introCCMap;
	}

	public final CNetworkNotation cloneNetworkNotation() {
		CNetworkNotation newNN = new CNetworkNotation(this.simulator,
				this.step, this.ruleID);

		for (Map.Entry<Long, AgentSites> entry : changesOfAllUsedSites.entrySet()) {
			AgentSites as = entry.getValue();
			newNN.getChangesOfAllUsedSites().put(entry.getKey(), as.clone());
		}

		// clone introMap
		newNN.introCCMap = new ArrayList<Map<Long, List<Integer>>>();// this.
		for (Map<Long, List<Integer>> map : this.introCCMap) {
			Map<Long, List<Integer>> newMap = new HashMap<Long, List<Integer>>();

			for (Map.Entry<Long, List<Integer>> entry : map.entrySet()) {
				List<Integer> list = entry.getValue();
				List<Integer> newList = new ArrayList<Integer>();

				for (int number : list) {
					newList.add(number);
				}
				newMap.put(entry.getKey(), newList);
			}
			newNN.introCCMap.add(newMap);
		}
		
		// clone usedAgentsFromRules
		for (Map.Entry<Long, AgentSitesFromRules> entry : usedAgentsFromRules.entrySet()) {
			AgentSitesFromRules aSFR = entry.getValue();
			newNN.getUsedAgentsFromRules().put(entry.getKey(), aSFR.clone());
		}
		
		// clone agentsNotation
		newNN.agentsNotation = new ArrayList<String>();
		for(String str : this.agentsNotation){
			newNN.agentsNotation.add(str);
		}
		
		return newNN;
	}

	private final void createAgentsNotation(List<CInjection> injectionsList,
			SimulationData data, CRule rule) {
		for (CInjection inj : injectionsList) {
			if (inj != CInjection.EMPTY_INJECTION) {
				IConnectedComponent cc = SolutionUtils.getConnectedComponent(inj
						.getAgentLinkList().get(0).getAgentTo());
				fillIntroMap(inj, cc);
			}
		}

	}

	public final void checkLinkForNetworkNotation(StateType index, CSite site) {
		if (site.getLinkState().getConnectedSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, -1, -1), index);
		else
			this.addToAgents(site, 
					new CStoriesSiteStates(index,
							((CAgent) site.getLinkState().getConnectedSite().getAgentLink()).getHash(), 
							((CSite) site.getLinkState().getConnectedSite()).getNameId()),
							index);
	}

	public final boolean changedSitesContains(CSite site) {
		CAgent agent = site.getAgentLink();
		AgentSites as = changesOfAllUsedSites.get(agent.getId());
		if (as != null) {
			IStoriesSiteStates sss = as.getSites().get(site.getNameId());
			if (sss != null)
				return true;
		}
		return false;
	}

	public final void checkLinkToUsedSites(StateType index, CSite site) {
		if (site.getLinkState().getConnectedSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index, site
							.getInternalState().getNameId(),
							((CAgent) site.getLinkState().getConnectedSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getConnectedSite()).getNameId()),
							index);
	}

	public final void checkLinkForNetworkNotationDel(StateType index, CSite site) {
		if (site.getLinkState().getConnectedSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index, site
							.getInternalState().getNameId(),
							((CAgent) site.getLinkState().getConnectedSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getConnectedSite()).getNameId()),
							index);
	}

	public final void addToAgents(CSite site, IStoriesSiteStates siteStates,
			StateType index) {

		Map<Long, AgentSites> map;
		map = changesOfAllUsedSites;
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = map.get(key);
			if (as == null) {
				as = new AgentSites();
				map.put(key, as);
			}
			as.addToSites(site.getNameId(), siteStates, index);
		}
	}

	public final void addToAgentsFromRules(CSite site,
			NetworkNotationMode agentMode,
			NetworkNotationMode internalStateMode,
			NetworkNotationMode linkStateMode) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(site.getAgentLink().getNameId());
				usedAgentsFromRules.put(key, aSFR);
			}
			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode, site.getAgentLink().getNameId());
		}
	}

	public final void addFixedSitesFromRules(CSite site,
			NetworkNotationMode agentMode, boolean internalState,
			boolean linkState) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(site.getAgentLink().getNameId());
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

	public final CRule getRule() {
		return simulator.getSimulationData().getKappaSystem().getRuleByID(
				ruleID);
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

	public final IntersectionType isIntersects(CNetworkNotation nn,
			boolean isAllUsedSites) {
		Map<Long, AgentSites> mapThis;
		Map<Long, AgentSites> mapCheck;

		mapThis = this.changesOfAllUsedSites;
		mapCheck = nn.changesOfAllUsedSites;

		int counter = 0;
		int fullCounter = 0;

		for (Map.Entry<Long, AgentSites> entry : mapThis.entrySet()) {
			if (mapCheck.containsKey(entry.getKey())) {
				IntersectionType it = checkSites(entry, mapThis, mapCheck);
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

	private final IntersectionType checkSites(Map.Entry<Long, AgentSites> entryOfMapThis,
			Map<Long, AgentSites> mapThis, Map<Long, AgentSites> mapCheck) {
		Long key = entryOfMapThis.getKey();
		AgentSites value = entryOfMapThis.getValue();
		
		int counter = 0;
		int fullCounter = 0;
		
		for (Map.Entry<Integer, IStoriesSiteStates> entry : value.getSites().entrySet()) {
			if (mapCheck.get(key).getSites().containsKey(entry.getKey())) {
				counter++;

				if (CStoriesSiteStates.isEqual(entry.getValue().getAfterState(), 
						mapCheck.get(key).getSites().get(entry.getKey()).getBeforeState())) {
					fullCounter++;
				}
			}
		}

		if ((fullCounter == value.getSites().size())
				&& (value.getSites().size() == mapCheck.get(key).getSites().size()))
			return IntersectionType.FULL_INTERSECTION;

		if (counter > 0)
			return IntersectionType.PART_INTERSECTION;

		return IntersectionType.NO_INTERSECTION;
	}

	public final boolean isEqualsNetworkNotation(CNetworkNotation checkNN) {
		if (this.changesOfAllUsedSites.size() != checkNN
				.getChangesOfAllUsedSites().size())
			return false;
		for (Map.Entry<Long, AgentSites> entry : changesOfAllUsedSites.entrySet()) {
			AgentSites asToCheck = checkNN.getChangesOfAllUsedSites().get(
					entry.getKey());

			if (asToCheck == null)
				return false;

			if (!entry.getValue().isEqualsAgentSites(asToCheck))
				return false;
		}

		return true;
	}

	public final boolean isEqualsNetworkNotation(CNetworkNotation checkNN,
			Long agentIDToDelete, Long checkAgentID) {
		if (this.changesOfAllUsedSites.size() != checkNN
				.getChangesOfAllUsedSites().size())
			return false;
		Iterator<Long> agentIterator = this.changesOfAllUsedSites.keySet()
				.iterator();
		while (agentIterator.hasNext()) {
			Long agentID = agentIterator.next();

			AgentSites as = this.changesOfAllUsedSites.get(agentIDToDelete);
			AgentSites asToCheck = null;
			if (agentID.equals(agentIDToDelete)) {
				asToCheck = checkNN.getChangesOfAllUsedSites()
						.get(checkAgentID);
			} else {
				asToCheck = checkNN.getChangesOfAllUsedSites().get(agentID);
			}

			if (asToCheck == null)
				return false;
			if (!as.isEqualsAgentSites(asToCheck))
				return false;

		}

		return true;
	}
}

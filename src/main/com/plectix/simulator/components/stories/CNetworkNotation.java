package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.components.solution.SolutionUtils;
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

	private int step;
	private int ruleID;
	private Simulator simulator;
	private Map<Long, AgentSites> changesOfAllUsedSites;
	private Map<Long, AgentSitesFromRules> usedAgentsFromRules;
	private List<Long> addedAgentsID;
	private List<String> agentsNotation;
	private List<Map<Long, List<Integer>>> introCCMap;

	public Simulator getSimulator() {
		return simulator;
	}

	public List<Long> getAddedAgentsID() {
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
				+ simulator.getSimulationData().getKappaSystem().getRulesByID(
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

	public CNetworkNotation(Simulator simulator, int step, int ruleID) {
		initParameters(simulator, step, ruleID);
	}

	public CNetworkNotation(Simulator simulator, int step, IRule rule,
			List<IInjection> injectionsList, SimulationData data) {
		initParameters(simulator, step, rule.getRuleID());
		createAgentsNotation(injectionsList, data, rule);
	}

	public void fillAddedAgentsID(SimulationData data) {
		IRule rule = data.getKappaSystem().getRulesByID(ruleID);
		this.addedAgentsID = rule.getAgentsAddedID();
	}

	private void fillIntroMap(IInjection inj, IConnectedComponent ccFromSolution) {
		IConnectedComponent ccFromRule = inj.getConnectedComponent();
		Map<Long, List<Integer>> currentMap = new HashMap<Long, List<Integer>>();

		for (IAgentLink agentLink : inj.getAgentLinkList()) {
			IAgent agent = agentLink.getAgentTo();
			long key = agent.getId();
			IAgent agentFromRule = ccFromRule.getAgents().get(
					agentLink.getIdAgentFrom());

			List<Integer> currentList = new ArrayList<Integer>();
			currentMap.put(key, currentList);

			for (ISite site : agentFromRule.getSites()) {
				currentList.add(site.getNameId());
			}
		}

		ISolution solution = simulator.getSimulationData().getKappaSystem()
				.getSolution();
		List<IAgent> newAgentsList = SolutionUtils.cloneAgentsList(ccFromSolution
				.getAgents(), simulator.getSimulationData().getKappaSystem());

		IAgent mainAgent = null;

		for (int i = 0; i < ccFromSolution.getAgents().size(); i++) {
			IAgent oldAgent = ccFromSolution.getAgents().get(i);
			List<Integer> sites = currentMap.get(oldAgent.getId());
			IAgent newAgent = newAgentsList.get(i);
			if (sites != null) {
				mainAgent = newAgent;

				for (ISite site : newAgent.getSites()) {
					if (!sites.contains(site.getNameId())) {
						ISite connectionSite = site.getLinkState().getSite();
						if (connectionSite != null) {
							site.getLinkState().setFreeLinkState();
							connectionSite.getLinkState().setFreeLinkState();
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
		Iterator<Long> iterator = this.changesOfAllUsedSites.keySet()
				.iterator();

		while (iterator.hasNext()) {
			Long key = iterator.next();
			AgentSites as = this.changesOfAllUsedSites.get(key);
			newNN.getChangesOfAllUsedSites().put(key, as.clone());
		}

		// clone introMap
		newNN.introCCMap = new ArrayList<Map<Long, List<Integer>>>();// this.
		for (Map<Long, List<Integer>> map : this.introCCMap) {
			iterator = map.keySet().iterator();
			Map<Long, List<Integer>> newMap = new HashMap<Long, List<Integer>>();

			while (iterator.hasNext()) {
				Long key = iterator.next();
				List<Integer> list = map.get(key);
				List<Integer> newList = new ArrayList<Integer>();

				for (int number : list) {
					newList.add(number);
				}
				newMap.put(key, newList);
			}
			newNN.introCCMap.add(newMap);
		}
		// clone usedAgentsFromRules
		iterator = this.usedAgentsFromRules.keySet().iterator();

		while (iterator.hasNext()) {
			Long key = iterator.next();
			AgentSitesFromRules aSFR = this.usedAgentsFromRules.get(key);
			newNN.getUsedAgentsFromRules().put(key, aSFR.clone());
		}
		// clone agentsNotation
		newNN.agentsNotation =new ArrayList<String>();
		for(String str : this.agentsNotation){
			newNN.agentsNotation.add(str.substring(0));
		}
		
		return newNN;
	}

	private final void createAgentsNotation(List<IInjection> injectionsList,
			SimulationData data, IRule rule) {
		ISolution solution = data.getKappaSystem().getSolution();
		for (IInjection inj : injectionsList) {
			if (inj != CInjection.EMPTY_INJECTION) {
				IConnectedComponent cc = SolutionUtils.getConnectedComponent(inj
						.getAgentLinkList().get(0).getAgentTo());
				fillIntroMap(inj, cc);
			}
		}

	}

	public final void checkLinkForNetworkNotation(StateType index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this
					.addToAgents(site, new CStoriesSiteStates(index, -1, -1),
							index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index,
							((CAgent) site.getLinkState().getSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getSite()).getNameId()),
							index);
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
					.getInternalState().getNameId(), -1, -1), index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index, site
							.getInternalState().getNameId(),
							((CAgent) site.getLinkState().getSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getSite()).getNameId()),
							index);
	}

	public final void checkLinkForNetworkNotationDel(StateType index, ISite site) {
		if (site.getLinkState().getSite() == null)
			this.addToAgents(site, new CStoriesSiteStates(index, site
					.getInternalState().getNameId(), -1, -1), index);
		else
			this
					.addToAgents(site, new CStoriesSiteStates(index, site
							.getInternalState().getNameId(),
							((CAgent) site.getLinkState().getSite()
									.getAgentLink()).getHash(), ((CSite) site
									.getLinkState().getSite()).getNameId()),
							index);
	}

	public final void addToAgents(ISite site, IStoriesSiteStates siteStates,
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

	public final void addToAgentsFromRules(ISite site,
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

	public final void addFixedSitesFromRules(ISite site,
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

	public final IRule getRule() {
		return simulator.getSimulationData().getKappaSystem().getRulesByID(
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

	public final boolean isEqualsNetworkNotation(CNetworkNotation checkNN) {
		if (this.changesOfAllUsedSites.size() != checkNN
				.getChangesOfAllUsedSites().size())
			return false;
		Iterator<Long> agentIterator = this.changesOfAllUsedSites.keySet()
				.iterator();
		while (agentIterator.hasNext()) {
			Long agentID = agentIterator.next();

			AgentSites as = this.changesOfAllUsedSites.get(agentID);

			AgentSites asToCheck = checkNN.getChangesOfAllUsedSites().get(
					agentID);

			if (asToCheck == null)
				return false;

			if (!as.isEqualsAgentSites(asToCheck))
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

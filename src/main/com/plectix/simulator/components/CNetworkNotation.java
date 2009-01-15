package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CStoriesSiteStates.StateType;
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

public class CNetworkNotation implements INetworkNotation {
	public enum NetworkNotationMode {
		TEST, TEST_OR_MODIFY, MODIFY, NONE;
	}

	public enum IntersectionType {
		NO_INTERSECTION, PART_INTERSECTION, FULL_INTERSECTION;
	}

	private boolean leaf;

	private boolean hasIntro;

	public boolean isHasIntro() {
		return hasIntro;
	}

	public void setHasIntro(boolean hasIntro) {
		this.hasIntro = hasIntro;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	private int step;

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	private final IRule rule;

	private Map<Long, AgentSites> changedAgentsFromSolution;

	public Map<Long, AgentSites> getChangedAgentsFromSolution() {
		return changedAgentsFromSolution;
	}

	private Map<Long, AgentSitesFromRules> usedAgentsFromRules;

	public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
		return usedAgentsFromRules;
	}

	List<String> agentsNotation;
	List<List<Long>> introCC;

	public List<String> getAgentsNotation() {
		return agentsNotation;
	}

	@Override
	public String toString() {
		String st = "hasIntro=" + Boolean.toString(hasIntro) + " ";
		st += "usedAgentsFromRules=" + usedAgentsFromRules.keySet().toString()
				+ " ";
		st += "changedAgentsFromSolution="
				+ changedAgentsFromSolution.keySet().toString() + " ";
		st += "ruleName=" + rule.getName() + " ";
		st += "agentsNotation=" + agentsNotation.toString() + " ";

		// return super.toString();
		return st;
	}

	// TODO separate!
	/* package */final class AgentSitesFromRules {
		// TODO private!!!
		HashMap<Integer, SitesFromRules> sites;
		private NetworkNotationMode mode;

		private IAgent agent;

		public IAgent getAgent() {
			return agent;
		}

		public AgentSitesFromRules(NetworkNotationMode mode, IAgent agent) {
			this.mode = mode;
			sites = new HashMap<Integer, SitesFromRules>();
			this.agent = agent;
		}

		// TODO separate!!!!!!!!!!!!!!!!!!!!
		/* package */final class SitesFromRules {
			// TODO private!!!
			private NetworkNotationMode internalStateMode = NetworkNotationMode.NONE;

			public NetworkNotationMode getInternalStateMode() {
				return internalStateMode;
			}

			private NetworkNotationMode linkStateMode = NetworkNotationMode.NONE;

			public NetworkNotationMode getLinkStateMode() {
				return linkStateMode;
			}

			private int linkAgentNameID;

			public SitesFromRules(NetworkNotationMode internalStateMode,
					NetworkNotationMode linkStateMode, int linkAgentNameID) {
				this.internalStateMode = internalStateMode;
				this.linkStateMode = linkStateMode;
				this.linkAgentNameID = linkAgentNameID;
			}

			public SitesFromRules() {
			}

			public final void setInternalStateMode(
					NetworkNotationMode internalStateMode, int linkAgentNameID) {
				this.internalStateMode = internalStateMode;
				this.linkAgentNameID = linkAgentNameID;
			}

			public final void setLinkStateMode(
					NetworkNotationMode linkStateMode, int linkAgentNameID) {
				this.linkStateMode = linkStateMode;
				// this.linkAgentNameID = linkAgentNameID;

			}

			public final boolean isCausing(SitesFromRules sfr, boolean isLink) {
				if (isLink) {
					if (isCausing(this.linkStateMode, sfr.linkStateMode))
						return true;
				} else if (isCausing(this.internalStateMode,
						sfr.internalStateMode))
					return true;

				return false;
			}

			public final boolean isCausing(NetworkNotationMode mode,
					NetworkNotationMode sfrMode) {
				if (mode == NetworkNotationMode.TEST_OR_MODIFY
						&& sfrMode == NetworkNotationMode.TEST_OR_MODIFY)
					return true;
				if (mode == NetworkNotationMode.TEST_OR_MODIFY
						&& sfrMode == NetworkNotationMode.TEST)
					return true;
				if (mode == NetworkNotationMode.MODIFY
						&& sfrMode == NetworkNotationMode.TEST)
					return true;

				return false;
			}
		}

		public final void addToSitesFromRules(int idSite,
				NetworkNotationMode internalStateMode,
				NetworkNotationMode linkStateMode, int linkAgentNameID) {
			SitesFromRules sFR = sites.get(idSite);
			if (sFR == null) {
				sFR = new SitesFromRules();
				sites.put(idSite, sFR);
			}
			if (internalStateMode != NetworkNotationMode.NONE)
				sFR.setInternalStateMode(internalStateMode, linkAgentNameID);
			if (linkStateMode != NetworkNotationMode.NONE)
				sFR.setLinkStateMode(linkStateMode, linkAgentNameID);
		}
	}

	public CNetworkNotation(int step, IRule rule,
			List<IInjection> injectionsList, SimulationData data) {
		this.step = step;
		this.rule = rule;
		leaf = false;
		hasIntro = false;
		this.changedAgentsFromSolution = new HashMap<Long, AgentSites>();
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
				boolean isStorify = false;
				int counter = 0;
				for (IAgentLink al : inj.getAgentLinkList()) {
					if (al.getAgentTo().isStorify()) {
						counter++;
					}
				}
				
				if (counter == inj.getAgentLinkList().size())
					isStorify = true;

				agentsNotation.add(SimulationUtils.printPartRule(cc,
						new int[] { 0 }, data.isOcamlStyleObsName()));
				
				List<Long> agentIDsList= new ArrayList<Long>();
				
				for (IAgent agent : cc.getAgents()) {
					agentIDsList.add(agent.getId());
				}
				introCC.add(agentIDsList);
				
				
				if (!isStorify) {
					hasIntro = true;
				}
			}
		}

	}

	private void clearAgentsForDeletedOppositeRules(CNetworkNotation nn) {
		if (nn.isHasIntro()) {
			for (IAgent agent : nn.getRule().getStoryfiedAgents())
				((CAgent) agent).unStorify();
			nn.getRule().clearStorifiedAgents();
		}
	}

	// public Map<Long, AgentSites> getChangedAgentsFromSolution() {
	// return Collections.unmodifiableMap(changedAgentsFromSolution);
	// }
	//
	// public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
	// return Collections.unmodifiableMap(usedAgentsFromRules);
	// }

	public List<List<Long>> getIntroCC() {
		return introCC;
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
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = changedAgentsFromSolution.get(key);
			if (as == null) {
				as = new AgentSites(site.getAgentLink());
				changedAgentsFromSolution.put(key, as);
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
		return rule;
	}

	public final boolean isNotOpposite(
			List<CNetworkNotation> networkNotationList) {
		for (int i = networkNotationList.size() - 1; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			switch (isIntersects(nn)) {
			case FULL_INTERSECTION:
				clearAgentsForDeletedOppositeRules(this);
				clearAgentsForDeletedOppositeRules(networkNotationList.get(i));
				networkNotationList.remove(i);
				return false;
			case PART_INTERSECTION:
				return true;
			}
		}
		return true;
	}

	public final boolean isOpposite(CNetworkNotation networkNotation) {
		switch (isIntersects(networkNotation)) {
		case FULL_INTERSECTION:
			clearAgentsForDeletedOppositeRules(this);
			clearAgentsForDeletedOppositeRules(networkNotation);
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
				if (checkSites(key, nn) == IntersectionType.FULL_INTERSECTION)
					fullCounter++;
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
						.get(key).getSites().get(keySite).getCurrentState(),
						nn.changedAgentsFromSolution.get(key).getSites().get(
								keySite).getLastState())) {
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
}

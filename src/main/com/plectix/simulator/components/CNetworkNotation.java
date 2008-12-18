package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	public static final byte MODE_TEST = 0;
	public static final byte MODE_TEST_OR_MODIFY = 1;
	public static final byte MODE_MODIFY = 2;
	public static final byte MODE_NONE = -1;

	public final static byte HAS_FULL_INTERSECTION = 2;
	public final static byte HAS_PART_INTERSECTION = 1;
	public final static byte HAS_NO_INTERSECTION = 0;

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

	// TODO is this field necessary?
	private int step;

	public void setStep(int step) {
		this.step = step;
	}

	public int getStep() {
		return step;
	}

	private final IRule rule;

	private Map<Long, AgentSites> changedAgentsFromSolution;
	private Map<Long, AgentSitesFromRules> usedAgentsFromRules;

	public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
		return usedAgentsFromRules;
	}

	List<String> agentsNotation;
	List<IAgent> introAgents;
	List<IConnectedComponent> introCC;

	public List<String> getAgentsNotation() {
		return agentsNotation;
	}

	// TODO separate!
	/* package */final class AgentSitesFromRules {
		// TODO private!!!
		HashMap<Integer, SitesFromRules> sites;
		private byte mode;

		public AgentSitesFromRules(byte mode) {
			this.mode = mode;
			sites = new HashMap<Integer, SitesFromRules>();
		}

		// TODO separate!!!!!!!!!!!!!!!!!!!!
		/* package */final class SitesFromRules {
			// TODO private!!!
			private byte internalStateMode = MODE_NONE;

			public byte getInternalStateMode() {
				return internalStateMode;
			}

			private byte linkStateMode = MODE_NONE;

			public byte getLinkStateMode() {
				return linkStateMode;
			}

			private int linkAgentNameID;

			public SitesFromRules(byte internalStateMode, byte linkStateMode,
					int linkAgentNameID) {
				this.internalStateMode = internalStateMode;
				this.linkStateMode = linkStateMode;
				this.linkAgentNameID = linkAgentNameID;
			}

			public SitesFromRules() {
			}

			public final void setInternalStateMode(byte internalStateMode,
					int linkAgentNameID) {
				this.internalStateMode = internalStateMode;
				this.linkAgentNameID = linkAgentNameID;
			}

			public final void setLinkStateMode(byte linkStateMode,
					int linkAgentNameID) {
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

			public final boolean isCausing(byte mode, byte sfrMode) {
				if (mode == MODE_TEST_OR_MODIFY
						&& sfrMode == MODE_TEST_OR_MODIFY)
					return true;
				if (mode == MODE_TEST_OR_MODIFY && sfrMode == MODE_TEST)
					return true;
				if (mode == MODE_MODIFY && sfrMode == MODE_TEST)
					return true;

				return false;
			}
		}

		public final void addToSitesFromRules(int idSite,
				byte internalStateMode, byte linkStateMode, int linkAgentNameID) {
			SitesFromRules sFR = sites.get(idSite);
			if (sFR == null) {
				sFR = new SitesFromRules();
				sites.put(idSite, sFR);
			}
			if (internalStateMode != MODE_NONE)
				sFR.setInternalStateMode(internalStateMode, linkAgentNameID);
			if (linkStateMode != MODE_NONE)
				sFR.setLinkStateMode(linkStateMode, linkAgentNameID);
		}

		public final void addFixedSitesFromRules(int idSite,
				byte internalStateMode, byte linkStateMode, int linkAgentNameID) {
			SitesFromRules sFR = sites.get(idSite);
			if (sFR == null)

				// TODO sFR can be only null =(
				if (internalStateMode != MODE_NONE)
					sFR
							.setInternalStateMode(internalStateMode,
									linkAgentNameID);
			if (linkStateMode != MODE_NONE)
				sFR.setLinkStateMode(linkStateMode, linkAgentNameID);
			sites.put(idSite, sFR);
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
		this.introCC = new ArrayList<IConnectedComponent>();
		this.introAgents = new ArrayList<IAgent>();
		createAgentsNotation(injectionsList, data);
	}

	private final void createAgentsNotation(List<IInjection> injectionsList,
			SimulationData data) {

		ISolution solution = data.getSolution();

		for (IInjection inj : injectionsList) {
			if (inj != CInjection.EMPTY_INJECTION) {
				IConnectedComponent cc = solution.getConnectedComponent(inj
						.getAgentLinkList().get(0).getAgentTo());
				boolean isStorify = false;
				for (IAgentLink al : inj.getAgentLinkList()) {
					if (al.getAgentTo().isStorify())
						isStorify = true;
					// break;
					// }else{
					// }
					this.introAgents.add(al.getAgentTo());
				}
				agentsNotation.add(SimulationUtils.printPartRule(cc,
						new int[] { 0 }, data.isOcamlStyleObsName()));
				introCC.add(cc);
				if (!isStorify) {
					hasIntro = true;
				}
			}
		}

	}

	// public Map<Long, AgentSites> getChangedAgentsFromSolution() {
	// return Collections.unmodifiableMap(changedAgentsFromSolution);
	// }
	//
	// public Map<Long, AgentSitesFromRules> getUsedAgentsFromRules() {
	// return Collections.unmodifiableMap(usedAgentsFromRules);
	// }

	public List<IConnectedComponent> getIntroCC() {
		return introCC;
	}

	public final void checkLinkForNetworkNotation(int index, ISite site) {
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

	public final void checkLinkForNetworkNotationDel(int index, ISite site) {
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
			int index) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSites as = changedAgentsFromSolution.get(key);
			if (as == null) {
				as = new AgentSites();
				changedAgentsFromSolution.put(key, as);
			}
			as.addToSites(site.getNameId(), siteStates, index);
		}
	}

	public final void addToAgentsFromRules(ISite site, byte agentMode,
			byte internalStateMode, byte linkStateMode) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode);
				usedAgentsFromRules.put(key, aSFR);
			}
			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode, site.getAgentLink().getNameId());
		}
	}

	public final void addFixedSitesFromRules(ISite site, byte agentMode,
			boolean internalState, boolean linkState) {
		if (site != null) {
			long key = site.getAgentLink().getHash();
			AgentSitesFromRules aSFR = usedAgentsFromRules.get(key);
			if (aSFR == null) {
				aSFR = new AgentSitesFromRules(agentMode);
				usedAgentsFromRules.put(key, aSFR);
			}
			byte internalStateMode = MODE_NONE;
			byte linkStateMode = MODE_NONE;

			if (internalState == true)
				internalStateMode = MODE_TEST;
			if (linkState == true)
				linkStateMode = MODE_TEST;

			aSFR.addToSitesFromRules(site.getNameId(), internalStateMode,
					linkStateMode, site.getAgentLink().getNameId());
		}
	}

	public final IRule getRule() {
		return rule;
	}

	public final boolean isOpposite(List<CNetworkNotation> networkNotationList) {
		for (int i = networkNotationList.size() - 1; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			switch (isIntersects(nn)) {
			case HAS_FULL_INTERSECTION:
				networkNotationList.remove(i);
				return false;
			case HAS_PART_INTERSECTION:
				return true;
			}
		}
		return true;
	}

	public final byte isIntersects(CNetworkNotation nn) {
		Iterator<Long> iterator = this.changedAgentsFromSolution.keySet()
				.iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Long key = iterator.next();

			if (nn.changedAgentsFromSolution.containsKey(key)) {
				if (checkSites(key, nn) == HAS_FULL_INTERSECTION)
					fullCounter++;
				counter++;
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.size())
				&& (this.changedAgentsFromSolution.size() == nn.changedAgentsFromSolution
						.size()))
			return HAS_FULL_INTERSECTION;

		if (counter > 0)
			return HAS_PART_INTERSECTION;

		return HAS_NO_INTERSECTION;
	}

	private final byte checkSites(long key, CNetworkNotation nn) {
		Iterator<Integer> iterator = this.changedAgentsFromSolution.get(key).sites
				.keySet().iterator();
		int counter = 0;
		int fullCounter = 0;

		while (iterator.hasNext()) {
			Integer keySite = iterator.next();
			if (nn.changedAgentsFromSolution.get(key).sites
					.containsKey(keySite)) {
				counter++;

				if (CStoriesSiteStates.isEqual(this.changedAgentsFromSolution
						.get(key).sites.get(keySite).getCurrentState(),
						nn.changedAgentsFromSolution.get(key).sites
								.get(keySite).getLastState())) {
					fullCounter++;
				}
			}
		}

		if ((fullCounter == this.changedAgentsFromSolution.get(key).sites
				.size())
				&& (this.changedAgentsFromSolution.get(key).sites.size() == nn.changedAgentsFromSolution
						.get(key).sites.size()))
			return HAS_FULL_INTERSECTION;

		if (counter > 0)
			return HAS_PART_INTERSECTION;

		return HAS_NO_INTERSECTION;
	}
}

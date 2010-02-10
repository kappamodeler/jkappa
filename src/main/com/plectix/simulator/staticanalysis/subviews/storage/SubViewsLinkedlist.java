package com.plectix.simulator.staticanalysis.subviews.storage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.StaticAnalysisException;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractLinkState;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.subviews.SubViewClass;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractAction;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractActionType;
import com.plectix.simulator.util.NameDictionary;

public class SubViewsLinkedlist implements SubViewsInterface {
	private final SubViewClass subViewClass;
	private final List<AbstractAgent> storage;

	public SubViewsLinkedlist(SubViewClass subViewClass) {
		this.subViewClass = subViewClass;
		this.storage = new LinkedList<AbstractAgent>();

	}

	public void fillingInitialState(
			Map<String, AbstractAgent> agentNameToAgent,
			Collection<Agent> agents) throws StaticAnalysisException {
		AbstractAgent defAgent = agentNameToAgent.get(subViewClass
				.getAgentType());
		AbstractAgent needAgent = new AbstractAgent(defAgent);
		for (AbstractSite defSite : defAgent.getSitesMap().values()) {
			String siteName = defSite.getName();
			if (!subViewClass.hasSite(siteName))
				needAgent.getSitesMap().remove(siteName);
		}
		// storage.add(needAgent);
		for (AbstractAgent agent : convertAgentsToAbstract(
				getNeedAgentsBySolution(agents), needAgent)) {
			addAbstractAgent(agent);
		}
	}

	public boolean addAbstractAgent(AbstractAgent agent) throws StaticAnalysisException
			{
		if (agent.getSitesMap().size() < subViewClass.getSitesNames().size())
			throw new StaticAnalysisException(subViewClass, agent);

		if (test(agent))
			return false;
		storage.add(agent);
		return true;
	}

	public boolean burnRule(AbstractAction action) throws StaticAnalysisException{
		AbstractAgent oldViews = action.getLeftHandSideAgent();
		AbstractAgent newViews = action.getRightHandSideAgent();

		// if (oldViews == null)
		// return addAbstractAgent(newViews);

		List<AbstractAgent> agentsList;
		if (testDeleteAction(action))
			agentsList = storage;
		else
			agentsList = getAllSubViews(oldViews);
		initBreakingSites(action, agentsList);
		switch (action.getActionType()) {
		case ADD:
			return addAbstractAgent(newViews.clone());
		case TEST_ONLY:
			return false;
		case DELETE:
			return false;
		}

		boolean isAdd = false;
		for (AbstractAgent storageAgent : agentsList) {
			AbstractAgent newAgent = storageAgent.clone();
			newAgent.addAllStates(newViews);
			if (addAbstractAgent(newAgent))
				isAdd = true;
		}
		return isAdd;

	}

	private boolean testDeleteAction(AbstractAction action) {
		if (action.getActionType() != AbstractActionType.DELETE)
			return false;
		boolean isNotHave = true;
		for (AbstractSite site : action.getLeftHandSideAgent().getSitesMap()
				.values())
			if (subViewClass.hasSite(site.getName()))
				return false;

		return isNotHave;
	}

	private void initBreakingSites(AbstractAction action,
			List<AbstractAgent> agentsList) {
		List<String> sideEffectId = action.getSideEffect();
		Map<Integer, AbstractAgent> sideEfectMap = new LinkedHashMap<Integer, AbstractAgent>();
		switch (action.getActionType()) {
		case DELETE: {
			for (AbstractAgent agent : agentsList)
				for (AbstractSite site : agent.getSitesMap().values())
					initSideEffectSite(site, agent, sideEfectMap, action);
			break;
		}
		case TEST_AND_MODIFICATION: {
			if (sideEffectId == null)
				return;
			for (AbstractAgent agent : agentsList)
				for (String siteName : sideEffectId) {
					AbstractSite site = agent.getSiteByName(siteName);
					initSideEffectSite(site, agent, sideEfectMap, action);
				}
			break;
		}
		}
	}

	private void initSideEffectSite(AbstractSite site, AbstractAgent agent,
			Map<Integer, AbstractAgent> sideEfectMap, AbstractAction action) {
		AbstractLinkState linkState = site.getLinkState();
		if (!NameDictionary.isDefaultAgentName(linkState.getAgentName())) {
			String newAgentName = linkState.getAgentName();
			String newSiteName = linkState.getConnectedSiteName();
			int hashValue = generateHash(newAgentName, newSiteName, agent
					.getName(), site.getName());
			if (sideEfectMap.get(hashValue) == null) {
				AbstractAgent newAgent = createSideEffectAgent(newAgentName,
						newSiteName, agent.getName(), site.getName());
				sideEfectMap.put(hashValue, newAgent);
				action.addSiteSideEffect(newAgent.getSiteByName(newSiteName));
			}
		}
	}

	private static AbstractAgent createSideEffectAgent(String agentName,
			String siteName, String linkAgentName, String linkSiteName) {
		AbstractAgent newAgent = new AbstractAgent(agentName);
		AbstractSite newSite = new AbstractSite(newAgent, siteName);
		newSite.getLinkState().setAgentName(linkAgentName);
		newSite.getLinkState().setLinkSiteName(linkSiteName);
		newSite.getLinkState().setStatusLink(LinkStatus.BOUND);
		newAgent.addSite(newSite);
		return newAgent;
	}

	private static int generateHash(String agentName, String siteName,
			String linkAgentName, String linkSiteName) {
		int hashValue = 11;
		hashValue = 31 * hashValue + agentName.hashCode();
		hashValue = 31 * hashValue + siteName.hashCode();
		hashValue = 31 * hashValue + linkAgentName.hashCode();
		hashValue = 31 * hashValue + linkSiteName.hashCode();
		return hashValue;
	}

	public boolean test(AbstractAction action) throws StaticAnalysisException {
		AbstractAgent agent = action.getLeftHandSideAgent();
		if (agent == null)
			return true;
		return test(agent);
	}

	public boolean test(AbstractAgent testView) throws StaticAnalysisException  {
		if (!testView.getName().equals(subViewClass.getAgentType()))
			throw new StaticAnalysisException(subViewClass, testView);
		boolean isHave = false;
		if (testView.getSitesMap().isEmpty())
			if (!storage.isEmpty()) {
				return true;
			} else
				return false;

		for (AbstractSite site : testView.getSitesMap().values())
			if (subViewClass.hasSite(site.getName()))
				isHave = true;
		if (!isHave)
			throw new StaticAnalysisException(subViewClass, testView);

		for (AbstractAgent aAgent : storage) {
			isHave = true;
			for (AbstractSite site : testView.getSitesMap().values()) {
				String siteName = site.getName();
				// if (!aAgent.getSite(siteId).isFit(site)) {
				if ((subViewClass.hasSite(siteName))
						&& (!site.isFit(aAgent.getSiteByName(siteName)))) {
					isHave = false;
					break;
				}
			}
			if (isHave)
				return true;
		}
		return false;
	}

	public List<AbstractAgent> getAllSubViews() {
		return storage;
	}

	public List<AbstractAgent> getAllSubViews(AbstractAgent view) {
		List<AbstractAgent> outList = new LinkedList<AbstractAgent>();
		if (view == null || !view.getName().equals(subViewClass.getAgentType()))
			return outList;
		if (view.getSitesMap().isEmpty())
			return storage;
		for (AbstractAgent aAgent : storage) {
			boolean isHave = false;
			for (AbstractSite site : view.getSitesMap().values()) {
				// if (!aAgent.getSite(site.getName()).isFit(site)) {
				String siteName = site.getName();
				if (subViewClass.hasSite(siteName)) {
					if (site.isFit(aAgent.getSiteByName(siteName)))
						isHave = true;
					else {
						isHave = false;
						break;
					}
				}
			}
			if (isHave)
				outList.add(aAgent);
		}
		return outList;
	}

	// ==========================================================================
	private List<AbstractAgent> convertAgentsToAbstract(List<Agent> agents,
			AbstractAgent fillingAgent) {
		List<AbstractAgent> outlist = new LinkedList<AbstractAgent>();
		for (Agent agent : agents) {

			AbstractAgent needAgent = fillingAgent.clone();
			needAgent.addAllStates(agent);
			// for (CAbstractSite aSite : needAgent.getSitesMap().values())
			// aSite.addStates(agent.getSiteById(aSite.getName()));
			outlist.add(needAgent);
		}
		return outlist;
	}

	private List<Agent> getNeedAgentsBySolution(Collection<Agent> agents) {
		List<Agent> outList = new LinkedList<Agent>();
		for (Agent a : agents)
			if (a.getName().equals(subViewClass.getAgentType()))
				outList.add(a);
		return outList;
	}

	public boolean isAgentFit(AbstractAgent agent) {
		for (AbstractSite site : agent.getSitesMap().values())
			if (!subViewClass.hasSite(site.getName()))
				return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SubViewClass))
			return false;

		SubViewClass inClass = (SubViewClass) obj;
		if (!subViewClass.equals(inClass))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return subViewClass.hashCode();
	}

	@Override
	public String toString() {
		return subViewClass.toString();
	}

	public boolean burnBreakAllNeedLinkState(AbstractAction action) throws StaticAnalysisException {
		List<AbstractSite> breakingSites = action.getSitesSideEffect();
		List<AbstractAgent> addlist = new LinkedList<AbstractAgent>();
		if (breakingSites == null)
			return false;
		for (AbstractSite site : breakingSites) {
			if (!site.getParentAgent().getName().equals(subViewClass
					.getAgentType()))
				continue;
			for (AbstractAgent storageAgent : storage) {
				AbstractSite siteFromStorage = storageAgent.getSiteByName(site
						.getName());
				// if (site.equalz(siteFromStorage)) {
				if (siteFromStorage != null && site.isFit(siteFromStorage)) {
					AbstractAgent newAgent = new AbstractAgent(storageAgent);
					newAgent.getSiteByName(site.getName()).getLinkState()
							.setFreeLinkState();
					addlist.add(newAgent);
				}
			}
		}
		boolean isAdd = false;
		for (AbstractAgent agent : addlist) {
			if (addAbstractAgent(agent))
				isAdd = true;
		}

		return isAdd;
	}

	public SubViewClass getSubViewClass() {
		return subViewClass;
	}

	public List<AbstractAgent> getAllSubViewsCoherent(AbstractAgent view) {
		List<AbstractAgent> outList = new LinkedList<AbstractAgent>();
		if (view == null || view.getSitesMap().isEmpty())
			return storage;

		for (AbstractAgent aAgent : storage) {
			boolean isHave = true;
			for (AbstractSite site : view.getSitesMap().values()) {
				String siteName = site.getName();
				if (subViewClass.hasSite(siteName)
						&& !site.isFit(aAgent.getSiteByName(siteName))) {
					isHave = false;
				}
			}
			if (isHave)
				outList.add(aAgent.summon(view));
		}
		return outList;
	}

	@Override
	public boolean isEmpty() {
		return storage.isEmpty();
	}
}

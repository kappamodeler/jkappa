package com.plectix.simulator.components.contactMap;

import java.util.*;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.IConnectedComponent;

public class CContactMapAbstractRule {

	private CContactMapAbstractSolution solution;
	private Map<Integer, IContactMapAbstractAgent> agentMapLeftHandSide;
	private Map<Integer, IContactMapAbstractAgent> agentMapRightHandSide;
	private List<IContactMapAbstractSite> lhsSites;
	private List<IContactMapAbstractSite> rhsSites;

	private IRule rule;
	private CContactMapAbstractAction abstractAction;

	public CContactMapAbstractRule(CContactMapAbstractSolution solution,
			IRule rule) {
		this.solution = solution;
		this.agentMapLeftHandSide = new HashMap<Integer, IContactMapAbstractAgent>();
		this.agentMapRightHandSide = new HashMap<Integer, IContactMapAbstractAgent>();
		this.rule = rule;
	}

	public void initAbstractRule() {
		if (!rule.isLHSisEmpty())
			abstractCCList(rule.getLeftHandSide(), agentMapLeftHandSide);
		if (!rule.isRHSisEmpty())
			abstractCCList(rule.getRightHandSide(), agentMapRightHandSide);
		this.lhsSites = initListsSites(agentMapLeftHandSide);
		this.rhsSites = initListsSites(agentMapRightHandSide);
		this.abstractAction = new CContactMapAbstractAction(this);
	}

	private List<IContactMapAbstractSite> initListsSites(
			Map<Integer, IContactMapAbstractAgent> map) {
		List<IContactMapAbstractSite> list = new ArrayList<IContactMapAbstractSite>();
		Iterator<Integer> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			IContactMapAbstractAgent agent = map.get(key);
			List<IContactMapAbstractSite> listSites = agent.getSites();
			if (listSites.isEmpty())
				list.add(agent.getEmptySite());
			list.addAll(listSites);
		}
		return list;
	}

	private void abstractCCList(List<IConnectedComponent> ccList,
			Map<Integer, IContactMapAbstractAgent> map) {
		for (IConnectedComponent cc : ccList) {
			for (IAgent agent : cc.getAgents()) {
				Integer key = agent.getNameId();
				IContactMapAbstractAgent cMAA = map.get(key);
				if (cMAA == null) {
					cMAA = new CContactMapAbstractAgent(agent);
					map.put(key, cMAA);
				}
				cMAA.addSites(agent);
			}
		}
	}

	public Map<Integer, IContactMapAbstractAgent> getAgentMapLeftHandSide() {
		return agentMapLeftHandSide;
	}

	public Map<Integer, IContactMapAbstractAgent> getAgentMapRightHandSide() {
		return agentMapRightHandSide;
	}

	public List<IContactMapAbstractSite> getLhsSites() {
		return lhsSites;
	}

	public List<IContactMapAbstractSite> getRhsSites() {
		return rhsSites;
	}

	public List<IContactMapAbstractSite> getNewData() {
		List<IContactMapAbstractSite> newData = new ArrayList<IContactMapAbstractSite>();
		int[] indexList = new int[lhsSites.size()];
		if (lhsSites.size() == 0) {
			newData.addAll(abstractAction.apply(
					new ArrayList<UCorrelationAbstractSite>(), solution));
			return newData;
		}

		List<List<IContactMapAbstractSite>> sitesLists = initSitesListsFromSolution();
		if (sitesLists == null)
			return null;
		sitesLists = clearSitesLists(sitesLists);
		int[] maxIndex = getMaxIndex(sitesLists);
		// TODO getNewData

		while (!isEnd(indexList, maxIndex)) {
			List<UCorrelationAbstractSite> injList = createInjectionList(
					indexList, sitesLists);
			newData.addAll(abstractAction.apply(injList, solution));

			upIndexList(indexList, maxIndex);
		}

		return newData;
	}

	private List<UCorrelationAbstractSite> createInjectionList(int[] indexList,
			List<List<IContactMapAbstractSite>> sitesLists) {
		List<IContactMapAbstractSite> listSites = new ArrayList<IContactMapAbstractSite>();
		int index = 0;
		for (int i : indexList)
			listSites.add(sitesLists.get(index++).get(i));
		List<UCorrelationAbstractSite> list = UCorrelationAbstractSite
				.createCorrelationSites(abstractAction,lhsSites, listSites,
						ECorrelationType.CORRELATION_LHS_AND_SOLUTION);
		return list;
	}

	private List<List<IContactMapAbstractSite>> clearSitesLists(
			List<List<IContactMapAbstractSite>> sitesLists) {
		int i = 0;
		List<List<IContactMapAbstractSite>> listOut = new ArrayList<List<IContactMapAbstractSite>>();
		for (List<IContactMapAbstractSite> list : sitesLists) {
			List<IContactMapAbstractSite> addList = new ArrayList<IContactMapAbstractSite>();
			IContactMapAbstractSite site = lhsSites.get(i);
			for (IContactMapAbstractSite s : list) {
				if (site.isFit(s))
					addList.add(s);
			}
			listOut.add(addList);
			i++;
		}
		return listOut;
	}

	private List<List<IContactMapAbstractSite>> initSitesListsFromSolution() {
		List<List<IContactMapAbstractSite>> sitesLists = new ArrayList<List<IContactMapAbstractSite>>();
		for (IContactMapAbstractSite s : lhsSites) {
			Integer keyAgent = s.getAgentLink().getNameId();
			Integer keySite = s.getNameId();
			IContactMapAbstractAgent agent = solution.getAbstractAgentMap()
					.get(keyAgent);
			if (agent == null)
				return null;
			List<IContactMapAbstractSite> sites;
			if (keySite == CSite.NO_INDEX)
				sites = agent.getSites();
			else
				sites = agent.getSitesMap().get(keySite);
			if (sites == null || sites.isEmpty())
				return null;
			sitesLists.add(sites);
		}
		return sitesLists;
	}

	private void upIndexList(int[] indexList, int[] maxIndex) {
		indexList[indexList.length - 1] = indexList[indexList.length - 1] + 1;
		for (int i = indexList.length - 1; i >= 1; i--) {
			if (indexList[i] > maxIndex[i]) {
				indexList[i] = 0;
				indexList[i - 1] = indexList[i - 1] + 1;
			}
		}
	}

	private int[] getMaxIndex(List<List<IContactMapAbstractSite>> sitesLists) {
		int[] mas = new int[sitesLists.size()];
		int index = 0;
		for (List<IContactMapAbstractSite> l : sitesLists)
			mas[index++] = l.size() - 1;
		return mas;
	}

	private boolean isEnd(int[] indexList, int[] maxIndex) {
		boolean end = true;
		for (int i = 0; i < maxIndex.length; i++)
			if (indexList[i] <= maxIndex[i])
				end = false;
			else {
				end = true;
				break;
			}

		if (end)
			return true;
		else
			return false;
	}

}

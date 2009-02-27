package com.plectix.simulator.components.contactMap;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractRule;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;
import com.plectix.simulator.interfaces.ILinkState;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.KappaSystem;

public class CContactMapAbstractRule implements IContactMapAbstractRule{

	private CContactMapAbstractSolution solution;
	private List<IContactMapAbstractAgent> lhsAgents;
	private List<IContactMapAbstractAgent> rhsAgents;
	private CContactMapAbstractAction abstractAction;
	private IRule rule;

	public IRule getRule() {
		return rule;
	}

	public CContactMapAbstractRule(CContactMapAbstractSolution solution,
			IRule rule) {
		this.solution = solution;
		this.rule = rule;
	}
	
	public CContactMapAbstractRule(IRule rule) {
		this.rule = rule;
		this.lhsAgents = initListSites(rule.getLeftHandSide());
		this.rhsAgents = initListSites(rule.getRightHandSide());
	}
	
	public void initAbstractRule() {
		this.lhsAgents = initListSites(rule.getLeftHandSide());
		this.rhsAgents = initListSites(rule.getRightHandSide());
		// this.lhsSites = initListsSites(agentMapLeftHandSide);
		// this.rhsSites = initListsSites(agentMapRightHandSide);
		this.abstractAction = new CContactMapAbstractAction(this);
	}
	
	public final boolean equalz(IContactMapAbstractRule obj){
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof CContactMapAbstractRule)) {
			return false;
		}

		CContactMapAbstractRule chRule = (CContactMapAbstractRule) obj;

		if (this.rule.getRuleID() != chRule.getRule().getRuleID())
			return false;

		return true;
	}
	
	public final boolean includedInCollection(
			Collection<IContactMapAbstractRule> collection) {
		for (IContactMapAbstractRule rule : collection) {
			if (this.equalz(rule)) {
				return true;
			}
		}
		return false;
	}

	private List<IContactMapAbstractAgent> initListSites(
			List<IConnectedComponent> listIn) {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		if (listIn == null)
			return listOut;
		for (IConnectedComponent c : listIn)
			for (IAgent a : c.getAgents()) {
				IContactMapAbstractAgent newAgent = new CContactMapAbstractAgent(
						a);
				for (ISite s : a.getSites()) {
					IContactMapAbstractSite newSite = new CContactMapAbstractSite(
							s, newAgent);
					newAgent.addSite(newSite);
				}
				listOut.add(newAgent);
			}
		return listOut;
	}

	// private List<IContactMapAbstractSite> initListsSites(
	// Map<Integer, IContactMapAbstractAgent> map) {
	// List<IContactMapAbstractSite> list = new
	// ArrayList<IContactMapAbstractSite>();
	// Iterator<Integer> iterator = map.keySet().iterator();
	// while (iterator.hasNext()) {
	// Integer key = iterator.next();
	// IContactMapAbstractAgent agent = map.get(key);
	// // List<IContactMapAbstractSite> listSites = agent.getSites();
	// // if (listSites.isEmpty())
	// // list.add(agent.getEmptySite());
	// // list.addAll(listSites);
	// }
	// return list;
	// }
	//
	// private void abstractCCList(List<IConnectedComponent> ccList,
	// Map<Integer, IContactMapAbstractAgent> map) {
	// for (IConnectedComponent cc : ccList) {
	// for (IAgent agent : cc.getAgents()) {
	// Integer key = agent.getNameId();
	// IContactMapAbstractAgent cMAA = map.get(key);
	// if (cMAA == null) {
	// cMAA = new CContactMapAbstractAgent(agent);
	// map.put(key, cMAA);
	// }
	// // cMAA.addSites(agent);
	// }
	// }
	// }

	// public Map<Integer, IContactMapAbstractAgent> getAgentMapLeftHandSide() {
	// return agentMapLeftHandSide;
	// }
	//
	// public Map<Integer, IContactMapAbstractAgent> getAgentMapRightHandSide()
	// {
	// return agentMapRightHandSide;
	// }

	public List<IContactMapAbstractAgent> getLhsAgents() {
		return lhsAgents;
	}

	public List<IContactMapAbstractAgent> getRhsAgents() {
		return rhsAgents;
	}

	public List<IContactMapAbstractAgent> getNewData() {
		List<IContactMapAbstractAgent> newData = new ArrayList<IContactMapAbstractAgent>();
		int[] indexList = new int[lhsAgents.size()];
		if (lhsAgents.size() == 0) {
			newData.addAll(abstractAction.apply(
					new ArrayList<UCorrelationAbstractAgent>(), solution));
			return newData;
		}

		List<List<IContactMapAbstractAgent>> agentsLists = initAgentsListsFromSolution();
		if (agentsLists == null)
			return null;
		agentsLists = clearAgentsLists(agentsLists);
		int[] maxIndex = getMaxIndex(agentsLists);
		// TODO getNewData

		while (!isEnd(indexList, maxIndex)) {
			List<UCorrelationAbstractAgent> injList = createInjectionList(
					indexList, agentsLists);
			newData.addAll(abstractAction.apply(injList, solution));

			upIndexList(indexList, maxIndex);
		}

		return newData;
	}

	private List<UCorrelationAbstractAgent> createInjectionList(int[] indexList,
			List<List<IContactMapAbstractAgent>> agentsLists) {
		List<IContactMapAbstractAgent> listAgents = new ArrayList<IContactMapAbstractAgent>();
		int index = 0;
		for (int i : indexList)
			listAgents.add(agentsLists.get(index++).get(i));
		List<UCorrelationAbstractAgent> list = UCorrelationAbstractAgent
				.createCorrelationSites(abstractAction, lhsAgents, listAgents,
						ECorrelationType.CORRELATION_LHS_AND_SOLUTION);
		return list;
	}

	private List<List<IContactMapAbstractAgent>> clearAgentsLists(
			List<List<IContactMapAbstractAgent>> agentsLists) {
		int i = 0;
		List<List<IContactMapAbstractAgent>> listOut = new ArrayList<List<IContactMapAbstractAgent>>();
		for (List<IContactMapAbstractAgent> list : agentsLists) {
			List<IContactMapAbstractAgent> addList = new ArrayList<IContactMapAbstractAgent>();
			IContactMapAbstractAgent agent = lhsAgents.get(i);
			for (IContactMapAbstractAgent a : list) {
				if (agent.isFit(a))
					addList.add(a);
			}
			listOut.add(addList);
			i++;
		}
		return listOut;
	}

	private List<List<IContactMapAbstractAgent>> initAgentsListsFromSolution() {
		List<List<IContactMapAbstractAgent>> agentsLists = new ArrayList<List<IContactMapAbstractAgent>>();
		for (IContactMapAbstractAgent a : lhsAgents) {
			Integer keyAgent = a.getNameId();
			List<IContactMapAbstractAgent> list = solution
					.getListOfAgentsByNameID(keyAgent);
			if(list==null || list.isEmpty())
				return null;
			agentsLists.add(list);
		}
		return agentsLists;
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

	private int[] getMaxIndex(List<List<IContactMapAbstractAgent>> agentsLists) {
		int[] mas = new int[agentsLists.size()];
		int index = 0;
		for (List<IContactMapAbstractAgent> l : agentsLists)
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

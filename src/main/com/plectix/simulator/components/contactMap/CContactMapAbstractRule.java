package com.plectix.simulator.components.contactMap;

import java.util.*;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractRule;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.components.CSite;

public class CContactMapAbstractRule implements IContactMapAbstractRule {

	private CContactMapAbstractSolution solution;
	private List<IContactMapAbstractAgent> lhsAgents;
	private List<IContactMapAbstractAgent> rhsAgents;
	private CContactMapAbstractAction abstractAction;
	private CRule rule;
	private int[] lastMaxIndex;

	public CRule getRule() {
		return rule;
	}

	public CContactMapAbstractRule(CContactMapAbstractSolution solution,
			CRule rule) {
		this.solution = solution;
		this.rule = rule;
	}

	public CContactMapAbstractRule(CRule rule) {
		this.rule = rule;
		this.lhsAgents = initListSites(rule.getLeftHandSide());
		this.rhsAgents = initListSites(rule.getRightHandSide());
	}

	public void initAbstractRule() {
		this.lhsAgents = initListSites(rule.getLeftHandSide());
		this.rhsAgents = initListSites(rule.getRightHandSide());
		this.abstractAction = new CContactMapAbstractAction(this);
	}

	public final boolean equalz(IContactMapAbstractRule obj) {
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
		Map<Integer, IContactMapAbstractAgent> map = new HashMap<Integer, IContactMapAbstractAgent>();
		if (listIn == null)
			return listOut;
		for (IConnectedComponent c : listIn)
			for (CAgent a : c.getAgents()) {
				IContactMapAbstractAgent newAgent = new CContactMapAbstractAgent(
						a);
				for (CSite s : a.getSites()) {
					IContactMapAbstractSite newSite = new CContactMapAbstractSite(
							s, newAgent);
					newAgent.addSite(newSite);
				}
				map.put(a.getIdInRuleHandside(), newAgent);
			}

		List<Integer> indexList = new ArrayList<Integer>();
		Iterator<Integer> iterator = map.keySet().iterator();
		while (iterator.hasNext())
			indexList.add(iterator.next());
		Collections.sort(indexList);
		for (Integer i : indexList)
			listOut.add(map.get(i));
		return listOut;
	}

	public List<IContactMapAbstractAgent> getFocusedAgents() {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		listOut.addAll(getAddAgents(lhsAgents));
		listOut.addAll(getAddAgents(rhsAgents));
		return listOut;
	}

	private List<IContactMapAbstractAgent> getAddAgents(
			List<IContactMapAbstractAgent> listIn) {
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		if (listIn.isEmpty() || listIn.get(0).getNameId() == CSite.NO_INDEX)
			return listOut;
		for (IContactMapAbstractAgent a : listIn)
			if (a.isAdd())
				listOut.add(a);
		return listOut;
	}

	public List<IContactMapAbstractAgent> getLhsAgents() {
		return lhsAgents;
	}

	public List<IContactMapAbstractAgent> getRhsAgents() {
		return rhsAgents;
	}

	public List<IContactMapAbstractAgent> getNewData() {
		List<IContactMapAbstractAgent> newData = new ArrayList<IContactMapAbstractAgent>();
		int[] indexList = new int[lhsAgents.size()];
		List<String> addListString = new ArrayList<String>();
		if (lhsAgents.size() == 0) {
			newData.addAll(abstractAction.apply(
					new ArrayList<UCorrelationAbstractAgent>(), solution,
					addListString));
			return newData;
		}

		List<List<IContactMapAbstractAgent>> agentsLists = initAgentsListsFromSolution();
		if (agentsLists == null)
			return null;
		agentsLists = clearAgentsLists(agentsLists);
		int[] maxIndex = getMaxIndex(agentsLists);

		if (lastMaxIndex != null && lastMaxIndex.equals(maxIndex))
			return newData;
		lastMaxIndex = maxIndex;
		// TODO getNewData

		while (!isEnd(indexList, maxIndex)) {
			List<UCorrelationAbstractAgent> injList = createInjectionList(
					indexList, agentsLists);
			newData.addAll(abstractAction.apply(injList, solution,
					addListString));

			upIndexList(indexList, maxIndex);
		}

		return newData;
	}

	private List<UCorrelationAbstractAgent> createInjectionList(
			int[] indexList, List<List<IContactMapAbstractAgent>> agentsLists) {
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
			if (list == null || list.isEmpty())
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

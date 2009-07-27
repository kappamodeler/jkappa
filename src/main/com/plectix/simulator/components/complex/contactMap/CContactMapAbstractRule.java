package com.plectix.simulator.components.complex.contactMap;

import java.util.*;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

import com.plectix.simulator.interfaces.IConnectedComponent;

/**
 * This class implements abstract rule.<br>
 * Uses for Contact map.
 * @author avokhmin
 *
 */
class CContactMapAbstractRule{

	private CContactMapAbstractSolution solution;
	private List<CAbstractAgent> lhsAgents;
	private List<CAbstractAgent> rhsAgents;
	private CContactMapAbstractAction abstractAction;
	private CRule rule;
	private int[] lastMaxIndex;

	/**
	 * This method returns present rule. 
	 * @return present rule.
	 */
	public CRule getRule() {
		return rule;
	}

	/**
	 * Constructor of CContactMapAbstractRule.<br>
	 * No fills left handSide and right handSide for abstract rule.
	 * @param solution given solution
	 * @param rule given present rule.
	 */
	public CContactMapAbstractRule(CContactMapAbstractSolution solution,
			CRule rule) {
		this.solution = solution;
		this.rule = rule;
	}

	/**
	 * Constructor of CContactMapAbstractRule.<br>
	 * Fills left handSide and right handSide for abstract rule.
	 * @param rule given present rule.
	 */
	public CContactMapAbstractRule(CRule rule) {
		this.rule = rule;
		this.lhsAgents = initListAgents(rule.getLeftHandSide());
		this.rhsAgents = initListAgents(rule.getRightHandSide());
	}

	/**
	 * This method initializes current rule.<br>
	 * Initializes abstract actions.  
	 */
	public void initAbstractRule() {
		this.lhsAgents = initListAgents(rule.getLeftHandSide());
		this.rhsAgents = initListAgents(rule.getRightHandSide());
		this.abstractAction = new CContactMapAbstractAction(this);
	}

	/**
	 * Util method. Uses for sort and creates list of abstract agent by given connected components.
	 * @param listIn given connected components
	 * @return list of abstract agent
	 */
	private List<CAbstractAgent> initListAgents(
			List<IConnectedComponent> listIn) {
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		Map<Integer, CAbstractAgent> map = new LinkedHashMap<Integer, CAbstractAgent>();
		if (listIn == null)
			return listOut;
		for (IConnectedComponent c : listIn)
			for (CAgent a : c.getAgents()) {
				CAbstractAgent newAgent = new CAbstractAgent(
						a);
				for (CSite s : a.getSites()) {
					CAbstractSite newSite = new CAbstractSite(
							s, newAgent);
					newAgent.addSite(newSite);
				}
				map.put(a.getIdInRuleHandside(), newAgent);
			}

		List<Integer> indexList = new ArrayList<Integer>();
		
		indexList.addAll(map.keySet());
		Collections.sort(indexList);
		for (Integer i : indexList)
			listOut.add(map.get(i));
		return listOut;
	}

	/**
	 * This method returns agents, necessary for "focus rule".
	 * @return necessary agents.
	 */
	public List<CAbstractAgent> getFocusedAgents() {
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		listOut.addAll(getAddAgents(lhsAgents));
		listOut.addAll(getAddAgents(rhsAgents));
		return listOut;
	}

	/**
	 * Util method. Uses for {@link #getFocusedAgents()}.<br>
	 * Finds necessary agents.
	 * @param listIn given list for finds.
	 * @return necessary agents.
	 */
	private List<CAbstractAgent> getAddAgents(
			List<CAbstractAgent> listIn) {
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		if (listIn.isEmpty() || listIn.get(0).getNameId() == CSite.NO_INDEX)
			return listOut;
		for (CAbstractAgent a : listIn)
			if (a.isAdd())
				listOut.add(a);
		return listOut;
	}

	/**
	 * This method returns agents from left handSide current rule.
	 * @return agents from left handSide current rule.
	 */
	public List<CAbstractAgent> getLhsAgents() {
		return lhsAgents;
	}

	/**
	 * This method returns agents from right handSide current rule.
	 * @return agents from right handSide current rule.
	 */
	public List<CAbstractAgent> getRhsAgents() {
		return rhsAgents;
	}

	/**
	 * This method apply current rule by exhaustive search possible injections and returns new data.
	 * @return new data.
	 */
	public List<CAbstractAgent> getNewData() {
		List<CAbstractAgent> newData = new ArrayList<CAbstractAgent>();
		int[] indexList = new int[lhsAgents.size()];
		List<String> addListString = new ArrayList<String>();
		if (lhsAgents.size() == 0) {
			newData.addAll(abstractAction.apply(
					new ArrayList<UCorrelationAbstractAgent>(), solution,
					addListString));
			return newData;
		}

		List<List<CAbstractAgent>> agentsLists = initAgentsListsFromSolution();
		if (agentsLists == null)
			return null;
		agentsLists = clearAgentsLists(agentsLists);
		int[] maxIndex = getMaxIndex(agentsLists);

		if (lastMaxIndex != null && lastMaxIndex.equals(maxIndex))
			return newData;
		lastMaxIndex = maxIndex;

		while (!isEnd(indexList, maxIndex)) {
			List<UCorrelationAbstractAgent> injList = createInjectionList(
					indexList, agentsLists);
			newData.addAll(abstractAction.apply(injList, solution,
					addListString));

			upIndexList(indexList, maxIndex);
		}

		return newData;
	}

	/**
	 * Util method. Uses for {@link #getNewData()}. Creates injection for apply rule.
	 * @param indexList list of index for take injection from <b>agentsLists</b>
	 * @param agentsLists all possible injections
	 * @return injection for apply rule.
	 */
	private List<UCorrelationAbstractAgent> createInjectionList(
			int[] indexList, List<List<CAbstractAgent>> agentsLists) {
		List<CAbstractAgent> listAgents = new ArrayList<CAbstractAgent>();
		int index = 0;
		for (int i : indexList)
			listAgents.add(agentsLists.get(index++).get(i));
		List<UCorrelationAbstractAgent> list = UCorrelationAbstractAgent
				.createCorrelationSites(abstractAction, lhsAgents, listAgents);
		return list;
	}

	/**
	 * Util method. Uses for {@link #getNewData()}. Checks given agent for fit. Returns lists of agents for creates injections.
	 * @param agentsLists given lists of agents.
	 * @return lists of agents for creates injections.
	 */
	private List<List<CAbstractAgent>> clearAgentsLists(
			List<List<CAbstractAgent>> agentsLists) {
		int i = 0;
		List<List<CAbstractAgent>> listOut = new ArrayList<List<CAbstractAgent>>();
		for (List<CAbstractAgent> list : agentsLists) {
			List<CAbstractAgent> addList = new ArrayList<CAbstractAgent>();
			CAbstractAgent agent = lhsAgents.get(i);
			for (CAbstractAgent a : list) {
				if (agent.isFit(a))
					addList.add(a);
			}
			listOut.add(addList);
			i++;
		}
		return listOut;
	}

	/**
	 * This method returns all agents from solution for further clears.
	 * @return agents from solution for further clears.
	 */
	private List<List<CAbstractAgent>> initAgentsListsFromSolution() {
		List<List<CAbstractAgent>> agentsLists = new ArrayList<List<CAbstractAgent>>();
		for (CAbstractAgent a : lhsAgents) {
			Integer keyAgent = a.getNameId();
			List<CAbstractAgent> list = solution
					.getListOfAgentsByNameID(keyAgent);
			if (list == null || list.isEmpty())
				return null;
			agentsLists.add(list);
		}
		return agentsLists;
	}

	/**
	 * Util method. Changes given <b>indexList</b> uses <b>maxIndex</b><br>
	 * @param indexList list for change
	 * @param maxIndex list of max value for <b>indexList</b>
	 */
	private void upIndexList(int[] indexList, int[] maxIndex) {
		indexList[indexList.length - 1] = indexList[indexList.length - 1] + 1;
		for (int i = indexList.length - 1; i >= 1; i--) {
			if (indexList[i] > maxIndex[i]) {
				indexList[i] = 0;
				indexList[i - 1] = indexList[i - 1] + 1;
			}
		}
	}

	/**
	 * Util method. Creates array of max value by given lists of agents.
	 * @param agentsLists given lists of agents
	 * @return array of max value
	 */
	private int[] getMaxIndex(List<List<CAbstractAgent>> agentsLists) {
		int[] mas = new int[agentsLists.size()];
		int index = 0;
		for (List<CAbstractAgent> l : agentsLists)
			mas[index++] = l.size() - 1;
		return mas;
	}

	/**
	 * Util method. Compares given arrays and returns <tt>true</tt> if value from <b>indexList</b> 
	 * larger <b>maxIndex</b>, otherwise <tt>false</tt>
	 * @param indexList given array for compares
	 * @param maxIndex given array of max value.
	 * @return <tt>true</tt> if value from <b>indexList</b> larger <b>maxIndex</b>, otherwise <tt>false</tt>
	 */
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

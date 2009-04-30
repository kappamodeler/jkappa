package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CSite;

/**
 * This class creates abstract atomic actions and apply theirs.
 * @author avokhmin
 *
 */
class CContactMapAbstractAction {
	private CContactMapAbstractRule rule;
	private List<CContactMapAbstractAgent> agentsToAdd;
	private List<UCorrelationAbstractAgent> correlationAgents;

	// NONE(-1),
	// BREAK(0),
	// DELETE(1),
	// ADD(2),
	// BOUND(3),
	// MODIFY(4);

	/**
	 * Default constructor of CContactMapAbstractAction.
	 * @param rule given rule, for which creates CContactMapAbstractAction
	 */
	public CContactMapAbstractAction(CContactMapAbstractRule rule) {
		this.rule = rule;
		this.agentsToAdd = new ArrayList<CContactMapAbstractAgent>();
		correlationAgents = new ArrayList<UCorrelationAbstractAgent>();

		initAtomicActions();
	}

	/**
	 * This method initializes abstract atomic actions.
	 */
	private void initAtomicActions() {
		List<CContactMapAbstractAgent> lhs = rule.getLhsAgents();
		List<CContactMapAbstractAgent> rhs = rule.getRhsAgents();

		if (lhs.get(0).getNameId() == CSite.NO_INDEX) {
			addAgentsToAdd(rhs);
			return;
		}

		if (rhs.isEmpty()) {
			for (CContactMapAbstractAgent a : lhs) {
				addAgentToDelete(a);
			}
			return;
		}

		int i = 0;
//		boolean[] checking = new boolean[lhs.size()];
		for (CContactMapAbstractAgent lhsAgent : lhs) {
			if (i >= rhs.size()) {
				addAgentToDelete(lhsAgent);
				continue;
			}
			CContactMapAbstractAgent rhsAgent = rhs.get(i++);
			if (isFit(lhsAgent, rhsAgent)) {
				UCorrelationAbstractAgent ua = new UCorrelationAbstractAgent(
						this, lhsAgent, rhsAgent);
				ua.initAtomicAction();
				correlationAgents.add(ua);
			} else {
				addAgentToDelete(lhsAgent);
				addAgentToAdd(rhsAgent);
			}
		}
		for (int j = i; j < rhs.size(); j++) {
			CContactMapAbstractAgent rhsAgent = rhs.get(j);
			addAgentToAdd(rhsAgent);
		}

	}

	/**
	 * Util method. Compares given agents. Uses only in {@link #initAtomicActions()}
	 * @param a1 given agent
	 * @param a2 given agent
	 * @return <tt>true</tt> if given agents are similar, otherwise <tt>false</tt>
	 */
	private boolean isFit(CContactMapAbstractAgent a1,
			CContactMapAbstractAgent a2) {
		if (a1.getNameId() != a2.getNameId())
			return false;
		if (a1.getSitesMap().size() != a2.getSitesMap().size())
			return false;
		
		for (Map.Entry<Integer, CContactMapAbstractSite> entry : a1.getSitesMap().entrySet()) {
			CContactMapAbstractSite s1 = entry.getValue();
			CContactMapAbstractSite s2 = a2.getSitesMap().get(entry.getKey());
			if ((s2 == null) || (s1.getNameId() != s2.getNameId()))
				return false;
		}
		return true;
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "DELETE" action.
	 * @param agentIn given agent
	 */
	private void addAgentToDelete(CContactMapAbstractAgent agentIn) {
		UCorrelationAbstractAgent ua = new UCorrelationAbstractAgent(this,
				agentIn, null);
		// ua.setType(CActionType.DELETE);
		ua.initAtomicAction();
		correlationAgents.add(ua);
		agentIn.shouldAdd();

	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD" action.
	 * @param listIn given list of agents
	 */
	private void addAgentsToAdd(List<CContactMapAbstractAgent> listIn) {
		for (CContactMapAbstractAgent a : listIn)
			addAgentToAdd(a);
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD" action.
	 * @param agentIn given agent
	 */
	private void addAgentToAdd(CContactMapAbstractAgent agentIn) {
		agentsToAdd.add(agentIn.clone());
		agentIn.shouldAdd();
	}

	/**
	 * This method apply all actions for given injections and returns new data.
	 * @param injList given list of injections for apply
	 * @param solution given solution
	 * @param addListString list of keys for checks
	 * @return new data for solution
	 */
	public List<CContactMapAbstractAgent> apply(
			List<UCorrelationAbstractAgent> injList,
			CContactMapAbstractSolution solution, List<String> addListString) {
		// TODO apply
		List<CContactMapAbstractAgent> listOut = new ArrayList<CContactMapAbstractAgent>();
		addToList(CContactMapAbstractAgent.cloneAll(agentsToAdd), listOut,
				solution, addListString);
		int i = 0;
		for (UCorrelationAbstractAgent corLHSandRHS : correlationAgents) {
			UCorrelationAbstractAgent corLHSandSolution = injList.get(i);
			CContactMapAbstractAgent newAgent = corLHSandSolution.getToAgent()
					.clone();

			// listOut.addAll(corLHSandRHS.modifySiteFromSolution(newAgent,
			// solution));
			addToList(corLHSandRHS.modifySiteFromSolution(newAgent, solution),
					listOut, solution, addListString);
			// if (!solution.getAgentsMap().containsKey(newAgent.getKey()))
			// listOut.add(newAgent);
			i++;
		}
		return listOut;
	}

	/**
	 * Util method. Checks agents from <b>listIn</b> in <b>solution</b>, <b>addListString</b>
	 * and if checks agent includen't there, adds to <b>listTo</b>
	 * @param listIn given list of agents for checks
	 * @param listTo given list to adds
	 * @param solution given solution
	 * @param addListString given list of keys for checks
	 */
	private void addToList(List<CContactMapAbstractAgent> listIn,
			List<CContactMapAbstractAgent> listTo,
			CContactMapAbstractSolution solution, List<String> addListString) {
		for (CContactMapAbstractAgent a : listIn) {
			String key = a.getKey();
			if (!solution.getAgentsMap().containsKey(key)
					&& !addListString.contains(key)) {
				listTo.add(a);
				addListString.add(key);
			}
		}
		// if(!a.includedInCollection(listTo))
		// listTo.add(a);
	}
}

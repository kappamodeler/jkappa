package com.plectix.simulator.components.complex.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;

/**
 * This class creates abstract atomic actions and apply theirs.
 * @author avokhmin
 *
 */
class CContactMapAbstractAction {
	private CContactMapAbstractRule rule;
	private List<CAbstractAgent> agentsToAdd;
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
		this.agentsToAdd = new ArrayList<CAbstractAgent>();
		correlationAgents = new ArrayList<UCorrelationAbstractAgent>();

		initAtomicActions();
	}

	/**
	 * This method initializes abstract atomic actions.
	 */
	private void initAtomicActions() {
		List<CAbstractAgent> lhs = rule.getLhsAgents();
		List<CAbstractAgent> rhs = rule.getRhsAgents();

		if (lhs.get(0).getNameId() == CSite.NO_INDEX) {
			addAgentsToAdd(rhs);
			return;
		}

		if (rhs.isEmpty()) {
			for (CAbstractAgent a : lhs) {
				addAgentToDelete(a);
			}
			return;
		}

		int i = 0;
//		boolean[] checking = new boolean[lhs.size()];
		for (CAbstractAgent lhsAgent : lhs) {
			if (i >= rhs.size()) {
				addAgentToDelete(lhsAgent);
				continue;
			}
			CAbstractAgent rhsAgent = rhs.get(i++);
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
			CAbstractAgent rhsAgent = rhs.get(j);
			addAgentToAdd(rhsAgent);
		}

	}

	/**
	 * Util method. Compares given agents. Uses only in {@link #initAtomicActions()}
	 * @param a1 given agent
	 * @param a2 given agent
	 * @return <tt>true</tt> if given agents are similar, otherwise <tt>false</tt>
	 */
	private boolean isFit(CAbstractAgent a1,
			CAbstractAgent a2) {
		if (a1.getNameId() != a2.getNameId())
			return false;
		if (a1.getSitesMap().size() != a2.getSitesMap().size())
			return false;
		
		for (Map.Entry<Integer, CAbstractSite> entry : a1.getSitesMap().entrySet()) {
			CAbstractSite s1 = entry.getValue();
			CAbstractSite s2 = a2.getSitesMap().get(entry.getKey());
			if ((s2 == null) || (s1.getNameId() != s2.getNameId()))
				return false;
		}
		return true;
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "DELETE" action.
	 * @param agentIn given agent
	 */
	private void addAgentToDelete(CAbstractAgent agentIn) {
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
	private void addAgentsToAdd(List<CAbstractAgent> listIn) {
		for (CAbstractAgent a : listIn)
			addAgentToAdd(a);
	}

	/**
	 * Util method. Uses only in {@link #initAtomicActions()}. Creates "ADD" action.
	 * @param agentIn given agent
	 */
	private void addAgentToAdd(CAbstractAgent agentIn) {
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
	public List<CAbstractAgent> apply(
			List<UCorrelationAbstractAgent> injList,
			CContactMapAbstractSolution solution, List<String> addListString) {
		// TODO apply
		List<CAbstractAgent> listOut = new ArrayList<CAbstractAgent>();
		addToList(CAbstractAgent.cloneAll(agentsToAdd), listOut,
				solution, addListString);
		int i = 0;
		for (UCorrelationAbstractAgent corLHSandRHS : correlationAgents) {
			UCorrelationAbstractAgent corLHSandSolution = injList.get(i);
			CAbstractAgent newAgent = corLHSandSolution.getToAgent()
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
	private void addToList(List<CAbstractAgent> listIn,
			List<CAbstractAgent> listTo,
			CContactMapAbstractSolution solution, List<String> addListString) {
		for (CAbstractAgent a : listIn) {
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

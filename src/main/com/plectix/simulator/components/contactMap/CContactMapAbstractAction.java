package com.plectix.simulator.components.contactMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IContactMapAbstractAgent;
import com.plectix.simulator.interfaces.IContactMapAbstractSite;

public class CContactMapAbstractAction {
	private Map<Integer, List<IContactMapAbstractSite>> siteMap;
	private CContactMapAbstractRule rule;
	private List<IContactMapAbstractAgent> agentsToAdd;
	private List<UCorrelationAbstractAgent> correlationAgents;

	// NONE(-1),
	// BREAK(0),
	// DELETE(1),
	// ADD(2),
	// BOUND(3),
	// MODIFY(4);

	public CContactMapAbstractAction(CContactMapAbstractRule rule) {
		this.rule = rule;
		this.agentsToAdd = new ArrayList<IContactMapAbstractAgent>();
		correlationAgents = new ArrayList<UCorrelationAbstractAgent>();

		// TODO
		initAtomicActions();
		// createAtomicActions();
	}

	private void initAtomicActions() {
		List<IContactMapAbstractAgent> lhs = rule.getLhsAgents();
		List<IContactMapAbstractAgent> rhs = rule.getRhsAgents();

		if (lhs.get(0).getNameId() == CSite.NO_INDEX) {
			addAgentsToAdd(rhs);
			return;
		}

		if (rhs.isEmpty()) {
			for (IContactMapAbstractAgent a : lhs) {
				addAgentToDelete(a);
			}
			return;
		}

		int i = 0;
//		boolean[] checking = new boolean[lhs.size()];
		for (IContactMapAbstractAgent lhsAgent : lhs) {
			if (i >= rhs.size()) {
				addAgentToDelete(lhsAgent);
				continue;
			}
			IContactMapAbstractAgent rhsAgent = rhs.get(i++);
			if (isFit(lhsAgent, rhsAgent)) {
				UCorrelationAbstractAgent ua = new UCorrelationAbstractAgent(
						this, lhsAgent, rhsAgent,
						ECorrelationType.CORRELATION_LHS_AND_RHS);
				ua.initAtomicAction();
				correlationAgents.add(ua);
			} else {
				addAgentToDelete(lhsAgent);
				addAgentToAdd(rhsAgent);
			}
		}
		for (int j = i; j < rhs.size(); j++) {
			IContactMapAbstractAgent rhsAgent = rhs.get(j);
			addAgentToAdd(rhsAgent);
		}

	}

	private boolean isFit(IContactMapAbstractAgent a1,
			IContactMapAbstractAgent a2) {
		if (a1.getNameId() != a2.getNameId())
			return false;
		if (a1.getSitesMap().size() != a2.getSitesMap().size())
			return false;
		Iterator<Integer> iterator = a1.getSitesMap().keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			IContactMapAbstractSite s1 = a1.getSitesMap().get(key);
			IContactMapAbstractSite s2 = a2.getSitesMap().get(key);
			if ((s2 == null) || (s1.getNameId() != s2.getNameId()))
				return false;
		}

		return true;
	}

	private void addAgentToDelete(IContactMapAbstractAgent agentIn) {
		UCorrelationAbstractAgent ua = new UCorrelationAbstractAgent(this,
				agentIn, null, ECorrelationType.CORRELATION_LHS_AND_RHS);
		// ua.setType(CActionType.DELETE);
		ua.initAtomicAction();
		correlationAgents.add(ua);
		agentIn.shouldAdd();

	}

	private void addAgentsToAdd(List<IContactMapAbstractAgent> listIn) {
		for (IContactMapAbstractAgent a : listIn)
			addAgentToAdd(a);
	}

	private void addAgentToAdd(IContactMapAbstractAgent agentIn) {
		agentsToAdd.add(agentIn.clone());
		agentIn.shouldAdd();
	}

	public List<IContactMapAbstractAgent> apply(
			List<UCorrelationAbstractAgent> injList,
			CContactMapAbstractSolution solution, List<String> addListString) {
		// TODO apply
		List<IContactMapAbstractAgent> listOut = new ArrayList<IContactMapAbstractAgent>();
		addToList(CContactMapAbstractAgent.cloneAll(agentsToAdd), listOut,
				solution, addListString);
		int i = 0;
		for (UCorrelationAbstractAgent corLHSandRHS : correlationAgents) {
			UCorrelationAbstractAgent corLHSandSolution = injList.get(i);
			IContactMapAbstractAgent newAgent = corLHSandSolution.getToAgent()
					.clone();

			// listOut.addAll(corLHSandRHS.modifySiteFromSolution(newAgent,
			// solution));
			addToList(corLHSandRHS.modifySiteFromSolution(newAgent, solution,rule.getDeletedSites()),
					listOut, solution, addListString);
			// if (!solution.getAgentsMap().containsKey(newAgent.getKey()))
			// listOut.add(newAgent);
			i++;
		}
		return listOut;
	}

	private void addToList(List<IContactMapAbstractAgent> listIn,
			List<IContactMapAbstractAgent> listTo,
			CContactMapAbstractSolution solution, List<String> addListString) {
		for (IContactMapAbstractAgent a : listIn) {
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

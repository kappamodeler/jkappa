package com.plectix.simulator.parser.builders;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.simulationclasses.action.Action;
import com.plectix.simulator.simulationclasses.action.ActionType;
import com.plectix.simulator.simulationclasses.perturbations.AbstractModification;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.OnceModification;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Link;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

public class MasterSolutionModel {
	private final Map<String, Agent> masterMap;

	public MasterSolutionModel() {
		masterMap = new LinkedHashMap<String, Agent>();
	}

	public boolean isCorrect(Agent agentIn) {
		Agent agentThis = masterMap.get(agentIn.getName());
		if (agentThis == null) {
			agentThis = agentIn.clone();
			masterMap.put(agentThis.getName(), agentThis);
		}
		if (agentThis.getSites().size() != agentIn.getSites().size())
			return false;

		for (Site siteThis : agentThis.getSites()) {
			if (agentIn.getSiteByName(siteThis.getName()) == null)
				return false;
		}

		for (Site siteThis : agentIn.getSites()) {
			Link linkState = siteThis.getLinkState();
			
			if (linkState.getStatusLink() == LinkStatus.WILDCARD
					|| (linkState.getConnectedSite() == null && linkState
							.getStatusLink() == LinkStatus.BOUND))
				return false;
		}

		return true;
	}

	public void checkCorrect(List<Agent> agents, SolutionLineData lineData)
			throws ParseErrorException {
		for (Agent agent : agents)
			if (!isCorrect(agent))
				throwExeption(lineData.toString());
	}

	public void checkCorrect(Rule rule, String line) throws ParseErrorException {
		for (Action action : rule.getActionList()) {
			if (action.getType() != ActionType.ADD)
				continue;
			Agent checkAgent = action.getTargetAgent();
			if (!isCorrect(checkAgent))
				throwExeption(line);
		}
	}

	private void throwExeption(String line) throws ParseErrorException {
		ParseErrorException exeption = new ParseErrorException(
				ParseErrorMessage.INCOMPLETE_SUBSTANCE, line);
		throw exeption;
	}

	public void checkCorrect(ComplexPerturbation<?, ?> res,
			ModelPerturbation perturbation) throws ParseErrorException {
		AbstractModification modification = res.getModification();
		if (modification instanceof OnceModification) {
			checkCorrect(((OnceModification) modification)
					.getPerturbationRule(), perturbation.toString());
		}
	}
}

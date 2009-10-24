package com.plectix.simulator.parser;

import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.simulationclasses.perturbations.Perturbation;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.Site;

public class PerturbationReader {

	private Perturbation perturbation;

	public PerturbationReader(Perturbation perturbation) {
		this.perturbation = perturbation;
	}

	public String read() {
		StringBuffer expression = new StringBuffer();
		expression.append("%mod: ");
		switch (perturbation.getType()) {

		case TIME: {
			expression.append(timeCondition(perturbation));
			expression.append("'"
					+ perturbation.getPerturbationRule().getName() + "' := ");
			expression.append(parameterList(
					perturbation.getRHSParametersList(), true));
			break;
		}

		case NUMBER: {
			expression.append(observableCondition(perturbation));
			expression.append(" do '"
					+ perturbation.getPerturbationRule().getName() + "' := ");
			expression.append(parameterList(
					perturbation.getRHSParametersList(), true));
			break;
		}

		case ONCE: {
			expression.append(timeCondition(perturbation));
			expression.append(" $");
			boolean isAdd = perturbation.getPerturbationRule()
					.leftHandSideIsEmpty();
			expression.append((isAdd) ? "ADDONCE " : "DELETEONCE ");
			List<ConnectedComponentInterface> listCC = (isAdd) ? perturbation
					.getPerturbationRule().getRightHandSide() : perturbation
					.getPerturbationRule().getLeftHandSide();
			for (ConnectedComponentInterface cC : listCC) {
				int i = 0;
				for (Agent agent : cC.getAgents()) {
					expression.append(agentExpression(agent));
					if (++i < cC.getAgents().size())
						expression.append(", ");
				}
			}

			break;
		}
		default:
			break;
		}
		expression.append("\n");
		return expression.toString();
	}

	private StringBuffer agentExpression(Agent agent) {
		StringBuffer sb = new StringBuffer();
		sb.append(agent.getName() + "(");
		int i = 0;
		for (Site site : agent.getSites()) {
			sb.append(site.getName());
			if (!site.getInternalState().hasDefaultName()) {
				sb.append("~" + site.getInternalState().getName());
			}
			if (site.getLinkIndex() != -1)
				sb.append("!" + site.getLinkIndex());
			if (++i < agent.getSites().size())
				sb.append(", ");
		}
		sb.append(")");
		return sb;
	}

	private StringBuffer observableCondition(Perturbation perturbation) {
		StringBuffer sb = new StringBuffer();
		sb.append("['" + perturbation.getObservableName() + "'] ");
		sb.append(perturbation.inequalitySign().toString());
		List<PerturbationExpressionInterface> left = perturbation
				.getLHSParametersList();
		sb.append(parameterList(left, false));
		return sb;
	}

	private StringBuffer parameterList(
			List<PerturbationExpressionInterface> list, boolean isRule) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;

		for (PerturbationExpressionInterface expr : list) {
			String name = expr.getName();
			Double rate = expr.getValue();
			if (first) {
				sb.append((rate < 0) ? "- " : "");
				first = false;
			} else {
				sb.append((rate < 0) ? " - " : " + ");
			}
			sb.append(Math.abs(rate));
			if (name != null) {
				if (isRule)
					sb.append(" * '" + name + "'");
				else
					sb.append(" * ['" + name + "']");
			}
		}
		return sb;
	}

	private String timeCondition(Perturbation perturbation) {
		return "$T > " + perturbation.getTimeCondition() + " do ";
	}
}

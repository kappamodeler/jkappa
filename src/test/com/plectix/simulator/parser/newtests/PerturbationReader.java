package com.plectix.simulator.parser.newtests;

import java.util.List;

import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.perturbations.CPerturbationType;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;

import com.plectix.simulator.interfaces.ISite;

public class PerturbationReader {

	private CPerturbation perturbation;

	public PerturbationReader(CPerturbation perturbation) {
		this.perturbation = perturbation;
	}

	public String read() {
		StringBuffer expression = new StringBuffer();
		expression.append("%mod: ");
		switch (perturbation.getType()) {
		
		case TIME: {
			expression.append(timeCondition(perturbation));
			expression.append("'" + perturbation.getPerturbationRule().getName()
					+ "' := ");
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
			boolean isAdd = perturbation.getPerturbationRule().leftHandSideIsEmpty();
			expression.append((isAdd)? "ADDONCE ":"DELETEONCE ");
			List<IConnectedComponent> listCC = (isAdd)? perturbation.getPerturbationRule().getRightHandSide():
				perturbation.getPerturbationRule().getLeftHandSide();
			for (IConnectedComponent cC : listCC) {
				int i = 0;
				for (IAgent agent : cC.getAgents()) {
					expression.append(agentExpression(agent));
					if (++i<cC.getAgents().size())
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

	private StringBuffer agentExpression(IAgent agent) {
		StringBuffer sb = new StringBuffer();
		sb.append(agent.getName() + "(");
		int i = 0;
		for (ISite site : agent.getSites()) {
			sb.append(site.getName());
			if (site.getInternalState().getNameId() != -1){
				sb.append("~" + site.getInternalState().getNameId());
			}
			if (site.getLinkIndex() != -1)
				sb.append("!" + site.getLinkIndex());
			if(++i< agent.getSites().size())
				sb.append(", ");
		}
		sb.append(")");
		return sb;
	}

	private StringBuffer observableCondition(CPerturbation perturbation) {
		StringBuffer sb = new StringBuffer();
		sb.append("['" + perturbation.getObsNameID() + "'] ");
		sb.append((perturbation.getGreater()) ? "> " : "< ");
		List<IPerturbationExpression> left = perturbation
				.getLHSParametersList();
		sb.append(parameterList(left, false));
		return sb;
	}

	private StringBuffer parameterList(List<IPerturbationExpression> list,
			boolean isRule) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;

		for (IPerturbationExpression expr : list) {
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

	private String timeCondition(CPerturbation perturbation) {
		return "$T > " + perturbation.getTimeCondition() + " do ";
	}
}

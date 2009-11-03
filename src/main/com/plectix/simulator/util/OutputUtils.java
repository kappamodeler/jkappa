package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.PerturbationExpressionInterface;
import com.plectix.simulator.simulationclasses.perturbations.Perturbation;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LinkRank;
import com.plectix.simulator.staticanalysis.Site;

public class OutputUtils {
	public static final String printPartRule(
			List<ConnectedComponentInterface> components, 
			boolean ocamlStyleNaming) {
		StringBuffer sb = new StringBuffer();
		int[] indexLink = new int[] { 0 };
		int length = 0;
		if (components == null)
			return sb.toString();
		for (ConnectedComponentInterface cc : components)
			length = length + cc.getAgents().size();
		int index = 1;
		for (ConnectedComponentInterface cc : components) {
			if (cc.isEmpty())
				return sb.toString();
			sb.append(printPartRule(cc, indexLink, ocamlStyleNaming));
			if (index < components.size())
				sb.append(",");
			index++;

		}
		return sb.toString();
	}

	public static final String printPartRule(
			ConnectedComponentInterface component, int[] index, boolean ocamlStyleNaming) {
		StringBuffer sb = new StringBuffer();
		int length = 0;
		if (component == null)
			return sb.toString();
		length = component.getAgents().size();

		int j = 1;
		if (component.isEmpty())
			return sb.toString();

		List<Agent> sortedAgents = component.getAgentsSortedByIdInRule();

		for (Agent agent : sortedAgents) {
			sb.append(agent.getName());
			sb.append("(");

			List<String> sitesList = new ArrayList<String>();

			for (Site site : agent.getSites()) {
				String siteStr = new String(site.getName());
				// line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (!site.getInternalState().hasDefaultName())) {
					siteStr = siteStr + "~" + site.getInternalState().getName();
					// line = line + "~" + site.getInternalState().getName();
				}
				switch (site.getLinkState().getStatusLink()) {
				case BOUND: {
					if (site.getLinkState().getStatusLinkRank() == LinkRank.SEMI_LINK) {
						siteStr = siteStr + "!_";
						// line = line + "!_";
					} else if (site.getParentAgent().getIdInRuleHandside() < ((Site) site
							.getLinkState().getConnectedSite()).getParentAgent()
							.getIdInRuleHandside()) {
						site.getLinkState().getConnectedSite().getLinkState()
								.setLinkStateId(index[0]);
						siteStr = siteStr + "!" + index[0];
						index[0]++;
						// line = line + "!" + indexLink++;
					} else {
						siteStr = siteStr + "!"
								+ site.getLinkState().getLinkStateId();
						// line = line + "!"
						// + site.getLinkState().getLinkStateID();
						site.getLinkState().setLinkStateId(-1);
					}

					break;
				}
				case WILDCARD: {
					siteStr = siteStr + "?";
					// line = line + "?";
					break;
				}
				}

				// if (agent.getSites().size() > i++)
				// line = line + ",";
				sitesList.add(siteStr);
			}

			sb.append(prepareSiteDescription(sortSiteLines(sitesList, ocamlStyleNaming)));
			sb.append((length > j) ? "),":")");
			sitesList.clear();
			j++;
		}

		return sb.toString();
	}
	
	private static final String prepareSiteDescription(List<String> siteLines) {
		StringBuffer sb = new StringBuffer();
		if (siteLines.size() == 0)
			return sb.toString();
		for (int i = 0; i < siteLines.size() - 1; i++) {
			sb.append(siteLines.get(i) + ",");
		}
		sb.append(siteLines.get(siteLines.size() - 1));

		return sb.toString();
	}

	private static final List<String> sortSiteLines(List<String> siteLines,
			boolean isOcamlStyleObsName) {
		if (isOcamlStyleObsName) {
			Collections.sort(siteLines);
		}
		return siteLines;
	}
	
	
	public static final String perturbationToString(Perturbation perturbation, KappaSystem kappaSystem) {
		StringBuffer sb = new StringBuffer();
		sb.append("-");
		switch (perturbation.getType()) {
		case TIME: {
			sb.append("Whenever current time ");
			sb.append(perturbation.inequalitySign().toString());
			sb.append(perturbation.getTimeCondition());
			break;
		}
		case NUMBER: {
			sb.append("Whenever [");
			sb.append(kappaSystem.getObservables().getComponentList().get(
					perturbation.getObservableName()).getName());
			sb.append("] ");
			sb.append(perturbation.inequalitySign().toString());
			sb.append(perturbationParametersToString(perturbation
							.getLHSParametersList()));
			break;
		}
		}

		sb.append(" do kin(");
		sb.append(perturbation.getPerturbationRule().getName());
		sb.append("):=");
		sb.append(perturbationParametersToString(perturbation
				.getRHSParametersList()));

		return sb.toString();
	}

	private static final String perturbationParametersToString(
			List<PerturbationExpressionInterface> perturbationExpressions) {
		StringBuffer sb = new StringBuffer();

		int index = 1;
		for (PerturbationExpressionInterface expression : perturbationExpressions) {
			sb.append(expression.getValueToString());
			if (expression.getName() != null) {
				sb.append("*[");
				sb.append(expression.getName());
				sb.append("]");
			}
			if (index < perturbationExpressions.size())
				sb.append(" + ");
			index++;
		}

		return sb.toString();
	}
}

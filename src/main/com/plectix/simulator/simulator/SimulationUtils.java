package com.plectix.simulator.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.options.SimulatorArguments;
import com.plectix.simulator.options.SimulatorOptions;
import com.plectix.simulator.parser.DataReading;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.util.Info;

public class SimulationUtils {

	public static final String printPartRule(List<IConnectedComponent> ccList,
			boolean isOcamlStyleObsName) {
		String line = new String();
		int[] indexLink = new int[] { 0 };
		int length = 0;
		if (ccList == null)
			return line;
		for (IConnectedComponent cc : ccList)
			length = length + cc.getAgents().size();
		int index = 1;
		for (IConnectedComponent cc : ccList) {
			if (cc == CRule.EMPTY_LHS_CC)
				return line;
			line += printPartRule(cc, indexLink, isOcamlStyleObsName);
			if (index < ccList.size())
				line += ",";
			index++;

		}
		return line;
	}

	public static final String printPartRule(IConnectedComponent cc,
			int[] index, boolean isOcamlStyleObsName) {
		String line = new String();
		int length = 0;
		if (cc == null)
			return line;
		length = cc.getAgents().size();

		int j = 1;
		if (cc == CRule.EMPTY_LHS_CC)
			return line;

		List<IAgent> sortedAgents = cc.getAgentsSortedByIdInRule();

		for (IAgent agent : sortedAgents) {
			line = line + agent.getName();
			line = line + "(";

			List<String> sitesList = new ArrayList<String>();

			int i = 1;
			for (ISite site : agent.getSites()) {
				String siteStr = new String(site.getName());
				// line = line + site.getName();
				if ((site.getInternalState() != null)
						&& (site.getInternalState().getNameId() >= 0)) {
					siteStr = siteStr + "~" + site.getInternalState().getName();
					// line = line + "~" + site.getInternalState().getName();
				}
				switch (site.getLinkState().getStatusLink()) {
				case CLinkState.STATUS_LINK_BOUND: {
					if (site.getLinkState().getStatusLinkRank() == CLinkState.RANK_SEMI_LINK) {
						siteStr = siteStr + "!_";
						// line = line + "!_";
					} else if (site.getAgentLink().getIdInRuleSide() < ((ISite) site
							.getLinkState().getSite()).getAgentLink()
							.getIdInRuleSide()) {
						((ISite) site.getLinkState().getSite()).getLinkState()
								.setLinkStateID(index[0]);
						siteStr = siteStr + "!" + index[0];
						index[0]++;
						// line = line + "!" + indexLink++;
					} else {
						siteStr = siteStr + "!"
								+ site.getLinkState().getLinkStateID();
						// line = line + "!"
						// + site.getLinkState().getLinkStateID();
						site.getLinkState().setLinkStateID(-1);
					}

					break;
				}
				case CLinkState.STATUS_LINK_WILDCARD: {
					siteStr = siteStr + "?";
					// line = line + "?";
					break;
				}
				}

				// if (agent.getSites().size() > i++)
				// line = line + ",";
				sitesList.add(siteStr);
			}

			line = line
					+ getSitesLine(sortSitesStr(sitesList, isOcamlStyleObsName));
			if (length > j) {
				line = line + "),";
			} else {
				line = line + ")";
			}
			sitesList.clear();
			j++;
		}

		return line;
	}

	private static final String getSitesLine(List<String> list) {
		String line = new String("");
		if (list.size() == 0)
			return line;
		for (int i = 0; i < list.size() - 1; i++) {
			line = line + list.get(i) + ",";
		}
		line = line + list.get(list.size() - 1);

		return line;
	}

	private static final List<String> sortSitesStr(List<String> list,
			boolean isOcamlStyleObsName) {
		if (isOcamlStyleObsName) {
			Collections.sort(list);
		}

		return list;
	}

	public static final List<IConnectedComponent> buildConnectedComponents(
			List<IAgent> agents) {

		if (agents == null || agents.isEmpty())
			return null;

		List<IConnectedComponent> result = new ArrayList<IConnectedComponent>();

		int index = 1;
		for (IAgent agent : agents)
			agent.setIdInRuleSide(index++);

		while (!agents.isEmpty()) {

			List<IAgent> connectedAgents = new ArrayList<IAgent>();

			findConnectedComponent(agents.get(0), agents, connectedAgents);

			// It needs recursive tree search of connected component
			result.add(new CConnectedComponent(connectedAgents));
		}

		return result;
	}

	private static final void findConnectedComponent(IAgent rootAgent,
			List<IAgent> hsRulesList, List<IAgent> agentsList) {
		agentsList.add(rootAgent);
		rootAgent.setIdInConnectedComponent(agentsList.size() - 1);
		removeAgent(hsRulesList, rootAgent);
		for (ISite site : rootAgent.getSites()) {
			if (site.getLinkIndex() != CSite.NO_INDEX) {
				IAgent linkedAgent = findLink(hsRulesList, site.getLinkIndex());
				if (linkedAgent != null) {
					if (!isAgentInList(agentsList, linkedAgent))
						findConnectedComponent(linkedAgent, hsRulesList,
								agentsList);
				}
			}
		}
	}

	private static final boolean isAgentInList(List<IAgent> list, IAgent agent) {
		for (IAgent lagent : list) {
			if (lagent == agent)
				return true;
		}
		return false;
	}

	private static final IAgent findLink(List<IAgent> agents, int linkIndex) {
		for (IAgent tmp : agents) {
			for (ISite s : tmp.getSites()) {
				if (s.getLinkIndex() == linkIndex) {
					return tmp;
				}
			}
		}
		return null;
	}

	private static final void removeAgent(List<IAgent> agents, IAgent agent) {
		int i = 0;
		for (i = 0; i < agents.size(); i++) {
			if (agents.get(i) == agent)
				break;
		}
		agents.remove(i);
	}

	public static final IRule buildRule(List<IAgent> left, List<IAgent> right,
			String name, double activity, int ruleID, boolean isStorify) {
		return new CRule(buildConnectedComponents(left),
				buildConnectedComponents(right), name, activity, ruleID,
				isStorify);
	}

	public final static String[] changeArgs(String[] args) {
		String[] argsNew = new String[args.length];
		int i = 0;
		for (String st : args)
			if (st.startsWith("-"))
				argsNew[i++] = st.substring(0, 2)
						+ st.substring(2).replaceAll("-", "_");
			else
				argsNew[i++] = st;
		return argsNew;
	}

	public static final SimulatorArguments parseArguments(
			SimulationData simulationData, String[] args)
			throws IllegalArgumentException {

		simulationData.addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
		SimulatorArguments arguments = new SimulatorArguments(args);
		try {
			arguments.parse();
		} catch (ParseException e) {
			Simulator.println("Error parsing arguments:");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}

		if (arguments.hasOption(SimulatorOptions.HELP)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("use --sim [file] [options]",
					SimulatorOptions.COMMAND_LINE_OPTIONS);
			// TODO are we to exit here?
			System.exit(0);
		}

		if (arguments.hasOption(SimulatorOptions.VERSION)) {
			SimulationMain.myOutputStream.println("Java simulator v."
					+ SimulationMain.VERSION + " SVN Revision: "
					+ BuildConstants.BUILD_SVN_REVISION);
			// TODO are we to exit here?
			System.exit(0);
		}

		if (arguments.hasOption(SimulatorOptions.XML_SESSION_NAME)) {
			simulationData.setXmlSessionName(arguments
					.getValue(SimulatorOptions.XML_SESSION_NAME));
		}
		if (arguments.hasOption(SimulatorOptions.OUTPUT_XML)) {
			simulationData.setXmlSessionName(arguments
					.getValue(SimulatorOptions.OUTPUT_XML));
		}
//		if (arguments.hasOption(SimulatorOptions.DO_XML)) {
//			simulationData.setXmlSessionName(arguments
//					.getValue(SimulatorOptions.DO_XML));
//		}

		try {
			if (arguments.hasOption(SimulatorOptions.INIT)) {
				simulationData.setInitialTime(Double.valueOf(arguments
						.getValue(SimulatorOptions.INIT)));
			}
			if (arguments.hasOption(SimulatorOptions.POINTS)) {
				simulationData.setPoints(Integer.valueOf(arguments
						.getValue(SimulatorOptions.POINTS)));
			}
			if (arguments.hasOption(SimulatorOptions.RESCALE)) {
				double rescale = Double.valueOf(arguments
						.getValue(SimulatorOptions.RESCALE));
				if (rescale > 0)
					simulationData.setRescale(rescale);
				else
					throw new Exception();
			}

			if (arguments.hasOption(SimulatorOptions.NO_SEED)) {
				simulationData.setSeed(0);
			}
			// TODO else?
			if (arguments.hasOption(SimulatorOptions.SEED)) {
				int seed = 0;
				seed = Integer.valueOf(arguments
						.getValue(SimulatorOptions.SEED));
				simulationData.setSeed(seed);
			}

			if (arguments.hasOption(SimulatorOptions.MAX_CLASHES)) {
				int max_clashes = 0;
				max_clashes = Integer.valueOf(arguments
						.getValue(SimulatorOptions.MAX_CLASHES));
				simulationData.setMaxClashes(max_clashes);
			}

			if (arguments.hasOption(SimulatorOptions.EVENT)) {
				long event = 0;
				event = Long
						.valueOf(arguments.getValue(SimulatorOptions.EVENT));
				simulationData.setEvent(event);
			}

			if (arguments.hasOption(SimulatorOptions.ITERATION)) {
				simulationData.setIterations(Integer.valueOf(arguments
						.getValue(SimulatorOptions.ITERATION)));
			}

		} catch (Exception e) {
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}

		if (arguments.hasOption(SimulatorOptions.RANDOMIZER_JAVA)) {
			simulationData.setRandomizer(arguments
					.getValue(SimulatorOptions.RANDOMIZER_JAVA));
		}

		if (arguments.hasOption(SimulatorOptions.NO_ACTIVATION_MAP)
				|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
				|| (arguments
						.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationData.setActivationMap(false);
		}

		if (arguments.hasOption(SimulatorOptions.MERGE_MAPS)) {
			simulationData.setInhibitionMap(true);
		}

		if (arguments.hasOption(SimulatorOptions.NO_INHIBITION_MAP)
				|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
				|| (arguments
						.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationData.setInhibitionMap(false);
		}

		if (arguments.hasOption(SimulatorOptions.OCAML_STYLE_OBS_NAME)) {
			simulationData.setOcamlStyleObsName(true);
		}

		if (arguments.hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			int iteration = 0;
			boolean exp = false;
			try {
				iteration = Integer.valueOf(arguments
						.getValue(SimulatorOptions.NUMBER_OF_RUNS));
			} catch (Exception e) {
				exp = true;
			}
			if ((exp) || (!arguments.hasOption(SimulatorOptions.SEED))) {
				throw new IllegalArgumentException("No SEED OPTION");
			}
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_ITERATIONS);
			simulationData.setIterations(iteration);
		}

		if (arguments.hasOption(SimulatorOptions.CLOCK_PRECISION)) {
			long clockPrecision = 0;
			clockPrecision = Long.valueOf(arguments
					.getValue(SimulatorOptions.CLOCK_PRECISION));
			clockPrecision *= 60000;
			simulationData.setClockPrecision(clockPrecision);
		}

		if (arguments.hasOption(SimulatorOptions.OUTPUT_FINAL_STATE)) {
			simulationData.setOutputFinalState(true);
		}

		if (arguments.hasOption(SimulatorOptions.OUTPUT_SCHEME)) {
			simulationData.setXmlSessionPath(arguments
					.getValue(SimulatorOptions.OUTPUT_SCHEME));
		}

		if (arguments.hasOption(SimulatorOptions.NO_SAVE_ALL)) {
			simulationData.setSerializationMode(simulationData.MODE_NONE);
		}

		if (arguments.hasOption(SimulatorOptions.SAVE_ALL)) {
			simulationData.setSerializationFileName(arguments
					.getValue(SimulatorOptions.SAVE_ALL));
		}
		if (arguments.hasOption(SimulatorOptions.DONT_COMPRESS_STORIES)) {
			simulationData.setStorifyMode(SimulationData.STORIFY_MODE_NONE);
		}
		if (arguments.hasOption(SimulatorOptions.COMPRESS_STORIES)) {
			simulationData.setStorifyMode(SimulationData.STORIFY_MODE_WEAK);
		}
		if (arguments.hasOption(SimulatorOptions.USE_STRONG_COMPRESSION)) {
			simulationData.setStorifyMode(SimulationData.STORIFY_MODE_STRONG);
		}

		return arguments;
	}

	public static final void readSimulatonFile(Simulator simulator,
			SimulatorArguments options) {

		SimulationData simulationData = simulator.getSimulationData();
		boolean option = false;
		String fileName = null;
		double timeSim = 0.;
		String snapshotTime;

		if (options.hasOption(SimulatorOptions.STORIFY)) {
			fileName = options.getValue(SimulatorOptions.STORIFY);
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_STORIFY);
			option = true;
		}
		if (options.hasOption(SimulatorOptions.TIME)) {
			try {
				timeSim = Double.valueOf(options
						.getValue(SimulatorOptions.TIME));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			simulationData.setTimeLength(timeSim);
		} else
			Simulator.println("*Warning* No time limit.");

		if (!option && (options.hasOption(SimulatorOptions.SIMULATIONFILE))) {
			option = true;
			fileName = options.getValue(SimulatorOptions.SIMULATIONFILE);
			if (options.hasOption(SimulatorOptions.SNAPSHOT_TIME)) {
				option = true;
				try {
					snapshotTime = options.getValue(SimulatorOptions.SNAPSHOT_TIME);
					simulationData.setSnapshotTime(snapshotTime);
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_SIM);
		}
		if (options.hasOption(SimulatorOptions.COMPILE)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.COMPILE);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_COMPILE);
		}

		if (options.hasOption(SimulatorOptions.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.GENERATE_MAP);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_GENERATE_MAP);
		}

		if (options.hasOption(SimulatorOptions.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = options.getValue(SimulatorOptions.CONTACT_MAP);
			} else
				option = false;
			simulationData
					.setSimulationType(SimulationData.SIMULATION_TYPE_CONTACT_MAP);
		}

		if (simulationData.getSimulationType() == SimulationData.SIMULATION_TYPE_NONE) {
			// HelpFormatter formatter = new HelpFormatter();
			// formatter.printHelp("use --sim [file]", cmdLineOptions);
			throw new IllegalArgumentException("No option specified");
		}

		simulationData.setInputFile(fileName);
		DataReading data = new DataReading(fileName);
		try {
			if (simulationData.getSimulationType() == SimulationData.SIMULATION_TYPE_CONTACT_MAP) {
				if (options.hasOption(SimulatorOptions.FOCUS_ON)) {
					String fileNameFocusOn = options
							.getValue(SimulatorOptions.FOCUS_ON);
					simulationData.setFocusOn(fileNameFocusOn, simulator);
				}
			}
			data.readData();
			Parser parser = new Parser(data, simulationData, simulator);
			parser.setForwarding(options.hasOption(SimulatorOptions.FORWARD));
			parser.parse();
		} catch (Exception e) {
			Simulator.println("Error in file \"" + fileName + "\" :");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}
	}

}
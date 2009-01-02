package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CContactMap;
import com.plectix.simulator.components.CContactMapChangedSite;
import com.plectix.simulator.components.CContactMapEdge;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.CStoryTrees;
import com.plectix.simulator.components.CStoryType;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.SnapshotElement;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.interfaces.IAction;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IInjection;
import com.plectix.simulator.interfaces.IObservables;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.parser.DataReading;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;

public class SimulationData {
	
	public final static byte MODE_SAVE = 2;
	public final static byte MODE_READ = 1;
	public final static byte MODE_NONE = 0;
	
	public final static byte STORIFY_MODE_NONE = 0;
	public final static byte STORIFY_MODE_WEAK = 1;
	public final static byte STORIFY_MODE_STRONG = 2;

	public final static byte SIMULATION_TYPE_NONE = -1;
	public final static byte SIMULATION_TYPE_COMPILE = 0;
	public final static byte SIMULATION_TYPE_STORIFY = 1;
	public final static byte SIMULATION_TYPE_SIM = 2;
	public final static byte SIMULATION_TYPE_AVERAGE_OF_RUNS = 3;
	public final static byte SIMULATION_TYPE_GENERATE_MAP = 4;
	public final static byte SIMULATION_TYPE_CONTACT_MAP = 5;
	
	private final static String TYPE_NEGATIVE_MAP = "NEGATIVE";
	private final static String TYPE_POSITIVE_MAP = "POSITIVE";

	private static final double DEFAULT_NUMBER_OF_POINTS = 1000;

	private List<Double> timeStamps;
	private List<List<RunningMetric>> runningMetrics;

	private List<IRule> rules;
	private CStories stories = null;
	private List<CPerturbation> perturbations;
	private IObservables observables = new CObservables();
	private List<CSnapshot> snapshots = null;
	private ISolution solution = new CSolution(); // soup of initial components

	private CContactMap contactMap = new CContactMap();

	private List<Info> infoList = new ArrayList<Info>();

	private byte simulationType = SIMULATION_TYPE_NONE;
	private byte storifyMode = STORIFY_MODE_NONE;

	private String tmpSessionName = "simplx.tmp";
	private String commandLineString;

	private PrintStream printStream = null;
	
	private List<Double> snapshotTimes;
	private long clockStamp;

	private double step;
	private double nextStep;
	private byte serializationMode = MODE_NONE;
	
	private int agentIdGenerator = 0;

	private boolean argumentsSet = false;
	private SimulationArguments simulationArguments = new SimulationArguments(); 
		
	public SimulationData() {
		super();
	}
	
	public final void resetSimulation() {
		addInfo(Info.TYPE_INFO, "-Reset simulation data.");
		addInfo(Info.TYPE_INFO, "-Initialization...");
		
		PlxTimer timer = new PlxTimer();
		timer.startTimer();
		
		rules.clear();
		observables.resetLists();
		solution.clearAgents();
		solution.clearSolutionLines();

		if (perturbations != null) {
			perturbations.clear();
		}

		if (serializationMode != SimulationData.MODE_READ) {
			readSimulatonFile();
		}
		
		initialize();
		
		stopTimer(timer, "-Initialization:");
		setClockStamp(System.currentTimeMillis());
	}

	public final boolean isOcamlStyleObsName() {
		return simulationArguments.isOcamlStyleObservableNames();
	}

	public final long generateNextAgentId() {
		return agentIdGenerator++;
	}
	
	public final void parseArguments(String[] args) throws IllegalArgumentException {
		// let's get the original command line before we change it:
		setCommandLineString(args);

		// let's replace all '-' by '_' 
		args = SimulationUtils.changeArguments(args);

		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(args);
		} catch (ParseException e) {
			println("Error parsing arguments:");
			if (printStream != null) {
				e.printStackTrace(printStream);
			}
			throw new IllegalArgumentException(e);
		}


		if (commandLine.hasOption(SimulatorOptions.NO_DUMP_STDOUT_STDERR)) {
			printStream = null;
		}

		if (commandLine.hasOption(SimulatorOptions.HELP)) {
			if (printStream != null) {            
				PrintWriter printWriter = new PrintWriter(printStream);
				HelpFormatter formatter = new HelpFormatter(); 
				formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, 
						SimulationMain.COMMAND_LINE_SYNTAX, null, 
						SimulatorOptions.COMMAND_LINE_OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, 
						HelpFormatter.DEFAULT_DESC_PAD, null, false);
				printWriter.flush();
			}
		}

		if (commandLine.hasOption(SimulatorOptions.VERSION)) {
			println("Java simulator SVN Revision: " + BuildConstants.BUILD_SVN_REVISION);
		}

		// let's dump the command line arguments
		println("Java " + commandLineString);

		// moved this below since the line above might have turned the printing off...
		addInfo(Info.TYPE_INFO, "-Initialization...");

		if (commandLine.hasOption(SimulatorOptions.XML_SESSION_NAME)) {
			simulationArguments.setXmlSessionName(commandLine.getValue(SimulatorOptions.XML_SESSION_NAME));
		}

		if (commandLine.hasOption(SimulatorOptions.OUTPUT_XML)) {
			simulationArguments.setXmlSessionName(commandLine.getValue(SimulatorOptions.OUTPUT_XML));
		}
		
		if (commandLine.hasOption(SimulatorOptions.INIT)) {
			simulationArguments.setInitialTime(Double.valueOf(commandLine.getValue(SimulatorOptions.INIT)));
		}

		if (commandLine.hasOption(SimulatorOptions.POINTS)) {
			simulationArguments.setPoints(Integer.valueOf(commandLine.getValue(SimulatorOptions.POINTS)));
		}

		if (commandLine.hasOption(SimulatorOptions.RESCALE)) {
			double rescale = Double.valueOf(commandLine.getValue(SimulatorOptions.RESCALE));
			if (rescale > 0) {
				simulationArguments.setRescale(rescale);
			} else {
				throw new IllegalArgumentException("Negative rescale value: " + rescale);
			}
		}

		if (commandLine.hasOption(SimulatorOptions.NO_SEED)) {
			simulationArguments.setSeed(0);
		}

		// TODO else?
		if (commandLine.hasOption(SimulatorOptions.SEED)) {
			int seed = Integer.valueOf(commandLine.getValue(SimulatorOptions.SEED));
			simulationArguments.setSeed(seed);
		}

		if (commandLine.hasOption(SimulatorOptions.MAX_CLASHES)) {
			int max_clashes = Integer.valueOf(commandLine.getValue(SimulatorOptions.MAX_CLASHES));
			setMaxClashes(max_clashes);
		}

		if (commandLine.hasOption(SimulatorOptions.EVENT)) {
			long event = Long.valueOf(commandLine.getValue(SimulatorOptions.EVENT));
			setEvent(event);
		}

		if (commandLine.hasOption(SimulatorOptions.ITERATION)) {
			simulationArguments.setIterations(Integer.valueOf(commandLine.getValue(SimulatorOptions.ITERATION)));
		}


		if (commandLine.hasOption(SimulatorOptions.RANDOMIZER_JAVA)) {
			simulationArguments.setRandomizer(commandLine.getValue(SimulatorOptions.RANDOMIZER_JAVA));
		}

		if (commandLine.hasOption(SimulatorOptions.NO_ACTIVATION_MAP)
				|| (commandLine.hasOption(SimulatorOptions.NO_MAPS))
				|| (commandLine.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setActivationMap(false);
		}

		if (commandLine.hasOption(SimulatorOptions.MERGE_MAPS)) {
			simulationArguments.setInhibitionMap(true);
		}
		
		if (commandLine.hasOption(SimulatorOptions.NO_INHIBITION_MAP)
				|| (commandLine.hasOption(SimulatorOptions.NO_MAPS))
				|| (commandLine.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
			simulationArguments.setInhibitionMap(false);
		}

		if (commandLine.hasOption(SimulatorOptions.COMPILE)) {
			simulationArguments.setCompile(true);
		}

		if (commandLine.hasOption(SimulatorOptions.DEBUG_INIT)) {
			simulationArguments.setDebugInitOption(true);
		}

		if (commandLine.hasOption(SimulatorOptions.GENERATE_MAP)) {
			simulationArguments.setGenereteMapOption(true);
		}

		if (commandLine.hasOption(SimulatorOptions.CONTACT_MAP)) {
			simulationArguments.setContactMapOption(true);
		}

		if (commandLine.hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			simulationArguments.setNumberOfRunsOption(true);
		}

		if (commandLine.hasOption(SimulatorOptions.STORIFY)) {
			simulationArguments.setStorifyOption(true);
		}

		if (commandLine.hasOption(SimulatorOptions.OCAML_STYLE_OBS_NAME)) {
			simulationArguments.setOcamlStyleObservableNames(true);
		}

		if (commandLine.hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
			simulationArguments.setIterations(Integer.valueOf(commandLine.getValue(SimulatorOptions.NUMBER_OF_RUNS)));

			if (!commandLine.hasOption(SimulatorOptions.SEED)) {
				throw new IllegalArgumentException("No SEED OPTION");
			}

			this.simulationType = SIMULATION_TYPE_AVERAGE_OF_RUNS;
		}

		if (commandLine.hasOption(SimulatorOptions.CLOCK_PRECISION)) {
			simulationArguments.setClockPrecision(60000 * Long.valueOf(commandLine.getValue(SimulatorOptions.CLOCK_PRECISION)));
		}

		if (commandLine.hasOption(SimulatorOptions.OUTPUT_FINAL_STATE)) {
			simulationArguments.setOutputFinalState(true);
		}

		if (commandLine.hasOption(SimulatorOptions.OUTPUT_SCHEME)) {
			simulationArguments.setXmlSessionPath(commandLine.getValue(SimulatorOptions.OUTPUT_SCHEME));
		}

		if (commandLine.hasOption(SimulatorOptions.NO_SAVE_ALL)) {
			serializationMode = MODE_NONE;
		}

		if (commandLine.hasOption(SimulatorOptions.SAVE_ALL)) {
			simulationArguments.setSerializationFileName(commandLine.getValue(SimulatorOptions.SAVE_ALL)) ;
		}

		if (commandLine.hasOption(SimulatorOptions.DONT_COMPRESS_STORIES)) {
			storifyMode = STORIFY_MODE_NONE;
		}

		if (commandLine.hasOption(SimulatorOptions.COMPRESS_STORIES)) {
			storifyMode = STORIFY_MODE_WEAK;
		}

		if (commandLine.hasOption(SimulatorOptions.USE_STRONG_COMPRESSION)) {
			storifyMode = STORIFY_MODE_STRONG;
		}

		if (commandLine.hasOption(SimulatorOptions.TIME)) {
			setTimeLength(Double.valueOf(commandLine.getValue(SimulatorOptions.TIME)));
		} else {
			println("*Warning* No time limit.");
		}
		
		boolean option = false;
		String fileName = null;
		
		if (commandLine.hasOption(SimulatorOptions.STORIFY)) {
			fileName = commandLine.getValue(SimulatorOptions.STORIFY);
			this.simulationType = SIMULATION_TYPE_STORIFY;
			option = true;
		}
		
	
		if (!option && (commandLine.hasOption(SimulatorOptions.SIMULATIONFILE))) {
			option = true;
			fileName = commandLine.getValue(SimulatorOptions.SIMULATIONFILE);
			if (commandLine.hasOption(SimulatorOptions.SNAPSHOT_TIME)) {
				option = true;
				try {
					simulationArguments.setSnapshotsTimeString(commandLine.getValue(SimulatorOptions.SNAPSHOT_TIME));
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			this.simulationType = SIMULATION_TYPE_SIM;
		}
		
		if (commandLine.hasOption(SimulatorOptions.COMPILE)) {
			if (!option) {
				option = true;
				fileName = commandLine.getValue(SimulatorOptions.COMPILE);
			} else {
				option = false;
			}
			this.simulationType = SIMULATION_TYPE_COMPILE;
		}
	
		if (commandLine.hasOption(SimulatorOptions.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = commandLine.getValue(SimulatorOptions.GENERATE_MAP);
			} else {
				option = false;
			}
			
			this.simulationType = SIMULATION_TYPE_GENERATE_MAP;
		}
	
		if (commandLine.hasOption(SimulatorOptions.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = commandLine.getValue(SimulatorOptions.CONTACT_MAP);
			} else {
				option = false;
			}
			
			this.simulationType = SIMULATION_TYPE_CONTACT_MAP;
		}
	
		if (simulationType == SIMULATION_TYPE_NONE) {
			// HelpFormatter formatter = new HelpFormatter();
			// formatter.printHelp("use --sim [file]", cmdLineOptions);
			throw new IllegalArgumentException("No option specified");
		}
	
		simulationArguments.setInputFile(fileName);
		
		if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			if (commandLine.hasOption(SimulatorOptions.FOCUS_ON)) {
				simulationArguments.setFocusFilename(commandLine.getValue(SimulatorOptions.FOCUS_ON));
			}
		}
		
		simulationArguments.setForwardOption(commandLine.hasOption(SimulatorOptions.FORWARD));
		
		this.argumentsSet = true;
	}

	public final void readSimulatonFile() {
		if (!this.argumentsSet) {
			throw new RuntimeException("Simulator Arguments must be set before reading the simulation file!");
		}
		
		// TODO: move the following lines to a reset method. they should not be part of reading the simulation file!
		// They are here now because this method used to parse arguments!
		resetBar();
		observables.setOcamlStyleObsName(simulationArguments.isOcamlStyleObservableNames());
		if (simulationArguments.getSnapshotsTimeString() != null) {
			setSnapshotTime(simulationArguments.getSnapshotsTimeString());
		}
		
		try {
			DataReading data = new DataReading(simulationArguments.getInputFile());
			
			if (simulationArguments.getFocusFilename() != null) {
				setFocusOn(simulationArguments.getFocusFilename());
			}
		
			data.readData();
			
			Parser parser = new Parser(data, this);
			parser.setForwarding(simulationArguments.isForwardOption());
			parser.parse();
		} catch (Exception e) {
			println("Error in file \"" + simulationArguments.getInputFile() + "\" :");

			if (printStream != null) {
				e.printStackTrace(printStream);
			}
			throw new IllegalArgumentException(e);
		}
	}
	
	public final boolean isParseSolution() {
		if (simulationType == SIMULATION_TYPE_GENERATE_MAP) {
			return false;
		}
		return true;
	}

	public final void addInfo(byte type, String message) {
		addInfo(new Info(type, message, printStream));
	}
	
	private final void addInfo(Info info) {
		for (Info inf : infoList) {
			if (inf.getMessageWithoutTime().equals(info.getMessageWithoutTime())) {
				inf.upCount(info.getTime());
				return;
			}
		}

		infoList.add(info);
	}

	private final void setCommandLineString(String[] args) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			stringBuffer.append(args[i] + " ");
		}
		this.commandLineString = stringBuffer.toString();
	}

	private final void setMaxClashes(long max_clashes) {
		if (max_clashes > 0) {
			simulationArguments.setMaxClashes(max_clashes);
		} else {
			throw new IllegalArgumentException("Can't set negative max_clashes: " + max_clashes);
		}
	}

	public final boolean isEndSimulation(double currentTime, long count) {
		long curClockTime = System.currentTimeMillis();
		if (curClockTime - clockStamp > simulationArguments.getClockPrecision()) {
			println("simulation interrupted because the clock time has expired");
			return true;
		}
		
		if (simulationArguments.isTime()) {
			if (currentTime <= simulationArguments.getTimeLength()) {
				if (currentTime >= nextStep) {
					print("#");
					nextStep += step;
				}
				return false;
			} else {
				println("#");
				return true;
			}
		} else if (count <= simulationArguments.getEvent()) {
			if (count >= nextStep) {
				print("#");
				nextStep += step;
			}
			return false;
		} else {
			println("#");
			return true;
		}
	}

	public final void addStories(String name) {
		byte index = 0;
		List<Integer> ruleIDs = new ArrayList<Integer>();
		for (IRule rule : rules) {
			if ((rule.getName() != null)
					&& (rule.getName().startsWith(name) && ((name.length() == rule
							.getName().length()) || ((rule.getName()
							.startsWith(name + "_op")) && ((name.length() + 3) == rule
							.getName().length()))))) {
				ruleIDs.add(rule.getRuleID());
				index++;
			}
			if (index == 2) {
				this.stories.addToStories(ruleIDs);
				return;
			}
		}
		this.stories.addToStories(ruleIDs);
	}

	public final boolean isStorify() {
		return (simulationType == SIMULATION_TYPE_STORIFY);
	}

	public final void setTimeLength(double timeLength) {
		simulationArguments.setTimeLength(timeLength);
		step = timeLength / 100;
		nextStep = step;
		simulationArguments.setTime(true);
	}

	public final void addRule(CRule rule) {
		rules.add(rule);
	}

	public final List<IRule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	public final void resetBar() {
		nextStep = step;
	}

	public final void setEvent(long event) {
		step = event / 100;
		nextStep = step;
		simulationArguments.setEvent(event);
	}

	public final void initIterations(List<Double> timeStamps, List<List<RunningMetric>> runningMetrics) {
		this.timeStamps = timeStamps;
		this.runningMetrics = runningMetrics;
		int observable_num = observables.getComponentListForXMLOutput().size();
		// int observable_num = observables.getComponentList().size();
		for (int i = 0; i < observable_num; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}

	}

	public final DOMSource createDOMModel()
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		PlxTimer timer = new PlxTimer();
		Element simplxSession = null;
		if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			simplxSession = doc.createElement("ComplxSession");
			simplxSession.setAttribute("xsi:schemaLocation",
					"http://synthesisstudios.com ComplxSession.xsd");

			// "unexpected element (uri:"", local:"ComplxSession
			// "). Expected elements are <{http://plectix.synthesisstudios.com/schemas/kappasession}ComplxSession>,<{http://plectix.synthesisstudios.com/schemas/kappasession}KappaResults>,<{http://plectix.synthesisstudios.com/schemas/kappasession}SimplxSession>"
			Element element = doc.createElement("Refinement");
			element.setAttribute("Name", "DAG");
			simplxSession.appendChild(element);
			element = doc.createElement("Refinement");
			element.setAttribute("Name", "Maximal");
			simplxSession.appendChild(element);

			Element ruleSet = doc.createElement("RuleSet");
			ruleSet.setAttribute("Name", "Original");
			addRulesToXML(ruleSet, rules.size(), doc);
			simplxSession.appendChild(ruleSet);
		} else {
			simplxSession = doc.createElement("SimplxSession");
			simplxSession.setAttribute("xsi:schemaLocation",
					"http://plectix.synthesisstudios.com SimplxSession.xsd");
		}
		simplxSession.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		simplxSession.setAttribute("xmlns",
				"http://plectix.synthesisstudios.com/schemas/kappasession");
		simplxSession.setAttribute("CommandLine", commandLineString);
		simplxSession.setAttribute("InputFile", simulationArguments.getInputFile());
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simplxSession.setAttribute("TimeStamp", df.format(d));
		doc.appendChild(simplxSession);

		timer.startTimer();

		if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			Element contactMapElement = doc.createElement("ContactMap");
			contactMapElement.setAttribute("Name", "Low resolution");

			Map<Integer, Map<Integer, CContactMapChangedSite>> agentsInContactMap = this.contactMap
					.getAgentsInContactMap();
			Map<Integer, Map<Integer, List<CContactMapEdge>>> bondsInContactMap = this.contactMap
					.getBondsInContactMap();

			List<Integer> agentIDWasRead = new ArrayList<Integer>();

			Iterator<Integer> agentIterator = agentsInContactMap.keySet()
					.iterator();

			while (agentIterator.hasNext()) {
				Element agent = doc.createElement("Agent");
				int agentKey = agentIterator.next();
				Map<Integer, CContactMapChangedSite> sitesMap = agentsInContactMap
						.get(agentKey);
				Iterator<Integer> siteIterator = sitesMap.keySet().iterator();
				int siteKey = siteIterator.next();
				CContactMapChangedSite chSite = sitesMap.get(siteKey);
				agent.setAttribute("Name", chSite.getSite().getAgentLink()
						.getName());
				addSiteToContactMapAgent(chSite, agent, doc);

				while (siteIterator.hasNext()) {
					siteKey = siteIterator.next();
					chSite = sitesMap.get(siteKey);
					addSiteToContactMapAgent(chSite, agent, doc);
				}
				contactMapElement.appendChild(agent);
			}

			agentIterator = bondsInContactMap.keySet().iterator();
			while (agentIterator.hasNext()) {
				int agentKey = agentIterator.next();
				agentIDWasRead.add(agentKey);
				Map<Integer, List<CContactMapEdge>> edgesMap = bondsInContactMap
						.get(agentKey);
				Iterator<Integer> siteIterator = edgesMap.keySet().iterator();
				while (siteIterator.hasNext()) {
					int siteKey = siteIterator.next();
					List<CContactMapEdge> edgesList = edgesMap.get(siteKey);
					for (CContactMapEdge edge : edgesList) {
						Element bond = doc.createElement("Bond");
						ISite vertexTo = edge.getVertexTo();
						ISite vertexFrom = edge.getVertexFrom();
						if (!agentIDWasRead.contains(vertexTo.getAgentLink()
								.getNameId())) {

							bond.setAttribute("FromAgent", vertexFrom
									.getAgentLink().getName());
							bond.setAttribute("FromSite", vertexFrom.getName());
							bond.setAttribute("ToAgent", vertexTo
									.getAgentLink().getName());
							bond.setAttribute("ToSite", vertexTo.getName());

							if (edge.getRules().size() != 0) {
								for (int ruleID : edge.getRules()) {
									Element rule = doc.createElement("Rule");
									rule.setAttribute("Id", Integer
											.toString(ruleID));
									bond.appendChild(rule);
								}
							}
							contactMapElement.appendChild(bond);
						}
					}
				}
			}
			simplxSession.appendChild(contactMapElement);
		}

		if (simulationArguments.isActivationMap()) {
			Element influenceMap = doc.createElement("InfluenceMap");

			List<IObservablesConnectedComponent> obsCCList = observables
					.getConnectedComponentListForXMLOutput();
			int rulesAndObsNumber = obsCCList.size() + rules.size();
			/**
			 * add observables
			 * */
			for (int i = obsCCList.size() - 1; i >= 0; i--) {
				IObservablesConnectedComponent obsCC = obsCCList.get(i);
				Element node = doc.createElement("Node");
				node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
				node.setAttribute("Type", "OBSERVABLE");

				String obsName = obsCC.getName();

				if (obsName == null)
					obsName = obsCC.getLine();

				node.setAttribute("Text", '[' + obsName + ']');
				node.setAttribute("Data", obsCC.getLine());
				node.setAttribute("Name", '[' + obsName + ']');
				influenceMap.appendChild(node);
			}

			/**
			 * add rules
			 * */
			addRulesToXML(influenceMap, rulesAndObsNumber, doc);
			// for (int i = rules.size() - 1; i >= 0; i--) {
			// Element node = doc.createElement("Node");
			// node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
			// node.setAttribute("Type", "RULE");
			// node.setAttribute("Text", rules.get(i).getName());
			// node.setAttribute("Data", rules.get(i).getData(
			// isOcamlStyleObsName()));
			// node.setAttribute("Name", rules.get(i).getName());
			// influenceMap.appendChild(node);
			// }

			/**
			 * add activation map
			 * */

			for (int i = rules.size() - 1; i >= 0; i--)
				printMap(doc, TYPE_POSITIVE_MAP, influenceMap, rules.get(i),
						rules.get(i).getActivatedRuleForXMLOutput(), rules.get(
								i).getActivatedObservableForXMLOutput());
			if (simulationArguments.isInhibitionMap()) {
				for (int i = rules.size() - 1; i >= 0; i--)
					printMap(doc, TYPE_NEGATIVE_MAP, influenceMap,
							rules.get(i), rules.get(i).getInhibitedRule(),
							rules.get(i).getInhibitedObservable());
			}
			simplxSession.appendChild(influenceMap);
			stopTimer(timer, "-Building xml tree for influence map:");
		}

		if (simulationType == SIMULATION_TYPE_STORIFY) {

			for (List<CStoryTrees> stList : stories.getTrees()) {
				for (CStoryTrees st : stList) {
					Element story = doc.createElement("Story");
					story.setAttribute("Observable", rules.get(st.getRuleID())
							.getName());
					double percentage = ((double) st.getIsomorphicCount())
							/ (double) simulationArguments.getIterations();
					story.setAttribute("Percentage", Double
							.toString(percentage * 100));
					story.setAttribute("Average", Double.toString(st
							.getAverageTime()
							/ st.getIsomorphicCount()));
					addConnection(story, st, doc, st.getRuleID());
					simplxSession.appendChild(story);
				}
			}

		}

		if (snapshots != null) {
			timer.startTimer();
			for (CSnapshot snapshot : snapshots) {
				Element snapshotElement = doc.createElement("FinalState");
				snapshotElement.setAttribute("Time", String.valueOf(snapshot
						.getSnapshotTime()));
				List<SnapshotElement> snapshotElementList = snapshot
						.getSnapshotElements();
				for (SnapshotElement se : snapshotElementList) {
					Element species = doc.createElement("Species");
					species.setAttribute("Kappa", se.getCcName());
					species.setAttribute("Number", String
							.valueOf(se.getCount()));
					snapshotElement.appendChild(species);
				}
				simplxSession.appendChild(snapshotElement);
			}
			stopTimer(timer, "-Building xml tree for snapshots:");
		}

		if (simulationType == SIMULATION_TYPE_SIM) {
			int obsCountTimeListSize = observables.getCountTimeList().size();
			Element simulation = doc.createElement("Simulation");
			simulation.setAttribute("TotalEvents", Long.toString(simulationArguments.getEvent()));
			simulation.setAttribute("TotalTime", Double.toString(simulationArguments.getTimeLength()));
			simulation.setAttribute("InitTime", Double.toString(simulationArguments.getInitialTime()));

			simulation.setAttribute("TimeSample", Double.valueOf(
					observables.getTimeSampleMin()).toString());
			simplxSession.appendChild(simulation);

			List<IObservablesComponent> list = observables
					.getComponentListForXMLOutput();
			for (int i = list.size() - 1; i >= 0; i--) {
				Element node = createElement(list.get(i), doc);
				simulation.appendChild(node);
			}

			timer.startTimer();
			Element csv = doc.createElement("CSV");
			CDATASection cdata = doc.createCDATASection("\n");

			for (int i = 0; i < obsCountTimeListSize; i++) {
				appendData(observables, list, cdata, i);
			}

			csv.appendChild(cdata);
			simulation.appendChild(csv);
			stopTimer(timer, "-Building xml tree for data points:");
		}

		appendInfo(simplxSession, doc);

		DOMSource domSource = new DOMSource(doc);
		return domSource;
	}

	private final void addRulesToXML(Element influenceMap, int rulesAndObsNumber,
			Document doc) {
		for (int i = rules.size() - 1; i >= 0; i--) {
			Element node = null;
			if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
				node = doc.createElement("Rule");
				node.setAttribute("Id", Integer.toString(rules.get(i)
						.getRuleID() + 1));
			} else {
				node = doc.createElement("Node");
				node.setAttribute("Type", "RULE");
				node.setAttribute("Text", rules.get(i).getName());
				node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
			}
			node.setAttribute("Data", rules.get(i).getData(
					isOcamlStyleObsName()));
			node.setAttribute("Name", rules.get(i).getName());
			influenceMap.appendChild(node);
		}
	}

	private final void addSiteToContactMapAgent(CContactMapChangedSite site,
			Element agent, Document doc) {
		Element siteNode = doc.createElement("Site");
		for (Integer ruleID : site.getUsedRuleIDs()) {
			Element siteRule = doc.createElement("Rule");
			siteRule.setAttribute("Id", Integer.toString(ruleID));
			siteNode.appendChild(siteRule);
		}
		siteNode.setAttribute("Name", site.getSite().getName());
		siteNode.setAttribute("CanChangeState", Boolean.toString(site
				.isInternalState()));
		siteNode.setAttribute("CanBeBound", Boolean
				.toString(site.isLinkState()));
		agent.appendChild(siteNode);
	}

	private final void writeToXML(Source source, PlxTimer timerOutput)
			throws ParserConfigurationException, TransformerException {
		TransformerFactory trFactory = TransformerFactory.newInstance();
		Transformer transformer = trFactory.newTransformer();
		stopTimer(timerOutput, "-Results outputted in xml session:");
		StreamResult streamesult = new StreamResult(getXmlSessionPath());
		Properties pr = new Properties();
		pr.setProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperties(pr);
		transformer.transform(source, streamesult);
	}

	private final void printMap(Document doc, String mapType,
			Element influenceMap, IRule rule, List<IRule> rulesToPrint,
			List<IObservablesConnectedComponent> obsToPrint) {
		int rulesNumber = rules.size() + 1;
		for (int j = obsToPrint.size() - 1; j >= 0; j--) {
			Element node = doc.createElement("Connection");
			node.setAttribute("FromNode", Integer
					.toString(rule.getRuleID() + 1));
			node.setAttribute("ToNode", Integer.toString(obsToPrint.get(j)
					.getNameID()
					+ rulesNumber));
			node.setAttribute("Relation", mapType);
			influenceMap.appendChild(node);
		}
		for (int j = rulesToPrint.size() - 1; j >= 0; j--) {
			Element node = doc.createElement("Connection");
			node.setAttribute("FromNode", Integer
					.toString(rule.getRuleID() + 1));
			node.setAttribute("ToNode", Integer.toString(rulesToPrint.get(j)
					.getRuleID() + 1));
			node.setAttribute("Relation", mapType);
			influenceMap.appendChild(node);
		}

	}

	private final void fillNodesLevelStoryTrees(
			List<CStoryType> currentStTypeList, List<Element> nodes,
			Document doc) {
		Element node;
		for (CStoryType stT : currentStTypeList) {
			if (stT.getType() == CStoryType.TYPE_OBS) {
				node = doc.createElement("Node");
				stT.fillNode(node, CStoryType.STRING_OBS);
				nodes.add(node);
			} else if (stT.getType() == CStoryType.TYPE_RULE) {
				node = doc.createElement("Node");
				stT.fillNode(node, CStoryType.STRING_RULE);
				nodes.add(node);
			} else {
				node = doc.createElement("Node");
				stT.fillNode(node, CStoryType.STRING_INTRO);
				nodes.add(node);
			}
		}
	}

	private final void fillIntroListStoryConnections(CStoryType toStoryType,
			List<CStoryType> introList, List<Element> connections, Document doc) {
		if (introList != null) {
			Element node;
			for (CStoryType stT : introList) {
				node = doc.createElement("Connection");
				stT.fillConnection(node, toStoryType.getId(),
						CStoryType.RELATION_STRONG);
				connections.add(node);
			}
		}
	}

	private final void fillRuleListStoryConnections(CStoryType toStoryType,
			HashMap<Integer, CStoryType> traceIdToStoryTypeRule,
			List<Integer> rIDs, List<Element> connections, Document doc,
			String relationType) {
		if (rIDs.size() != 0) {
			Element node;
			for (int rID : rIDs) {
				CStoryType st = traceIdToStoryTypeRule.get(rID);
				node = doc.createElement("Connection");
				if (st == null) {
					println();
				}
				st.fillConnection(node, toStoryType.getId(), relationType);
				connections.add(node);
			}
		}
	}

	private final void addConnection(Element story, CStoryTrees storyTree,
			Document doc, int item) {

		HashMap<Integer, List<CStoryType>> allLevels = new HashMap<Integer, List<CStoryType>>();

		HashMap<Integer, List<CStoryType>> traceIdToStoryTypeIntro = new HashMap<Integer, List<CStoryType>>();
		HashMap<Integer, CStoryType> traceIdToStoryTypeRule = new HashMap<Integer, CStoryType>();

		int counter = 0;

		Iterator<Integer> iterator = storyTree.getLevelToTraceID().keySet()
				.iterator();
		int depth = storyTree.getLevelToTraceID().size();

		while (iterator.hasNext()) {
			int level = iterator.next();
			List<Integer> list = storyTree.getLevelToTraceID().get(level);
			List<CStoryType> listST = new ArrayList<CStoryType>();
			allLevels.put(level, listST);
			for (Integer traceID : list) {
				List<String> introStringList = storyTree
						.getTraceIDToIntroString().get(traceID);
				List<CStoryType> introList = traceIdToStoryTypeIntro
						.get(traceID);

				if (introStringList != null) {
					if (introList == null) {
						introList = new ArrayList<CStoryType>();
						traceIdToStoryTypeIntro.put(traceID, introList);
					}
					for (String str : introStringList) {
						CStoryType stT = new CStoryType(CStoryType.TYPE_INTRO,
								traceID, counter++, "intro:" + str, "", depth
										- level - 1);
						listST.add(stT);
						introList.add(stT);
					}
				}
			}
		}
		iterator = storyTree.getLevelToTraceID().keySet().iterator();
		int traceIDSize = storyTree.getTraceIDToLevel().size() + counter - 1;
		while (iterator.hasNext()) {
			int level = iterator.next();
			List<Integer> list = storyTree.getLevelToTraceID().get(level);
			List<CStoryType> listST = allLevels.get(level);
			byte type;
			if (level == 0)
				type = CStoryType.TYPE_OBS;
			else
				type = CStoryType.TYPE_RULE;

			if (listST == null) {
				listST = new ArrayList<CStoryType>();
				allLevels.put(level, listST);
			}
			for (Integer traceID : list) {
				CStoryType storyType;
				if (storifyMode == STORIFY_MODE_NONE)
					storyType = new CStoryType(type, traceID,
							counter + traceID, storyTree.getTraceIDToText()
									.get(traceID), storyTree.getTraceIDToData()
									.get(traceID), depth - level);
				else
					storyType = new CStoryType(type, traceID, traceIDSize--,
							storyTree.getTraceIDToText().get(traceID),
							storyTree.getTraceIDToData().get(traceID), depth
									- level);
				listST.add(storyType);

				CStoryType rule = traceIdToStoryTypeRule.get(traceID);

				if (rule == null) {
					rule = storyType;
					traceIdToStoryTypeRule.put(traceID, rule);
				}
			}
		}

		List<Element> nodes = new ArrayList<Element>();
		List<Element> connections = new ArrayList<Element>();
		TreeMap<Integer, List<Integer>> traceIDToTraceIDs = storyTree
				.getTraceIDToTraceID();
		TreeMap<Integer, List<Integer>> traceIDToTraceIDsWeak = storyTree
				.getTraceIDToTraceIDWeak();

		List<CStoryType> currentStTypeList = new ArrayList<CStoryType>();
		iterator = allLevels.keySet().iterator();

		while (iterator.hasNext()) {
			int curKey = iterator.next();
			currentStTypeList = allLevels.get(curKey);
			fillNodesLevelStoryTrees(currentStTypeList, nodes, doc);

			for (CStoryType stT : currentStTypeList) {
				if (stT.getType() != CStoryType.TYPE_INTRO) {
					List<CStoryType> introList = traceIdToStoryTypeIntro
							.get(stT.getTraceID());
					fillIntroListStoryConnections(stT, introList, connections,
							doc);
					int trID = stT.getTraceID();
					List<Integer> rIDs = traceIDToTraceIDs.get(trID);
					fillRuleListStoryConnections(stT, traceIdToStoryTypeRule,
							rIDs, connections, doc, CStoryType.RELATION_STRONG);
					rIDs = traceIDToTraceIDsWeak.get(trID);
					if (rIDs != null)
						fillRuleListStoryConnections(stT,
								traceIdToStoryTypeRule, rIDs, connections, doc,
								CStoryType.RELATION_WEAK);
				}
			}
		}

		for (Element el : nodes)
			story.appendChild(el);
		for (Element el : connections)
			story.appendChild(el);
	}

	private final void appendInfo(Element simplxSession, Document doc) {

		Element log = doc.createElement("Log");

		for (Info info : infoList) {
			Element node = doc.createElement("Entry");
			node.setAttribute("Position", info.getPosition());
			node.setAttribute("Count", info.getCount());
			node.setAttribute("Message", info.getMessage());
			node.setAttribute("Type", info.getType());
			log.appendChild(node);
		}

		simplxSession.appendChild(log);
	}

	private final Element createElement(IObservablesComponent obs, Document doc) {
		Element node = doc.createElement("Plot");
		String obsName = obs.getName();
		if (obsName == null)
			obsName = obs.getLine();

		// TODO do otherway
		if (obs instanceof IObservablesConnectedComponent) {
			node.setAttribute("Type", "OBSERVABLE");
			node.setAttribute("Text", '[' + obsName + ']');
		} else {
			node.setAttribute("Type", "RULE");
			node.setAttribute("Text", obsName);
		}
		return node;
	}

	public final void stopTimer(PlxTimer timer, String message) {
		if (timer == null) {
			return;
		}
		timer.stopTimer();
		
		message += " ";
		println(message + timer.getTimeMessage() + " sec. CPU");
		// timer.getTimer();
		addInfo(new Info(Info.TYPE_INFO, message, timer.getThreadTimeInSeconds(), 1));
	}

	public final void createTMPReport() {
		// model.getSimulationData().updateData();
		PlxTimer timer = new PlxTimer();
		timer.startTimer();

		int number_of_observables = observables.getComponentListForXMLOutput()
				.size();
		try {
			for (int observable_num = 0; observable_num < number_of_observables; observable_num++) {
				int oCamlObservableNo = number_of_observables - observable_num
						- 1; // everything is backward with OCaml!
				BufferedWriter writer = new BufferedWriter(new FileWriter(tmpSessionName + "-" + oCamlObservableNo));

				double timeSampleMin = 0.;
				double timeNext = 0.;
				double fullTime = timeStamps.get(timeStamps.size() - 1);
				if (simulationArguments.getInitialTime() > 0.0) {
					timeNext = simulationArguments.getInitialTime();
					fullTime = fullTime - timeNext;
				} else
					timeNext = timeSampleMin;

				timeSampleMin = getTimeSampleMin(fullTime);

				for (int timeStepCounter = 0; timeStepCounter < timeStamps
						.size(); timeStepCounter++) {
					if (timeStamps.get(timeStepCounter) > timeNext) {
						timeNext += timeSampleMin;
						String st = timeStamps.get(timeStepCounter)
								+ " "
								+ runningMetrics.get(observable_num).get(
										timeStepCounter).getMin()
								+ " "
								+ runningMetrics.get(observable_num).get(
										timeStepCounter).getMax()
								+ " "
								+ runningMetrics.get(observable_num).get(
										timeStepCounter).getMean()
								+ " "
								+ runningMetrics.get(observable_num).get(
										timeStepCounter).getStd();

						writer.write(st);
						writer.newLine();
					}
				}

				writer.close();
			}
		} catch (IOException e) {
			if (printStream != null) {
				e.printStackTrace(printStream);
			}
		}

		println("-Results outputted in tmp session: "
				+ timer.getTimeMessage() + " sec. CPU");
	}

	private final double getTimeSampleMin(double fullTime) {
		if (simulationArguments.getPoints() > 0) {
			return (fullTime / simulationArguments.getPoints());
		} else {
			return (fullTime / DEFAULT_NUMBER_OF_POINTS);
		}
	}

	private final void appendData(IObservables obs, List<IObservablesComponent> list,
			CDATASection cdata, int index) {
		String enter = "\n";
		cdata.appendData(observables.getCountTimeList().get(index).toString());
		for (int j = list.size() - 1; j >= 0; j--) {
			cdata.appendData(",");
			IObservablesComponent oCC = list.get(j);
			cdata.appendData(getItem(obs, index, oCC));
		}
		cdata.appendData(enter);
	}

	private final String getItem(IObservables obs, int index,
			IObservablesComponent oCC) {
		if (oCC.isUnique())
			return oCC.getItem(index, obs);
		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList())
			if (cc.getNameID() == oCC.getNameID())
				value *= cc.getValue(index, obs);

		return Long.valueOf(value).toString();
	}

	private final String getXmlSessionPath() {
		if (simulationArguments.getXmlSessionPath().length() > 0) {
			return simulationArguments.getXmlSessionPath() + File.separator + simulationArguments.getXmlSessionName();
		} else {
			return simulationArguments.getXmlSessionName(); 
		}
	}


	public final void initialize() {

		if (serializationMode == MODE_READ) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(simulationArguments.getSerializationFileName()));
				solution = (CSolution) ois.readObject();
				rules = (List<IRule>) ois.readObject();
				observables = (IObservables) ois.readObject();
				perturbations = (List<CPerturbation>) ois.readObject();
				snapshotTimes = (List<Double>) ois.readObject();
				simulationArguments.setEvent((long) ois.readLong());
				simulationArguments.setTimeLength((double) ois.readDouble());
				ois.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (serializationMode == MODE_SAVE) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(simulationArguments.getSerializationFileName()));
				oos.writeObject(solution);
				oos.writeObject(rules);
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeObject(snapshotTimes);
				oos.writeLong(simulationArguments.getEvent());
				oos.writeDouble(simulationArguments.getTimeLength());
				oos.flush();
				oos.close();
				serializationMode = MODE_READ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getObservables().init(simulationArguments.getTimeLength(), simulationArguments.getInitialTime(), simulationArguments.getEvent(), simulationArguments.getPoints(), simulationArguments.isTime());
		CSolution solution = (CSolution) getSolution();
		List<IRule> rules = getRules();

		if (this.simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			contactMap.addCreatedAgentsToSolution(this.solution, rules);
		}

		Iterator<IAgent> iterator = solution.getAgents().values().iterator();
		getObservables().checkAutomorphisms();

		if (simulationArguments.isActivationMap()) {
			PlxTimer timer = new PlxTimer();
			addInfo(Info.TYPE_INFO, "--Abstracting activation map...");
			
			timer.startTimer();
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(getObservables());
			}
			stopTimer(timer, "--Abstraction:");
			addInfo(Info.TYPE_INFO, "--Activation map computed");
		}

		if (simulationArguments.isInhibitionMap()) {
			PlxTimer timer = new PlxTimer();
			addInfo(Info.TYPE_INFO, "--Abstracting inhibition map...");
			
			timer.startTimer();
			for (IRule rule : rules) {
				rule.createInhibitedRulesList(rules);
				rule.createInhibitedObservablesList(getObservables());
			}
			stopTimer(timer, "--Abstraction:");
			addInfo(Info.TYPE_INFO, "--Inhibition map computed");
		}

		while (iterator.hasNext()) {
			IAgent agent = iterator.next();
			for (IRule rule : rules) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					if (cc != null) {
						IInjection inj = cc.getInjection(agent);
						if (inj != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(cc,
									inj))
								cc.setInjection(inj);
						}
					}
				}
			}

			for (IObservablesConnectedComponent oCC : getObservables()
					.getConnectedComponentList())
				if (oCC != null)
					if (oCC.getMainAutomorphismNumber() == ObservablesConnectedComponent.NO_INDEX) {
						IInjection inj = oCC.getInjection(agent);
						if (inj != null) {
							if (!agent.isAgentHaveLinkToConnectedComponent(oCC,
									inj))
								oCC.setInjection(inj);
						}
					}
		}

		if (this.simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			contactMap.constructReachableRules(rules);
			contactMap.constructContactMap();
		}

	}

	public final void checkOutputFinalState(double currentTime){
		if (simulationArguments.isOutputFinalState()) {
			createSnapshots(currentTime);
		}
	}
	
	public final void createSnapshots(double currentTime){
		addSnapshot(new CSnapshot(this, currentTime));
//		simulationData.setSnapshotTime(currentTime);
	}

	public final void outputData() {
		outputRules();
		outputPertubation();
		outputSolution();
	}

	private final void outputSolution() {
		println("INITIAL SOLUTION:");
		for (SolutionLines sl : ((CSolution) solution).getSolutionLines()) {
			print("-");
			print("" + sl.getCount());
			print("*[");
			print(sl.getLine());
			println("]");
		}
	}
	
	private final void outputRules() {
		for (IRule rule : getRules()) {
			// int countAgentsInLHS = rule.getCountAgentsLHS();
			// int indexNewAgent = countAgentsInLHS;

			for (IAction action : rule.getActionList()) {
				switch (CActionType.getById(action.getTypeId())) {
				case BREAK: {
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() < siteTo
							.getAgentLink().getIdInRuleSide()) {
						// BRK (#0,a) (#1,x)
						print("BRK (#");
						print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						print(",");
						print(action.getSiteFrom().getName());
						print(") ");
						print("(#");
						print(""
										+ (siteTo.getAgentLink()
												.getIdInRuleSide() - 1));
						print(",");
						print(siteTo.getName());
						print(") ");
						println();
					}
					break;
				}
				case DELETE: {
					// DEL #0
					print("DEL #");
					println(""
							+ (action.getAgentFrom().getIdInRuleSide() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					print("ADD " + action.getAgentTo().getName()
							+ "#");

					print(""
							+ (action.getAgentTo().getIdInRuleSide() - 1));
					print("(");
					int i = 1;
					for (ISite site : action.getAgentTo().getSites()) {
						print(site.getName());
						if ((site.getInternalState() != null)
								&& (site.getInternalState().getNameId() >= 0))
							print("~"
									+ site.getInternalState().getName());
						if (action.getAgentTo().getSites().size() > i++)
							print(",");
					}
					println(") ");

					break;
				}
				case BOUND: {
					// BND (#1,x) (#0,a)
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() > siteTo
							.getAgentLink().getIdInRuleSide()) {
						print("BND (#");
						print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						print(",");
						print(action.getSiteFrom().getName());
						print(") ");
						print("(#");
						print(""
								+ (action.getSiteTo().getAgentLink()
										.getIdInRuleSide() - 1));
						print(",");
						print(siteTo.getName());
						print(") ");
						println();
					}
					break;
				}
				case MODIFY: {
					// MOD (#1,x) with p
					print("MOD (#");
					print(""
							+ (action.getSiteFrom().getAgentLink()
									.getIdInRuleSide() - 1));
					print(",");
					print(action.getSiteFrom().getName());
					print(") with ");
					print(action.getSiteTo().getInternalState()
							.getName());
					println();
					break;
				}
				}

			}

			String line = SimulationUtils.printPartRule(rule.getLeftHandSide(), isOcamlStyleObsName());
			line = line + "->";
			line = line + SimulationUtils.printPartRule(rule.getRightHandSide(), isOcamlStyleObsName());
			String ch = new String();
			for (int j = 0; j < line.length(); j++)
				ch = ch + "-";

			println(ch);
			if (rule.getName() != null) {
				print(rule.getName());
				print(": ");
			}
			print(line);
			println();
			println(ch);
			println();
			println();
		}
	}

	private final void outputPertubation() {
		println("PERTURBATIONS:");

		for (CPerturbation perturbation : perturbations) {
			println(perturbationToString(perturbation));
		}

	}

	public final void outputData(Source source, long count) {
		try {
			PlxTimer timerOutput = new PlxTimer();
			timerOutput.startTimer();
			writeToXML(source, timerOutput);
		} catch (ParserConfigurationException e) {
			if (printStream != null) {
				e.printStackTrace(printStream);
			}
		} catch (TransformerException e) {
			if (printStream != null) {
				e.printStackTrace(printStream);
			}
		}
	}

	private final String perturbationToString(CPerturbation perturbation) {
		String st = "-";
		String greater;
		if (perturbation.getGreater()) {
			greater = "> ";
		} else {
			greater = "< ";
		}

		switch (perturbation.getType()) {
		case CPerturbation.TYPE_TIME: {
			st += "Whenever current time ";
			st += greater;
			st += perturbation.getTimeCondition();
			break;
		}
		case CPerturbation.TYPE_NUMBER: {
			st += "Whenever [";
			st += observables.getComponentList().get(perturbation.getObsNameID()).getName();
			st += "] ";
			st += greater;
			st += SimulationUtils.perturbationParametersToString(perturbation.getLHSParametersList());
			break;
		}
		}
		
		st += " do kin(";
		st += perturbation.getPerturbationRule().getName();
		st += "):=";
		st += SimulationUtils.perturbationParametersToString(perturbation.getRHSParametersList());

		return st;
	}

	private final void doPositiveUpdateForDeletedAgents(List<IAgent> agentsList) {
		for (IAgent agent : agentsList) {
			for (IRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
			}
			for (IObservablesConnectedComponent obsCC : observables.getConnectedComponentList()) {
				IInjection inj = obsCC.getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	public final void doPositiveUpdate(IRule rule, List<IInjection> currentInjectionsList) {
		if (simulationArguments.isActivationMap()) {
			SimulationUtils.positiveUpdate(rule.getActivatedRule(), rule.getActivatedObservable(), rule);
		} else {
			SimulationUtils.positiveUpdate(getRules(), observables.getConnectedComponentList(), rule);
		}

		List<IAgent> freeAgents = SimulationUtils.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgents(freeAgents);
	}

	public final boolean checkSnapshots(double currentTime) {
		if (snapshotTimes != null) {
			for (Double time : snapshotTimes) {
				if (currentTime > time) {
					snapshotTimes.remove(time);
					return true;
				}
			}
		}
		return false;
	}
	

	public final void checkPerturbation(double currentTime) {
		if (perturbations.size() != 0) {
			for (CPerturbation pb : perturbations) {
				switch (pb.getType()) {
				case CPerturbation.TYPE_TIME: {
					if (!pb.isDo())
						pb.checkCondition(currentTime);
					break;
				}
				case CPerturbation.TYPE_NUMBER: {
					pb.checkCondition(observables);
					break;
				}
				case CPerturbation.TYPE_ONCE: {
					if (!pb.isDo())
						pb.checkConditionOnce(currentTime);
					break;
				}
				}

			}
		}
	}


	public final void setSnapshotTime(String snapshotTimeStr) {
		StringTokenizer st = new StringTokenizer(snapshotTimeStr, ",");
		String timeSt;
		while (st.hasMoreTokens()) {
			timeSt = st.nextToken().trim();
			double time = Double.valueOf(timeSt);
			if (snapshotTimes == null)
				snapshotTimes = new ArrayList<Double>();
			snapshotTimes.add(time);
		}
		Collections.sort(snapshotTimes);
	}

	private final void setFocusOn(String fileNameFocusOn) throws Exception {
		DataReading dataReading = new DataReading(fileNameFocusOn);
		dataReading.readData();
		
		Parser parser = new Parser(dataReading, this);
		List<IRule> ruleList = parser.createRules(dataReading.getRules());
		
		if (ruleList != null && !ruleList.isEmpty()) {
			contactMap.setFocusRule(ruleList.get(0));
			contactMap.setMode(CContactMap.MODE_AGENT_OR_RULE);
		}
	}

	private final void print(String text) {
		if (printStream != null) {
			printStream.print(text);
		}
	}
	
	private final void println() {
		if (printStream != null) {
			printStream.println();
		}
	}
	
	protected final void println(String text) {
		if (printStream != null) {
			printStream.println(text);
		}
	}

	//**************************************************************************
	//
	// GETTERS AND SETTERS
	// 
	//

	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}

	public final void addSnapshot(CSnapshot snapshot) {
		if (snapshots == null) {
			snapshots = new ArrayList<CSnapshot>();
		}
		this.snapshots.add(snapshot);
	}

	public final IObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public final void setStories(CStories stories) {
		this.stories = stories;
	}

	public final byte getStorifyMode() {
		return storifyMode;
	}

	public final byte getSimulationType() {
		return simulationType;
	}

	public final void setPerturbations(List<CPerturbation> perturbations) {
		this.perturbations = perturbations;
	}

	public final List<Double> getTimeStamps() {
		return timeStamps;
	}

	public final List<List<RunningMetric>> getRunningMetrics() {
		return runningMetrics;
	}

	public final ISolution getSolution() {
		return solution;
	}

	public final void setRules(List<IRule> rules) {
		this.rules = rules;
	}

	public final void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}

	public final CContactMap getContactMap() {
		return contactMap;
	}

	public final void setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
	}
}

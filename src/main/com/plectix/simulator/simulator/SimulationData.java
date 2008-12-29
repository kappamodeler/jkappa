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
import com.plectix.simulator.components.actions.CActionType;
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
import com.plectix.simulator.options.SimulatorArguments;
import com.plectix.simulator.options.SimulatorOptions;
import com.plectix.simulator.parser.DataReading;
import com.plectix.simulator.parser.Parser;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.PlxTimer;

public class SimulationData {

	public final static byte DEFAULT_SEED = -1;
	
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
	public final static byte SIMULATION_TYPE_ITERATIONS = 3;
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

	private double initialTime = 0.0;

	private String randomizer;
	private int iterations = 1;

	private long event;

	private byte simulationType = SIMULATION_TYPE_NONE;
	private byte storifyMode = STORIFY_MODE_NONE;

	private double rescale = -1.;
	private int points = -1;
	private double timeLength = 0;
	private boolean isTime = false;
	private int seed = DEFAULT_SEED;

	private String xmlSessionName = "simplx.xml";
	private String xmlSessionPath = "";
	private String tmpSessionName = "simplx.tmp";
	private String commandLine;
	private String inputFile;

	private boolean activationMap = true;
	private boolean inhibitionMap = false;
	
	private boolean compile = false;
	private boolean debugInitOption = false;
	private boolean genereteMapOption = false;
	private boolean contactMapOption = false;
	private boolean numberOfRunsOption = false;
	private boolean storifyOption = false;
	
	private long maxClashes = 100;
	private List<Double> snapshotTimes;
	private boolean outputFinalState = false;
	private long clockPrecision = 3600000;
	private long clockStamp;

	private double step;
	private double nextStep;
	private byte serializationMode = MODE_NONE;
	private String serializationFileName = "~tmp.sd";

	private SimulatorArguments simulatorArguments;
	
	public SimulationData() {
		super();
	}

	public final void clearRules() {
		rules.clear();
	}

	public final void clearPerturbations() {
		if (perturbations == null) {
			return;
		}
		perturbations.clear();
	}

	public final boolean isOcamlStyleObsName() {
		return observables.isOcamlStyleObsName();
	}

	public final void parseArguments(String[] args)
				throws IllegalArgumentException {
	
			addInfo(new Info(Info.TYPE_INFO, "-Initialization..."));
			
			// let's replace all '-' by '_' 
			args = SimulationUtils.changeArguments(args);
			
			SimulatorArguments arguments = new SimulatorArguments(args);
			this.simulatorArguments = arguments;
			
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
				setXmlSessionName(arguments.getValue(SimulatorOptions.XML_SESSION_NAME));
			}
			if (arguments.hasOption(SimulatorOptions.OUTPUT_XML)) {
				setXmlSessionName(arguments.getValue(SimulatorOptions.OUTPUT_XML));
			}
	//		if (arguments.hasOption(SimulatorOptions.DO_XML)) {
	//			setXmlSessionName(arguments
	//					.getValue(SimulatorOptions.DO_XML));
	//		}
	
			try {
				if (arguments.hasOption(SimulatorOptions.INIT)) {
					initialTime = Double.valueOf(arguments.getValue(SimulatorOptions.INIT));
				}
				if (arguments.hasOption(SimulatorOptions.POINTS)) {
					points = Integer.valueOf(arguments.getValue(SimulatorOptions.POINTS));
				}
				if (arguments.hasOption(SimulatorOptions.RESCALE)) {
					double rescale = Double.valueOf(arguments.getValue(SimulatorOptions.RESCALE));
					if (rescale > 0) {
						this.rescale = rescale;
					} else {
						throw new Exception();
					}
				}
	
				if (arguments.hasOption(SimulatorOptions.NO_SEED)) {
					setSeed(0);
				}
				
				// TODO else?
				if (arguments.hasOption(SimulatorOptions.SEED)) {
					int seed = Integer.valueOf(arguments.getValue(SimulatorOptions.SEED));
					setSeed(seed);
				}
	
				if (arguments.hasOption(SimulatorOptions.MAX_CLASHES)) {
					int max_clashes = Integer.valueOf(arguments.getValue(SimulatorOptions.MAX_CLASHES));
					setMaxClashes(max_clashes);
				}
	
				if (arguments.hasOption(SimulatorOptions.EVENT)) {
					long event = Long.valueOf(arguments.getValue(SimulatorOptions.EVENT));
					setEvent(event);
				}
	
				if (arguments.hasOption(SimulatorOptions.ITERATION)) {
					setIterations(Integer.valueOf(arguments.getValue(SimulatorOptions.ITERATION)));
				}
	
			} catch (Exception e) {
				e.printStackTrace(Simulator.getErrorStream());
				throw new IllegalArgumentException(e);
			}
	
			
			if (arguments.hasOption(SimulatorOptions.RANDOMIZER_JAVA)) {
				setRandomizer(arguments.getValue(SimulatorOptions.RANDOMIZER_JAVA));
			}
	
			if (arguments.hasOption(SimulatorOptions.NO_ACTIVATION_MAP)
					|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
					|| (arguments.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
				activationMap = false;
			}
	
			if (arguments.hasOption(SimulatorOptions.MERGE_MAPS)) {
				inhibitionMap = true;
			}
			
			if (arguments.hasOption(SimulatorOptions.COMPILE)) {
				compile = true;
			}
			
			
			if (arguments.hasOption(SimulatorOptions.DEBUG_INIT)) {
				debugInitOption = false;
			}
			
			if (arguments.hasOption(SimulatorOptions.GENERATE_MAP)) {
				genereteMapOption = false;
			}

			if (arguments.hasOption(SimulatorOptions.CONTACT_MAP)) {
				contactMapOption = false;
			}
			
			if (arguments.hasOption(SimulatorOptions.NUMBER_OF_RUNS)) {
				numberOfRunsOption = false;
			}
			
			if (arguments.hasOption(SimulatorOptions.STORIFY)) {
				storifyOption = false;
			}
					
					
			if (arguments.hasOption(SimulatorOptions.NO_INHIBITION_MAP)
					|| (arguments.hasOption(SimulatorOptions.NO_MAPS))
					|| (arguments.hasOption(SimulatorOptions.NO_BUILD_INFLUENCE_MAP))) {
				inhibitionMap = false;
			}
	
			if (arguments.hasOption(SimulatorOptions.OCAML_STYLE_OBS_NAME)) {
				setOcamlStyleObsName(true);
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
				
				setSimulationType(SIMULATION_TYPE_ITERATIONS);
				setIterations(iteration);
			}
	
			if (arguments.hasOption(SimulatorOptions.CLOCK_PRECISION)) {
				clockPrecision = 60000 * Long.valueOf(arguments.getValue(SimulatorOptions.CLOCK_PRECISION));
			}
	
			if (arguments.hasOption(SimulatorOptions.OUTPUT_FINAL_STATE)) {
				outputFinalState = true;
			}
	
			if (arguments.hasOption(SimulatorOptions.OUTPUT_SCHEME)) {
				xmlSessionPath = arguments.getValue(SimulatorOptions.OUTPUT_SCHEME);
			}
	
			if (arguments.hasOption(SimulatorOptions.NO_SAVE_ALL)) {
				serializationMode = MODE_NONE;
			}
	
			if (arguments.hasOption(SimulatorOptions.SAVE_ALL)) {
				serializationFileName = arguments.getValue(SimulatorOptions.SAVE_ALL) ;
			}
			
			if (arguments.hasOption(SimulatorOptions.DONT_COMPRESS_STORIES)) {
				storifyMode = STORIFY_MODE_NONE;
			}
			
			if (arguments.hasOption(SimulatorOptions.COMPRESS_STORIES)) {
				storifyMode = STORIFY_MODE_WEAK;
			}
			
			if (arguments.hasOption(SimulatorOptions.USE_STRONG_COMPRESSION)) {
				storifyMode = STORIFY_MODE_STRONG;
			}
		}

	public final void readSimulatonFile(Simulator simulator) {
	
		if (this.simulatorArguments == null) {
			throw new RuntimeException("Simulator Arguments must be set before reading the simulation file!");
		}
		
		boolean option = false;
		String fileName = null;
		double timeSim = 0.;
		String snapshotTime;
	
		if (simulatorArguments.hasOption(SimulatorOptions.STORIFY)) {
			fileName = simulatorArguments.getValue(SimulatorOptions.STORIFY);
			setSimulationType(SIMULATION_TYPE_STORIFY);
			option = true;
		}
		
		if (simulatorArguments.hasOption(SimulatorOptions.TIME)) {
			try {
				timeSim = Double.valueOf(simulatorArguments.getValue(SimulatorOptions.TIME));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			
			setTimeLength(timeSim);
		} else {
			Simulator.println("*Warning* No time limit.");
		}
	
		if (!option && (simulatorArguments.hasOption(SimulatorOptions.SIMULATIONFILE))) {
			option = true;
			fileName = simulatorArguments.getValue(SimulatorOptions.SIMULATIONFILE);
			if (simulatorArguments.hasOption(SimulatorOptions.SNAPSHOT_TIME)) {
				option = true;
				try {
					snapshotTime = simulatorArguments.getValue(SimulatorOptions.SNAPSHOT_TIME);
					setSnapshotTime(snapshotTime);
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
			setSimulationType(SIMULATION_TYPE_SIM);
		}
		
		if (simulatorArguments.hasOption(SimulatorOptions.COMPILE)) {
			if (!option) {
				option = true;
				fileName = simulatorArguments.getValue(SimulatorOptions.COMPILE);
			} else {
				option = false;
			}
			setSimulationType(SIMULATION_TYPE_COMPILE);
		}
	
		if (simulatorArguments.hasOption(SimulatorOptions.GENERATE_MAP)) {
			if (!option) {
				option = true;
				fileName = simulatorArguments.getValue(SimulatorOptions.GENERATE_MAP);
			} else {
				option = false;
			}
			
			setSimulationType(SIMULATION_TYPE_GENERATE_MAP);
		}
	
		if (simulatorArguments.hasOption(SimulatorOptions.CONTACT_MAP)) {
			if (!option) {
				option = true;
				fileName = simulatorArguments.getValue(SimulatorOptions.CONTACT_MAP);
			} else {
				option = false;
			}
			
			setSimulationType(SIMULATION_TYPE_CONTACT_MAP);
		}
	
		if (simulationType == SIMULATION_TYPE_NONE) {
			// HelpFormatter formatter = new HelpFormatter();
			// formatter.printHelp("use --sim [file]", cmdLineOptions);
			throw new IllegalArgumentException("No option specified");
		}
	
		inputFile = fileName;
		
		DataReading data = new DataReading(fileName);
		try {
			if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
				if (simulatorArguments.hasOption(SimulatorOptions.FOCUS_ON)) {
					String fileNameFocusOn = simulatorArguments
							.getValue(SimulatorOptions.FOCUS_ON);
					setFocusOn(fileNameFocusOn, simulator);
				}
			}
			
			data.readData();
			
			Parser parser = new Parser(data, this, simulator);
			parser.setForwarding(simulatorArguments.hasOption(SimulatorOptions.FORWARD));
			parser.parse();
		} catch (Exception e) {
			Simulator.println("Error in file \"" + fileName + "\" :");
			e.printStackTrace(Simulator.getErrorStream());
			throw new IllegalArgumentException(e);
		}
	}
	
	public final boolean isParseSolution() {
		switch (simulationType) {
		case SIMULATION_TYPE_GENERATE_MAP:
			return false;
		}
		return true;
	}

	public final void setOcamlStyleObsName(boolean ocamlStyleObsName) {
		observables.setOcamlStyleObsName(ocamlStyleObsName);
	}

	public final void addInfo(Info info) {
		for (Info inf : infoList) {
			if (inf.getMessageWithoutTime().equals(info.getMessageWithoutTime())) {
				inf.upCount(info.getTime());
				return;
			}
		}

		infoList.add(info);
	}

	public final void setCommandLine(String[] args) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			stringBuffer.append(args[i] + " ");
		}
		this.commandLine = stringBuffer.toString();
	}

	public final void setMaxClashes(long max_clashes) {
		if (max_clashes > 0) {
			this.maxClashes = max_clashes;
		} else {
			throw new IllegalArgumentException("Can't set negative max_clashes: " + max_clashes);
		}
	}

	public final boolean isEndSimulation(double currentTime, long count) {
		long curClockTime = System.currentTimeMillis();
		if (curClockTime - clockStamp > clockPrecision) {
			Simulator.println("simulation interrupted because the clock time has expired");
			return true;
		}
		
		if (isTime) {
			if (currentTime <= timeLength) {
				if (currentTime >= nextStep) {
					Simulator.print("#");
					nextStep += step;
				}
				return false;
			} else {
				Simulator.println("#");
				return true;
			}
		} else if (count <= event) {
			if (count >= nextStep) {
				Simulator.print("#");
				nextStep += step;
			}
			return false;
		} else {
			Simulator.println("#");
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

	public boolean isStorify() {
		return (simulationType == SIMULATION_TYPE_STORIFY);
	}

	public void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
		step = timeLength / 100;
		nextStep = step;
		this.isTime = true;
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
		this.event = event;
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
		simplxSession.setAttribute("CommandLine", commandLine);
		simplxSession.setAttribute("InputFile", inputFile);
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

		if (activationMap) {
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
			if (inhibitionMap) {
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
							/ (double) iterations;
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
			simulation.setAttribute("TotalEvents", Long.toString(event));
			simulation.setAttribute("TotalTime", Double.toString(timeLength));
			simulation.setAttribute("InitTime", Double.toString(initialTime));

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

	private void addRulesToXML(Element influenceMap, int rulesAndObsNumber,
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

	public final void addSiteToContactMapAgent(CContactMapChangedSite site,
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

	public final void writeToXML(Source source, PlxTimer timerOutput)
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
				if (st == null)
					System.out.println();
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
		Simulator.println(message + timer.getTimeMessage() + " sec. CPU");
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
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						tmpSessionName + "-" + oCamlObservableNo));

				double timeSampleMin = 0.;
				double timeNext = 0.;
				double fullTime = timeStamps.get(timeStamps.size() - 1);
				if (initialTime > 0.0) {
					timeNext = initialTime;
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
			e.printStackTrace(Simulator.getErrorStream());
		}

		Simulator.println("-Results outputted in tmp session: "
				+ timer.getTimeMessage() + " sec. CPU");
	}

	public final double getTimeSampleMin(double fullTime) {
		if (points > 0) {
			return (fullTime / points);
		} else {
			return (fullTime / DEFAULT_NUMBER_OF_POINTS);
		}
	}

	private void appendData(IObservables obs, List<IObservablesComponent> list,
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

	public final String getXmlSessionPath() {
		if (xmlSessionPath.length() > 0) {
			return xmlSessionPath + File.separator + xmlSessionName;
		} else {
			return xmlSessionName; 
		}
	}


	public final void initialize() {

		if (getSerializationMode() == MODE_READ) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(serializationFileName));
				solution = (CSolution) ois.readObject();
				rules = (List<IRule>) ois.readObject();
				observables = (IObservables) ois.readObject();
				perturbations = (List<CPerturbation>) ois.readObject();
				snapshotTimes = (List<Double>) ois.readObject();
				event = (long) ois.readLong();
				timeLength = (double) ois.readDouble();
				ois.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (getSerializationMode() == MODE_SAVE) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializationFileName));
				oos.writeObject(solution);
				oos.writeObject(rules);
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeObject(snapshotTimes);
				oos.writeLong(event);
				oos.writeDouble(timeLength);
				oos.flush();
				oos.close();
				serializationMode = MODE_READ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getObservables().init(timeLength, initialTime, event, points, isTime());
		CSolution solution = (CSolution) getSolution();
		List<IRule> rules = getRules();

		if (this.simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			contactMap.addCreatedAgentsToSolution(this.solution, rules);
		}

		Iterator<IAgent> iterator = solution.getAgents().values().iterator();
		getObservables().checkAutomorphisms();

		if (activationMap) {
			PlxTimer timer = new PlxTimer();
			addInfo(new Info(Info.TYPE_INFO, "--Abstracting activation map..."));
			
			timer.startTimer();
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(getObservables());
			}
			stopTimer(timer, "--Abstraction:");
			addInfo(new Info(Info.TYPE_INFO, "--Activation map computed"));
		}

		if (inhibitionMap) {
			PlxTimer timer = new PlxTimer();
			addInfo(new Info(Info.TYPE_INFO, "--Abstracting inhibition map..."));
			
			timer.startTimer();
			for (IRule rule : rules) {
				rule.createInhibitedRulesList(rules);
				rule.createInhibitedObservablesList(getObservables());
			}
			stopTimer(timer, "--Abstraction:");
			addInfo(new Info(Info.TYPE_INFO, "--Inhibition map computed"));
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

	public void checkOutputFinalState(double currentTime){
		if (outputFinalState) {
			createSnapshots(currentTime);
		}
	}
	
	public void createSnapshots(double currentTime){
		addSnapshot(new CSnapshot(this, currentTime));
//		simulationData.setSnapshotTime(currentTime);
	}
	
	public final void outputSolution() {
		Simulator.println("INITIAL SOLUTION:");
		for (SolutionLines sl : ((CSolution) solution).getSolutionLines()) {
			Simulator.print("-");
			Simulator.print("" + sl.getCount());
			Simulator.print("*[");
			Simulator.print(sl.getLine());
			Simulator.println("]");
		}
	}
	
	public final void outputRules() {
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
						Simulator.print("BRK (#");
						Simulator.print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(action.getSiteFrom().getName());
						Simulator.print(") ");
						Simulator.print("(#");
						Simulator
								.print(""
										+ (siteTo.getAgentLink()
												.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(siteTo.getName());
						Simulator.print(") ");
						Simulator.println();
					}
					break;
				}
				case DELETE: {
					// DEL #0
					Simulator.print("DEL #");
					Simulator.println(""
							+ (action.getAgentFrom().getIdInRuleSide() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					Simulator.print("ADD " + action.getAgentTo().getName()
							+ "#");

					Simulator.print(""
							+ (action.getAgentTo().getIdInRuleSide() - 1));
					Simulator.print("(");
					int i = 1;
					for (ISite site : action.getAgentTo().getSites()) {
						Simulator.print(site.getName());
						if ((site.getInternalState() != null)
								&& (site.getInternalState().getNameId() >= 0))
							Simulator.print("~"
									+ site.getInternalState().getName());
						if (action.getAgentTo().getSites().size() > i++)
							Simulator.print(",");
					}
					Simulator.println(") ");

					break;
				}
				case BOUND: {
					// BND (#1,x) (#0,a)
					ISite siteTo = ((ISite) action.getSiteFrom().getLinkState()
							.getSite());
					if (action.getSiteFrom().getAgentLink().getIdInRuleSide() > siteTo
							.getAgentLink().getIdInRuleSide()) {
						Simulator.print("BND (#");
						Simulator.print(""
								+ (action.getSiteFrom().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(action.getSiteFrom().getName());
						Simulator.print(") ");
						Simulator.print("(#");
						Simulator.print(""
								+ (action.getSiteTo().getAgentLink()
										.getIdInRuleSide() - 1));
						Simulator.print(",");
						Simulator.print(siteTo.getName());
						Simulator.print(") ");
						Simulator.println();
					}
					break;
				}
				case MODIFY: {
					// MOD (#1,x) with p
					Simulator.print("MOD (#");
					Simulator.print(""
							+ (action.getSiteFrom().getAgentLink()
									.getIdInRuleSide() - 1));
					Simulator.print(",");
					Simulator.print(action.getSiteFrom().getName());
					Simulator.print(") with ");
					Simulator.print(action.getSiteTo().getInternalState()
							.getName());
					Simulator.println();
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

			Simulator.println(ch);
			if (rule.getName() != null) {
				Simulator.print(rule.getName());
				Simulator.print(": ");
			}
			Simulator.print(line);
			Simulator.println();
			Simulator.println(ch);
			Simulator.println();
			Simulator.println();
		}
	}

	public final void outputPertubation() {
		Simulator.println("PERTURBATIONS:");

		for (CPerturbation perturbation : perturbations) {
			Simulator.println(perturbationToString(perturbation));
		}

	}

	public final void outputData(Source source, long count) {
		try {
			PlxTimer timerOutput = new PlxTimer();
			timerOutput.startTimer();
			writeToXML(source, timerOutput);
		} catch (ParserConfigurationException e) {
			e.printStackTrace(Simulator.getErrorStream());
		} catch (TransformerException e) {
			e.printStackTrace(Simulator.getErrorStream());
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

	public final void doPositiveUpdateForDeletedAgents(List<IAgent> agentsList) {
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
		if (activationMap) {
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


	public final void setSnapshotTime(String snapshotTimeStr) throws Exception {
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
		// this.snapshotTimes = -1;
		// this.snapshotTime = snapshotTime;
	}

	public void setFocusOn(String fileNameFocusOn, Simulator simulator) throws Exception {
		DataReading dataReading = new DataReading(fileNameFocusOn);
		dataReading.readData();
		
		Parser parser = new Parser(dataReading, this, simulator);
		List<IRule> ruleList = parser.createRules(dataReading.getRules());
		
		if (ruleList != null && !ruleList.isEmpty()) {
			contactMap.setFocusRule(ruleList.get(0));
			contactMap.setMode(CContactMap.MODE_AGENT_OR_RULE);
		}
	}

	//**************************************************************************
	//
	// GETTERS AND SETTERS
	// 
	//


	public final void setXmlSessionName(String xmlSessionName) {
		this.xmlSessionName = xmlSessionName;
	}

	public final int getSeed() {
		return seed;
	}

	public final void setSeed(int seed) {
		this.seed = seed;
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

	public final boolean isTime() {
		return this.isTime;
	}

	public final byte getStorifyMode() {
		return storifyMode;
	}

	public final byte getSimulationType() {
		return simulationType;
	}

	public final void setSimulationType(byte simulationType) {
		this.simulationType = simulationType;
	}

	public final String getRandomizer() {
		return randomizer;
	}

	public final void setRandomizer(String randomizer) {
		this.randomizer = randomizer;
	}

	public final int getIterations() {
		return iterations;
	}

	public final void setIterations(int iterations) {
		this.iterations = iterations;
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

	public final double getRescale() {
		return rescale;
	}

	public final void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}

	public final int getSerializationMode() {
		return serializationMode;
	}
	
	public final String getCommandLine() {
		return this.commandLine;
	}

	public final long getMaxClashes() {
		return maxClashes;
	}

	public final CContactMap getContactMap() {
		return contactMap;
	}


	public final boolean isCompile() {
		return compile;
	}

	public final boolean isDebugInitOption() {
		return debugInitOption;
	}

	public final boolean isGenereteMapOption() {
		return genereteMapOption;
	}

	public final boolean isContactMapOption() {
		return contactMapOption;
	}

	public final boolean isNumberOfRunsOption() {
		return numberOfRunsOption;
	}

	public final boolean isStorifyOption() {
		return storifyOption;
	}
}

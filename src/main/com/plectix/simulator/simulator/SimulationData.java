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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CPerturbationType;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.components.SnapshotElement;
import com.plectix.simulator.components.SolutionLines;
import com.plectix.simulator.components.contactMap.CContactMap;
import com.plectix.simulator.components.contactMap.CContactMapChangedSite;
import com.plectix.simulator.components.contactMap.CContactMapEdge;
import com.plectix.simulator.components.contactMap.CContactMap.ContactMapMode;
import com.plectix.simulator.components.stories.CStories;
import com.plectix.simulator.components.stories.CStoryTrees;
import com.plectix.simulator.components.stories.CStoryType;
import com.plectix.simulator.components.stories.CStoryType.StoryOutputType;
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
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.parser.util.IdGenerator;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationData {

	private final static String TYPE_NEGATIVE_MAP = "NEGATIVE";
	private final static String TYPE_POSITIVE_MAP = "POSITIVE";

	private static final double DEFAULT_NUMBER_OF_POINTS = 1000;

	private List<Double> timeStamps = null;
	private List<List<RunningMetric>> runningMetrics = null;

	private List<IRule> rules = null;
	private CStories stories = null;
	private List<CPerturbation> perturbations = null;
	private IObservables observables = new CObservables();
	private List<CSnapshot> snapshots = null;
	private ISolution solution = new CSolution(); // soup of initial components

	private CContactMap contactMap = new CContactMap();

	private List<Info> infoList = new ArrayList<Info>();

	private String tmpSessionName = "simplx.tmp";

	private PrintStream printStream = null;

	private List<Double> snapshotTimes = null;

	private long clockStamp;

	private double step;
	private double nextStep;
	private double stepStories;
	private double nextStepStories;

//	private int agentIdGenerator = 0;

	private boolean argumentsSet = false;
	private SimulationArguments simulationArguments = new SimulationArguments();
	private IdGenerator myIdGenerator = new IdGenerator();
	private IdGenerator myRuleIdGenerator = new IdGenerator();
	
	public SimulationData() {
		super();
	}

	public final void resetSimulation(InfoType outputType) {
		if (!simulationArguments.isShortConsoleOutput())
			outputType = InfoType.OUTPUT;
		addInfo(outputType, InfoType.INFO, "-Reset simulation data.");
		addInfo(outputType, InfoType.INFO, "-Initialization...");

		PlxTimer timer = new PlxTimer();
		timer.startTimer();

		rules.clear();
		observables.resetLists();
		solution.clearAgents();
		solution.clearSolutionLines();
		
		myIdGenerator.reset();
		
		if (perturbations != null) {
			perturbations.clear();
		}

		if (simulationArguments.getSerializationMode() != SimulationArguments.SerializationMode.READ) {
			readSimulatonFile(outputType);
		}

		initialize(outputType);

		stopTimer(outputType, timer, "-Initialization:");
		setClockStamp(System.currentTimeMillis());
	}

	public final boolean isOcamlStyleObsName() {
		return simulationArguments.isOcamlStyleObservableNames();
	}

	public final long generateNextRuleId() {
		return myRuleIdGenerator.generateNextAgentId();
	}
	
	public final long generateNextAgentId() {
		return myIdGenerator.generateNextAgentId();
//		return agentIdGenerator++;
	}

	public final void setSimulationArguments(InfoType outputType,
			SimulationArguments arguments) {
		this.simulationArguments = arguments;

		if (simulationArguments.isNoDumpStdoutStderr()) {
			printStream = null;
		}

		// do not print anything above because the line above might have turned
		// the printing off...

		if (simulationArguments.isHelp()) {
			if (printStream != null) {
				PrintWriter printWriter = new PrintWriter(printStream);
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH,
						SimulationMain.COMMAND_LINE_SYNTAX, null,
						SimulatorOptions.COMMAND_LINE_OPTIONS,
						HelpFormatter.DEFAULT_LEFT_PAD,
						HelpFormatter.DEFAULT_DESC_PAD, null, false);
				printWriter.flush();
			}
		}

		if (simulationArguments.isVersion()) {
			println("Java simulator SVN Revision: "
					+ BuildConstants.BUILD_SVN_REVISION);
		}

		// let's dump the command line arguments
		if (simulationArguments.getCommandLineString() != null) {
			println("Java " + simulationArguments.getCommandLineString());
		}

		addInfo(outputType, InfoType.INFO, "-Initialization...");

		// TODO: remove the following lines after checking all the dependencies
		// to them!!!
		if (simulationArguments.isTime()) {
			setTimeLength(simulationArguments.getTimeLength());
		} else {
			setEvent(simulationArguments.getEvent());
			println("*Warning* No time limit.");
		}

		this.argumentsSet = true;
	}

	public final void readSimulatonFile(InfoType outputType) {
		if (!this.argumentsSet) {
			throw new RuntimeException(
					"Simulator Arguments must be set before reading the simulation file!");
		}

		// TODO: move the following lines to a reset method. they should not be
		// part of reading the simulation file!
		// They are here now because this method used to parse arguments!
		resetBar();
		observables.setOcamlStyleObsName(simulationArguments
				.isOcamlStyleObservableNames());
		if (simulationArguments.getSnapshotsTimeString() != null) {
			setSnapshotTime(simulationArguments.getSnapshotsTimeString());
		}

		try {
			KappaFileReader kappaFileReader = new KappaFileReader(
					simulationArguments.getInputFilename());

			if (simulationArguments.getFocusFilename() != null) {
				setFocusOn(simulationArguments.getFocusFilename());
			}

			KappaFile kappaFile = kappaFileReader.parse();

			KappaSystemParser parser = new KappaSystemParser(kappaFile, this);
			parser.setForwarding(simulationArguments.isForwardOnly());
			parser.parse(outputType);
		} catch (Exception e) {
			println("Error in file \"" + simulationArguments.getInputFilename()
					+ "\" :");

			if (printStream != null) {
				e.printStackTrace(printStream);
			}
			e.printStackTrace();
			//throw new IllegalArgumentException(e);
		}
	}

	public final boolean isParseSolution() {
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.GENERATE_MAP) {
			return false;
		}
		return true;
	}

	public final void addInfo(InfoType outputType, InfoType type, String message) {
		if (!simulationArguments.isShortConsoleOutput())
			outputType = InfoType.OUTPUT;
		addInfo(new Info(outputType, type, message, printStream));
	}

	private final void addInfo(Info info) {
		for (Info inf : infoList) {
			if (inf.getMessageWithoutTime()
					.equals(info.getMessageWithoutTime())) {
				inf.upCount(info.getTime());
				return;
			}
		}

		infoList.add(info);
	}

	public final boolean isEndSimulation(double currentTime, long count) {
		if (System.currentTimeMillis() - clockStamp > simulationArguments.getWallClockTimeLimit()) {
			println("Simulation is interrupted because the wall clock time has expired");
			return true;
		}

		if (simulationArguments.isTime()) {
			if (currentTime <= simulationArguments.getTimeLength()) {
				if (currentTime >= nextStep) {
					outputBar();
					nextStep += step;
				}
				return false;
			} else {
				outputBar();
				return true;
			}
		} else if (count <= simulationArguments.getEvent()) {
			if (count >= nextStep) {
				outputBar();
				nextStep += step;
			}
			return false;
		} else {
			outputBar();
			return true;
		}
	}

	private void outputBar() {
		if (!simulationArguments.isShortConsoleOutput() || !isStorify())
			print("#");
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
		return (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY);
	}

	public final void addRule(CRule rule) {
		rules.add(rule);
	}

	public final List<IRule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	public final IRule getRulesByID(int ruleID) {
		for (IRule rule : rules)
			if (rule.getRuleID() == ruleID)
				return rule;
		return null;
		// return Collections.unmodifiableList(rules);
	}

	public final void resetBar() {
		nextStep = step;
	}

	private void checkAndInitStoriesBar() {
		if (simulationArguments.isStorify()) {
			stepStories = simulationArguments.getIterations() / 100.;
			nextStepStories = stepStories;
		}
	}

	public void checkStoriesBar(int i) {
		if (i >= nextStepStories) {
			double r;
			if (stepStories >= 1)
				r = 1;
			else
				r = 100 / simulationArguments.getIterations();
			while (r > 0) {
				print("#");
				r = r - 1;
			}
			nextStepStories += stepStories;
		}
	}

	public final void setEvent(long event) {
		checkAndInitStoriesBar();
		step = event / 100;
		nextStep = step;
		simulationArguments.setEvent(event);
	}

	public final void setTimeLength(double timeLength) {
		checkAndInitStoriesBar();
		step = timeLength / 100;
		nextStep = step;
		simulationArguments.setTimeLength(timeLength);
		simulationArguments.setTime(true);
	}

	public final void initIterations(List<Double> timeStamps,
			List<List<RunningMetric>> runningMetrics) {
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
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
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
		simplxSession.setAttribute("CommandLine", simulationArguments
				.getCommandLineString());
		simplxSession.setAttribute("InputFile", simulationArguments
				.getInputFilename());
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simplxSession.setAttribute("TimeStamp", df.format(d));
		doc.appendChild(simplxSession);

		timer.startTimer();

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
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
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for influence map:");
		}

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY) {
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
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for snapshots:");
		}

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.SIM) {
			int obsCountTimeListSize = observables.getCountTimeList().size();
			Element simulation = doc.createElement("Simulation");
			simulation.setAttribute("TotalEvents", Long
					.toString(simulationArguments.getEvent()));
			simulation.setAttribute("TotalTime", Double
					.toString(simulationArguments.getTimeLength()));
			simulation.setAttribute("InitTime", Double
					.toString(simulationArguments.getInitialTime()));

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
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for data points:");
		}

		appendInfo(simplxSession, doc);

		DOMSource domSource = new DOMSource(doc);
		return domSource;
	}

	private final void addRulesToXML(Element influenceMap,
			int rulesAndObsNumber, Document doc) {
		for (int i = rules.size() - 1; i >= 0; i--) {
			Element node = null;
			if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
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
		stopTimer(InfoType.OUTPUT, timerOutput,
				"-Results outputted in xml session:");
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
			if (stT.getType() == StoryOutputType.OBS) {
				node = doc.createElement("Node");
				stT.fillNode(node, CStoryType.STRING_OBS);
				nodes.add(node);
			} else if (stT.getType() == StoryOutputType.RULE) {
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
						CStoryType stT = new CStoryType(StoryOutputType.INTRO,
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
			StoryOutputType type;
			if (level == 0)
				type = StoryOutputType.OBS;
			else
				type = StoryOutputType.RULE;

			if (listST == null) {
				listST = new ArrayList<CStoryType>();
				allLevels.put(level, listST);
			}
			for (Integer traceID : list) {
				CStoryType storyType;
				if (simulationArguments.getStorifyMode() == SimulationArguments.StorifyMode.NONE)
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
				if (stT.getType() != StoryOutputType.INTRO) {
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
			node.setAttribute("Message", info.getMessageWithTime());
			node.setAttribute("Type", info.getType().toString());
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

	public final void stopTimer(InfoType outputType, PlxTimer timer,
			String message) {
		if (timer == null) {
			return;
		}
		timer.stopTimer();

		if (outputType == InfoType.OUTPUT) {
			message += " ";
			println(message + timer.getTimeMessage() + " sec. CPU");
		}
		// timer.getTimer();
		addInfo(new Info(outputType, InfoType.INFO, message, timer
				.getThreadTimeInSeconds(), 1));
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

		println("-Results outputted in tmp session: " + timer.getTimeMessage()
				+ " sec. CPU");
	}

	private final double getTimeSampleMin(double fullTime) {
		if (simulationArguments.getPoints() > 0) {
			return (fullTime / simulationArguments.getPoints());
		} else {
			return (fullTime / DEFAULT_NUMBER_OF_POINTS);
		}
	}

	private final void appendData(IObservables obs,
			List<IObservablesComponent> list, CDATASection cdata, int index) {
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
			return simulationArguments.getXmlSessionPath() + File.separator
					+ simulationArguments.getXmlSessionName();
		} else {
			return simulationArguments.getXmlSessionName();
		}
	}

	public final void initialize(InfoType outputType) {

		if (simulationArguments.getSerializationMode() == SimulationArguments.SerializationMode.READ) {
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
		if (simulationArguments.getSerializationMode() == SimulationArguments.SerializationMode.SAVE) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(simulationArguments.getSerializationFileName()));
				oos.writeObject(solution);
				oos.writeObject(rules);
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeObject(snapshotTimes);
				oos.writeLong(simulationArguments.getEvent());
				oos.writeDouble(simulationArguments.getTimeLength());
				oos.flush();
				oos.close();
				simulationArguments
						.setSerializationMode(SimulationArguments.SerializationMode.READ);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getObservables().init(simulationArguments.getTimeLength(),
				simulationArguments.getInitialTime(),
				simulationArguments.getEvent(),
				simulationArguments.getPoints(), simulationArguments.isTime());
		CSolution solution = (CSolution) getSolution();
		List<IRule> rules = getRules();

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			// contactMap.addCreatedAgentsToSolution(this.solution, rules);
			contactMap.setSolution(this.solution);
		}

		Iterator<IAgent> iterator = solution.getAgents().values().iterator();
		getObservables().checkAutomorphisms();

		if (simulationArguments.isActivationMap()) {
			PlxTimer timer = new PlxTimer();
			addInfo(outputType, InfoType.INFO,
					"--Abstracting activation map...");

			timer.startTimer();
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(getObservables());
			}
			stopTimer(outputType, timer, "--Abstraction:");
			addInfo(outputType, InfoType.INFO, "--Activation map computed");
		}

		if (simulationArguments.isInhibitionMap()) {
			PlxTimer timer = new PlxTimer();
			addInfo(outputType, InfoType.INFO,
					"--Abstracting inhibition map...");

			timer.startTimer();
			for (IRule rule : rules) {
				rule.createInhibitedRulesList(rules);
				rule.createInhibitedObservablesList(getObservables());
			}
			stopTimer(outputType, timer, "--Abstraction:");
			addInfo(outputType, InfoType.INFO, "--Inhibition map computed");
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

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			contactMap.constructReachableRules(rules);
			contactMap.constructContactMap();
		}

	}

	public final void checkOutputFinalState(double currentTime) {
		if (simulationArguments.isOutputFinalState()) {
			createSnapshots(currentTime);
		}
	}

	public final void createSnapshots(double currentTime) {
		addSnapshot(new CSnapshot(this, currentTime));
		// simulationData.setSnapshotTime(currentTime);
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
								+ (siteTo.getAgentLink().getIdInRuleSide() - 1));
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
					println("" + (action.getAgentFrom().getIdInRuleSide() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					print("ADD " + action.getAgentTo().getName() + "#");

					print("" + (action.getAgentTo().getIdInRuleSide() - 1));
					print("(");
					int i = 1;
					for (ISite site : action.getAgentTo().getSites()) {
						print(site.getName());
						if ((site.getInternalState() != null)
								&& (site.getInternalState().getNameId() >= 0))
							print("~" + site.getInternalState().getName());
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
					print(action.getSiteTo().getInternalState().getName());
					println();
					break;
				}
				}

			}

			String line = SimulationUtils.printPartRule(rule.getLeftHandSide(),
					isOcamlStyleObsName());
			line = line + "->";
			line = line
					+ SimulationUtils.printPartRule(rule.getRightHandSide(),
							isOcamlStyleObsName());
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
		case TIME: {
			st += "Whenever current time ";
			st += greater;
			st += perturbation.getTimeCondition();
			break;
		}
		case NUMBER: {
			st += "Whenever [";
			st += observables.getComponentList().get(
					perturbation.getObsNameID()).getName();
			st += "] ";
			st += greater;
			st += SimulationUtils.perturbationParametersToString(perturbation
					.getLHSParametersList());
			break;
		}
		}

		st += " do kin(";
		st += perturbation.getPerturbationRule().getName();
		st += "):=";
		st += SimulationUtils.perturbationParametersToString(perturbation
				.getRHSParametersList());

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
			for (IObservablesConnectedComponent obsCC : observables
					.getConnectedComponentList()) {
				IInjection inj = obsCC.getInjection(agent);
				if (inj != null) {
					if (!agent.isAgentHaveLinkToConnectedComponent(obsCC, inj))
						obsCC.setInjection(inj);
				}
			}
		}
	}

	private final void doPositiveUpdateForDeletedAgentsForContactMap(
			List<IAgent> agentsList, List<IRule> rules) {
		for (IAgent agent : agentsList) {
			for (IRule rule : getRules()) {
				for (IConnectedComponent cc : rule.getLeftHandSide()) {
					IInjection inj = cc.getInjection(agent);
					if (inj != null) {
						if (!agent.isAgentHaveLinkToConnectedComponent(cc, inj))
							cc.setInjection(inj);
					}
				}
				if (rule.isInvokedRule() && !rule.includedInCollection(rules)) {
					rules.add(rule);
				}
			}
		}
	}

	public final void doPositiveUpdate(IRule rule,
			List<IInjection> currentInjectionsList) {
		if (simulationArguments.isActivationMap()) {
			SimulationUtils.positiveUpdate(rule.getActivatedRule(), rule
					.getActivatedObservable(), rule);
		} else {
			SimulationUtils.positiveUpdate(getRules(), observables
					.getConnectedComponentList(), rule);
		}

		List<IAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgents(freeAgents);
	}

	public final void doPositiveUpdateForContactMap(IRule rule,
			List<IInjection> currentInjectionsList, List<IRule> invokedRules) {
		SimulationUtils.positiveUpdateForContactMap(getRules(), rule,
				invokedRules);
		List<IAgent> freeAgents = SimulationUtils
				.doNegativeUpdateForDeletedAgents(rule, currentInjectionsList);
		doPositiveUpdateForDeletedAgentsForContactMap(freeAgents, invokedRules);
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
				case TIME: {
					if (!pb.isDo())
						pb.checkCondition(currentTime);
					break;
				}
				case NUMBER: {
					pb.checkCondition(observables);
					break;
				}
				case ONCE: {
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
		KappaFileReader kappaFileReader = new KappaFileReader(fileNameFocusOn);
		KappaFile kappaFile = kappaFileReader.parse();

		KappaSystemParser parser = new KappaSystemParser(kappaFile, this);
		List<IRule> ruleList = parser.createRules();

		if (ruleList != null && !ruleList.isEmpty()) {
			contactMap.setFocusRule(ruleList.get(0));
			contactMap.setMode(ContactMapMode.AGENT_OR_RULE);
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

	protected final void printlnBar() {
		if (!simulationArguments.isShortConsoleOutput())
			if (printStream != null) {
				printStream.println("#");
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

	public IdGenerator getIdGenerator() {
		return myIdGenerator ;
	}

	public void setIdGenerator(IdGenerator generator) {
		myIdGenerator = generator;
	}
	
	public void setSolution(ISolution solution) {
		this.solution = solution;
	}

	public void setObservables(IObservables observables) {
		this.observables = observables;
	}
}

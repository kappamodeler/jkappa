package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

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
	private CSnapshot snapshot = null;
	private ISolution solution = new CSolution(); // soup of initial components

	private CContactMap contactMap = new CContactMap();

	private List<Info> infoList = new ArrayList<Info>();

	private double initialTime = 0.0;

	private String randomizer;
	private int iterations = 1;

	private long event;

	private byte simulationType = SIMULATION_TYPE_NONE;
	private byte storifyMode = STORIFY_MODE_STRONG;

	private DOMSource myOutputDOMSource;
	// private boolean compile = false;
	// private boolean storify = false;

	private double rescale = -1.;
	private int points = -1;
	private double timeLength = 0;
	private boolean isTime = false;
	private int seed = 0;
	private String xmlSessionName = "simplx.xml";
	private String xmlSessionPath = "";
	private String tmpSessionName = "simplx.tmp";
	private String commandLine;
	private String inputFile;

	private boolean activationMap = true;
	private boolean inhibitionMap = false;

	private long maxClashes = 100;
	private double snapshotTime = -1.;
	private long clockPrecision = 3600000;
	private long clockStamp;

	private double step;
	private double nextStep;
	private byte serializationMode = MODE_NONE;
	private String serializationFileName = "~tmp.sd";
	
	public SimulationData() {
		super();
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
			if (inf.getMessageWithoutTime()
					.equals(info.getMessageWithoutTime())) {
				inf.upCount(info.getTime());
				return;
			}
		}

		infoList.add(info);
	}


	public final void setCommandLine(String[] args) {
		String st = new String();
		for (int i = 0; i < args.length; i++) {
			st += args[i] + " ";
		}
		this.commandLine = st;
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
			Simulator
					.println("simulation interrupted because the clock time has expired");
			return true;
		}
		if (isTime)
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
		else if (count <= event) {
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
		if (simulationType == SIMULATION_TYPE_STORIFY) {
			return true;
		} 
		return false;
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

	public final DOMSource createDOMModel() throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		TimerSimulation timer = new TimerSimulation();

		Element simplxSession = doc.createElement("SimplxSession");
		simplxSession.setAttribute("xsi:schemaLocation",
				"http://plectix.synthesisstudios.com SimplxSession.xsd");
		simplxSession.setAttribute("xmlns",
				"http://plectix.synthesisstudios.com/schemas/kappasession");
		simplxSession.setAttribute("xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");

		simplxSession.setAttribute("CommandLine", commandLine);
		simplxSession.setAttribute("InputFile", inputFile);
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simplxSession.setAttribute("TimeStamp", df.format(d));
		doc.appendChild(simplxSession);

		timer.startTimer();

		if (simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			Element contactMapElement = doc.createElement("ContactMap");

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
								Element rule = doc.createElement("Rule");
								for (int ruleID : edge.getRules()) {
									rule.setAttribute("Id", Integer
											.toString(ruleID));
								}
								bond.appendChild(rule);
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

			for (int i = rules.size() - 1; i >= 0; i--) {
				Element node = doc.createElement("Node");
				node.setAttribute("Id", Integer.toString(rulesAndObsNumber--));
				node.setAttribute("Type", "RULE");
				node.setAttribute("Text", rules.get(i).getName());
				node.setAttribute("Data", rules.get(i).getData(
						isOcamlStyleObsName()));
				node.setAttribute("Name", rules.get(i).getName());
				influenceMap.appendChild(node);
			}

			/**
			 * add activation map
			 * */

			for (int i = rules.size() - 1; i >= 0; i--)
				printMap(doc, TYPE_POSITIVE_MAP, influenceMap, rules.get(i),
						rules.get(i).getActivatedRule(), rules.get(i)
								.getActivatedObservable());
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

		if (snapshotTime >= 0.0) {
			timer.startTimer();
			Element snapshotElement = doc.createElement("FinalState");
			snapshotElement.setAttribute("Time", String.valueOf(snapshotTime));
			if (snapshot != null) {
				List<SnapshotElement> snapshotElementList = snapshot
						.getSnapshotElements();
				for (SnapshotElement se : snapshotElementList) {
					Element species = doc.createElement("Species");
					species.setAttribute("Kappa", se.getCcName());
					species.setAttribute("Number", String
							.valueOf(se.getCount()));
					snapshotElement.appendChild(species);
				}
			}
			simplxSession.appendChild(snapshotElement);
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

	public final void addSiteToContactMapAgent(CContactMapChangedSite site,
			Element agent, Document doc) {
		Element siteNode = doc.createElement("Site");
		Element siteRule = doc.createElement("Rule");
		for (Integer ruleID : site.getUsedRuleIDs()) {
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

	public final void writeToXML(Source source, TimerSimulation timerOutput)
			throws ParserConfigurationException, TransformerException {
		TransformerFactory trFactory = TransformerFactory.newInstance();
		Transformer transformer = trFactory.newTransformer();
		stopTimer(timerOutput, "-Results outputted in xml session:");
		StreamResult streamesult = new StreamResult(getXmlSessionPath());
		Properties pr = new Properties();
		pr.setProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperties(pr);
		transformer.transform(source, streamesult);

		// GraphDrawer gd = new GraphDrawer();
		// gd.createGraphs(observables,initialTime,timeLength);

		// Simulator.println("-Results outputted in xml session: "
		// + timerOutput.getTimer() + " sec. CPU");
	}

	private final void printMap(Document doc, String mapType, Element influenceMap,
			IRule rule, List<IRule> rulesToPrint,
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
				stT.fillConnection(node, toStoryType.getId());
				connections.add(node);
			}
		}
	}

	private final void fillRuleListStoryConnections(CStoryType toStoryType,
			HashMap<Integer, CStoryType> traceIdToStoryTypeRule,
			List<Integer> rIDs, List<Element> connections, Document doc) {
		if (rIDs.size() != 0) {
			Element node;
			for (int rID : rIDs) {
				CStoryType st = traceIdToStoryTypeRule.get(rID);
				node = doc.createElement("Connection");
				st.fillConnection(node, toStoryType.getId());
				connections.add(node);
			}
		}
	}

	private final void addConnection(Element story, CStoryTrees storyTree,
			Document doc, int item) {

		storyTree.fillMaps(isOcamlStyleObsName());

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
		int traceIDSize = storyTree.getTraceIDToLevel().size()+counter-1;
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
		HashMap<Integer, List<Integer>> traceIDToTraceIDs = storyTree
				.getTraceIDToTraceID();

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
							rIDs, connections, doc);
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

	public final void stopTimer(TimerSimulation timer, String message) {
		if (timer == null) {
			return;
		}
		message += " ";
		Simulator.println(message + timer.getTimerMessage() + " sec. CPU");
		// timer.getTimer();
		addInfo(new Info(Info.TYPE_INFO, message, timer.getThreadTimeInSeconds(), 1));
	}

	public final void createTMPReport() {
		// model.getSimulationData().updateData();
		TimerSimulation timer = new TimerSimulation();
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
				+ timer.getTimerMessage() + " sec. CPU");
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

	private final String getItem(IObservables obs, int index, IObservablesComponent oCC) {
		if (oCC.isUnique())
			return oCC.getItem(index, obs);
		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList())
			if (cc.getNameID() == oCC.getNameID())
				value *= cc.getValue(index, obs);

		return Long.valueOf(value).toString();
	}


	public final void clearRules() {
		rules.clear();
	}

	public final void clearPerturbations() {
		perturbations.clear();
	}

	public final String getXmlSessionPath() {
		if (xmlSessionPath.length() > 0)
			return xmlSessionPath + "\\" + xmlSessionName;
		else
			return xmlSessionName;
	}

	public final boolean isOcamlStyleObsName() {
		return observables.isOcamlStyleObsName();
	}

	public final void initialize() {

		if (getSerializationMode() == MODE_READ) {
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(new FileInputStream(
						getSerializationFileName()));
				solution = (CSolution) ois.readObject();
				rules = (List<IRule>) ois.readObject();
				observables = (IObservables) ois.readObject();
				perturbations = (List<CPerturbation>) ois.readObject();
				snapshotTime = (double) ois.readDouble();
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
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(getSerializationFileName()));
				oos.writeObject(solution);
				oos.writeObject(rules);
				oos.writeObject(observables);
				oos.writeObject(perturbations);
				oos.writeDouble(snapshotTime);
				oos.writeLong(event);
				oos.writeDouble(timeLength);
				oos.flush();
				oos.close();
				setSerializationMode(MODE_READ);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getObservables().init(getTimeLength(), getInitialTime(), getEvent(),
				getPoints(), isTime());
		CSolution solution = (CSolution) getSolution();
		List<IRule> rules = getRules();

		if (this.simulationType == SIMULATION_TYPE_CONTACT_MAP) {
			contactMap.addCreatedAgentsToSolution(this.solution, rules);
		}

		Iterator<IAgent> iterator = solution.getAgents().values().iterator();
		getObservables().checkAutomorphisms();

		if (isActivationMap()) {
			TimerSimulation timer = new TimerSimulation(true);
			addInfo(new Info(Info.TYPE_INFO, "--Abstracting activation map..."));
			for (IRule rule : rules) {
				rule.createActivatedRulesList(rules);
				rule.createActivatedObservablesList(getObservables());
			}
			stopTimer(timer, "--Abstraction:");
			addInfo(new Info(Info.TYPE_INFO, "--Activation map computed"));
		}

		if (isInhibitionMap()) {
			TimerSimulation timer = new TimerSimulation(true);
			addInfo(new Info(Info.TYPE_INFO, "--Abstracting inhibition map..."));
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

	//*******************************************************************************
	//
	//              GETTERS AND SETTERS
	// 
	//

	public final void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	public final double getSnapshotTime() {
		return snapshotTime;
	}

	public final void setSnapshotTime(double snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	public final CSnapshot getSnapshot() {
		return snapshot;
	}

	public final String getXmlSessionName() {
		return xmlSessionName;
	}

	public final void setXmlSessionName(String xmlSessionName) {
		this.xmlSessionName = xmlSessionName;
	}

	public final int getSeed() {
		return seed;
	}

	public final void setSeed(int seed) {
		this.seed = seed;
	}
	
	public final void setSnapshot(CSnapshot snapshot) {
		this.snapshot = snapshot;
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
	
	public final boolean isInhibitionMap() {
		return inhibitionMap;
	}

	public final byte getStorifyMode() {
		return storifyMode;
	}

	public final void setStorifyMode(byte storifyMode) {
		this.storifyMode = storifyMode;
	}

	public final void setInhibitionMap(boolean inhibitionMap) {
		this.inhibitionMap = inhibitionMap;
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

	public final List<CPerturbation> getPerturbations() {
		return perturbations;
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
	
	public final String getTmpSessionName() {
		return tmpSessionName;
	}

	public final void setTmpSessionName(String tmpSessionName) {
		this.tmpSessionName = tmpSessionName;
	}

	public final boolean isActivationMap() {
		return activationMap;
	}

	public final void setActivationMap(boolean activationMap) {
		this.activationMap = activationMap;
	}

	public final Double getInitialTime() {
		return initialTime;
	}

	public final void setInitialTime(double intialTime) {
		this.initialTime = intialTime;
	}

	public final long getEvent() {
		return event;
	}

	public final double getTimeLength() {
		return timeLength;
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

	public final void setRescale(double rescale) {
		this.rescale = rescale;
	}

	public final Integer getPoints() {
		return points;
	}

	public final void setPoints(int points) {
		this.points = points;
	}

	public final void setClockPrecision(long clockPrecision) {
		this.clockPrecision = clockPrecision;
	}

	public final long getClockPrecision() {
		return clockPrecision;
	}

	public final void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}

	public final long getClockStamp() {
		return clockStamp;
	}

	public final void setXmlSessionPath(String path) {
		this.xmlSessionPath = path;
	}
	
	public final void setSerializationMode(byte serializationMode) {
		this.serializationMode = serializationMode;
	}

	public final int getSerializationMode() {
		return serializationMode;
	}

	public final List<Info> getInfoList() {
		return infoList;
	}
	
	public final String getCommandLine() {
		return this.commandLine;
	}

	public final long getMaxClashes() {
		return maxClashes;
	}
	
	public final void setSerializationFileName(String serializationFileName) {
		this.serializationFileName = serializationFileName;
	}

	public final String getSerializationFileName() {
		return serializationFileName;
	}

	public final CContactMap getContactMap() {
		return contactMap;
	}
}

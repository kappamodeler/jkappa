package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	private static List<Double> timeStamps;
	private static List<ArrayList<RunningMetric>> runningMetrics;

	private List<IRule> rules;
	private CStories stories = null;
	private List<CPerturbation> perturbations;
	private IObservables observables = new CObservables();
	private CSnapshot snapshot = null;
	private ISolution solution = new CSolution(); // soup of initial components

	private List<Info> infoList = new ArrayList<Info>();

	private Double initialTime = 0.0;

	private String randomizer;
	private int iterations = 0;

	private long event;

	private boolean compile = false;
	private boolean storify = false;

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
	private long maxClashes = 100;
	private double snapshotTime = -1.;
	private long clockPrecision = 3600000;
	private long clockStamp;

	public final void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public final void setOcamlStyleObsName(boolean ocamlStyleObsName) {
		CObservables.setOcamlStyleObsName(ocamlStyleObsName);
	}

	public final boolean isTime() {
		return this.isTime;
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

	public final List<Info> getInfoList() {
		return infoList;
	}

	public final void setCommandLine(String[] args) {
		String st = new String();
		for (int i = 0; i < args.length; i++) {
			st += args[i] + " ";
		}
		this.commandLine = st;
	}

	public final String getCommandLine() {
		return this.commandLine;
	}

	public long getMaxClashes() {
		return maxClashes;
	}

	public void setMaxClashes(long max_clashes) {
		if (max_clashes > 0)
			this.maxClashes = max_clashes;
	}

	private double step;
	private double nextStep;

	public final boolean isEndSimulation(double currentTime, long count,
			Integer iteration_num) {
		long curClockTime = System.currentTimeMillis();
		if (curClockTime - clockStamp > clockPrecision) {
			System.out
					.println("simulation interrupted because the clock time has expired");
			return true;
		}
		if (isTime)
			if (currentTime <= timeLength) {
				if (currentTime >= nextStep) {
					System.out.print("#");
					nextStep += step;
				}
				return false;
			} else {
				System.out.println("#");
				return true;
			}
		else if (count <= event) {
			if (count >= nextStep) {
				System.out.print("#");
				nextStep += step;
			}
			return false;
		} else {
			System.out.println("#");
			return true;
		}
	}

	public double getSnapshotTime() {
		return snapshotTime;
	}

	public void setSnapshotTime(double snapshotTime) {
		this.snapshotTime = snapshotTime;
	}

	public CSnapshot getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(CSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	public IObservables getObservables() {
		return observables;
	}

	public final CStories getStories() {
		return stories;
	}

	public final void setStories(CStories list) {
		this.stories = list;
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
		return storify;
	}

	public void setStorify(boolean storify) {
		this.storify = storify;
	}

	public String getXmlSessionName() {
		return xmlSessionName;
	}

	public void setXmlSessionName(String xmlSessionName) {
		this.xmlSessionName = xmlSessionName;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public void setTimeLength(double timeLength) {
		this.timeLength = timeLength;
		step = timeLength / 100;
		nextStep = step;
		this.isTime = true;
	}

	public final void initializeLifts() {
		// creates lifts for all rules
	}

	public final void initializeInjections() {
		// creates injections for all rules
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

	public final void addRule(CRule rule) {
		rules.add(rule);
	}

	public final List<IRule> getRules() {
		return Collections.unmodifiableList(rules);
	}

	public boolean isCompile() {
		return compile;
	}

	public void setCompile(boolean compile) {
		this.compile = compile;
	}

	public final long getEvent() {
		return event;
	}

	public final void resetBar() {
		nextStep = step;
	}

	public final void setEvent(long event) {
		step = event / 100;
		nextStep = step;
		this.event = event;
	}

	public String getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(String randomizer) {
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

	public final static List<Double> getTimeStamps() {
		return timeStamps;
	}

	public final static void setTimeStamps(List<Double> timeStamps) {
		SimulationData.timeStamps = timeStamps;
	}

	public final static List<ArrayList<RunningMetric>> getRunningMetrics() {
		return runningMetrics;
	}

	public final static void setRunningMetrics(
			List<ArrayList<RunningMetric>> runningMetrics) {
		SimulationData.runningMetrics = runningMetrics;
	}

	public final void initIterations() {
		SimulationData.timeStamps = new ArrayList<Double>();
		SimulationData.runningMetrics = new ArrayList<ArrayList<RunningMetric>>();
		int observable_num = observables.getComponentListForXMLOutput().size();
		// int observable_num = observables.getComponentList().size();
		for (int i = 0; i < observable_num; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}

	}

	public void writeToXML(TimerSimulation timerOutput)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		TimerSimulation timer = new TimerSimulation();

		Element simplxSession = doc.createElement("SimplxSession");
		simplxSession.setAttribute("CommandLine", commandLine);
		simplxSession.setAttribute("InputFile", inputFile);
		simplxSession.setAttribute("TimeStamp", "stmp");
		doc.appendChild(simplxSession);

		timer.startTimer();
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
				node.setAttribute("ID", Integer.toString(rulesAndObsNumber--));
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
				node.setAttribute("ID", Integer.toString(rulesAndObsNumber--));
				node.setAttribute("Type", "RULE");
				node.setAttribute("Text", rules.get(i).getName());

				String line = SimulatorManager.printPartRule(rules.get(i)
						.getLeftHandSide());
				line = line + "->";
				line = line
						+ SimulatorManager.printPartRule(rules.get(i)
								.getRightHandSide());

				node.setAttribute("Data", line);
				node.setAttribute("Name", rules.get(i).getName());
				influenceMap.appendChild(node);
			}

			/**
			 * add activation map
			 * */

			int lastRuleID = rules.size();
			for (int i = rules.size() - 1; i >= 0; i--) {
				for (int j = rules.get(i).getActivatedObservable().size() - 1; j >= 0; j--) {
					Element node = doc.createElement("Connection");
					node.setAttribute("FromNode", Integer.toString(rules.get(i)
							.getRuleID() + 1));
					node.setAttribute("ToNode", Integer.toString(rules.get(i)
							.getActivatedObservable().get(j).getNameID()
							+ 1 + lastRuleID));
					node.setAttribute("Relation", "POSITIVE");
					influenceMap.appendChild(node);
				}
				for (int j = rules.get(i).getActivatedRule().size() - 1; j >= 0; j--) {
					Element node = doc.createElement("Connection");
					node.setAttribute("FromNode", Integer.toString(rules.get(i)
							.getRuleID() + 1));
					node.setAttribute("ToNode", Integer.toString(rules.get(i)
							.getActivatedRule().get(j).getRuleID() + 1));
					node.setAttribute("Relation", "POSITIVE");
					influenceMap.appendChild(node);
				}
			}

			simplxSession.appendChild(influenceMap);
			stopTimer(timer, "-Building xml tree for influence map:");
		}

		if (storify) {
			Element story = doc.createElement("Story");

			for (CStoryTrees st : stories.getTrees()) {
				story.setAttribute("Observable", rules.get(st.getRuleID())
						.getName());
				int depth = 0;
				HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
				addConnection(story, st, doc, st.getRuleID(), depth, map);
			}

			simplxSession.appendChild(story);
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

		int obsCountTimeListSize = CObservables.getCountTimeList().size();

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

		stopTimer(timerOutput, "-Results outputted in xml session:");

		appendInfo(simplxSession, doc);

		TransformerFactory trFactory = TransformerFactory.newInstance();
		Transformer transformer = trFactory.newTransformer();
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamesult = new StreamResult(getXmlSessionPath());
		Properties pr = new Properties();
		pr.setProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperties(pr);
		transformer.transform(domSource, streamesult);

		// GraphDrawer gd = new GraphDrawer();
		// gd.createGraphs(observables,initialTime,timeLength);

		// System.out.println("-Results outputted in xml session: "
		// + timerOutput.getTimer() + " sec. CPU");
	}

	private final void addConnection(Element story, CStoryTrees st,
			Document doc, int item, int depth, HashMap<Integer, Integer> map) {
		// depth++;
		//
		// if (map.get(item) == null)
		// map.put(item, map.size());
		// if (st.getList(item) == null)
		// return;
		// for (Integer i : st.getList(item)) {
		// if (map.get(i) == null)
		// map.put(i, map.size());
		// Element node = doc.createElement("Connection");
		// node.setAttribute("FromNode", map.get(item).toString());
		// node.setAttribute("ToNode", map.get(i).toString());
		// node.setAttribute("Relation", "STRONG");
		// story.appendChild(node);
		// addConnection(story, st, doc, i, depth, map);
		// }
		// Element node = doc.createElement("Node");
		// node.setAttribute("Id", map.get(item).toString());
		// if (item == st.getRuleID())
		// node.setAttribute("Type", "OBSERVABLE");
		// else
		// node.setAttribute("Type", "RULE");
		// node.setAttribute("Text", rules.get(item).getName());
		// String line = SimulatorManager.printPartRule(rules.get(item)
		// .getLeftHandSide());
		// line = line + "->";
		// line = line
		// + SimulatorManager.printPartRule(rules.get(item)
		// .getRightHandSide());
		//
		// node.setAttribute("Data", line);
		// node.setAttribute("Depth", Integer.valueOf(depth).toString());
		// story.appendChild(node);
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

	public final void stopTimer(TimerSimulation timer, String mess) {
		if (timer == null)
			return;
		mess += " ";
		System.out.println(mess + timer.getTimerMess() + " sec. CPU");
		// timer.getTimer();
		addInfo(new Info(Info.TYPE_INFO, mess, timer.getThreadTimeInSeconds(),
				1));
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

				Double timeSampleMin = 0.;
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
			e.printStackTrace();
		}

		System.out.println("-Results outputted in tmp session: "
				+ timer.getTimerMess() + " sec. CPU");
	}

	public final double getTimeSampleMin(double fullTime) {
		double timeSampleMin;
		if (points != -1)
			timeSampleMin = fullTime / points;
		else
			timeSampleMin = fullTime / 1000;
		return timeSampleMin;
	}

	private void appendData(IObservables obs, List<IObservablesComponent> list,
			CDATASection cdata, int index) {
		String enter = "\n";
		cdata.appendData(CObservables.getCountTimeList().get(index).toString());
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

	public void setClockPrecision(long clockPrecision) {
		this.clockPrecision = clockPrecision;
	}

	public long getClockPrecision() {
		return clockPrecision;
	}

	public void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}

	public long getClockStamp() {
		return clockStamp;
	}

	public void setXmlSessionPath(String path) {
		this.xmlSessionPath = path;
	}

	public final void clearRules() {
		rules.clear();
	}
	
	public final void clearPerturbations() {
		perturbations.clear();
	}
	
	public String getXmlSessionPath() {
		if (xmlSessionPath.length() > 0)
			return xmlSessionPath + "\\" + xmlSessionName;
		else
			return xmlSessionName;
	}

}

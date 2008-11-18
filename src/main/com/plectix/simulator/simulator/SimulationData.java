package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.ObservablesConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.graphs.GraphDrawer;
import com.plectix.simulator.util.RunningMetric;

public class SimulationData {
	private static List<Double> timeStamps;
	private static List<ArrayList<RunningMetric>> runningMetrics;

	private List<CRule> rules;
	private CStories stories = null;
	private List<CPerturbation> perturbations;
	private CObservables observables = new CObservables();
	private CSnapshot snapshot = null;
	private ISolution solution = new CSolution(); // soup of initial components

	private Double initialTime = 0.0;

	private String randomizer;
	private int iterations = 0;

	private long event;

	private long numPoints;
	private boolean compile = false;
	private boolean storify = false;

	private double rescale = -1.;
	private int points = -1;
	private double timeLength = 0;
	private boolean isTime = false;
	private int seed = 0;
	private String xmlSessionName = "simplx.xml";
	private String tmpSessionName = "simplx.tmp";
	private String commandLine;
	private String inputFile;

	private boolean activationMap = true;
	private long maxClashes = 100;
	private double snapshotTime = -1.;

	public final void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public final void setOcamlStyleObsName(boolean ocamlStyleObsName) {
		ObservablesConnectedComponent.setOcamlStyleObsName(ocamlStyleObsName);
	}

	public final boolean isTime() {
		return this.isTime;
	}

	public final void setCommandLine(String[] args) {
		String st = new String();
		for (int i = 0; i < args.length; i++) {
			st += args[i] + " ";
		}
		this.commandLine = st;
	}

	public long getMaxClashes() {
		return maxClashes;
	}

	public void setMaxClashes(long max_clashes) {
		if (max_clashes > 0)
			this.maxClashes = max_clashes;
	}

	public final boolean isEndSimulation(double currentTime, long count) {
		if (isTime)
			if (currentTime <= timeLength)
				return false;
			else
				return true;
		else if (count <= event)
			return false;
		else
			return true;
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

	public CObservables getObservables() {
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
		for (CRule rule : rules) {
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

	public final void setRules(List<CRule> rules) {
		this.rules = rules;
	}

	public final List<CRule> getRules() {
		return rules;
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

	public final void setEvent(long event) {
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
		int observable_num = observables.getComponentList().size();
		for (int i = 0; i < observable_num; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}

	}

	public void writeToXML() throws ParserConfigurationException,
			TransformerException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element simplxSession = doc.createElement("SimplxSession");
		simplxSession.setAttribute("CommandLine", commandLine);
		simplxSession.setAttribute("InputFile", inputFile);
		simplxSession.setAttribute("TimeStamp", "stmp");
		doc.appendChild(simplxSession);

		Element influenceMap = doc.createElement("InfluenceMap");

		int rulesAndObsNumber = observables.getConnectedComponentList().size()
				+ rules.size();
		/**
		 * add observables
		 * */
		for (int i = observables.getConnectedComponentList().size() - 1; i >= 0; i--) {
			Element node = doc.createElement("Node");
			node.setAttribute("ID", Integer.toString(rulesAndObsNumber--));
			node.setAttribute("Type", "OBSERVABLE");
			String obsName = observables.getConnectedComponentList().get(i)
					.getName();

			if (obsName == null)
				obsName = observables.getConnectedComponentList().get(i)
						.getLine();

			node.setAttribute("Text", '[' + obsName + ']');
			node.setAttribute("Data", observables.getConnectedComponentList()
					.get(i).getLine());
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

		if (snapshotTime >= 0.0) {
			Element snapshotElement = doc.createElement("FinalState");
			snapshotElement.setAttribute("Time", String.valueOf(snapshotTime));
			if (snapshot != null) {
				List<CSnapshot.SnapshotElement> snapshotElementList = snapshot
						.getSnapshotElements();
				for (CSnapshot.SnapshotElement se : snapshotElementList) {
					Element species = doc.createElement("Species");
					species.setAttribute("Kappa", se.getCcName());
					species.setAttribute("Number", String
							.valueOf(se.getCount()));
					snapshotElement.appendChild(species);
				}
			}
			simplxSession.appendChild(snapshotElement);
		}

		int obsCountTimeListSize = observables.getCountTimeList().size();

		Element simulation = doc.createElement("Simulation");
		simulation.setAttribute("TotalEvents", Long.toString(event));
		simulation.setAttribute("TotalTime", Double.toString(timeLength));
		simulation.setAttribute("InitTime", Double.toString(initialTime));

		simulation.setAttribute("TimeSample", Double.valueOf(
				observables.getTimeSampleMin()).toString());
		simplxSession.appendChild(simulation);

		for (int i = observables.getComponentList().size() - 1; i >= 0; i--) {
			Element node = createElement(observables.getComponentList().get(i),
					doc);
			simulation.appendChild(node);
		}

		Element csv = doc.createElement("CSV");
		CDATASection cdata = doc.createCDATASection("\n");

		for (int i = 0; i < obsCountTimeListSize; i++) {
			appendData(observables, cdata, i);
		}

		csv.appendChild(cdata);
		simulation.appendChild(csv);

		TransformerFactory trFactory = TransformerFactory.newInstance();
		Transformer transformer = trFactory.newTransformer();
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamesult = new StreamResult(xmlSessionName);
		Properties pr = new Properties();
		pr.setProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperties(pr);
		transformer.transform(domSource, streamesult);

		// GraphDrawer gd = new GraphDrawer();
		// gd.createGraphs(observables,initialTime,timeLength);
	}

	private final Element createElement(IObservablesComponent obs, Document doc) {
		Element node = doc.createElement("Plot");
		String obsName = obs.getName();
		if (obsName == null)
			obsName = obs.getLine();
		switch (obs.getType()) {
		case IObservablesComponent.TYPE_CONNECTED_COMPONENT: {
			node.setAttribute("Type", "OBSERVABLE");
			node.setAttribute("Text", '[' + obsName + ']');
			break;
		}
		case IObservablesComponent.TYPE_RULE_COMPONENT: {
			node.setAttribute("Type", "RULE");
			node.setAttribute("Text", obsName);
			break;
		}
		}
		return node;
	}

	public final void createTMPReport() {
		// model.getSimulationData().updateData();
		SimulationMain.getSimulationManager().startTimer();

		int number_of_observables = observables.getComponentList().size();
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
				+ SimulationMain.getSimulationManager().getTimer()
				+ " sec. CPU");
	}

	public final double getTimeSampleMin(double fullTime) {
		double timeSampleMin;
		if (points != -1)
			timeSampleMin = fullTime / points;
		else
			timeSampleMin = fullTime / 1000;
		return timeSampleMin;
	}

	private void appendData(CObservables obs, CDATASection cdata, int index) {
		String enter = "\n";
		cdata.appendData(obs.getCountTimeList().get(index).toString());
		for (int j = obs.getComponentList().size() - 1; j >= 0; j--) {
			cdata.appendData(",");
			IObservablesComponent oCC = obs.getComponentList().get(j);
			cdata.appendData(oCC.getItem(index, obs));
		}

		cdata.appendData(enter);
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
}

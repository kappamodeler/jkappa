package com.plectix.simulator.simulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.HelpFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.action.CAction;
import com.plectix.simulator.action.CActionType;
import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSnapshot;
import com.plectix.simulator.components.SnapshotElement;
import com.plectix.simulator.components.complex.contactMap.EContactMapMode;
import com.plectix.simulator.components.perturbations.CPerturbation;
import com.plectix.simulator.components.solution.SolutionLine;
import com.plectix.simulator.components.stories.CStoryType;
import com.plectix.simulator.components.stories.CStoryType.StoryOutputType;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IObservablesConnectedComponent;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.builders.RuleBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.xml.EntityModifier;
import com.plectix.simulator.simulator.xml.RelationModifier;
import com.plectix.simulator.util.DecimalFormatter;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.RunningMetric;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationData {


	private static final double DEFAULT_NUMBER_OF_POINTS = 1000;

	private static final int NUMBER_OF_SIGNIFICANT_DIGITS = 6;

	private List<Double> timeStamps = null;
	private List<List<RunningMetric>> runningMetrics = null;

	private List<CSnapshot> snapshots = null;

	private List<Info> infoList = new ArrayList<Info>();

	private String tmpSessionName = "simplx.tmp";

	private PrintStream printStream = null;

	private List<Double> snapshotTimes = null;

	private long clockStamp;

	private double step;
	private double nextStep;
	private double stepStories;
	private double nextStepStories;


	private boolean argumentsSet = false;
	private SimulationArguments simulationArguments = new SimulationArguments();

	private KappaModel myInitialModel = null;
	private KappaSystem myKappaSystem = new KappaSystem(this);

	public SimulationData() {
		super();
	}

	public KappaSystem getKappaSystem() {
		return myKappaSystem;
	}


	public KappaModel getInitialModel() {
		return myInitialModel;
	}

	public void setInitialModel(KappaModel model) {
		myInitialModel = model;
	}

	public final void resetSimulation(InfoType outputType) {
		if (!simulationArguments.isShortConsoleOutput())
			outputType = InfoType.OUTPUT;
		addInfo(outputType, InfoType.INFO, "-Reset simulation data.");
		addInfo(outputType, InfoType.INFO, "-Initialization...");

		PlxTimer timer = new PlxTimer();
		timer.startTimer();

		myKappaSystem.clearRules();
		myKappaSystem.getObservables().resetLists();
		myKappaSystem.getSolution().clear();
		myKappaSystem.getSolution().clearSolutionLines();

		myKappaSystem.resetIdGenerators();

		if (myKappaSystem.getPerturbations() != null) {
			myKappaSystem.clearPerturbations();
		}

		if (simulationArguments.getSerializationMode() != SimulationArguments.SerializationMode.READ) {
			readSimulatonFile(outputType);
		}

		myKappaSystem.initialize(outputType);

		stopTimer(outputType, timer, "-Initialization:");
		setClockStamp(System.currentTimeMillis());
	}

	public final boolean isOcamlStyleObsName() {
		return simulationArguments.isOcamlStyleObservableNames();
	}

	public final void setSimulationArguments(InfoType outputType,
			SimulationArguments arguments) {
		this.simulationArguments = arguments;
		this.simulationArguments.updateRandom();
		
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

		if (simulationArguments.getMonitorPeakMemory() > 0) {
			println("Turning memory monitoring on using a period of "
					+ simulationArguments.getMonitorPeakMemory()
					+ " milliseconds");
			MemoryUtil.monitorPeakMemoryUsage(simulationArguments
					.getMonitorPeakMemory());
		}

		addInfo(outputType, InfoType.INFO, "-Initialization...");

		// TODO: remove the following lines after checking all the dependencies
		// to them!!!
		if (simulationArguments.isTime()) {
			setTimeLength(simulationArguments.getTimeLength());
		} else {
			setEvent(simulationArguments.getMaxNumberOfEvents());
			println("*Warning* No time limit.");
		}

		this.argumentsSet = true;
	}

	// TODO separate
	public final void readSimulatonFile(InfoType outputType) {
		if (!this.argumentsSet) {
			throw new RuntimeException(
					"Simulator Arguments must be set before reading the simulation file!");
		}

		// TODO: move the following lines to a reset method. they should not be
		// part of reading the simulation file!
		// They are here now because this method used to parse arguments!
		resetBar();
		myKappaSystem.getObservables().setOcamlStyleObsName(
				simulationArguments.isOcamlStyleObservableNames());
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
			// parser.setForwarding(simulationArguments.isForwardOnly());
			parser.parse(outputType);
		} catch (Exception e) {
			println("Error in file \"" + simulationArguments.getInputFilename()
					+ "\" :");

			if (printStream != null) {
				e.printStackTrace(printStream);
			}
			e.printStackTrace();
			// throw new IllegalArgumentException(e);
		}
	}

	public final boolean isEndSimulation(double currentTime, long count) {
		if (System.currentTimeMillis() - clockStamp > simulationArguments
				.getWallClockTimeLimit()) {
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
				println();
				return true;
			}
		} else if (count < simulationArguments.getMaxNumberOfEvents()) {
			if (count >= nextStep) {
				outputBar();
				nextStep += step;
			}
			return false;
		} else {
			outputBar();
			println();
			return true;
		}
	}

	public final void resetBar() {
		nextStep = step;
	}

	private void checkAndInitStoriesBar() {
		if (simulationArguments.isStorify()) {
			stepStories = simulationArguments.getIterations() * 1.0
					/ simulationArguments.getClockPrecision();
			nextStepStories = stepStories;
		}
	}

	public void checkStoriesBar(int i) {
		if (i >= nextStepStories) {
			double r;
			if (stepStories >= 1)
				r = 1;
			else
				r = simulationArguments.getClockPrecision() * 1.0
						/ simulationArguments.getIterations();
			while (r > 0) {
				print("#");
				r = r - 1;
			}
			nextStepStories += stepStories;
		}
	}

	public final void setEvent(long event) {
		checkAndInitStoriesBar();
		step = event * 1.0 / simulationArguments.getClockPrecision();
		nextStep = step;
		simulationArguments.setMaxNumberOfEvents(event);
	}

	public final void setTimeLength(double timeLength) {
		checkAndInitStoriesBar();
		step = timeLength / simulationArguments.getClockPrecision();
		nextStep = step;
		simulationArguments.setTimeLength(timeLength);
		simulationArguments.setTime(true);
	}

	public final void initIterations() {
		this.timeStamps =  new ArrayList<Double>();
		this.runningMetrics = new ArrayList<List<RunningMetric>>();
		int observable_num = myKappaSystem.getObservables()
				.getUniqueComponentList().size();
		for (int i = 0; i < observable_num; i++) {
			runningMetrics.add(new ArrayList<RunningMetric>());
		}

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

	private final double getTimeSampleMin(double fullTime) {
		if (simulationArguments.getPoints() > 0) {
			return (fullTime / simulationArguments.getPoints());
		} else {
			return (fullTime / DEFAULT_NUMBER_OF_POINTS);
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

	private final void setFocusOn(String fileNameFocusOn) throws Exception {
		KappaFileReader kappaFileReader = new KappaFileReader(fileNameFocusOn);
		KappaFile kappaFile = kappaFileReader.parse();
		KappaModel model = (new KappaModelCreator(simulationArguments))
				.createModel(kappaFile);
		List<CRule> ruleList = (new RuleBuilder(new KappaSystem(this)))
				.build(new RulesParagraphReader(model, simulationArguments,
						new AgentFactory(false)).readComponent(kappaFile
						.getRules()));

		myKappaSystem.getContactMap().setSimulationData(this);
		if (ruleList != null && !ruleList.isEmpty()) {
			myKappaSystem.getContactMap().setFocusRule(ruleList.get(0));
			myKappaSystem.getContactMap()
					.setMode(EContactMapMode.AGENT_OR_RULE);
		}
	}

	// **************************************************************************
	//
	// INFO OUTPUT
	// 
	// TODO separate info output

	public final void addInfo(InfoType outputType, InfoType type, String message) {
		if (!simulationArguments.isShortConsoleOutput()) {
			outputType = InfoType.OUTPUT;
		}
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

	// **************************************************************************
	//
	// CONSOLE OUTPUT
	// 
	// TODO separate console output

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

	public final void outputData() {
		outputRules();
		outputPertubation();
		outputSolution();
	}

	private final void outputSolution() {
		println("INITIAL SOLUTION:");
		for (SolutionLine sl : (myKappaSystem.getSolution())
				.getSolutionLines()) {
			print("-");
			print("" + sl.getCount());
			print("*[");
			print(sl.getLine());
			println("]");
		}
	}

	private final void outputRules() {
		for (CRule rule : myKappaSystem.getRules()) {
			// int countAgentsInLHS = rule.getCountAgentsLHS();
			// int indexNewAgent = countAgentsInLHS;

			for (CAction action : rule.getActionList()) {
				switch (CActionType.getById(action.getTypeId())) {
				case BREAK: {
					CSite siteTo = ((CSite) action.getSiteFrom().getLinkState()
							.getConnectedSite());
					if (action.getSiteFrom().getParentAgent()
							.getIdInRuleHandside() < siteTo.getParentAgent()
							.getIdInRuleHandside()) {
						// BRK (#0,a) (#1,x)
						print("BRK (#");
						print(""
								+ (action.getSiteFrom().getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(action.getSiteFrom().getName());
						print(") ");
						print("(#");
						print(""
								+ (siteTo.getParentAgent().getIdInRuleHandside() - 1));
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
							+ (action.getAgentFrom().getIdInRuleHandside() - 1));
					break;
				}
				case ADD: {
					// ADD a#0(x)
					print("ADD " + action.getAgentTo().getName() + "#");

					print("" + (action.getAgentTo().getIdInRuleHandside() - 1));
					print("(");
					int i = 1;
					for (CSite site : action.getAgentTo().getSites()) {
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
					CSite siteTo = ((CSite) action.getSiteFrom().getLinkState()
							.getConnectedSite());
					if (action.getSiteFrom().getParentAgent()
							.getIdInRuleHandside() > siteTo.getParentAgent()
							.getIdInRuleHandside()) {
						print("BND (#");
						print(""
								+ (action.getSiteFrom().getParentAgent()
										.getIdInRuleHandside() - 1));
						print(",");
						print(action.getSiteFrom().getName());
						print(") ");
						print("(#");
						print(""
								+ (action.getSiteTo().getParentAgent()
										.getIdInRuleHandside() - 1));
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
							+ (action.getSiteFrom().getParentAgent()
									.getIdInRuleHandside() - 1));
					print(",");
					print(action.getSiteFrom().getName());
					print(") with ");
					print(action.getSiteTo().getInternalState().getName());
					println();
					break;
				}
				}

			}

			StringBuffer sb = new StringBuffer();
			sb.append(SimulationUtils.printPartRule(rule.getLeftHandSide(),
					isOcamlStyleObsName()));
			sb.append("->");
			sb.append(SimulationUtils.printPartRule(rule.getRightHandSide(),
					isOcamlStyleObsName()));
			StringBuffer ch = new StringBuffer();
			for (int j = 0; j < sb.length(); j++)
				ch.append("-");

			println(ch.toString());
			if (rule.getName() != null) {
				print(rule.getName());
				print(": ");
			}
			print(sb.toString());
			println();
			println(ch.toString());
			println();
			println();
		}
	}

	private final void outputPertubation() {
		println("PERTURBATIONS:");

		for (CPerturbation perturbation : myKappaSystem.getPerturbations()) {
			println(perturbationToString(perturbation));
		}

	}

	private final String perturbationToString(CPerturbation perturbation) {
		StringBuffer sb = new StringBuffer();
		sb.append("-");
		String greater = (perturbation.getGreater()) ? "> " : "< ";
		switch (perturbation.getType()) {
		case TIME: {
			sb.append("Whenever current time ");
			sb.append(greater);
			sb.append(perturbation.getTimeCondition());
			break;
		}
		case NUMBER: {
			sb.append("Whenever [");
			sb.append(myKappaSystem.getObservables().getComponentList().get(
					perturbation.getObsNameID()).getName());
			sb.append("] ");
			sb.append(greater);
			sb.append(SimulationUtils
					.perturbationParametersToString(perturbation
							.getLHSParametersList()));
			break;
		}
		}

		sb.append(" do kin(");
		sb.append(perturbation.getPerturbationRule().getName());
		sb.append("):=");
		sb.append(SimulationUtils.perturbationParametersToString(perturbation
				.getRHSParametersList()));

		return sb.toString();
	}

	private void outputBar() {
		if (!simulationArguments.isShortConsoleOutput()
				|| !simulationArguments.isStorify())
			print("#");
	}

	public final void createTMPReport() {
		// model.getSimulationData().updateData();
		PlxTimer timer = new PlxTimer();
		timer.startTimer();

		int number_of_observables = myKappaSystem.getObservables()
				.getUniqueComponentList().size();
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
						StringBuffer sb = new StringBuffer();
						sb.append(timeStamps.get(timeStepCounter));
						sb.append(" ");
						sb.append(runningMetrics.get(observable_num).get(
								timeStepCounter).getMin());
						sb.append(" ");
						sb.append(runningMetrics.get(observable_num).get(
								timeStepCounter).getMax());
						sb.append(" ");
						sb.append(runningMetrics.get(observable_num).get(
								timeStepCounter).getMean());
						sb.append(" ");
						sb.append(runningMetrics.get(observable_num).get(
								timeStepCounter).getStd());

						writer.write(sb.toString());
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

	// 
	// XML OUTPUT
	// 
	// TODO separate xml output

	private final String getXmlSessionPath() {
		if (simulationArguments.getXmlSessionPath().length() > 0) {
			return simulationArguments.getXmlSessionPath() + File.separator
					+ simulationArguments.getXmlSessionName();
		} else {
			return simulationArguments.getXmlSessionName();
		}
	}

	public final void createXMLOutput(Writer outstream)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, StoryStorageException {
		PlxTimer timer = new PlxTimer();
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = output.createXMLStreamWriter(outstream);

		writer.writeStartDocument("utf-8", "1.0");
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			writer.writeStartElement("ComplxSession");
			// simplxSession.setAttribute("xsi:schemaLocation",
			// "http://synthesisstudios.com ComplxSession.xsd");

			/*
			 * simplxSession.setAttribute("xsi:schemaLocation","http://synthesisstudios.com ComplxSession.xsd"
			 * );
			 * 
			 * // "unexpected element (uri:"", local:"ComplxSession //
			 * "). Expected elements are <{http://plectix.synthesisstudios.com/schemas/kappasession}ComplxSession>,<{http://plectix.synthesisstudios.com/schemas/kappasession}KappaResults>,<{http://plectix.synthesisstudios.com/schemas/kappasession}SimplxSession>"
			 * Element element = doc.createElement("Refinement");
			 * element.setAttribute("Name", "DAG");
			 * simplxSession.appendChild(element); element =
			 * doc.createElement("Refinement");
			 * element.setAttribute("Name","Maximal");
			 * simplxSession.appendChild(element);
			 * 
			 * Element ruleSet = doc.createElement("RuleSet");
			 * ruleSet.setAttribute("Name", "Original"); addRulesToXML(ruleSet,
			 * myKappaSystem.getRules().size(), doc);
			 * simplxSession.appendChild(ruleSet);
			 */
		} else {
			writer.writeStartElement("SimplxSession");
			writer.writeAttribute("xsi:schemaLocation", "http://plectix.synthesisstudios.com SimplxSession.xsd");
			/*
			 * simplxSession.setAttribute("xsi:schemaLocation",
			 * "http://plectix.synthesisstudios.com SimplxSession.xsd");
			 */
		}
		writer.setDefaultNamespace("http://plectix.synthesisstudios.com/schemas/kappasession");
		writer.writeDefaultNamespace("http://plectix.synthesisstudios.com/schemas/kappasession");
		writer.writeNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");

		writer.writeAttribute("CommandLine", simulationArguments.getCommandLineString());

		writer.writeAttribute("InputFile", simulationArguments.getInputFilename());
		Date d = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		writer.writeAttribute("TimeStamp", df.format(d));

		timer.startTimer();

		// TODO check it
		if (simulationArguments.isSubViews()) {
			myKappaSystem.getSubViews().createXML(writer);
		}
		
		if(simulationArguments.isEnumerationOfSpecies()){
			myKappaSystem.getEnumerationOfSpecies().writeToXML(writer);
		}

		if(simulationArguments.isLocalViews()){
			myKappaSystem.getLocalViews().writeToXML(writer);
		}
		
		if (simulationArguments.isQualitativeCompression() || simulationArguments.isQuantitativeCompression()){
			myKappaSystem.getRuleCompressionBuilder().writeToXML(writer, isOcamlStyleObsName());
		}

		// TODO check it
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
			myKappaSystem.getContactMap().createXML(writer);
		}

		if (simulationArguments.isActivationMap()) {
			myKappaSystem.getInfluenceMap().createXML(
					writer,
					myKappaSystem.getRules().size(),
					myKappaSystem.getObservables()
							.getConnectedComponentListForXMLOutput(),
					simulationArguments.isInhibitionMap(), myKappaSystem,
					simulationArguments.isOcamlStyleObservableNames());
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for influence map:");
		}

		// //TODO STORIES!!!!!!!!!!!
		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.STORIFY) {
			
			myKappaSystem.getStories().createXML(writer, myKappaSystem, simulationArguments.getIterations());
		

		}

		if (snapshots != null) {
			timer.startTimer();
			writeSnapshotsToXML(writer);
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for snapshots:");
		}

		if (simulationArguments.getSimulationType() == SimulationArguments.SimulationType.SIM) {
			int obsCountTimeListSize = myKappaSystem.getObservables()
					.getCountTimeList().size();
			writer.writeStartElement("Simulation");
			writer.writeAttribute("TotalEvents", Long
					.toString(simulationArguments.getMaxNumberOfEvents()));
			writer.writeAttribute("TotalTime", DecimalFormatter.toStringWithSetNumberOfSignificantDigits(simulationArguments.getTimeLength(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));
			writer.writeAttribute("InitTime", DecimalFormatter
					.toStringWithSetNumberOfSignificantDigits(
							simulationArguments.getInitialTime(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			writer.writeAttribute("TimeSample", DecimalFormatter.toStringWithSetNumberOfSignificantDigits(myKappaSystem
							.getObservables().getTimeSampleMin(),
							NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));

			
			List<IObservablesComponent> list = myKappaSystem.getObservables()
					.getUniqueComponentList();
			for (int i = list.size() - 1; i >= 0; i--) {
				createElement(list.get(i), writer);
			}

			// CData right
			timer.startTimer();
			writer.writeStartElement("CSV");
			StringBuffer cdata = new StringBuffer();
			for (int i = 0; i < obsCountTimeListSize; i++) {
				cdata
						.append(appendData(myKappaSystem.getObservables(),
								list, i));
			}
			writer.writeCData(cdata.toString());
			writer.writeEndElement();
			writer.writeEndElement();
			stopTimer(InfoType.OUTPUT, timer,
					"-Building xml tree for data points:");
		}

		appendInfo(writer);

		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();

	}

	private void writeSnapshotsToXML(XMLStreamWriter writer)
			throws XMLStreamException {
		for (CSnapshot snapshot : snapshots) {
			writer.writeStartElement("FinalState");
			writer.writeAttribute("Time", String.valueOf(snapshot
					.getSnapshotTime()));
			List<SnapshotElement> snapshotElementList = snapshot
					.getSnapshotElements();
			for (SnapshotElement se : snapshotElementList) {
				writer.writeStartElement("Species");
				writer.writeAttribute("Kappa", se.getComponentsName());
				writer.writeAttribute("Number", String.valueOf(se
						.getCount()));
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
	}

	private final void appendInfo(XMLStreamWriter writer)
			throws XMLStreamException {
		writer.writeStartElement("Log");
		for (Info info : infoList) {
			writer.writeStartElement("Entry");
			writer.writeAttribute("Position", info.getPosition());
			writer.writeAttribute("Count", info.getCount());
			writer.writeAttribute("Message", info.getMessageWithTime());
			writer.writeAttribute("Type", info.getType().toString());
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	private final void createElement(IObservablesComponent obs,
			XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("Plot");
		String obsName = obs.getName();
		if (obsName == null)
			obsName = obs.getLine();

		if (obs instanceof IObservablesConnectedComponent) {
			writer.writeAttribute("Type", "OBSERVABLE");
			writer.writeAttribute("Text", '[' + obsName + ']');
		} else {
			writer.writeAttribute("Type", "RULE");
			writer.writeAttribute("Text", obsName);
		}
		writer.writeEndElement();
	}



	public final void outputData(long count)
			throws ParserConfigurationException, TransformerException,
			XMLStreamException, IOException, StoryStorageException {
		PlxTimer timerOutput = new PlxTimer();
		timerOutput.startTimer();
		createXMLOutput(new BufferedWriter(new FileWriter(getXmlSessionPath())));
		stopTimer(InfoType.OUTPUT, timerOutput,
				"-Results outputted in xml session:");

	}

	private final StringBuffer appendData(CObservables obs,
			List<IObservablesComponent> list, int index) {
		StringBuffer cdata = new StringBuffer();
		cdata.append(DecimalFormatter.toStringWithSetNumberOfSignificantDigits(
				myKappaSystem.getObservables().getCountTimeList().get(index),
				NUMBER_OF_SIGNIFICANT_DIGITS).replace(",", "."));
		for (int j = list.size() - 1; j >= 0; j--) {
			cdata.append(",");
			IObservablesComponent oCC = list.get(j);
			cdata.append(getItem(obs, index, oCC));
		}
		cdata.append("\n");
		return cdata;
	}

	private final String getItem(CObservables obs, int index,
			IObservablesComponent oCC) {
		if (oCC.isUnique()) {
			return oCC.getStringItem(index, obs);
		}

		long value = 1;
		for (IObservablesConnectedComponent cc : obs
				.getConnectedComponentList()) {
			if (cc.getId() == oCC.getId()) {
				value *= cc.getItem(index, obs);
			}
		}

		return Double.valueOf(value).toString();
	}

	/**
	 * This method creates string representation of given rule, which using in
	 * XML output
	 * 
	 * @param rule
	 * @param isOcamlStyleObsName
	 *            <tt>true</tt> if option <code>--ocaml-style-obs-name</code> is
	 *            enabled, otherwise <tt>false</tt>
	 * @return string representation of given rule
	 */
	public static final String getData(CRule rule, boolean isOcamlStyleObsName) {
		StringBuffer sb = new StringBuffer();
		sb.append(SimulationUtils.printPartRule(rule.getLeftHandSide(),
				isOcamlStyleObsName));
		sb.append("->");
		sb.append(SimulationUtils.printPartRule(rule.getRightHandSide(),
				isOcamlStyleObsName));
		return sb.toString();
	}

	// **************************************************************************
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

	public final List<Double> getTimeStamps() {
		return timeStamps;
	}

	public List<Double> getSnapshotTimes() {
		return snapshotTimes;
	}

	public void setSnapshotTimes(List<Double> snapshotTimes) {
		this.snapshotTimes = snapshotTimes;
	}

	public final List<List<RunningMetric>> getRunningMetrics() {
		return runningMetrics;
	}

	public final void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}

	public final void setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
	}
}

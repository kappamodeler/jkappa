package com.plectix.simulator.simulator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.staticanalysis.Snapshot;
import com.plectix.simulator.staticanalysis.stories.storage.StoriesAgentTypesStorage;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.Info.InfoType;

public final class SimulationData {
	private List<Snapshot> snapshots = null;
	private final List<Info> infoList = new ArrayList<Info>();
	private List<Double> snapshotTimes = null;

	private boolean argumentsInitialized = false;

	private SimulationArguments simulationArguments = new SimulationArguments();
	private KappaModel initialModel = null;
	private final KappaSystem kappaSystem = new KappaSystem(this);

	private final ConsoleOutputManager consoleOutputManager = new ConsoleOutputManager(this); 
	
	public final KappaSystem getKappaSystem() {
		return kappaSystem;
	}

	public final KappaModel getInitialModel() {
		return initialModel;
	}

	public final void setInitialModel(KappaModel kappaModel) {
		initialModel = kappaModel;
	}

	public final void clear() throws RuntimeException, SimulationDataFormatException, IOException {
		kappaSystem.clearRules();
		kappaSystem.getObservables().resetLists();
		kappaSystem.getSolution().clear();
		kappaSystem.getSolution().clearSolutionLines();
		kappaSystem.resetIdGenerators();
		kappaSystem.clearPerturbations();
	}

	public final void setSimulationArguments(InfoType outputType,
			SimulationArguments arguments) {
		this.simulationArguments = arguments;
		this.simulationArguments.updateRandom();

		
		if (simulationArguments.isVersion()) {
			consoleOutputManager.println("Java Simulator Revision: "
					+ BuildConstants.BUILD_SVN_REVISION);
		}

		// let's dump the command line arguments
		if (simulationArguments.getCommandLineString() != null) {
			consoleOutputManager.println("Java " + simulationArguments.getCommandLineString());
		}

		if (simulationArguments.getMonitorPeakMemory() > 0) {
			consoleOutputManager.println("Turning memory monitoring on using a period of "
					+ simulationArguments.getMonitorPeakMemory()
					+ " milliseconds");
			MemoryUtil.monitorPeakMemoryUsage(simulationArguments
					.getMonitorPeakMemory());
		}

		this.addInfo(InfoType.INFO, "-Initialization...");

		this.argumentsInitialized = true;
	}

	public final void checkOutputFinalState(double currentTime) {
		if (simulationArguments.isOutputFinalState()) {
			createSnapshots(currentTime);
		}
	}

	public final void createSnapshots(double currentTime) {
		addSnapshot(new Snapshot(this, currentTime));
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

	
	public final void addInfo(InfoType type, String message) {
		consoleOutputManager.addAdditionalInfo(type, message);
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

	// **************************************************************************
	// GETTERS AND SETTERS
	//

	// TODO THIS IS VERY BAD, I GONNA CRY, we need to move this part later
	public final void setTimeLimit(double timeLimit) {
		double oldValue = simulationArguments.getTimeLimit();
		simulationArguments.setTime(true);
		simulationArguments.setTimeLimit(timeLimit);
		if (Math.abs(timeLimit - oldValue) > 0.001) {
			kappaSystem.getObservables().init(timeLimit, 
					simulationArguments.getInitialTime(), 
					simulationArguments.getMaxNumberOfEvents(),
					simulationArguments.getPoints(),
					simulationArguments.isTime());
		}
	}
	
	public final SimulationArguments getSimulationArguments() {
		return simulationArguments;
	}

	public final void addSnapshot(Snapshot snapshot) {
		if (snapshots == null) {
			snapshots = new ArrayList<Snapshot>();
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

	public final List<Double> getSnapshotTimes() {
		return snapshotTimes;
	}

	public void setSnapshotTimes(List<Double> snapshotTimes) {
		this.snapshotTimes = snapshotTimes;
	}

	public final void setConsolePrintStream(PrintStream printStream) {
		consoleOutputManager.setPrintStream(printStream);
	}

	public StoriesAgentTypesStorage getStoriesAgentTypesStorage() {
		return kappaSystem.getStories() != null ? kappaSystem.getStories()
				.getStoriesAgentTypesStorage() : null;
	}

	public ConsoleOutputManager getConsoleOutputManager() {
		return consoleOutputManager;
	}

	public boolean argumentsInitialized() {
		return argumentsInitialized;
	}

	public List<Info> getInfo() {
		return infoList;
	}
	
	public List<Snapshot> getSnapshots() {
		return snapshots;
	}
}

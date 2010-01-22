package com.plectix.simulator.simulator;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.cli.HelpFormatter;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.controller.SimulatorInterface;
import com.plectix.simulator.controller.SimulatorResultsData;
import com.plectix.simulator.controller.SimulatorStatusInterface;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.io.SimulationDataReader;
import com.plectix.simulator.io.xml.SimulationDataXMLWriter;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.simulationclasses.perturbations.TimeCondition;
import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.RuleApplicator;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.stories.storage.EventBuilder;
import com.plectix.simulator.streaming.LiveData;
import com.plectix.simulator.streaming.LiveDataSourceInterface;
import com.plectix.simulator.streaming.LiveDataStreamer;
import com.plectix.simulator.util.MemoryUtil;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;
import com.plectix.simulator.util.MemoryUtil.PeakMemoryUsage;
import com.plectix.simulator.util.io.PlxLogger;

public final class Simulator implements SimulatorInterface {

	private static final String NAME = "Java Simulator JSIM";
	private static final String INTRO_MESSAGE = "JSIM: Build on "
			+ BuildConstants.BUILD_DATE + " from Revision "
			+ BuildConstants.BUILD_SVN_REVISION + ", JRE: "
			+ System.getProperty("java.vendor") + " "
			+ System.getProperty("java.version");
	private static final String STATUS_READING_KAPPA = "Reading Kappa input";
	private static final String STATUS_INITIALIZING = "Initializing";
	private static final String STATUS_RUNNING = "Running";
	private static final String STATUS_WRAPPING = "Wrapping the simulation results";
	private static final String STATUS_IDLE = "Idle";
	private static final String STATUS_EXCEPTION = "Exception thrown: ";

	private static final PlxLogger LOGGER = ThreadLocalData
			.getLogger(Simulator.class);

	/** Use synchronized (statusLock) when changing the value of this variable */
	private double currentTime = 0.0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private long currentEventNumber = 0;
	/** Use synchronized (statusLock) when changing the value of this variable */
	private int currentIterationNumber = 0;

	private final SimulationData simulationData = new SimulationData();
	private final SimulatorStatus simulatorStatus = new SimulatorStatus();
	private final SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
	private final LiveDataStreamer liveDataStreamer = new LiveDataStreamer();

	/**
	 * Object to lock when we are reading variables to compute the current
	 * status
	 */
	private final Object statusLock = new Object();
	private final RuleApplicator ruleApplicator = new RuleApplicator(
			simulationData);

	//---------------------------------------------------
	
	private OperationType latestOperationDone;
	
	public final OperationType getLatestOperation() {
		return latestOperationDone;
	}
	
	//---------------------------------------------------
	
	public Simulator() {
		super();
		simulatorStatus.setStatusMessage(STATUS_IDLE);
	}

	/**
	 * We assume that this method is called from a separate thread than the
	 * simulation thread. We also assume that there can be only one thread
	 * calling this method at a time.
	 * 
	 */
	public final SimulatorStatusInterface getStatus() {
		synchronized (statusLock) {
			// save the current state variables in the status object and use
			// them below
			simulatorStatus.setCurrentTime(currentTime);
			simulatorStatus.setCurrentEventNumber(currentEventNumber);
			simulatorStatus.setCurrentIterationNumber(currentIterationNumber);
		}

		// let's compute our progress:
		double progress = simulatorStatus.getProgress();
		if (Double.isNaN(progress) || progress < 1.0) {
			SimulationArguments simulationArguments = simulationData
					.getSimulationArguments();
			if (simulationArguments.isTime()) {
				progress = simulatorStatus.getCurrentTime()
						/ simulationArguments.getTimeLimit();
			} else {
				progress = simulatorStatus.getCurrentEventNumber() * 1.0
						/ simulationArguments.getMaxNumberOfEvents();
			}

			if (simulationArguments.storiesModeIsOn()) {
				progress = (progress + simulatorStatus
						.getCurrentIterationNumber())
						/ simulationArguments.getIterations();
				if (progress > 1.0) {
					progress = 1.0;
				}
			}
			simulatorStatus.setProgress(progress);
		}

		return simulatorStatus;
	}

	/**
	 * Returns the live data
	 * 
	 * @param liveData
	 * @return
	 */
	public final LiveData getLiveData() {
		return liveDataStreamer.getLiveData();
	}

	@Override
	public final void cleanUpAfterException(Exception exception) {
		simulatorStatus.setStatusMessage(STATUS_EXCEPTION
				+ exception.getClass().getName());
		simulatorStatus.setProgress(1.0);
	}

	private final void endMerge(PlxTimer timer) {
		SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, timer,
				"-Merge stories:");
	}

	private final void endSimulation(InfoType outputType, boolean noRulesLeft,
			PlxTimer timer) {
		outputType = simulationData.getSimulationArguments()
				.getOutputTypeForAdditionalInfo();
		SimulationClock.stopTimer(simulationData, outputType, timer, "-Simulation:");

		switch (outputType) {
		case OUTPUT:
			if (noRulesLeft) {
				LOGGER.info("end of simulation: there are no active rules");
			} else {
				LOGGER.info("end of simulation: time");
			}
			break;
		}
	}

	public final void reInitializeSimulationData() throws RuntimeException,
			SimulationDataFormatException, IOException {
		synchronized (statusLock) {
			currentTime = 0.0;
		}

		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"-Reset simulation data.");
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"-Initialization...");

		simulationData.clear();
		this.readInputKappaFile();
		this.initializeKappaSystem();
	}

	/**
	 * This method only reads Kappa File and builds the KappaSystem object We
	 * should be able to run it independently
	 * 
	 * @throws RuntimeException
	 * @throws SimulationDataFormatException
	 * @throws IOException
	 */
	public final void readInputKappaFile() throws RuntimeException,
			SimulationDataFormatException, IOException {
		PlxTimer readingKappaTimer = new PlxTimer();
		readingKappaTimer.startTimer();

		simulatorStatus.setStatusMessage(STATUS_READING_KAPPA);
		simulationData.getConsoleOutputManager().addAdditionalInfo(InfoType.INFO,
				"--Computing initial state");
		
		new SimulationDataReader(simulationData).readAndCompile();

		SimulationClock.stopTimer(simulationData, InfoType.OUTPUT, readingKappaTimer,
				"-Reading Kappa input:");
	}

	public final void loadSimulationArguments(SimulatorInputData simulatorInputData) {
		SimulationArguments simulationArguments = simulatorInputData
				.getSimulationArguments();
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager(); 
		
		if (simulationArguments.isNoDumpStdoutStderr()) {
			consoleOutputManager.setPrintStream(null);
		}

		PrintStream printStream = consoleOutputManager.getPrintStream();
		// do not print anything above because the line above might have turned
		// the printing off...

		if (simulationArguments.isHelp()) {
			if (printStream != null) {
				PrintWriter printWriter = new PrintWriter(printStream);
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH,
						SimulationMain.COMMAND_LINE_SYNTAX, null,
						SimulatorOption.COMMAND_LINE_OPTIONS,
						HelpFormatter.DEFAULT_LEFT_PAD,
						HelpFormatter.DEFAULT_DESC_PAD, null, false);
				printWriter.flush();
			}
		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, simulationArguments);
	}

	// TODO
	public final void initializeKappaSystem() {
		PlxTimer initializationTimer = new PlxTimer();
		initializationTimer.startTimer();
		
		simulatorStatus.setStatusMessage(STATUS_INITIALIZING);
		simulationData.getKappaSystem().initialize(InfoType.OUTPUT);

		SimulationClock.stopTimer(simulationData, InfoType.OUTPUT,
				initializationTimer, "-Initialization:");
	}

	public final void outputCurrentSimulationDataToXML() throws Exception {
		(new SimulationDataXMLWriter(simulationData)).outputXMLData();
	}

	private final void checkAndOutputMemory() {
		PeakMemoryUsage peakMemoryUsage = MemoryUtil.getPeakMemoryUsage();
		if (peakMemoryUsage != null) {
			simulationData.getConsoleOutputManager().addAdditionalInfo(InfoType.INFO,
					"-Peak Memory Usage (in bytes): "
							+ peakMemoryUsage
							+ " [period= "
							+ simulationData.getSimulationArguments()
									.getMonitorPeakMemory() + " milliseconds]");
		}
	}

	private final boolean hasNoNeedToRunAnything(
			SimulationArguments simulationArguments) {
		return simulationArguments.isGenereteMap()
				|| (simulationArguments.getSimulationType() == SimulationType.CONTACT_MAP)
				|| simulationArguments.debugModeIsOn();
	}

	public final void run(SimulatorInputData simulatorInputData)
			throws Exception {
		// add info about JSIM:
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		simulationData.setConsolePrintStream(simulatorInputData.getPrintStream());

		consoleOutputManager.addAdditionalInfo(InfoType.INFO, INTRO_MESSAGE);

		this.loadSimulationArguments(simulatorInputData);
		SimulationClock clock = this.createClock();

		this.readInputKappaFile();
		this.initializeKappaSystem();

		if (simulationData.getSimulationArguments().isCompile()) {
			consoleOutputManager.outputData();
			return;
		}

		if (!this.hasNoNeedToRunAnything(simulationData.getSimulationArguments())) {
			if (simulationData.getSimulationArguments().storiesModeIsOn()) {
				runStories(clock);
			} else {
				runSimulation(clock);
			}
		}

		// Let's see if we monitor peak memory usage
		this.checkAndOutputMemory();

		// Output XML data:
		this.outputCurrentSimulationDataToXML();

		simulatorStatus.setStatusMessage(STATUS_IDLE);
	}

	private final SimulationClock createClock() {
		SimulationArguments arguments = simulationData.getSimulationArguments();
		SimulationClock clock = new SimulationClock(simulationData);
		
		// TODO we may do it in constructor of SimulationClock
		if (simulationData.getSimulationArguments().isTime()) {
			clock.setTimeLimit(arguments.getTimeLimit());
		} else {
			clock.setEvent(arguments.getMaxNumberOfEvents());
			simulationData.getConsoleOutputManager().println("*Warning* No time limit.");
		}
		
		return clock;
	}
	
	public final void runSimulation() throws Exception {
		this.runSimulation(this.createClock());
	}
	
	public final void runSimulation(SimulationClock clock) throws Exception {
		clock.setClockStamp(System.currentTimeMillis());

		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO, "-Simulation...");

		PlxTimer simulationTimer = new PlxTimer();
		simulationTimer.startTimer();

		int seed = simulationData.getSimulationArguments().getSeed();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());

		synchronized (statusLock) {
			currentEventNumber = 0;
		}

		long clashesNumber = 0;
		long maxClashes = 0;
		boolean isEndRules = false;
		boolean isCalculateObs = false;
		LiveDataSourceInterface liveDataSource = new ObservablesLiveDataSource(
				simulationData.getKappaSystem().getObservables());
		liveDataStreamer.reset(simulationData.getSimulationArguments()
				.getLiveDataInterval(), simulationData.getSimulationArguments()
				.getLiveDataPoints(), simulationData.getSimulationArguments()
				.getLiveDataConsumerClassname(), liveDataSource);
		simulatorStatus.setStatusMessage(STATUS_RUNNING);

		simulationData.getKappaSystem().getObservables().addInitialState();
		while (!clock.isEndSimulation(currentTime, currentEventNumber)
				&& maxClashes <= simulationData.getSimulationArguments()
						.getMaxClashes()) {
			if (Thread.interrupted()) {
				// TODO: Do any necessary clean-up and collect data we can
				// return
				consoleOutputManager
						.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}

			while (simulationData.checkSnapshots(currentTime)) {
				simulationData.createSnapshots(currentTime);
			}

			simulationData.getKappaSystem().checkPerturbation(currentTime);
			simulationData.getKappaSystem().updateRuleActivities();
			Rule rule = simulationData.getKappaSystem().getRandomRule();

			if (rule == null) {
				List<ComplexPerturbation<?, ?>> perturbations = simulationData
						.getKappaSystem().getPerturbations();
				double tmpTime = simulationData.getSimulationArguments()
						.getTimeLimit();
				ComplexPerturbation<TimeCondition, ?> tmpPerturbation = null;

				for (ComplexPerturbation<?, ?> perturbation : perturbations) {
					if (perturbation.getCondition() instanceof TimeCondition) {
						// TODO awful type cast
						ComplexPerturbation<TimeCondition, ?> castedPerturbation = (ComplexPerturbation<TimeCondition, ?>) perturbation;
						double conditionTimeLimit = castedPerturbation
								.getCondition().getTimeLimit();
						if (conditionTimeLimit > currentTime
								&& conditionTimeLimit < tmpTime) {
							tmpTime = conditionTimeLimit;
							tmpPerturbation = castedPerturbation;
						}
					}
				}
				// TODO is it right way: to add timeSampleMin to currentTime for
				// applying the perturbation?
				if (tmpPerturbation != null) {
					currentTime = tmpPerturbation.getCondition().getTimeLimit()
							+ simulationData.getKappaSystem().getObservables()
									.getTimeSampleMin();
					simulationData.getKappaSystem().checkPerturbation(
							currentTime);
					rule = simulationData.getKappaSystem().getRandomRule();
				} else {
					isEndRules = true;
					clock.setTimeLimit(currentTime);
					consoleOutputManager.println("#");
					break;
				}
			}

			if (!rule.hasInfiniteRate()) {
				synchronized (statusLock) {
					currentTime += simulationData.getKappaSystem()
							.getTimeValue();
				}
				liveDataStreamer.addNewDataPoint(currentEventNumber,
						currentTime);
			}
			if (clock.isEndSimulation(currentTime, currentEventNumber)) {
				if (simulationData.getSimulationArguments().isTime()) {
					simulationData.checkOutputFinalState(simulationData
							.getSimulationArguments().getTimeLimit());
				} else {
					simulationData.checkOutputFinalState(currentTime);
				}

			}
			if (isCalculateObs 
					&& simulationData.getSimulationArguments().getReportExactSampleTime()
					&& simulationData.getSimulationArguments().isTime()) {
				simulationData.getKappaSystem().getObservables().calculateExactSampleObs(
								currentTime,
								currentEventNumber,
								simulationData.getSimulationArguments().isTime());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rule: " + rule.getName());
			}

			List<Injection> injectionsList = simulationData.getKappaSystem()
					.chooseInjectionsForRuleApplication(rule);
			isCalculateObs = false;
			if (injectionsList != null) {
				// negative update
				maxClashes = 0;
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("negative update");

				synchronized (statusLock) {
					currentEventNumber++;
				}

				List<Injection> newInjections = ruleApplicator.applyRule(rule,
						injectionsList, simulationData);
				if (newInjections != null) {

					UpdatesPerformer.doNegativeUpdate(newInjections);

					// positive update
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("positive update");
					}

					// on the 4th mode we should set only super injections, so
					// we do it manually
					// directly after the rule application
					if (simulationData.getSimulationArguments()
							.getOperationMode() != OperationMode.FOURTH) {
						simulationData.getKappaSystem().doPositiveUpdate(rule,
								newInjections);
					}

					simulationData.getKappaSystem().getSolution()
							.flushPoolContent(rule.getPool());
					isCalculateObs = true;

					// simulationData.getKappaSystem().getObservables().calculateObs(currentTime,
					// currentEventNumber,
					// simulationData.getSimulationArguments().isTime());
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Rule rejected");
					}
				}
			} else {
				consoleOutputManager.addAdditionalInfo(InfoType.INTERNAL,
						"Application of rule exp is clashing");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Clash");
				}
				clashesNumber++;
				maxClashes++;
			}
			if (!clock.isEndSimulation(currentTime, currentEventNumber)
					&& maxClashes > simulationData.getSimulationArguments()
							.getMaxClashes()) {
				simulationData.checkOutputFinalState(currentTime);

			}

			if (isCalculateObs
					&& (!simulationData.getSimulationArguments()
							.getReportExactSampleTime() || !simulationData
							.getSimulationArguments().isTime())) {
				simulationData.getKappaSystem().getObservables().calculateObs(
						currentTime, currentEventNumber,
						simulationData.getSimulationArguments().isTime());
			}

		}

		liveDataStreamer.stop();
		simulatorStatus.setStatusMessage(STATUS_WRAPPING);

		// simulationData.getKappaSystem().getObservables().calculateObsLast(
		// currentTime, currentEventNumber);
		if (simulationData.getSimulationArguments().isTime()
				&& currentTime > simulationData.getSimulationArguments()
						.getTimeLimit()) {
			clock.setTimeLimit(simulationData.getSimulationArguments()
					.getTimeLimit());
			clock.setEvent(currentEventNumber - 1);

		} else {
			clock.setTimeLimit(currentTime);
			clock.setEvent(currentEventNumber);
		}
		endSimulation(InfoType.OUTPUT, isEndRules, simulationTimer);
	}

	public void runStories() throws Exception {
		this.runStories(this.createClock());
	}

	public final void runStories(SimulationClock clock) throws Exception {
		clock.setClockStamp(System.currentTimeMillis());
		
		Stories stories = simulationData.getKappaSystem().getStories();
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		
		synchronized (statusLock) {
			currentEventNumber = 0;
		}

		InfoType additionalInfoOutputType = simulationData
				.getSimulationArguments().getOutputTypeForAdditionalInfo();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO, "-Simulation...");

		clock.resetBar();
		PlxTimer timerAllStories = new PlxTimer();
		timerAllStories.startTimer();

		simulatorStatus.setStatusMessage(STATUS_RUNNING);
		synchronized (statusLock) {
			currentIterationNumber = 0;
		}

		int seed = simulationData.getSimulationArguments().getSeed();
		consoleOutputManager.addAdditionalInfo(InfoType.INFO,
				"--Seeding random number generator with given seed "
						+ Integer.valueOf(seed).toString());

		EventBuilder eventBuilder = new EventBuilder();
		while (currentIterationNumber < simulationData.getSimulationArguments()
				.getIterations()) {
			PlxTimer timer = null;
			if (additionalInfoOutputType != InfoType.DO_NOT_OUTPUT) {
				consoleOutputManager.addAdditionalInfo(InfoType.INFO,
						"-Simulation...");
				timer = new PlxTimer();
				timer.startTimer();
			}

			boolean isEndRules = false;
			long clash = 0;
			long max_clash = 0;
			simulationData.getStoriesAgentTypesStorage().setIteration(
					currentIterationNumber);
			while (!clock.isEndSimulation(currentTime, currentEventNumber)
					&& max_clash <= simulationData.getSimulationArguments()
							.getMaxClashes()) {
				if (Thread.interrupted()) {
					// TODO: Do any necessary clean-up and collect data we can
					// return
					consoleOutputManager
							.println("Simulation is interrupted because the thread is cancelled");
					simulatorResultsData.setCancelled(true);
					simulatorStatus.setProgress(1.0);
					break;
				}

				simulationData.getKappaSystem().checkPerturbation(currentTime);
				simulationData.getKappaSystem().updateRuleActivities();
				Rule rule = simulationData.getKappaSystem().getRandomRule();

				if (rule == null) {
					clock.setTimeLimit(currentTime);
					consoleOutputManager.println("#");
					break;
				}

				List<Injection> injectionsList = simulationData
						.getKappaSystem().chooseInjectionsForRuleApplication(
								rule);
				if (!rule.hasInfiniteRate()) {
					synchronized (statusLock) {
						currentTime += simulationData.getKappaSystem()
								.getTimeValue();
					}
				}
				if (injectionsList != null) {
					eventBuilder.setNewEvent(currentEventNumber, rule
							.getRuleId());
					max_clash = 0;
					// what is this??
					if (stories.checkRule(rule.getRuleId(),
							currentIterationNumber)) {
						rule.applyRuleForStories(injectionsList, eventBuilder,
								simulationData, true);
						stories.addLastEventToStoryStorifyRule(
								currentIterationNumber,
								eventBuilder.getEvent(), currentTime);
						synchronized (statusLock) {
							currentEventNumber++;
						}
						isEndRules = true;
						consoleOutputManager.println("#");
						break;
					}

					rule.applyRuleForStories(injectionsList, eventBuilder,
							simulationData, false);
					if (!rule.doesNothing()) {
						stories.addEventToStory(currentIterationNumber,
								eventBuilder.getEvent());
					}
					synchronized (statusLock) {
						currentEventNumber++;
					}

					UpdatesPerformer.doNegativeUpdate(injectionsList);
					simulationData.getKappaSystem().doPositiveUpdate(rule,
							injectionsList);
				} else {
					clash++;
					max_clash++;
				}

			} // end of simulation here...

			clock.checkStoriesBar(currentIterationNumber);
			synchronized (statusLock) {
				currentEventNumber = 0;
			}

			stories.cleaningStory(currentIterationNumber);
			endSimulation(additionalInfoOutputType, isEndRules, timer);

			if (currentIterationNumber < simulationData
					.getSimulationArguments().getIterations() - 1) {
				reInitializeSimulationData();
			}

			// check whether the thread is interrupted above or since then...
			if (simulatorResultsData.isCancelled() || Thread.interrupted()) {
				// TODO: Do any necessary clean-up and collect data we can
				// return
				consoleOutputManager
						.println("Simulation is interrupted because the thread is cancelled");
				simulatorResultsData.setCancelled(true);
				simulatorStatus.setProgress(1.0);
				break;
			}

			synchronized (statusLock) {
				currentIterationNumber++;
			}
		} // end of iteration here...

		simulatorStatus.setStatusMessage(STATUS_WRAPPING);

		if (additionalInfoOutputType != InfoType.DO_NOT_OUTPUT) {
			consoleOutputManager.println("#");
			endSimulation(InfoType.OUTPUT, false, timerAllStories);
		}

		PlxTimer mergeTimer = new PlxTimer();
		mergeTimer.startTimer();
		endMerge(mergeTimer);
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// GETTERS AND SETTERS
	//
	//

	public final String getName() {
		return NAME;
	}

	public final SimulationData getSimulationData() {
		return simulationData;
	}

	public final SimulatorResultsData getSimulatorResultsData() {
		return simulatorResultsData;
	}

}

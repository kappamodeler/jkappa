package com.plectix.simulator.experiments;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.simulator.api.steps.experiments.ConnectedComponentPattern;
import com.plectix.simulator.simulator.api.steps.experiments.Experiment;
import com.plectix.simulator.simulator.api.steps.experiments.ExperimentListener;
import com.plectix.simulator.simulator.api.steps.experiments.RulePattern;
import com.plectix.simulator.simulator.api.steps.experiments.SimulationDataProcessor;
import com.plectix.simulator.staticanalysis.observables.ObservableComponentsManager;
import com.plectix.simulator.staticanalysis.observables.Observables;
import com.plectix.simulator.util.io.PlxLogger;

public class TestExperimentRunner implements ExperimentListener {
	
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(TestExperimentRunner.class);
	
	private SimulatorInputData simulatorInputData = null;
	
	private double sumMax = 0.0;
	private double sumFinal = 0.0;
	private double rateConstant = 1.0;
	
	public TestExperimentRunner(SimulatorInputData simulatorInputData) {
		super();
		this.simulatorInputData = simulatorInputData;
	}
	
	private void run() throws Exception {
		Experiment experiment = new Experiment(simulatorInputData);
		
		final double additionalRate = 0.05;
		SimulationDataProcessor simulationDataProcessor = new SimulationDataProcessor(experiment.getEngine()){
			public void process() {
				this.incrementRuleRate(new RulePattern("a(x) -> a(x), a(x)"), additionalRate);
			}
		};
		
		for (int experimentNo= 0; experimentNo < 11; experimentNo++) {
			// simulate 100 runs
			experiment.run(100, this);
			// dump the average of 50 runs for this rateConstant
			System.err.println(rateConstant + " " + sumFinal + " " + sumMax);
			
			// update variables:
			sumMax = 0.0;
			sumFinal = 0.0;
			simulationDataProcessor.process();
			rateConstant = rateConstant + 0.05;
		}
	}

	private final int seedValueByRunNumber(int runNo) {
		return runNo + 1;
	}
	
	@Override
	public void startingRun(int runNo, Simulator simulator) throws Exception {
		// TODO: Create methods to change important simulation parameters such as seed, rescale, time/event option, operation mode, etc.
		simulator.getSimulationData().getSimulationArguments().setSeed(this.seedValueByRunNumber(runNo));
	}

	@Override
	public void finishedRun(int runNo, Simulator simulator) {
		Observables observables = simulator.getSimulationData().getKappaSystem().getObservables();
		ObservableComponentsManager manager = observables.getComponentManager();
		double countFinal = manager.getFinalComponentState(new ConnectedComponentPattern("a(x)"));
		double countMax = manager.getMaxComponentState(new ConnectedComponentPattern("a(x)"));
		sumFinal = sumFinal + countFinal;
		sumMax = sumMax + countMax;
	}
	
	@Override
	public void finishedAll(int runNo, Simulator simulator) {
		sumFinal = sumFinal / runNo;
		sumMax = sumMax / runNo;
	}

	public static void main(String[] args) throws Exception {
		SimulationMain.initializeLogging();
		
		// Command Line Arguments = "--sim debugging-link.ka --time 50.0 --operation-mode 1"  // make sure all XML and console output are turned off...
 		// New Command line arguments: "--sim data/exponentielle.ka --time 5.0 --operation-mode 1"
		
		SimulatorCommandLine commandLine = null;
		try {
			commandLine = new SimulatorCommandLine(args);
		} catch (ParseException parseException) {
			parseException.printStackTrace();
			LOGGER.fatal("Caught fatal ParseException", parseException);
			System.exit(-2);
		}
		
		TestExperimentRunner experimentRunner = new TestExperimentRunner(new SimulatorInputData(commandLine.getSimulationArguments()));
		experimentRunner.run();
	}
}

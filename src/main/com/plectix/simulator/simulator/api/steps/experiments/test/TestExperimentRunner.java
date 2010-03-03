package com.plectix.simulator.simulator.api.steps.experiments.test;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Experiment;
import com.plectix.simulator.simulator.ExperimentListener;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.simulator.api.steps.experiments.ConnectedComponentPattern;
import com.plectix.simulator.simulator.api.steps.experiments.ExperimentOutput;
import com.plectix.simulator.util.io.PlxLogger;

public class TestExperimentRunner implements ExperimentListener {
	
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(TestExperimentRunner.class);
	
	private SimulatorInputData simulatorInputData = null;
	
	private double sum = 0.0;
	private double rateConstant = 1.0;
	
	public TestExperimentRunner(SimulatorInputData simulatorInputData) {
		super();
		this.simulatorInputData = simulatorInputData;
	}

	private void run() throws Exception {
		Experiment experiment = new Experiment(simulatorInputData);
		TestSimulationDataProcessor simulationDataProcessor 
				= new TestSimulationDataProcessor(experiment.getEngine());
		
		for (int experimentNo= 0; experimentNo < 10; experimentNo++) {
			// TODO: create methods to change the contents of the input Kappa, such rate constants
			// TODO: set the rate constant for "A(r), A(l) -> A(r!1), A(l!1)" here...
			simulationDataProcessor.process(experimentNo);
			
			// simulate 50 runs
			experiment.run(5, this);
			// dump the average of 50 runs for this rateConstant
			System.err.println(rateConstant + " " + sum);
			
			// update variables:
			sum = 0.0;
			rateConstant = rateConstant + experimentNo + 1;
		}
	}

	@Override
	public void startingRun(int runNo, Simulator simulator) throws Exception {
		// TODO: Create methods to change important simulation parameters such as seed, rescale, time/event option, operation mode, etc.
		// TODO: simulatorInputData.getSimulationArguments().setSeed(i+1);
	}

	@Override
	public void finishedRun(int runNo, ExperimentOutput experimentOutput) {
		double count = experimentOutput.getObservableFinalState(new ConnectedComponentPattern("A(r)"));  // TODO: Get the count for A(r)
		sum = sum + count;
	}
	
	@Override
	public void finishedAll(int runNo, Simulator simulator) {
		sum = sum / runNo;
	}

	public static void main(String[] args) throws Exception {
		SimulationMain.initializeLogging();
		
		// Command Line Arguments = "--sim debugging-link.ka --time 50.0 --operation-mode 1"  // make sure all XML and console output are turned off...
 		
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

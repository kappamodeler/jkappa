package com.plectix.simulator.experiments;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.experiments.ConnectedComponentPattern;
import com.plectix.simulator.simulator.api.steps.experiments.ExperimentRunner;
import com.plectix.simulator.simulator.api.steps.experiments.SimulationDataProcessor;
import com.plectix.simulator.staticanalysis.observables.ObservableComponentsManager;
import com.plectix.simulator.staticanalysis.observables.Observables;

public class TempExperimentRunner extends ExperimentRunner {
	
	private double sumMax = 0.0;
	private double sumFinal = 0.0;
	private double rateConstant = 1.0;
	
	private SimulationDataProcessor simulationDataProcessor = null;
	
	public TempExperimentRunner(SimulatorInputData simulatorInputData) throws Exception {
		super(simulatorInputData);

		simulationDataProcessor = new SimulationDataProcessor(experiment.getSimulator()){
			public void updateInitialModel() throws IncompletesDisabledException, SimulationDataFormatException {
				this.changeInitialCondition("a(x)", 12);
			}
		};
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
	public void finishedAllRuns(int runNo, Simulator simulator) {
		sumFinal = sumFinal / runNo;
		sumMax = sumMax / runNo;
	}
	
	@Override
	public void finishedExperiment(int numberOfExperiments, Simulator simulator) {
		// dump the average of 50 runs for this rateConstant
		System.err.println(rateConstant + " " + sumFinal + " " + sumMax);
		
		// update variables:
		sumMax = 0.0;
		sumFinal = 0.0;
		rateConstant = rateConstant + 0.05;
		try {
			simulationDataProcessor.updateInitialModel();
		} catch (IncompletesDisabledException e) {
			e.printStackTrace();
			System.err.println("Could not update initial model");
			System.exit(-3);
		} catch (SimulationDataFormatException e) {
			e.printStackTrace();
			System.err.println("Could not update initial model");
			System.exit(-4);
		}
	}

	// Command Line Arguments = "--sim debugging-link.ka --time 50.0 --operation-mode 1"  // make sure all XML and console output are turned off...
	// New Command line arguments: "--sim data/exponentielle.ka --time 5.0 --operation-mode 1"
	public static void main(String[] args) throws Exception {
		SimulationMain.initializeLogging();
		
		SimulatorInputData simulatorInputData = SimulationMain.getSimulatorInputData(args, null);
		
		TempExperimentRunner experimentRunner = new TempExperimentRunner(simulatorInputData);
		experimentRunner.run(11, 100);
	}
}

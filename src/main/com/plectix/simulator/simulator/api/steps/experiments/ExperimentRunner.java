package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;

/**
 * Implements <code>ExperimentRunnerListener</code> and provides empty methods if the 
 * class extending this class doesn't want to use them...
 * 
 * @author ecemis
 */
public class ExperimentRunner extends AbstractExperimentRunner implements ExperimentRunnerListener {

	public ExperimentRunner(SimulatorInputData simulatorInputData) throws Exception {
		super(simulatorInputData);
	}

	public void run(int numberOfExperiments, int numberOfRuns) {
		super.run(numberOfExperiments, numberOfRuns, this);
	}
	
	@Override
	public void startingRun(int runNo, Simulator simulator) throws Exception {
		
	}

	@Override
	public void finishedRun(int runNo, Simulator simulator) {
		
	}

	@Override
	public void finishedAllRuns(int runNo, Simulator simulator) {
	
	}

	@Override
	public void startingExperiment(int experimentNo, Simulator simulator) throws Exception {
		
	}

	@Override
	public void finishedExperiment(int experimentNo, Simulator simulator) {  
		
	}
	
	@Override
	public void finishedAllExperiments(int experimentNo, Simulator simulator) { 
		
	}

}

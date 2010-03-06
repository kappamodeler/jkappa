package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.controller.SimulatorInputData;

abstract public class AbstractExperimentRunner implements ExperimentListener {
	
	protected Experiment experiment = null;
	
	public AbstractExperimentRunner(SimulatorInputData simulatorInputData) throws Exception {
		super();
		this.experiment = new Experiment(simulatorInputData);
	}

	public void run(int numberOfExperiments, int numberOfRuns, ExperimentRunnerListener experimentRunnerListener) {
		int experimentNo= 0;
		try {
			while (experimentNo < numberOfExperiments) {
	            if (experimentRunnerListener != null) {
	            	experimentRunnerListener.startingExperiment(experimentNo, experiment.getSimulator());
	            }
				
				// run numberOfRuns simulations:
				experiment.run(numberOfRuns, this);

	            if (experimentRunnerListener != null) {
	            	experimentRunnerListener.finishedExperiment(experimentNo, experiment.getSimulator());
	            }
	            
	            experimentNo++;
			}
		} catch (Exception e) {
        	e.printStackTrace();
        } catch (OutOfMemoryError outOfMemoryError) {
        	outOfMemoryError.printStackTrace();
        	System.err.println("Caught an OutOfMemoryError!");
        } finally {
        	if (experimentRunnerListener != null) {
        		experimentRunnerListener.finishedAllExperiments(experimentNo, experiment.getSimulator());
        	}
        }
	}

}

package com.plectix.simulator.simulator;

import com.plectix.simulator.controller.SimulatorInputData;

public class Experiment {
	
	private SimulatorInputData simulatorInputData = null;
	private Simulator simulator = new Simulator();
	
	public Experiment(SimulatorInputData simulatorInputData) {
		super();
		this.simulatorInputData = simulatorInputData;
	}

	public final void run(int numberOfRuns, ExperimentListener experimentListener) {
		int runNo= 0;
        try {
        	while (runNo < numberOfRuns) {
                if (experimentListener != null) {
                	experimentListener.startingRun(runNo, simulator);   // the listener may change the input data here...
                }
                // run the simulator
                simulator.run(simulatorInputData);
                if (experimentListener != null) {
                	experimentListener.finishedRun(runNo, simulator);   // the listener processes the results here...
                }
                // TODO: reset the solution, observables, etc. in the simulator but not the kappa file, keep that in memory...
                
                // increase the run count:
                runNo++;
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	simulator.cleanUpAfterException(e);
        } catch (OutOfMemoryError outOfMemoryError) {
        	outOfMemoryError.printStackTrace();
        	simulator.getSimulatorResultsData().getSimulatorExitReport().setException(new Exception(outOfMemoryError));
        	System.err.println("Caught an OutOfMemoryError!");
        } finally {
        	if (experimentListener != null) {
        		experimentListener.finishedAll(runNo, simulator);
        	}
        }
	}
}

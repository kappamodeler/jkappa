package com.plectix.simulator.simulator.api.steps.experiments;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.ExperimentWorkflow;
import com.plectix.simulator.simulator.api.steps.KappaModelBuildingOperation;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;

public class Experiment {
	private Simulator simulator = new Simulator();
	
	public Experiment(SimulatorInputData simulatorInputData) throws Exception {
		super();
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
    	manager.perform(new SimulatorInitializationOperation(simulator, simulatorInputData));
		manager.perform(new KappaModelBuildingOperation(simulator.getSimulationData()));
	}

	public final void run(int numberOfRuns, ExperimentListener experimentListener) {
		int runNo= 0;
        try {
        	
        	OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
        	while (runNo < numberOfRuns) {
                if (experimentListener != null) {
                	experimentListener.startingRun(runNo, simulator);   // the listener may change the input data here...
                }
                
                // run the simulator
                manager.perform(new ExperimentWorkflow(simulator));
                
                if (experimentListener != null) {
                	experimentListener.finishedRun(runNo, simulator);   // the listener processes the results here...
                }
                
                // reset the solution, observables, etc. in the simulator but not the kappa file, keep that in memory...
                simulator.getSimulationData().reset();
                
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
	
	public final Simulator getSimulator() {
		return simulator;
	}
}

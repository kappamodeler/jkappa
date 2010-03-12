package com.plectix.simulator.experiments;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.experiments.ConnectedComponentPattern;
import com.plectix.simulator.simulator.api.steps.experiments.ExperimentRunner;
import com.plectix.simulator.simulator.api.steps.experiments.RulePattern;
import com.plectix.simulator.simulator.api.steps.experiments.SimulationDataProcessor;
import com.plectix.simulator.staticanalysis.observables.ObservableComponentsManager;
import com.plectix.simulator.staticanalysis.observables.Observables;

public class TestExponentielleExperiment extends ExperimentRunner {
	
	private double sumMax = 0.0;
	private double sumFinal = 0.0;
	private double rateConstant = 1.0;
	
	private final Set<String> experimentsResults = new LinkedHashSet<String>();
	
	private SimulationDataProcessor simulationDataProcessor = null;

	public TestExponentielleExperiment() throws Exception {
		super(SimulationMain.getSimulatorInputData(
				new String[]{
						"--sim", "data/exponentielle.ka",
						"--time", "5.0" 
				},
				null));
	}

	
	@Test
	public void test() throws Exception {
		try {
			this.run(11, 100);
		} catch(ExperimentFailedException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Before
	public void testExperimentRunner() throws Exception {
		simulationDataProcessor = new SimulationDataProcessor(this.getExperiment().getSimulator()){
			public void updateInitialModel() {
				this.setRuleRate(new RulePattern("a(x) -> a(x), a(x)"), rateConstant);
			}
		};
		experimentsResults.add("1.0 148.63 148.63");
		experimentsResults.add("1.05 190.79 190.79");
		experimentsResults.add("1.1 244.05 244.05");
		experimentsResults.add("1.1500000000000001 314.43 314.43");
		experimentsResults.add("1.2000000000000002 403.59 403.59");
		experimentsResults.add("1.2500000000000002 518.05 518.05");
		experimentsResults.add("1.3000000000000003 667.99 667.99");
		experimentsResults.add("1.3500000000000003 856.3 856.3");
		experimentsResults.add("1.4000000000000004 1099.17 1099.17");
		experimentsResults.add("1.4500000000000004 1407.56 1407.56");
		experimentsResults.add("1.5000000000000004 1807.06 1807.06");
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
	public void finishedExperiment(int numberOfExperiments, Simulator simulator) throws ExperimentFailedException {
		// dump the average of 50 runs for this rateConstant
		this.checkLine(rateConstant + " " + sumFinal + " " + sumMax);
		
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
	
	private final void checkLine(String line) throws ExperimentFailedException {
		if (!experimentsResults.contains(line)) {
			throw new ExperimentFailedException("No experiment should end with result : " + line);
		}
	}
}

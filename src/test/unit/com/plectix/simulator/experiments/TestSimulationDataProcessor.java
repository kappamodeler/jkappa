package com.plectix.simulator.experiments;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulationclasses.solution.SolutionLine;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.steps.experiments.ConnectedComponentPattern;
import com.plectix.simulator.simulator.api.steps.experiments.RulePattern;
import com.plectix.simulator.simulator.api.steps.experiments.SimulationDataProcessor;
import com.plectix.simulator.util.IdGenerator;

public class TestSimulationDataProcessor {
	private static final class LightweightKappaLine extends KappaFileLine {
		public LightweightKappaLine(String line) {
			super(-1, line);
		}
	}
	
	private final KappaModel model = this.produceTestModel();
	private static final IdGenerator nameGenerator = new IdGenerator();
	private static final Set<RulePattern> modelRules = new LinkedHashSet<RulePattern>();
	private static final Set<ConnectedComponentPattern> modelSolution = new LinkedHashSet<ConnectedComponentPattern>();
	
	static {
		modelRules.add(new RulePattern("A() -> B()"));
		modelRules.add(new RulePattern("A(x) -> B(x)"));
		modelRules.add(new RulePattern("B() -> A()"));
		modelRules.add(new RulePattern("B(x) -> A(x)"));
		modelRules.add(new RulePattern("B(x) -> A(x), B(x)"));
		
		modelSolution.add(new ConnectedComponentPattern("A()"));
		modelSolution.add(new ConnectedComponentPattern("A(x)"));
		modelSolution.add(new ConnectedComponentPattern("B()"));
		modelSolution.add(new ConnectedComponentPattern("B(x)"));
	}
	
	@Test
	public final void namesCrashTest() {
		for (int i = 0; i < 4; i++) {
			Assert.assertTrue("rule with name '" + i + "' wasn't found", model.getRuleByName("" + i) != null);
		}
	}
	
	@Test
	public final void patternsCrashTest() {
		for (RulePattern pattern : modelRules) {
			Assert.assertTrue("'" + pattern + "' rule wasn't found", model.getRuleByPattern(pattern) != null);
		}
	}

	@Test
	public final void testSeedSettingMethod() throws Exception {
		Simulator simulator = new Simulator();
		simulator.getSimulationData().setInitialModel(this.model);
		for (final RulePattern pattern : modelRules) {
			SimulationDataProcessor processor = new SimulationDataProcessor(simulator) {
				@Override
				public void updateInitialModel() throws IncompletesDisabledException, 
						SimulationDataFormatException {
					this.setRuleRate(pattern, 1984);
				}
			};
			processor.updateInitialModel();
			Assert.assertTrue(Math.abs(this.model.getRuleByPattern(pattern).getRate() - 1984) < 0.001);
		}
	}
	
	@Test
	public final void testInitialConditions1() throws Exception {
		Simulator simulator = new Simulator();
		simulator.getSimulationData().setInitialModel(this.model);
		for (final ConnectedComponentPattern pattern : modelSolution) {
			SimulationDataProcessor processor = new SimulationDataProcessor(simulator) {
				@Override
				public void updateInitialModel() throws IncompletesDisabledException, 
						SimulationDataFormatException {
					this.addInitialCondition(pattern, 10);
					this.changeInitialCondition(pattern, 15);
//					this.removeInitialCondition(pattern);
				}
			};
			processor.updateInitialModel();
			Assert.assertTrue(this.countSubstance(pattern) == 15);
		}
	}
	
	@Test
	public final void testInitialConditions2() throws Exception {
		Simulator simulator = new Simulator();
		simulator.getSimulationData().setInitialModel(this.model);
		for (final ConnectedComponentPattern pattern : modelSolution) {
			SimulationDataProcessor processor = new SimulationDataProcessor(simulator) {
				@Override
				public void updateInitialModel() throws IncompletesDisabledException, 
						SimulationDataFormatException {
					this.addInitialCondition(pattern, 10);
					this.removeInitialCondition(pattern);
				}
			};
			processor.updateInitialModel();
			Assert.assertTrue(this.countSubstance(pattern) == -1);
		}
	}
	
	private final long countSubstance(ConnectedComponentPattern pattern) {
		for (SolutionLineData substanceInfo : model.getSolution().getAgents()) {
			String connectedComponent = substanceInfo.toString().split(" \\* ")[1];
			connectedComponent = connectedComponent.substring(1, connectedComponent.length() - 1);
			if (connectedComponent.equals(pattern.toString())) {
				return substanceInfo.getCount();
			}
		}
		return -1;
	}
	
	private final KappaModel produceTestModel() { 
		KappaModel model = new KappaModel();
		KappaFileParagraph ruleParagraph = new KappaFileParagraph();
		
		for (RulePattern pattern : modelRules) {
			ruleParagraph.addLine(this.generateNextLine(pattern));
		}
		
		RulesParagraphReader reader = new RulesParagraphReader(new SimulationArguments(), 
				new AgentFactory(true));
		try {
			for (ModelRule rule : reader.readComponent(ruleParagraph)) {
				model.addRule(rule);
			}
		} catch(Exception e) {
			Assert.fail("Something wrong with the code of this test or RulesParagraphReader class\n"
					+ "\tcaused by " + e.getMessage());
		}
		
		return model;
	}
	
	private final LightweightKappaLine generateNextLine(RulePattern pattern) { 
		return new LightweightKappaLine("'" + nameGenerator.generateNext() + "' " + pattern);
	}
}

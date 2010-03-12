package com.plectix.simulator.experiments;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.api.steps.experiments.RulePattern;
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
	
	static {
		modelRules.add(new RulePattern("A() -> B()"));
		modelRules.add(new RulePattern("A(x) -> B(x)"));
		modelRules.add(new RulePattern("B() -> A()"));
		modelRules.add(new RulePattern("B(x) -> A(x)"));
		modelRules.add(new RulePattern("B(x) -> A(x), B(x)"));
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

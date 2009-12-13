package com.plectix.simulator.rulecompression;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.rulecompression.QuantitativeCompressor;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

@RunWith(value = Parameterized.class)
public class TestQuantitativeCompressions {
	private static final String prefixSourseModel = InitData.pathForSourseModel
			+ "quantitative" + File.separator;
	
	private LocalViewsMain localViews;
	private List<Rule> rules;

	private final InitTestRuleCompressions initTestRuleCompressions = new InitTestRuleCompressions();
	
	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator
		.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
				"~kappa"),false);
	}

	public TestQuantitativeCompressions(String count, String patch, Integer opMode) throws Exception {
		initTestRuleCompressions.initializeSimulation(patch, count, opMode);
	}

	@Before
	public void setUp() {
		initLocalViews(initTestRuleCompressions.getSubViews());
		this.rules = initTestRuleCompressions.getRules();

	}

	private void initLocalViews(AllSubViewsOfAllAgentsInterface subViews) {

		if (localViews == null) {
			localViews = new LocalViewsMain(subViews);
			localViews.buildLocalViews();
		}
	}

	@Test
	public void testQuantitativeCompression() {
		for (Rule rule : rules) {
			QuantitativeCompressor q = new QuantitativeCompressor(localViews);
			q.compress(rule);
			Rule comp = q.getCompressedRule();
			for (ConnectedComponentInterface ic : comp.getLeftHandSide()) {
				if (ic.getAgents().isEmpty())
					continue;
			}
			for (ConnectedComponentInterface ic : comp.getRightHandSide()) {
				if (ic.getAgents().isEmpty())
					continue;
			}

		}

	}

}

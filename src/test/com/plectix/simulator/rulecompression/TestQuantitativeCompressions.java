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
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.component.complex.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.util.DefaultPropertiesForTest;

@RunWith(value = Parameterized.class)
public class TestQuantitativeCompressions {
	private static final String prefixSourseModel = InitData.pathForSourseModel
			+ "quantitative" + File.separator;
	
	private LocalViewsMain localViews;
	private List<Rule> rules;

	private InitTestRuleCompressions initTestRuleCompressions = new InitTestRuleCompressions();
	
	@Parameters
	public static Collection<Object[]> configs() {
		return FileNameCollectionGenerator
		.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
				"~kappa");
	}

	public TestQuantitativeCompressions(String count, String patch) {
		initTestRuleCompressions.initializeSimulation(patch, count);
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
				// System.out.println(((ConnectedComponent)(ic)).getHash());
			}
			for (ConnectedComponentInterface ic : comp.getRightHandSide()) {
				if (ic.getAgents().isEmpty())
					continue;
				// System.out.println(((ConnectedComponent)(ic)).getHash());
			}
			// for(Action av : comp.getActionList())
			// System.out.println(av);

		}

	}

}
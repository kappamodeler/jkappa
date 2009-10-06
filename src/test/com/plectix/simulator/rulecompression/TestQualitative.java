package com.plectix.simulator.rulecompression;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
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
import com.plectix.simulator.util.DefaultPropertiesForTest;

@RunWith(value = Parameterized.class)
public class TestQualitative {
	private static final String prefixSourseModel = InitData.pathForSourseModel
			+ "qualitative" + File.separator;

	private LocalViewsMain localViews;
	private List<Rule> rules;

	private InitTestRuleCompressions initTestRuleCompressions = new InitTestRuleCompressions();
	
	@Parameters
	public static Collection<Object[]> configs() {
		return FileNameCollectionGenerator
		.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
				"~kappa");
	}

	public TestQualitative(String filenameCount, String pathModel) {
		initTestRuleCompressions.initializeSimulation(pathModel, filenameCount);
	}

	@Before
	public void setUp() {
		initLocalViews(initTestRuleCompressions.getSubViews());
		this.rules = new LinkedList<Rule>();
	}

	private void initLocalViews(AllSubViewsOfAllAgentsInterface subViews) {
		if (localViews == null) {
			localViews = new LocalViewsMain(subViews);
			localViews.buildLocalViews();
		}
	}

	@Test
	public void testGroupBuild() {
		for (Rule rule : rules) {
			RuleMaster rr = new RuleMaster(rule);
			System.out.println(rule);
			for (RootedRule r2 : rr.getAllRootedVersions()) {
				System.out.println("rooted rule :");
				for (String s : r2.getActionsString()) {
					System.out.println(s);
				}
			}
		}
	}

}

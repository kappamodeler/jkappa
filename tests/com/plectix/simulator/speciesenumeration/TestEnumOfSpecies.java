package com.plectix.simulator.speciesenumeration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.speciesenumeration.GeneratorSpecies;

@RunWith(value = Parameterized.class)
public class TestEnumOfSpecies {

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	private InitTestEnumOfSpecies initTestEnumOfSpecies = new InitTestEnumOfSpecies();

	private LocalViewsMain localViews;

	private GeneratorSpecies generatorSpecies;

	private Map<String, Integer> resultMap = new HashMap<String, Integer>();

	@Parameters
	public static Collection<Object[]> configs() {
		return FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
						"~kappa");
	}

	public TestEnumOfSpecies(String count, String patch) {
		initTestEnumOfSpecies.initializeSimulation(patch, count);
		initResult();
	}

	private void initResult() {
		resultMap.put("001", 6);
		resultMap.put("002", 356);
		resultMap.put("003", 8);
		resultMap.put("004", 0);
		resultMap.put("005", 83);
		resultMap.put("006", 62);
		resultMap.put("007", 8);
	}

	@Before
	public void setUp() {
		localViews = new LocalViewsMain(initTestEnumOfSpecies.getSubViews());
		localViews.buildLocalViews();
	}

	@Test
	public void test() {
		generatorSpecies = new GeneratorSpecies(localViews.getLocalViews());
		generatorSpecies.enumerate();
		Assert.assertEquals("[Error] Species", generatorSpecies.getSpecies()
				.keySet().size(), resultMap.get(initTestEnumOfSpecies.getMyCountInside()).intValue());
	}

}

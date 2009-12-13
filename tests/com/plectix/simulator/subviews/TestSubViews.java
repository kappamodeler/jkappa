package com.plectix.simulator.subviews;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.staticanalysis.subviews.storage.SubViewsInterface;

@RunWith(value = Parameterized.class)
public class TestSubViews {
	private static final String prefixSourseModel = InitData.pathForSourseModel;
//	private static final String prefixResult = InitData.pathForResult;

	private AllSubViewsOfAllAgentsInterface subViews;

	private Map<String, Integer> resultNumClassesForAgentsMap;
	private Map<String, Integer> resultNumSubViewsForClassesMap;

	private Map<String, Integer> testNumClassesForAgentsMap;
	private Map<String, Integer> testNumSubViewsForClassesMap;

//	private static boolean isConsole = InitData.isPrintinConsoleAndFile;

	private final InitTestSubView initTestSubView = new InitTestSubView();
	
	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator
		.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
				"~kappa"),false);
	}

	public TestSubViews(String count, String patch, Integer opMode) throws Exception {
		initTestSubView.initializeSimulation(patch, count, opMode);
		init(initTestSubView.getSourcePath());
	}

	private void init(String soursePath) {
		resultNumClassesForAgentsMap = new LinkedHashMap<String, Integer>();
		resultNumSubViewsForClassesMap = new LinkedHashMap<String, Integer>();
		ParserFileTesterSubViews parser = new ParserFileTesterSubViews(
				soursePath);
		parser.parseFile(resultNumClassesForAgentsMap,
				resultNumSubViewsForClassesMap);
	}

	@Before
	public void setUp() {
		subViews = initTestSubView.getSubViews();
	}

	@Test
	public void testSubViews() {
		numClassesForAgentsTest();
		numSubViewsForClassTest();
	}
	
	public void numClassesForAgentsTest() {

		Iterator<String> allTypesOfAgents = subViews.getAllTypesIdOfAgents();

		initTestNumClassesForAgentMap(allTypesOfAgents);

		StringBuffer errorStr = new StringBuffer();

		for (String agentName : resultNumClassesForAgentsMap.keySet()) {

			Integer resultNumClasses = resultNumClassesForAgentsMap
					.get(agentName);

			if (testNumClassesForAgentsMap.containsKey(agentName)) {

				Integer testNumClasses = testNumClassesForAgentsMap
						.get(agentName);

				if (resultNumClasses.intValue() != testNumClasses.intValue()) {

					String errorString = "[Error] Agent " + agentName
							+ " | [file] ClassNum = " + resultNumClasses
							+ " <> " + " [test] ClassNum = " + testNumClasses
							+ "\n";

					errorStr.append(errorString);

				}

			} else {

				String errorString = "[Error] In test has not Agent "
						+ agentName + " ClassNum = " + resultNumClasses + "\n";

				errorStr.append(errorString);

			}
		}

		if (errorStr.length() > 0) {
			fail(errorStr.toString());
		}

	}

	private void initTestNumClassesForAgentMap(Iterator<String> allTypesOfAgents) {

		testNumClassesForAgentsMap = new LinkedHashMap<String, Integer>();

		while (allTypesOfAgents.hasNext()) {

			String agentType = allTypesOfAgents.next();
			String agent = subViews.getFullMapOfAgents().get(agentType)
					.toString();
			Integer numAllSubViews = subViews.getAllSubViewsByType(agentType)
					.size();
			testNumClassesForAgentsMap.put(agent, numAllSubViews);

//			String srtInfo = "[Info] Test Agent " + agent + " Amount = "
//					+ numAllSubViews;

		}
	}

	public void numSubViewsForClassTest() {

		Iterator<String> allTypesOfAgents = subViews.getAllTypesIdOfAgents();

		initTestNumSubViewsForClassesMap(allTypesOfAgents);

		StringBuffer errorStr = new StringBuffer();

		for (String subViewName : resultNumSubViewsForClassesMap.keySet()) {

			Integer resultNumSubView = resultNumSubViewsForClassesMap
					.get(subViewName);

			if (testNumSubViewsForClassesMap.containsKey(subViewName)) {

				Integer testNumSubView = testNumSubViewsForClassesMap
						.get(subViewName);

				if (resultNumSubView.intValue() != testNumSubView.intValue()) {

					String errorString = "[Error] SubViews " + subViewName
							+ " | [file] SubViewsNum = " + resultNumSubView
							+ " <> " + "[test] SubViewsNum = " + testNumSubView
							+ "\n";

					errorStr.append(errorString);
				}

			} else {

				String errorString = "[Error] In test has not SubViews "
						+ subViewName + " SubViewsNum = " + resultNumSubView
						+ "\n";

				errorStr.append(errorString);
			}
		}

		if (errorStr.length() > 0) {
			fail(errorStr.toString());
		}

	}

	private void initTestNumSubViewsForClassesMap(
			Iterator<String> allTypesOfAgents) {

		testNumSubViewsForClassesMap = new LinkedHashMap<String, Integer>();

		while (allTypesOfAgents.hasNext()) {

			String agentType = allTypesOfAgents.next();

			String agent = subViews.getFullMapOfAgents().get(agentType)
					.toString();

			if (resultNumClassesForAgentsMap.containsKey(agent)) {

				List<SubViewsInterface> allSubViewsClasses = subViews
						.getAllSubViewsByType(agentType);

				Integer numSubClass = 0;

				for (SubViewsInterface subViewsClass : allSubViewsClasses) {

					// String subClassName = getClassForAgent(subViewsClass);
					numSubClass = numSubClass
							+ subViewsClass.getAllSubViews().size();
					/*
					 * testNumSubViewsForClassesMap.put(subClassName,
					 * numSubClass); String srtInfo = "[Info] Test SubView " +
					 * subClassName + " Amount = " + numSubClass;
					 * printConsole(srtInfo);
					 */
				}
				testNumSubViewsForClassesMap.put(agent, numSubClass);
//				String srtInfo = "[Info] Test AllSubViews for Agent " + agent
//						+ " Amount = " + numSubClass;
			}
		}
	}


}

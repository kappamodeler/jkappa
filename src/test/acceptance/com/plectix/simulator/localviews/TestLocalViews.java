package com.plectix.simulator.localviews;

import static org.junit.Assert.fail;

import java.util.Collection;
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
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.ReadAndParserFile;

@RunWith(value = Parameterized.class)
public class TestLocalViews {

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	private static final String SPLITER = " ";
	private static final String TOTAL = "TOTAL";

	private AllSubViewsOfAllAgentsInterface subViews;
	private LocalViewsMain localViews;

	private final Map<String, Integer> amountsLocalViewsSimplexMap = new LinkedHashMap<String, Integer>();
	private final Map<String, Integer> amountsLocalViewsJavaMap = new LinkedHashMap<String, Integer>();

	private final InitTestLocalViews initTestLocalViews = new InitTestLocalViews();

	@Parameters
	public static Collection<Object[]> configs() {
		Collection<Object[]> fileNames = FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
						"~kappa");
		return OperationModeCollectionGenerator.generate(fileNames,false);
	}

	public TestLocalViews(String count, String pathSourse, Integer opMode) throws Exception {
		initTestLocalViews.initializeSimulation(pathSourse, count, opMode);
		init(initTestLocalViews.getSourcePath());
	}

	private void init(String soursePath) {
		ReadAndParserFile parser = new ReadAndParserFile(soursePath, SPLITER);
		parser.addTypeData("#TOTALLOCALVIEWS", amountsLocalViewsSimplexMap);
		parser.addTypeData("#LOCALVIEWS", amountsLocalViewsSimplexMap);
		parser.parseFile();
	}

	@Before
	public void setUp() {
		subViews = initTestLocalViews.getSubViews();
		initLocalViews(subViews);

	}

	private void initLocalViews(AllSubViewsOfAllAgentsInterface subViews) {

		if (localViews == null) {
			localViews = new LocalViewsMain(subViews);
			localViews.buildLocalViews();
		}
	}

	@Test
	public void testLocalViews() {
		totalAmountLocalViewsTest();
		amountLocalViewsTest();
	}

	public void totalAmountLocalViewsTest() {

		// TODO: THINKING FILE SOURSE003 "TOTAL 13" in SIMPLEX.
		int amountlocalViewsJava = localViews.getLocalViews().size();

		if (!amountsLocalViewsSimplexMap.get(TOTAL).equals(
				Integer.valueOf(amountlocalViewsJava))) {

			String errorString = "[Error] Test Total AmountLocalViews = "
					+ amountlocalViewsJava
					+ " | Simplex Total AmountLocalViews = "
					+ amountsLocalViewsSimplexMap.get(TOTAL) + "\n";
			fail(errorString);

		}

	}

	public void amountLocalViewsTest() {

		initAmountLocalViewsJavaMap();

		StringBuffer errorStr = new StringBuffer();

		for (String keyNameSimplex : amountsLocalViewsSimplexMap.keySet()) {

			if (keyNameSimplex.equals(TOTAL)) {
				continue;
			}

			if (amountsLocalViewsJavaMap.containsKey(keyNameSimplex)) {

				if (!amountsLocalViewsSimplexMap.get(keyNameSimplex).equals(
						amountsLocalViewsJavaMap.get(keyNameSimplex))) {

					String errorString = "[Error] LocalViews for Agent = "
							+ keyNameSimplex
							+ " | [file - Simplex] AmountLocalViews = "
							+ amountsLocalViewsSimplexMap.get(keyNameSimplex)
							+ " <> " + "[test - Java] AmountLocalViews = "
							+ amountsLocalViewsJavaMap.get(keyNameSimplex)
							+ "\n";

					errorStr.append(errorString);

				}

			} else {

				String errorString = "[Error] LocalViews for Agent = "
						+ keyNameSimplex + " AmountLocalViews = "
						+ amountsLocalViewsSimplexMap.get(keyNameSimplex)
						+ " > NO in JAVA \n";

				errorStr.append(errorString);

			}

		}

		if (errorStr.length() > 0) {
			fail(errorStr.toString());
		}
	}

	private void initAmountLocalViewsJavaMap() {

		Map<String, List<AbstractAgent>> localViewsJava = localViews
				.getLocalViews();

		for (List<AbstractAgent> agents : localViewsJava.values()) {
			amountsLocalViewsJavaMap.put(agents.iterator().next().getName(),
					agents.size());
		}
	}
}

package com.plectix.simulator.parser.newtests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.TestRunner;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.FileComparator;

public class TestParser extends TestRunner {
	private static final String ABSTRACT = "abstract";
	private static final String myTestFileNamePrefix = RunParserTests
			.getFileNamePrefix();
	private static final String separator = File.separator;
	private static final String resultsDir = myTestFileNamePrefix + "results" + separator;
	private static final String sourceDir = myTestFileNamePrefix + "source" + separator;
	private static final String compareDir = myTestFileNamePrefix + "compare" + separator;

	private final String fileWithPerturbations = "perturbations" + RunAllTests.FILENAME_EXTENSION;
	private final String fileWithStories = "stories" + RunAllTests.FILENAME_EXTENSION;

	private boolean isStorify;

	private void prepareResult(String fileName, boolean isStorify)
			throws SimulationDataFormatException, FileNotFoundException,
			ParseException {
		PrintWriter pw = null;
		try {
			KappaFile kf = (new KappaFileReader(sourceDir + fileName)).parse();
			SimulationArguments args = Initializator
					.prepareDefaultArguments(sourceDir + fileName);
			KappaModel model = new KappaModelCreator(args).createModel(kf);
			pw = new PrintWriter(resultsDir + ABSTRACT + fileName);
			pw.print(model.toString());
			pw.close();

			TestSimulationData dataTest = new TestSimulationData(model,
					isStorify);
			dataTest.build();
			pw = new PrintWriter(resultsDir + fileName);
			pw.print(dataTest.getData());

		} catch (DocumentFormatException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@BeforeClass
	public static void init() {
		File resultDir = new File(resultsDir);
		resultDir.mkdir();
	}

	@Before
	public void parseFile() {
		try {
			isStorify = false;
			prepareResult(fileWithPerturbations, isStorify);
			isStorify = true;
			prepareResult(fileWithStories, isStorify);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private void test(String fileName) {
		Failer failer = new Failer();
		int line;
		try {
			line = new FileComparator(compareDir + fileName, resultsDir
					+ fileName).compare();
			if (line != -1) {
				failer.fail("Files differ in line " + line);
			}
		} catch (Exception e) {
			failer.fail(e.getMessage());
		}
	}

	@Test
	public void testAbstractPerturbations() {
		String fileName = ABSTRACT + fileWithPerturbations;
		test(fileName);
	}

	@Test
	public void testAbstractStory() {
		String fileName = ABSTRACT + fileWithStories;
		test(fileName);
	}

	// TODO: THINKING...
//	@Test
//	public void testPerturbations() {
//		String fileName = fileWithPerturbations;
//		test(fileName);
//
//	}

	@Test
	public void testStory() {
		String fileName = fileWithStories;
		test(fileName);
	}

	// @AfterClass
	public static void clear() {
		String dirName = resultsDir;
		File fileDir = new File(dirName);
		for (String fileName : fileDir.list()) {
			(new File(fileName)).delete();
		}
	}

}

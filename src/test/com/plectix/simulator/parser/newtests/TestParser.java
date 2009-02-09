package com.plectix.simulator.parser.newtests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.cli.ParseException;
import org.junit.*;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.FileComparator;

public class TestParser {
	private static final String myTestFileNamePrefix = RunParserTests
			.getFileNamePrefix();
	private static final String myResultsDir = myTestFileNamePrefix
			+ "results/";
	// path to unformatted source file
	private final String mySourceFileName = myTestFileNamePrefix + "test_read_this.ka";
	// path to formatted file
	private final String myCompareFileName = myTestFileNamePrefix
			+ "test_compare_with_this.ka";
	private final String myResultFilePath = myResultsDir + "result.ka";

	private void prepareResult() throws SimulationDataFormatException, FileNotFoundException, ParseException {
		PrintWriter pw = null;
		try {
			KappaFile kf = (new KappaFileReader(mySourceFileName)).parse();
			SimulationArguments args = Initializator
					.prepareDefaultArguments(mySourceFileName);
			KappaModel model = new KappaModelCreator(args).createModel(kf);
			pw = new PrintWriter(myResultFilePath);
			pw.print(model.toString());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@BeforeClass
	public static void init() {
		File resultDir = new File(myResultsDir);
		resultDir.mkdir();
	}

	@Before
	public void parseFile() {
		try {
			prepareResult();
		} catch (Exception e) {
			// e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void test() {
		Failer failer = new Failer();
		int line;
		try {
			line = new FileComparator(myCompareFileName, myResultFilePath)
					.compare();
			if (line != -1) {
				failer.fail("Files differ in line " + line);
			}
		} catch (Exception e) {
			failer.fail(e.getMessage());
		}
	}

	// @AfterClass
	public static void clear() {
		String dirName = myResultsDir;
		File fileDir = new File(dirName);
		for (String fileName : fileDir.list()) {
			(new File(fileName)).delete();
		}
	}
}

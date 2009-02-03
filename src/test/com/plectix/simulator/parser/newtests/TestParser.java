package com.plectix.simulator.parser.newtests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.cli.ParseException;
import org.junit.*;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.parser.FileReadingException;
import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.FileComparator;
import com.plectix.simulator.util.FileDirComparator;

public class TestParser extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix();
	private static final String myResultsDir = myTestFileNamePrefix + "results/";
	private String myFileName;
	private String myTestFilePath;
	private String myResultFilePath;
	
	public TestParser(String fileName) {
		myFileName = fileName;
		myTestFilePath = myTestFileNamePrefix + myFileName;
		myResultFilePath = myResultsDir + myFileName;
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return DirectoryTestsRunner.getAllTestFileNames(myTestFileNamePrefix);
	}

	@Override
	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}

	private String parseFile(String fileName) throws ParseException, FileReadingException, ParseErrorException {
		KappaFile kf = (new KappaFileReader(myTestFilePath)).parse();
		SimulationArguments args = getInitializator().prepareDefaultArguments(myTestFilePath);
		KappaModel model = new KappaModelCreator(args).createModel(kf);
		return model.toString();
	}
	
	@BeforeClass
	public static void init() {
		File resultDir = new File(myResultsDir);
		resultDir.mkdir();
	}
	
	@Before
	public void parseFile() {
		PrintWriter pw = null;
		try {
			File newResultFile = new File(myResultFilePath);
			newResultFile.createNewFile();
			String modelString = parseFile(myTestFilePath);
			pw = new PrintWriter(newResultFile);
			pw.println(modelString);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@Test
	public void test() {
		Failer failer = new Failer();
		int line;
		try {
			line = new FileComparator(myTestFilePath, myResultsDir + myFileName).compare();
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

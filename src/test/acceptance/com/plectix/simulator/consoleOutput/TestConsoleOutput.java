package com.plectix.simulator.consoleOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.EasyReader;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.util.BackingUpPrintStream;
import com.plectix.simulator.util.Failer;

@RunWith(Parameterized.class)
public class TestConsoleOutput extends DirectoryTestsRunner {
	private static final String testDir = "test.data" + File.separator + "consoleOutput" + File.separator;
	private static final String prefixFileName = testDir + "kappasource" + File.separator;
	private final String currentCommandLine;
	private final static Failer failer = new Failer();
	private final BackingUpPrintStream resultFileContent;
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(prefixFileName);
	}
	
//	private String prepareFilenameString(String filename) {
//		return filename.replaceAll(File.separator + File.separator, File.separator + File.separator + File.separator + File.separator);
//	}
	
	private String resultFilePath(String fileName) {
		return testDir + "compareTo" + File.separator + fileName.substring(0, fileName.indexOf(".")) + ".result";
	}
	
	private BackingUpPrintStream getResultFileContent(String fileName) throws FileNotFoundException {
		EasyReader reader = new EasyReader(fileName);
		String line = reader.getLine();
		BackingUpPrintStream ps = new BackingUpPrintStream();
		
		while (line != null) {
			ps.println(line);
			line = reader.getLine();	
		}
		return ps;
	}
	
	public TestConsoleOutput(String fileName) throws FileNotFoundException {
		String fullPath = prefixFileName + fileName;
		EasyReader reader = new EasyReader(fullPath);
		String line = reader.getLine();
		line = line.substring(1);
		currentCommandLine = line + fullPath;
		resultFileContent = this.getResultFileContent(this.resultFilePath(fileName));
	}
	
	@Test
	public void testUsualWay() throws Exception {
		Simulator simulator = new Simulator();
		BackingUpPrintStream ps = new BackingUpPrintStream();
		
		SimulatorCommandLine commandLine = new SimulatorCommandLine(currentCommandLine);
		SimulatorInputData inputData = new SimulatorInputData(commandLine.getSimulationArguments(), ps);
		
		simulator.run(inputData);
		
		String psItem = "";
		String resultItem = "";
		for (int i = 0; psItem != null && resultItem != null; i++) {
			if (!bothStartsWithOneOfThese(psItem, resultItem, 
					"-Reading Kappa input", 
					"JSIM: Build on",
					"java "
					)) {
				
				failer.assertEquals("line " + i + " : ", resultItem.trim(), psItem.trim());
			}
			psItem = ps.getContentItem(i);
			resultItem = resultFileContent.getContentItem(i);
		}
		failer.assertTrue("One output contains more information than the other" + psItem, psItem == null && resultItem == null);
	}
	
	private boolean bothStartsWithOneOfThese(String s1, String s2, String...exceptions) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		for (String exElement : exceptions) {
			exElement = exElement.toLowerCase();
			if (s1.startsWith(exElement) && s2.startsWith(exElement)) {
				return true;
			}
		}
		
		for (OperationType exElement : OperationType.values()) {
			String currentHeader = ("-" + exElement).toLowerCase();
			if (s1.startsWith(currentHeader) && s2.startsWith(currentHeader)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void testApiOutput() {
		//TODO implement!
	}

	@Override
	public String getPrefixFileName() {
		return prefixFileName;
	}
}


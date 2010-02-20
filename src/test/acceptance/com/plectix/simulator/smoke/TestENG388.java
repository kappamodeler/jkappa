package com.plectix.simulator.smoke;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.parser.EasyReader;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.SilentPrintStream;

public class TestENG388 {
	private final String testFile = "data" + File.separator + "debugging-inf.ka";
	
	@Test
	public void test() throws Exception { 
		File tempFile = File.createTempFile("temp", "xml");
		String[] commandLine = new String[]{"--sim", 
											testFile, 
											"--time", 
											"100", 
											"--xml-session-name",
											tempFile.getPath()};
		
		SimulatorInputData inputData = new SimulatorInputData(new SimulatorCommandLine(commandLine).getSimulationArguments(), new SilentPrintStream());
		Simulator simulator = new Simulator();
		simulator.run(inputData);
		this.checkFile(tempFile.getPath());
	}
	
	private boolean lineIsAcceptable(String line) {
		return line.endsWith(",5,1") || line.endsWith(",4,3");
	}
	
	private void checkFile(String filePath) throws FileNotFoundException {
		EasyReader reader = new EasyReader(filePath);
		System.out.println(filePath);
		String line = reader.getLine().trim();
		boolean csvStarted = false;
		int i = 0;
		while (!line.startsWith("]]>")) {
			if (line.startsWith("<CSV><")) {
				csvStarted = true;
			} else if (csvStarted) {
				if (!this.lineIsAcceptable(line)) {
					org.junit.Assert.fail("Line " + line + " shows that simulation went unexpected way");
				} else {
					i++;
				}
			}
			line = reader.getLine().trim();
		}
		if (i < 100) {
			org.junit.Assert.fail("Output file contains not enough lines");
		}
	}
}

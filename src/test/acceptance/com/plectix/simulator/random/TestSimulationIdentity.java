package com.plectix.simulator.random;

import java.io.File;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;
import com.plectix.simulator.util.io.BackingUpPrintStream;

public class TestSimulationIdentity {
//	@Test
//	public final void test1() throws Exception {
//		testCommandLine(new String[]{"--sim", "data" + File.separator + "exponentielle.ka",
//							"--event", "10", "--seed", "2"});
//	}
	
	@Test
	public final void testAll() throws Exception {
		for (String fileName : new File("data").list()) {
			if (fileName.endsWith(".ka") && !this.skipFile(fileName)) {
				try {
					testCommandLine(new String[]{"--sim", "data" + File.separator + fileName,
					"--event", "10", "--seed", "2"});
				} catch(OutOfMemoryError e) {
					System.out.println("OutOfMemory detected : "  + fileName);
					System.gc();
				} catch(Exception e) {
					System.out.println(e + " : " + fileName);
				}
			}
		}
	}
	
	private final boolean skipFile(String fileName) {
		return fileName.equals("StaticAnalysisLARGE.ka")
			|| fileName.startsWith("large_systems")
			|| fileName.equals("invexp.ka")
			|| fileName.equals("egfr.ka")
			|| fileName.equals("debugging-compression.ka")
			|| fileName.equals("Simulation.ka")
			|| fileName.equals("TyThomson-ReceptorAndGProtein.ka")
			|| fileName.equals("Seda_111008_Insulin_Present.ka")
			|| fileName.startsWith("ENG");
	}
	
	private final void testCommandLine(String[] commandLine) throws Exception {
		BackingUpPrintStream[] simulationsOutput = new BackingUpPrintStream[10];
		for (int i = 0; i< simulationsOutput.length; i++) {
			simulationsOutput[i] = this.generateSimulationResult(commandLine);
		}
		this.streamsHaveSimilarContent(simulationsOutput);
	}
	
	private final BackingUpPrintStream generateSimulationResult(String[] commandLine) throws Exception {
		Simulator simulator = new Simulator();
		SimulatorInputData inputData = new SimulatorInputData(new SimulatorCommandLine(
				commandLine).getSimulationArguments());
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SimulatorInitializationOperation(simulator, inputData));

		BackingUpPrintStream stream1 = new BackingUpPrintStream();
		simulator.getSimulationData().setConsolePrintStream(stream1);
		
		manager.perform(new TalkingAlotSimulationOperation(simulator));
		return stream1;
	}
	
	private final void debugOutput(BackingUpPrintStream...streams) {
		for (int index = 0; ;index++) {
			if (streams[0].getContentItem(index) == null
					&& streams[1].getContentItem(index) == null) {
				return;
			}
			System.out.println("---");
			for (BackingUpPrintStream stream : streams) {
				System.out.println(stream.getContentItem(index));	
			}
		}
	}
	
	private final boolean streamsHaveSimilarContent(BackingUpPrintStream...streams) {
		for (int index = 0; ;index++) {
			String firstStringItem = streams[0].getContentItem(index); 
			if (firstStringItem == null) {
				for (BackingUpPrintStream stream : streams) {
					if (stream.getContentItem(index) != null) {
						return false;
					}
				}
				return true;
			} else {
				for (BackingUpPrintStream stream : streams) {
					try {
						Double.valueOf(firstStringItem);
						if (!firstStringItem.equals(stream.getContentItem(index))) {
							return false;
						}
					} catch(NumberFormatException e) {
						
					}
				}
			}
		}
	}
}

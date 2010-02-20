//package com.plectix.simulator.xmlcompare;
//
//import java.io.File;
//import java.io.PrintStream;
//import java.util.Collection;
//import java.util.LinkedList;
//
//import com.plectix.simulator.controller.SimulationService;
//import com.plectix.simulator.controller.SimulatorInputData;
//import com.plectix.simulator.simulator.DefaultSimulatorFactory;
//import com.plectix.simulator.simulator.SimulatorCommandLine;
//
//public final class JavaXMLMaker {
//
//	private final String mySourcePath;
//	private final String myOutputPath;
//
//	private final PrintStream DEFAULT_OUTPUT_STREAM = System.out;
//
//	private final double[] time = new double[] { 0.01, 1000, 0.5, 100, 50, 50,
//			25, 25, 10, 10, 10, 40, 10, 10, 15, 10 // ,1000
//			, 15, 20 };
//
//	public JavaXMLMaker(String sourcePath, String outPath) {
//		mySourcePath = sourcePath;
//		myOutputPath = outPath;
//	}
//
//	private Collection<String> getAllTestFileNames(String prefix) {
//		LinkedList<String> parameters = new LinkedList<String>();
//		try {
//			File testFolder = new File(prefix);
//			if (testFolder.isDirectory()) {
//				for (String fileName : testFolder.list()) {
//					if (fileName.endsWith(".ka")) {
//						parameters.add(fileName);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			junit.framework.Assert.fail(e.getMessage());
//		}
//		return parameters;
//	}
//
//	private void setup(SimulationService service, String fileName, double time)
//			throws Exception {
//		SimulatorCommandLine commandLine = new SimulatorCommandLine(
//				new String[] { "--sim", mySourcePath + fileName, "--time",
//						"" + time });
//
//		commandLine.getSimulationArguments().setXmlSessionName(
//				myOutputPath + fileName.substring(0, fileName.indexOf(".ka"))
//						+ "_java.xml");
//
//		service.submit(new SimulatorInputData(commandLine
//				.getSimulationArguments(), DEFAULT_OUTPUT_STREAM), null);
//	}
//
//	public void make() throws Exception {
//		int i = 0;
//		SimulationService service = new SimulationService(
//				new DefaultSimulatorFactory());
//		for (String fileName : getAllTestFileNames(mySourcePath)) {
//			setup(service, fileName, time[i]);
//			i++;
//		}
//		service.shutdown();
//	}
//
//	public static void main(String[] args) {
//		JavaXMLMaker xmlMaker = new JavaXMLMaker(PathFinder.SOURCE_DIR,
//				PathFinder.OUTPUT_DIR);
//		try {
//			xmlMaker.make();
//		} catch (Exception e) {
//			e.printStackTrace();
//			junit.framework.Assert.fail(e.getMessage());
//		}
//	}
//}

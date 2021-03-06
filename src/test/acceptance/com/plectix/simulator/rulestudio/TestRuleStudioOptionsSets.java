package com.plectix.simulator.rulestudio;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulationclasses.solution.OperationMode;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.api.steps.CommandLineDefinedWorkflow;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.util.io.XMLOutputOracle;

public class TestRuleStudioOptionsSets {

	@Test
	public final void testSimulationA() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine("--sim " + kappaFile
				+ " --memory-limit 1024 --no-maps --event 1 --points 1000"
				+ " --rescale 1.0 --xml-session-name " + xmlOutputFile.getPath()
				+ " --clock-precision 50");
			
			Assert.assertTrue("simulation plot contains no data", 
				XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
			Assert.assertFalse("Final state conains snapshots", 
					XMLOutputOracle.finalStateDataIsNotEmpty(kappaSystem));
			
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			checkSimulationArgumentsInCase1(arguments);
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testSimulationB() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine("--sim " + kappaFile
				+ " --memory-limit 1024 --no-maps --event 1 --points 1000"
				+ " --rescale 1.0 --xml-session-name " + xmlOutputFile.getPath()
				+ " --clock-precision 50 --output-final-state");
			
			Assert.assertTrue("simulation plot contains no data", 
				XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
			Assert.assertTrue("Final state conains no snapshots", 
					XMLOutputOracle.finalStateDataIsNotEmpty(kappaSystem));
			
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong rescale", Math.abs(arguments.getRescale() - 1) < 0.001);
			checkSimulationArgumentsInCase1(arguments);
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private void checkSimulationArgumentsInCase1(SimulationArguments arguments) {
		Assert.assertTrue("Wrong rescale", Math.abs(arguments.getRescale() - 1) < 0.001);
		// memory limit cannot be set inside jsim
		Assert.assertTrue("Wrong events number", arguments.getMaxNumberOfEvents() == 1);
		Assert.assertTrue("Wrong points number", arguments.getPoints() == 1000);
		Assert.assertTrue("Wrong clock precision", arguments.getClockPrecision() == 50);
		Assert.assertFalse("Wrong simulation mode", arguments.isTime());
		Assert.assertFalse("We calculate subviews", arguments.needToCreateSubViews());
	}
	
	@Test
	public final void testStories() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "ex_stories.ka";
			KappaSystem kappaSystem = this.processCommandLine("--storify " + kappaFile
				+ " --compress-stories --use-strong-compression --iteration 10"
				+ " --memory-limit 1024 --no-maps --time 10"
				+ " --xml-session-name " + xmlOutputFile.getPath()
				+ " --clock-precision 50");
			
			Assert.assertFalse("Final state conains snapshots", 
					XMLOutputOracle.finalStateDataIsNotEmpty(kappaSystem));
			Assert.assertTrue("There are no stories sections", 
					XMLOutputOracle.storiesDataIsNotEmpty(kappaSystem));
			
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
			Assert.assertTrue("Wrong time limit number", Math.abs(arguments.getTimeLimit() - 10) < 0.1);
			Assert.assertFalse("We calculate subviews", arguments.needToCreateSubViews());
			Assert.assertTrue("Wrong simulation mode", arguments.needToStorify());
			Assert.assertTrue("Wrong iterations number", arguments.getIterations() == 10);
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testLiveDataA() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "easy-egfr.ka";
			KappaSystem kappaSystem = this.processCommandLine("--sim " + kappaFile 
					+ " --operation-mode 1 --time 0.0001 --points 120"
					+ " --xml-session-name " + xmlOutputFile
	                + " --live-data-interval 12 --live-data-points 1");
			
			Assert.assertTrue("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
			Assert.assertTrue("Wrong time limit number", Math.abs(arguments.getTimeLimit() - 0.0001) < 0.001);
			checkSimulationArgumentsInCase3(arguments);
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testLiveDataB() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "easy-egfr.ka";
			KappaSystem kappaSystem = this.processCommandLine("--sim " + kappaFile 
					+ " --operation-mode 1 --event 35 --points 120"
					+ " --xml-session-name " + xmlOutputFile
	                + " --live-data-interval 12 --live-data-points 1");
			
			Assert.assertTrue("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
			Assert.assertTrue("Wrong time limit number", arguments.getMaxNumberOfEvents() == 35);
			checkSimulationArgumentsInCase3(arguments);
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private void checkSimulationArgumentsInCase3(SimulationArguments arguments) {
		Assert.assertTrue("Wrong simulation mode", arguments.getOperationMode().equals(OperationMode.FIRST));
		Assert.assertTrue("Wrong points number", arguments.getPoints() == 120);
		Assert.assertTrue("Wrong live data interval", arguments.getLiveDataInterval() == 12);
		Assert.assertTrue("Wrong live data points", arguments.getLiveDataPoints() == 1);
		Assert.assertTrue("Wrong simulation mode", arguments.needToSimulate());
	}
	
	//TODO implement --output-*** options
//	@Test
//	public final void testCaseQualitativeCompression() {
//		try {
//			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
//			String kappaFile = "data" + File.separator + "abc2.ka";
//			KappaSystem kappaSystem = this.processCommandLine("--build-influence-map"
//	                + " --no-dump-iteration-number"
//	                + " --no-dump-rule-iteration"
//	                + " --no-enumerate-complexes"
//	                + " --contact-map " + kappaFile
//	                + " --output-qualitative-compression " + xmlOutputFile
//	                + " --xml-session-name " + xmlOutputFile);
//			
//			Assert.assertFalse("simulation plot contains no data", 
//					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
//				
//			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
//			Assert.assertTrue("Wrong output file path", 
//					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
//			Assert.assertTrue("We do not compress rules", arguments.needToRunQualitativeCompression());
//			Assert.assertFalse("We do wrong compression", arguments.needToRunQuantitativeCompression());
//		} catch(Exception e) {
//			e.printStackTrace();
//			Assert.fail(e.getMessage());
//		}
//	}
//	
//  TODO implement --output-*** options
//	@Test
//	public final void testCaseQuantitativeCompression() {
//	try {
//		File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
//		String kappaFile = "data" + File.separator + "abc2.ka";
//		KappaSystem kappaSystem = this.processCommandLine("--build-influence-map"
//                + " --no-dump-iteration-number"
//                + " --no-dump-rule-iteration"
//                + " --no-enumerate-complexes"
//                + " --contact-map " + kappaFile
//                + " --output-quantitative-compression " + xmlOutputFile
//                + " --xml-session-name " + xmlOutputFile);
//		
//		Assert.assertFalse("simulation plot contains no data", 
//				XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
//			
//		SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
//		Assert.assertTrue("Wrong output file path", 
//				arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
//		Assert.assertFalse("We do wrong compression", arguments.needToRunQualitativeCompression());
//		Assert.assertTrue("We do not compress rules", arguments.needToRunQuantitativeCompression());
//	} catch(Exception e) {
//		e.printStackTrace();
//		Assert.fail(e.getMessage());
//	}
//}
	
	@Test
	public final void testCaseContactMap() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine(
					"--no-build-influence-map",
	                "--no-compute-qualitative-compression",
	                "--no-compute-quantitative-compression",
	                "--no-dump-iteration-number",
	                "--no-dump-rule-iteration",
	                "--no-enumerate-complexes",
	                "--contact-map",
	                kappaFile,
	                "--xml-session-name",
	                xmlOutputFile.getPath());
			
			Assert.assertFalse("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
			Assert.assertFalse("We enumerate complexes", arguments.needToEnumerationOfSpecies());
			Assert.assertFalse("We compress rules", arguments.needToRunQuantitativeCompression());
			Assert.assertFalse("We compress rules", arguments.needToRunQualitativeCompression());
			Assert.assertFalse("We do not build influence map", arguments.needToBuildInfluenceMap());
			Assert.assertTrue("We do not build contact map", arguments.needToBuildContactMap());
			Assert.assertTrue("Wrong simulation mode", arguments.needToBuildContactMap());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testCaseInfluenceMap() {
		try {
			File xmlOutputFile = File.createTempFile("testOptionsSet", "test");
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine(
					"--no-compute-qualitative-compression",
	                "--no-compute-quantitative-compression",
	                "--no-dump-iteration-number",
	                "--no-dump-rule-iteration",
	                "--no-enumerate-complexes",
	                "--contact-map",
	                kappaFile,
	                "--xml-session-name",
	                xmlOutputFile.getPath(),
	                "--build-influence-map" );
			
			Assert.assertFalse("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("Wrong output file path", 
					arguments.getXmlOutputDestination().equals(xmlOutputFile.getPath()));
			Assert.assertFalse("We enumerate complexes", arguments.needToEnumerationOfSpecies());
			Assert.assertFalse("We compress rules", arguments.needToRunQuantitativeCompression());
			Assert.assertFalse("We compress rules", arguments.needToRunQualitativeCompression());
			Assert.assertTrue("We do not build influence map", arguments.needToBuildInfluenceMap());
			Assert.assertTrue("We do not build contact map", arguments.needToBuildContactMap());
			Assert.assertTrue("Wrong simulation mode", arguments.needToBuildContactMap());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testCaseReachables() {
		try {
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine(
	                "--contact-map",
	                kappaFile,
	                "--compute-local-views");
			
			Assert.assertFalse("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("We do not compute local views", arguments.needToCreateLocalViews());
			Assert.assertTrue("We do not build contact map", arguments.needToBuildContactMap());
			Assert.assertFalse("We do enumerate species", arguments.needToEnumerationOfSpecies());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testCaseEnumerating() {
		try {
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine(
	                "--contact-map",
	                kappaFile,
	                "--compute-local-views",
	                "--enumerate-complexes");
			
			Assert.assertFalse("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("We do not compute local views", arguments.needToCreateLocalViews());
			Assert.assertTrue("We do not build contact map", arguments.needToBuildContactMap());
			Assert.assertTrue("We do not enumerate species", arguments.needToEnumerationOfSpecies());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public final void testCaseCompilation() {
		try {
			String kappaFile = "data" + File.separator + "example.ka";
			KappaSystem kappaSystem = this.processCommandLine(
	                "--compile",
	                kappaFile);
			
			Assert.assertFalse("simulation plot contains no data", 
					XMLOutputOracle.simulationPlotDataIsNotEmpty(kappaSystem));
				
			SimulationArguments arguments = kappaSystem.getSimulationData().getSimulationArguments();
			Assert.assertTrue("We do not compile", arguments.needToCompile());
			Assert.assertFalse("We simulate", arguments.needToSimulate());
			Assert.assertFalse("We compute stories", arguments.needToStorify());
			Assert.assertFalse("We compute contact map", arguments.needToBuildContactMap());
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	private KappaSystem processCommandLine(String commandLine) throws Exception {
		return this.processCommandLine(commandLine.split(" "));
	}
	
	private KappaSystem processCommandLine(String...commandLine) throws Exception {
		Simulator simulator = new Simulator();
		SimulatorCommandLine simulatorCommandLine = new SimulatorCommandLine(commandLine);
		SimulationArguments arguments = simulatorCommandLine.getSimulationArguments();
		SimulatorInputData inputData = new SimulatorInputData(arguments);
		KappaSystem kappaSystem = simulator.getSimulationData().getKappaSystem();
		OperationManager manager = kappaSystem.getOperationManager();
		
		manager.perform(new CommandLineDefinedWorkflow(simulator, inputData));
		return kappaSystem;
	}
}

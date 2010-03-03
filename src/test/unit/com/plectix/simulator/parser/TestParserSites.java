package com.plectix.simulator.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.Initializator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.abstractmodel.reader.KappaModelCreator;
import com.plectix.simulator.parser.builders.KappaSystemBuilder;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;

public class TestParserSites extends DirectoryTestsRunner {

	private static String testDirectory = "test.data/parser";

	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator.generate(
				getAllTestFileNames("test.data/parser"), false);
	}

	private Object fileName;
	private Integer time = 1;
	private Integer opMode;
	private SimulationData simulationData;
	private SimulationArguments args;

	@Override
	public String getPrefixFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public TestParserSites(String fileName, Integer opMode) {
		this.fileName = fileName;
		this.opMode = opMode;

	}

	@Before
	public void setup() {
		init(testDirectory + fileName, opMode);

	}

	@Override
	public void init(String filePath, Integer opMode) {
		Simulator mySimulator = new Simulator();

		simulationData = mySimulator.getSimulationData();

		args = null;
		try {
			// seed=2: 13
			// seed=9: 13
			// seed=13: 11(storage)
			args = Initializator.prepareTimeArguments(filePath, time, opMode);

		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

	}

	@Test
	public void test() throws Exception {
		KappaFileReader kappaFileReader = null;

		try {
			kappaFileReader = new KappaFileReader(testDirectory + File.separator
					+ fileName, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		KappaFile kappaFile = kappaFileReader.parse();
		
//		SimulationData simulationData = new SimulationData();
//		OperationManager manager = new OperationManager(simulationData.getKappaSystem());
//		KappaFile kappaFile = manager.perform(new KappaFileLoadingOperation(simulationData));
		
		try {
			KappaModel model = new KappaModelCreator(args)
					.createModel(kappaFile);
			new KappaSystemBuilder(simulationData).build(model);
		} catch (SimulationDataFormatException e) {
			if (fileName.equals("test00.test")) {
				assertTrue(e.getErrorType().getMessage().equals(
						"repeated site name in agent"));
				return;
			}
			if (fileName.equals("test01.test")) {
				assertTrue(e.getErrorType().getMessage().equals(
						"agent connected with himself"));
				return;
			}
			if (fileName.equals("test02.test")) {
				assertTrue(e.getErrorType().getMessage().equals(
						"* in site name"));
				return;
			}
		}

		assertFalse(true);
	}

}

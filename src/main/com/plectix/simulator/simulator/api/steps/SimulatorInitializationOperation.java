package com.plectix.simulator.simulator.api.steps;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;

import com.plectix.simulator.BuildConstants;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.options.OptionsSetSingleton;
import com.plectix.simulator.util.Info.InfoType;

public class SimulatorInitializationOperation extends AbstractOperation<Simulator> {
	private final SimulatorInputData inputData;
	private final Simulator simulator;
	private static final String INTRO_MESSAGE = "JSIM: Build on "
		+ BuildConstants.BUILD_DATE + " from Revision "
		+ BuildConstants.BUILD_SVN_REVISION + ", JRE: "
		+ System.getProperty("java.vendor") + " "
		+ System.getProperty("java.version");

	
	public SimulatorInitializationOperation(Simulator simulator, SimulatorInputData inputData) {
		super(simulator.getSimulationData(), OperationType.SIMULATOR_INITIALIZATION);
		this.inputData = inputData;
		this.simulator = simulator;
	}
	
	protected Simulator performDry() {
		SimulationData simulationData = simulator.getSimulationData();
		simulationData.setConsolePrintStream(inputData.getPrintStream());

		SimulationArguments simulationArguments = inputData.getSimulationArguments();
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager(); 
		consoleOutputManager.addAdditionalInfo(InfoType.INFO, INTRO_MESSAGE);
		
		if (simulationArguments.isNoDumpStdoutStderr()) {
			consoleOutputManager.setPrintStream(null);
		}

		// TODO move this code to DumpHelpOperation
//		PrintStream printStream = consoleOutputManager.getPrintStream();
//		// do not print anything above because the line above might have turned
//		// the printing off...
//
//		if (simulationArguments.needToDumpHelp()) {
//			if (printStream != null) {
//				PrintWriter printWriter = new PrintWriter(printStream);
//				HelpFormatter formatter = new HelpFormatter();
//				formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH,
//						SimulationMain.COMMAND_LINE_SYNTAX, null,
//						OptionsSetSingleton.getInstance(),
//						HelpFormatter.DEFAULT_LEFT_PAD,
//						HelpFormatter.DEFAULT_DESC_PAD, null, false);
//				printWriter.flush();
//			}
//		}

		simulationData.setSimulationArguments(InfoType.OUTPUT, simulationArguments);
		return simulator;
	}

	/**
	 * Anyway, we must reload inputData each time 
	 */
	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Simulator retrievePreparedResult() {
		return null;
	}

}

package com.plectix.simulator.simulator.api.steps;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.simulator.options.OptionsSetSingleton;

public class DumpHelpOperation extends AbstractOperation<Object> {
	private final SimulationData simulationData;
	
	public DumpHelpOperation(SimulationData simulationData) {
		super(simulationData, OperationType.DUMP_HELP);
		this.simulationData = simulationData;
	}

	@Override
	protected Object performDry() throws Exception {
		PrintStream printStream = simulationData.getConsoleOutputManager().getPrintStream();
		// do not print anything above because the line above might have turned
		// the printing off...

		if (printStream != null) {
			PrintWriter printWriter = new PrintWriter(printStream);
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH,
					SimulationMain.COMMAND_LINE_SYNTAX, null,
					OptionsSetSingleton.getInstance(),
					HelpFormatter.DEFAULT_LEFT_PAD,
					HelpFormatter.DEFAULT_DESC_PAD, null, false);
			printWriter.flush();
		}
		return null;
	}

	@Override
	protected Object retrievePreparedResult() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}
}

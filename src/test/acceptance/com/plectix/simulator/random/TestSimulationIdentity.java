package com.plectix.simulator.random;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import com.plectix.simulator.controller.SimulatorInputData;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.simulator.api.steps.OperationManager;
import com.plectix.simulator.simulator.api.steps.SimulationOperation;
import com.plectix.simulator.simulator.api.steps.SimulatorInitializationOperation;
import com.plectix.simulator.util.io.BackingUpPrintStream;

public class TestSimulationIdentity {
	@Test
	public final void test() throws Exception {
		BackingUpPrintStream stream1 = this.getSimulationResult();
		BackingUpPrintStream stream2 = this.getSimulationResult();
		BackingUpPrintStream stream3 = this.getSimulationResult();
		BackingUpPrintStream stream4 = this.getSimulationResult();
		BackingUpPrintStream stream5 = this.getSimulationResult();
		this.debugOutput(stream1, stream2, stream3, stream4, stream5);
		Assert.assertTrue(stream1.hasEqualContent(stream2));
	}
	
	private final BackingUpPrintStream getSimulationResult() throws Exception {
		Simulator simulator = new Simulator();
		SimulatorInputData inputData = new SimulatorInputData(new SimulatorCommandLine(
				new String[]{"--sim", "data" + File.separator + "exponentielle.ka",
						"--event", "10", "--seed", "2"}).getSimulationArguments());
		OperationManager manager = simulator.getSimulationData().getKappaSystem().getOperationManager();
		manager.perform(new SimulatorInitializationOperation(simulator, inputData));

		BackingUpPrintStream stream1 = new BackingUpPrintStream();
		simulator.getSimulationData().setConsolePrintStream(stream1);
		
		manager.perform(new TalkingAlotSimulationOperation(simulator));
		System.out.println("----------------------------------------------------");
		return stream1;
	}
	
	private final void debugOutput(BackingUpPrintStream...streams) {
		for (int index = 0; ;index++) {
			if (streams[0].getContentItem(index) == null
					&& streams[1].getContentItem(index) == null) {
				return;
			}
			System.out.println("---");
			System.out.println(streams[0].getContentItem(index));
			System.out.println(streams[1].getContentItem(index));
			System.out.println(streams[2].getContentItem(index));
			System.out.println(streams[3].getContentItem(index));
			System.out.println(streams[4].getContentItem(index));
		}
	}
}

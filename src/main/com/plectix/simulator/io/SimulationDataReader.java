package com.plectix.simulator.io;

import java.util.List;

import com.plectix.simulator.parser.KappaFile;
import com.plectix.simulator.parser.KappaFileReader;
import com.plectix.simulator.parser.KappaSystemParser;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.builders.RuleBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMapMode;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationDataReader {
	private final SimulationData simulationData;
	
	public SimulationDataReader(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	// TODO separate
	public final void readSimulationFile(InfoType outputType) {
		if (!simulationData.argumentsInitialized()) {
			throw new RuntimeException(
					"Simulator Arguments must be set before reading the simulation file!");
		}
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		
		simulationData.getClock().resetBar();
		kappaSystem.getObservables().setOcamlStyleObsName(
				simulationArguments.isOcamlStyleNameingInUse());
		kappaSystem.getObservables().setUnifiedTimeSeriesOutput(
				simulationArguments.isUnifiedTimeSeriesOutput());
		if (simulationArguments.getSnapshotsTimeString() != null) {
			simulationData.setSnapshotTime(simulationArguments.getSnapshotsTimeString());
		}

		try {
			KappaFileReader kappaFileReader;
			if (simulationArguments.getInputFilename() != null) {
				kappaFileReader = new KappaFileReader(simulationArguments.getInputFilename(), true);
			} else {
				kappaFileReader = new KappaFileReader(simulationArguments.getInputCharArray());
			}

			if (simulationArguments.getFocusFilename() != null) {
				this.setFocusOn(simulationArguments.getFocusFilename());
			} else {
				kappaSystem.getContactMap().setMode(ContactMapMode.MODEL);
			}

			KappaFile kappaFile = kappaFileReader.parse();
			KappaSystemParser parser = new KappaSystemParser(kappaFile, simulationData);
			parser.parse(outputType);
		} catch (Exception e) {
			ConsoleOutputManager console = simulationData.getConsoleOutputManager();
			console.println("Error in file \"" + simulationArguments.getInputFilename()
						+ "\" :");
			if (console.initialized()) {
				e.printStackTrace(console.getPrintStream());
			}
			throw new IllegalArgumentException(e);
		}
	}
	
	private final void setFocusOn(String fileNameFocusOn) throws Exception {
		KappaSystem kappaSystem = simulationData.getKappaSystem();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments(); 
		
		KappaFileReader kappaFileReader = new KappaFileReader(fileNameFocusOn, true);
		KappaFile kappaFile = kappaFileReader.parse();
		List<Rule> ruleList = (new RuleBuilder(new KappaSystem(simulationData)))
				.build(new RulesParagraphReader(simulationArguments,
						new AgentFactory(false)).readComponent(kappaFile
						.getRules()), null);

		kappaSystem.getContactMap().setSimulationData(kappaSystem);
		if (ruleList != null && !ruleList.isEmpty()) {
			kappaSystem.getContactMap().setFocusRule(ruleList.get(0));
			kappaSystem.getContactMap().setMode(ContactMapMode.AGENT_OR_RULE);
		}
		else{
			kappaSystem.getContactMap().setFocusRule(null);
		}
	}
}

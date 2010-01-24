package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

public abstract class KappaParagraphReader<E> {
	private final SimulationArguments simulationArguments;
	private final AgentFactory agentFactory;
	
	KappaParagraphReader(SimulationArguments simulationArguments,
			AgentFactory factory) {
		this.simulationArguments = simulationArguments;
		this.agentFactory = factory;
	}
	
	final List<ModelAgent> parseAgents(String line)
			throws ParseErrorException, DocumentFormatException, IncompletesDisabledException {
		return agentFactory.parseAgent(line);
	}
	
	final SimulationArguments getArguments() {
		return simulationArguments;
	}
	
	public abstract E readComponent(KappaFileParagraph paragraph) throws SimulationDataFormatException;
}

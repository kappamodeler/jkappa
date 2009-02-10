package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

public abstract class KappaParagraphReader<E> {
	private final KappaModel myModel;
	private final SimulationArguments myArgs;
	private final AgentFactory myAgentFactory;
	
	public KappaParagraphReader(KappaModel model, SimulationArguments args, AgentFactory factory) {
		myModel = model;
		myArgs = args;
		myAgentFactory = factory;
	}
	
	protected final List<AbstractAgent> parseAgent(String line) throws ParseErrorException, DocumentFormatException {
		return myAgentFactory.parseAgent(line);
	}
	
	protected SimulationArguments getArguments() {
		return myArgs;
	}
	
	protected KappaModel getModel() {
		return myModel;
	}
	
	public abstract E readComponent(KappaFileParagraph paragraph) throws SimulationDataFormatException;
}

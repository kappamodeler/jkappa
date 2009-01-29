package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/ abstract class KappaParagraphReader<E> {
	private final KappaModel myModel;
	private final SimulationArguments myArgs;
	private final AgentFactory myAgentFactory;
	
	public KappaParagraphReader(KappaModel model, SimulationArguments args, AgentFactory factory) {
		myModel = model;
		myArgs = args;
		myAgentFactory = factory;
	}
	
	protected final List<AbstractAgent> parseAgent(String line) throws ParseErrorException {
		return myAgentFactory.parseAgent(line);
	}
	
//	protected final List<AbstractAgent> parseAgent(String line, long count) throws ParseErrorException {
//		return myAgentFactory.parseAgent(line, count);
//	}
	
	protected SimulationArguments getArguments() {
		return myArgs;
	}
	
	protected KappaModel getModel() {
		return myModel;
	}
	
	public abstract E addComponent(KappaFileParagraph paragraph) throws ParseErrorException;
}

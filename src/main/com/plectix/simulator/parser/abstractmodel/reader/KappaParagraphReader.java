package com.plectix.simulator.parser.abstractmodel.reader;

import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/ abstract class KappaParagraphReader<E> {
	private final KappaModel myModel;
	private final SimulationArguments myArgs;
	
	public KappaParagraphReader(KappaModel model, SimulationArguments args) {
		myModel = model;
		myArgs = args;
	}
	
	protected SimulationArguments getArguments() {
		return myArgs;
	}
	
	protected KappaModel getModel() {
		return myModel;
	}
	
	public abstract E addComponent(KappaFileParagraph paragraph) throws ParseErrorException;
}

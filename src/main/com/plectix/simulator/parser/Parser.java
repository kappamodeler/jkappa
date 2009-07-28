package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;



/*package*/ abstract class Parser<E> {
	private EasyFileReader myReader;
	
	public Parser(String path) throws FileNotFoundException {
		myReader = new EasyFileReader(path);
	}
	
	protected EasyFileReader getFileReader() {
		return myReader;
	}
	
	protected abstract E unsafeParse() throws SimulationDataFormatException, IOException;
	
	public E parse() throws SimulationDataFormatException {
		try {
			return unsafeParse();
		} catch(IOException e) {
			throw new FileReadingException(e.getMessage());
		} finally {
			myReader.close();
		}
	}
}

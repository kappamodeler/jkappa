package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Helper, wrapping class to file parser
 * @param <E> The structure should be returned by parser after any file reading 
 */
public abstract class Parser<E> {
	private final EasyReader easyReader;
	
	Parser(String string, boolean isFilename) throws FileNotFoundException {
		this.easyReader = new EasyReader(string, isFilename);
	}
	
	Parser(char[] buf) {
		this.easyReader = new EasyReader(buf);
	}
	
	final EasyReader getReader() {
		return easyReader;
	}
	
	protected abstract E unsafeParse() throws SimulationDataFormatException, IOException;
	
	/**
	 * Reads file and returns some kind of inner representation of it's data
	 * @return inner representation of it's data
	 * @throws SimulationDataFormatException if an error occurred
	 * @throws IOException 
	 */
	public final E parse() throws SimulationDataFormatException, IOException {
		try {
			return unsafeParse();
		} finally {
			easyReader.close();
		}
	}
}

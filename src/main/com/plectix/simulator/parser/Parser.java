package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Helper, wrapping class to file parser
 * @param <E> The structure should be returned by parser after any file reading 
 */
/*package*/ abstract class Parser<E> {
	private final EasyFileReader fileReader;
	
	public Parser(String path) throws FileNotFoundException {
		this.fileReader = new EasyFileReader(path);
	}
	
	protected final EasyFileReader getFileReader() {
		return fileReader;
	}
	
	protected abstract E unsafeParse() throws SimulationDataFormatException, IOException;
	
	/**
	 * Reads file and returns some kind of inner representation of it's data
	 * @return inner representation of it's data
	 * @throws SimulationDataFormatException if an error occurred
	 */
	public final E parse() throws SimulationDataFormatException {
		try {
			return unsafeParse();
		} catch(IOException e) {
			throw new FileReadingException(e.getMessage());
		} finally {
			fileReader.close();
		}
	}
}

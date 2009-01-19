package com.plectix.simulator.parser;

import java.io.*;

/*package*/ abstract class Parser<E> {
	private EasyFileReader myReader;
	
	public Parser(String path) {
		try{
			myReader = new EasyFileReader(path);
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
	
	protected EasyFileReader getFileReader() {
		return myReader;
	}
	
	protected abstract E unsafeParse() throws FileReadingException, IOException, ParseErrorException;
	
	public E parse() throws FileReadingException, ParseErrorException {
		try {
			return unsafeParse();
		} catch(IOException e) {
			throw new FileReadingException(e.getMessage());
		} finally {
			myReader.close();
		}
	}
}

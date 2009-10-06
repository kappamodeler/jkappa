package com.plectix.simulator.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is easy file reader - wrapping class for BufferedReader with couple of changes made
 */
/*package*/ class EasyFileReader {
	private final BufferedReader reader;
	private final String filePath;
	
	public EasyFileReader(String path) throws FileNotFoundException {
		this.filePath = path;
		this.reader = new BufferedReader(new FileReader(filePath));
	}
	
	/**
	 * Reads line from file
	 * @return another line
	 */
	public final String getLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final void close() {
		try {
			reader.close();
		} catch (IOException e) {
			// TODO something
			System.err.println("Can't close reader for file " + filePath);
		}
	}
}

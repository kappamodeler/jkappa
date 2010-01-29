package com.plectix.simulator.parser;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * This is easy file reader - wrapping class for BufferedReader with couple of changes made
 */
public class EasyReader {
	private final BufferedReader bufferedReader;
	private final String filePath;
	
	public EasyReader(String string, boolean isFilename) throws FileNotFoundException {
		if (isFilename) {
			this.filePath = string;
			this.bufferedReader = new BufferedReader(new FileReader(filePath));
		} else {
			this.filePath = null;
			this.bufferedReader = new BufferedReader(new StringReader(string));
		}
	}
	
	public EasyReader(String string) throws FileNotFoundException {
		this.filePath = string;
		this.bufferedReader = new BufferedReader(new FileReader(filePath));
	}
	
	public EasyReader(char[] buf) {
		this.filePath = null;
		this.bufferedReader = new BufferedReader(new CharArrayReader(buf));
	}
	
	/**
	 * Reads line from file
	 * @return another line
	 */
	public final String getLine() {
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final void close() {
		try {
			bufferedReader.close();
		} catch (IOException e) {
			// TODO something
			System.err.println("Can't close reader for file " + filePath);
		}
	}
}

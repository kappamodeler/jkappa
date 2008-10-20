package com.plectix.simulator.util;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*package*/ class EasyFileReader {
	private BufferedReader myReader;
	private String myDataFilePath;
	
	public EasyFileReader(String path) throws FileNotFoundException {
		myDataFilePath = path;
		myReader = new BufferedReader(new FileReader(myDataFilePath));
	}
	
	public String getStringFromFile() {
		try {
			return myReader.readLine();
		} catch (IOException e) {
			fail(e.getMessage());
			return null;
		}
	}
	
	public void close() {
		try {
			myReader.close();
		} catch(IOException e) {
			System.err.println("Can't close reader for file " + myDataFilePath);
		}
	}
}

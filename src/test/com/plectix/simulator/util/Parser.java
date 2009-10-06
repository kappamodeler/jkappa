package com.plectix.simulator.util;

import java.io.FileNotFoundException;

/*package*/abstract class Parser<E> {
	private EasyFileReader myReader;

	public Parser(String path) {
		try {
			myReader = new EasyFileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			junit.framework.Assert.fail(e.getMessage());
		}
	}

	protected EasyFileReader getFileReader() {
		return myReader;
	}

	protected abstract E unsafeParse();

	public E parse() {
		try {
			return unsafeParse();
		} finally {
			myReader.close();
		}
	}
}

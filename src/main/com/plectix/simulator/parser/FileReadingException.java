package com.plectix.simulator.parser;

import com.plectix.simulator.parser.exceptions.SimulationDataFormatException;

public class FileReadingException extends SimulationDataFormatException {
	private static final long serialVersionUID = 5936885230417363799L;

	public FileReadingException(String message) {
		super(message);
	}
}

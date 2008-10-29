package com.plectix.simulator.parser;

import com.plectix.simulator.components.CDataString;

public class ParserExceptionHandler {
	public String formMessage(CDataString cdata) {
		return cdata.getLineNumber() + ": [" + cdata.getLine() + "]";
	}
}

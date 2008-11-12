package com.plectix.simulator.parser.Exeptions;

import com.plectix.simulator.components.CSite;

public class ParseErrorExeptionLinkState extends ParseErrorException {
	private static final long serialVersionUID = -6203768272604059857L;

	public ParseErrorExeptionLinkState(String site) {
		super();
		String message = " Unexpected Link State: '" + site + "'";
		setMessage(message);
	}

	public ParseErrorExeptionLinkState(CSite site) {
		super();
		String message = " Unexpected Link State: '" + site.getName()
				+ "' from Agent: '" + site.getAgentLink().getName() + "'";
		setMessage(message);
	}

}

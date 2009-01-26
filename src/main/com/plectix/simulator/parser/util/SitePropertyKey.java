package com.plectix.simulator.parser.util;

/*package*/enum SitePropertyKey {
	INTERNAL_STATE("~"), BLIND_CONNECTION("!_"), WILDCARD("?"), CONNECTION("!");

	private final String mySymbol;

	private SitePropertyKey(String symbol) {
		mySymbol = symbol;
	}

	public String getSymbol() {
		return mySymbol;
	}
}

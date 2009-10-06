package com.plectix.simulator.parser.util;

//TODO REMOVE!
/*package*/ enum SitePropertyKey {
	INTERNAL_STATE("~"), BLIND_CONNECTION("!_"), WILDCARD("?"), CONNECTION("!");

	private final String symbol;

	private SitePropertyKey(String symbol) {
		this.symbol = symbol;
	}

	public final String getSymbol() {
		return symbol;
	}
}

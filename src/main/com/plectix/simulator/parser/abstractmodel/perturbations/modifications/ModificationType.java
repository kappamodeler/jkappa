package com.plectix.simulator.parser.abstractmodel.perturbations.modifications;

public enum ModificationType {
	ADDONCE("ADD"),
	DELETEONCE("DELETE"),
	RATE("RATE_MODIFICATION (you were not supposed to see that)");
	
	private final String string;
	
	private ModificationType(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}

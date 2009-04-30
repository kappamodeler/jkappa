package com.plectix.simulator.simulator.xml;

public enum EntityModifier {
	INTRO("INTRO"),
	RULE("RULE"),
	OBS("OBSERVABLE");
	
	private final String name;
	
	private EntityModifier(String o) {
		name = o;
	}
	
	public String getString() {
		return name;
	}
	
}

package com.plectix.simulator.simulator.xml;

public enum RelationModifier {

	STRONG("STRONG"),
	WEAK("WEAK");

	private final String name;
	
	private RelationModifier(String o) {
		name = o;
	}
	
	public String getString() {
		return name;
	}
}

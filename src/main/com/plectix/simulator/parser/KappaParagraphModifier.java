package com.plectix.simulator.parser;

/*package*/ enum KappaParagraphModifier {
	INITIAL_CONDITIONS_PREFIX("%init"), //7;
	SIMULATION_PREFIX("%obs"), // 6;
	STORIFY_PREFIX("%story"), // 8;
	MOD_PREFIX("%mod"); // 6;
	
	private final String string;
	
	private KappaParagraphModifier(String o) {
		string = o;
	}
	
	public String getString() {
		return string;
	}
	
	public static KappaParagraphModifier getValue(String line) {
		if (line != null) {
			for (KappaParagraphModifier modifier : values()) {
				if (line.startsWith(modifier.string)) {
					return modifier;
				}
			}
		}
		return null;
	}
}

package com.plectix.simulator.parser;

/**
 * KappaFileModifier is a String which helps parser to recognize the
 * beginning of new paragraph.
 * @author evlasov
 */
/*package*/ enum KappaParagraphModifier {
	INITIAL_CONDITIONS_PREFIX("%init"), //7;
	SIMULATION_PREFIX("%obs"), // 6;
	STORIFY_PREFIX("%story"), // 8;
	MOD_PREFIX("%mod"); // 6;
	
	private final String modifier;
	
	private KappaParagraphModifier(String modifier) {
		this.modifier = modifier;
	}
	
	public String getString() {
		return modifier;
	}
	
	public static KappaParagraphModifier getValue(String line) {
		if (line != null) {
			for (KappaParagraphModifier modifier : values()) {
				if (line.startsWith(modifier.modifier)) {
					return modifier;
				}
			}
		}
		return null;
	}
}

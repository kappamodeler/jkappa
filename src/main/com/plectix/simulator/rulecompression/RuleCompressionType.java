package com.plectix.simulator.rulecompression;

public enum RuleCompressionType {
	QUALITATIVE("Qualitative"),
	QUANTITATIVE("Quantitative");
	
	private final String string;
	
	private RuleCompressionType(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
}

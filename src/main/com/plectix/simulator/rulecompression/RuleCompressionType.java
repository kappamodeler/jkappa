package com.plectix.simulator.rulecompression;
/**
 * QUALITATIVE("Qualitative"),
 * QUANTITATIVE("Quantitative"
 * @author nkalinin
 *
 */
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

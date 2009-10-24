package com.plectix.simulator.xmlmap.rulecompression;

public class Association {

	private String fromRule;
	private String toRule;

	public Association(String from, String to) {
		fromRule = from;
		toRule = to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Association))
			return false;
		Association a = (Association) obj;
		return (fromRule.equals(a.fromRule) && toRule.equals(a.toRule));
	}

	@Override
	public int hashCode() {
		int result = 101;
		result = getResult(result, fromRule.hashCode());
		result = getResult(result, toRule.hashCode());
		return result;
	}

	private static int getResult(int result, int c) {
		return 37 * result + c;
	}

	@Override
	public String toString() {
		return "FromRule=\"" + fromRule + "\" ToRule=\"" + toRule + "\"";
	}
}

package com.plectix.simulator.parser.util;

/*package*/ class AgentFormatChecker {
	private static final String PATTERN_LINE_AGENT_SITE = "([0-9[a-zA-Z]]+[0-9[a-zA-Z]*\\_\\^\\-]*)";
	private static final String PATTERN_LINE_STATE = "([0-9[a-zA-Z]]+)";
	private static final String PATTERN_LINE_CONNECTED = "((!_)|(![0-9]+)|(\\?))*";
	private static final String PATTERN_LINE_SITE_STATE = "(("
			+ PATTERN_LINE_AGENT_SITE + PATTERN_LINE_CONNECTED + ")+|("
			+ PATTERN_LINE_AGENT_SITE + "(~)" + PATTERN_LINE_STATE
			+ PATTERN_LINE_CONNECTED + ")+)";
	
	private static final String PATTERN_LINE_AGENT = "("
		+ PATTERN_LINE_AGENT_SITE + "(\\()(" + PATTERN_LINE_SITE_STATE
		+ "*|(" + PATTERN_LINE_SITE_STATE + "((\\,)"
		+ PATTERN_LINE_SITE_STATE + ")*)*)" + "(\\))" + ")";

	private static final String PATTERN_LINE = "(" + PATTERN_LINE_AGENT
		+ "((\\,)" + PATTERN_LINE_AGENT + ")*)";

	public static boolean check(String line) {
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			if (line.indexOf(")") == -1)
				return false;
			line = line.substring(0, line.length() - 1);
		}

		String[] agents = line.split("\\)");
		for (String agent : agents) {
			if (agent.trim().startsWith(",")) {
				agent = agent.substring(1);
			}
			if (!(agent.trim() + ")").matches(PATTERN_LINE_AGENT)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkState(String state) {
		return state.matches(PATTERN_LINE_SITE_STATE);
	}
}

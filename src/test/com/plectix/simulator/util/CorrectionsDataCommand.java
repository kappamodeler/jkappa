package com.plectix.simulator.util;

/*package*/enum CorrectionsDataCommand {
	GET_ALL_A("ALL A_"), GET_ALL_B("ALL B_"), GET_ALL_AA("ALL AA"), GET_ALL_Y(
			"ALL y_"), GET_ALL_X_INTERNAL("ALL x~"), GET_ALL_Y_INTERNAL(
			"ALL y~"), GET_ALL_X_LINKED("ALL x!"), GET_ALL_Y_LINKED("ALL y!"), GET_ALL_L(
			"ALL l_"), GET_ALL_K_INTERNAL("ALL k~"), GET_ALL_L_INTERNAL(
			"ALL l~"), GET_ALL_K_LINKED("ALL k!"), GET_ALL_L_LINKED("ALL l!"), GET_ALL_A_DOUBLE_LINKED(
			"ALL A2"),

	UNKNOWN("");

	private final String myName;

	private CorrectionsDataCommand(String name) {
		myName = name;
	}

	public static CorrectionsDataCommand getByName(String name) {
		for (CorrectionsDataCommand command : CorrectionsDataCommand.values()) {
			if (command.myName.equals(name)) {
				return command;
			}
		}
		return UNKNOWN;
	}
}

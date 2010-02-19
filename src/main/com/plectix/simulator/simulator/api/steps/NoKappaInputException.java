package com.plectix.simulator.simulator.api.steps;

@SuppressWarnings("serial")
public class NoKappaInputException extends RuntimeException {
	@Override
	public String getMessage() {
		return "No kappa input was set";
	}
}

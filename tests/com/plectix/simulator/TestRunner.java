package com.plectix.simulator;

public abstract class TestRunner extends Initializator {
	private static final Initializator myInitializator = new Initializator();

	public static Initializator getInitializator() {
		return myInitializator;
	}
}

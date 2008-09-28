package com.plectix.testharness;

public class Test {
	public String getName() {
		return name;
	}

	private String name;
	private String command;
	private String output;

	public Test(String command, String output, String name) {
		this.command = command;
		this.output = output;
		this.name = name;
	}

	
	public String getCommand() {
		return command;
	}
	
	public String getOutput() {
		return output;
	}
	
}
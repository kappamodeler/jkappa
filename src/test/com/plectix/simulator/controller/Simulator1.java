package com.plectix.simulator.controller;


public class Simulator1 implements SimulatorInterface {

	private static final String NAME = "Simulator1";
	
	private double currentTime = 0;
	
	private SimulatorResultsData resultsData = new SimulatorResultsData();
	
	public Simulator1() {
		
	}
	
	@Override
	public Simulator1 clone() {
		return new Simulator1();
	}
	
	public double getCurrentTime() {
		return currentTime;
	}

	public String getName() {
		return NAME;
	}

	public SimulatorResultsData getSimulatorResultsData() {
		return resultsData;
	}

	public void run(SimulatorInputData simulatorInputData) throws InterruptedException {
		for (int i= 0; i < 2000; i++) {
			currentTime += Math.random();
			Thread.sleep(10);
		}
	}

}

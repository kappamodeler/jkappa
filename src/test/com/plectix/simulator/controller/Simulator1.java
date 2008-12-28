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
	
	@Override
	public double getCurrentTime() {
		return currentTime;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public SimulatorResultsData getSimulatorResultsData() {
		return resultsData;
	}

	@Override
	public void run(SimulatorInputData simulatorInputData) throws InterruptedException {
		for (int i= 0; i < 2000; i++) {
			currentTime += Math.random();
			Thread.sleep(10);
		}
	}

}

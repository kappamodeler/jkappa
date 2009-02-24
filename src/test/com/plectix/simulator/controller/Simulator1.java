package com.plectix.simulator.controller;

import com.plectix.simulator.simulator.ThreadLocalData;


public class Simulator1 implements SimulatorInterface {

	private static final String NAME = "Simulator1";
	
	private double currentTime = 0;
	
	private SimulatorResultsData resultsData = new SimulatorResultsData();
	
	public Simulator1() {
		super();
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

	public SimulatorStatusInterface getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void run(SimulatorInputData simulatorInputData) throws InterruptedException {
		for (int i= 0; i < 2000; i++) {
			currentTime += Math.random();
			Thread.sleep(10);
			System.err.println("Dump --> " + this + " is using " + ThreadLocalData.getNameDictionary());
		}
	}

	public static final class Simulator1Factory implements SimulatorFactoryInterface {
		public final SimulatorInterface createSimulator() {
			return new Simulator1();
		}
	}

}

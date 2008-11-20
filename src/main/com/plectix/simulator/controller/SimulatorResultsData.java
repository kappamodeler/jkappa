package com.plectix.simulator.controller;

/**
 * 
 * @author ecemis
 */
public class SimulatorResultsData {

	private SimulatorCallableExitReport simulatorCallableExitReport = new SimulatorCallableExitReport();

    //***********************************************************************************
	/**
	 * 
	 */
	public SimulatorResultsData() {
		super();
	}

    //***********************************************************************************
    //
    //                  GETTERS AND SETTERS
    //
    //  

	public final SimulatorCallableExitReport getSimulatorExitReport() {
		return simulatorCallableExitReport;
	}

	public final void setSimulatorExitReport(SimulatorCallableExitReport simulatorCallableExitReport) {
		this.simulatorCallableExitReport = simulatorCallableExitReport;
	}
	
}

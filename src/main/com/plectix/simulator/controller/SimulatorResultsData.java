package com.plectix.simulator.controller;

import java.util.*;

import javax.xml.transform.Source;

import com.plectix.simulator.simulator.SimulationData;

/**
 * 
 * @author ecemis
 */
public class SimulatorResultsData {

	private SimulatorCallableExitReport simulatorCallableExitReport = new SimulatorCallableExitReport();

	private final SimulationData mySimulationData;
	
	private List<Source> myResultSource = null;
	
    //***********************************************************************************
	/**
	 * 
	 */
	public SimulatorResultsData(SimulationData data) {
		super();
		mySimulationData = data;
	}

	public void addResultSource(Source source) {
		if (myResultSource == null) {
			myResultSource = new LinkedList<Source>();
		}
		myResultSource.add(source);
	}
	
	public List<Source> getResultSource() {
		return Collections.unmodifiableList(myResultSource);
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

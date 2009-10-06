package com.plectix.simulator.controller;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Source;

/**
 * 
 * @author ecemis
 */
public class SimulatorResultsData {

	private boolean isCancelled = false;

	private SimulatorCallableExitReport simulatorCallableExitReport = new SimulatorCallableExitReport();
	
	private List<Source> sources = null;
	
    //***********************************************************************************
	/**
	 * 
	 */
	public SimulatorResultsData() {
		super();
	}

	public void addResultSource(Source source) {
		if (sources == null) {
			sources = new LinkedList<Source>();
		}
		sources.add(source);
	}
	
	public List<Source> getResultSource() {
		return sources;
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

	
	public final boolean isCancelled() {
		return isCancelled;
	}

	public final void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}

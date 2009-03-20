package com.plectix.simulator.controller;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;

import com.plectix.simulator.simulator.DefaultSimulatorFactory;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulatorCommandLine;

public class SimulationServiceTest implements SimulatorCallableListener {

	public static void main(String[] args) throws InterruptedException, ParseException {	
		// SimulationService service = new SimulationService(new Simulator1.Simulator1Factory());
		SimulationService service = new SimulationService(new DefaultSimulatorFactory());
		
		SimulatorCommandLine commandLine = new SimulatorCommandLine(args);
		SimulationArguments simulationArguments = commandLine.getSimulationArguments();
		long jobID = service.submit(new SimulatorInputData(simulationArguments), null); // new SimulationServiceTest());
		/*
		 * long jobID2 = service.submit(new SimulatorInputData(simulationArguments), null);
		
		SimulatorResultsData results = service.getSimulatorResultsData(jobID2, 5, TimeUnit.SECONDS);
		Exception e = results.getSimulatorExitReport().getException();
		if (e != null) {
			e.printStackTrace();
		}
		*/
		
		for (int i= 0; i< 1000; i++) {
			SimulatorStatusInterface status = service.getSimulatorStatus(jobID);
			if (status != null) {
				System.err.println("--> Job " + jobID + " currentTime= " + status.getCurrentTime() + " progress = " + status.getProgress());
			}
			System.err.println("--> Job " + jobID + " is " + (service.isDone(jobID)?"":"NOT ") + "done");

			if (i == 10) {
				System.err.println("Killing ID: " + jobID);
				service.cancel(jobID, true, true);
				System.err.println("Killed ID: " + jobID);
				System.err.println("--> Job " + jobID + " is " + (service.isDone(jobID)?"":"NOT ") + "done");
				System.err.println("Sleeping another second...");
				Thread.sleep(1000);
				break;
			}

			Thread.sleep(750);
		}
		
		System.err.println("Shutting the service down...");
		service.shutdown();
		// System.exit(0);
	}

	public void finished(SimulatorCallable simulatorCallable) {
		Exception e = simulatorCallable.getSimulatorExitReport().getException();
		System.err.println("Simulator [" + simulatorCallable.getSimulator().getName() 
				+ "'] with ID= " 
				+ simulatorCallable.getId() 
				+ " is done. Runtime: " + simulatorCallable.getSimulatorExitReport().getRunTimeInMillis()
				+ " Exception: " + (e == null ? "No Exception!" : e.getMessage()));
		if (e != null) {
			e.printStackTrace();
		}
	}
	
	public void dumpDates() {
		Date date = new Date();
		System.err.println(date.getTime() + " vs " + System.currentTimeMillis());
		
		float ourPosition = System.currentTimeMillis() / 1000.0f;
		float simplxStartPosition = 1228536517.613349f;
		System.err.println("our: " + ourPosition + " sim: " + simplxStartPosition + " diff: " + (ourPosition-simplxStartPosition));
		
		long startDate = (long)(simplxStartPosition*1000); 
		date.setTime(startDate); //
		System.err.println("simplx start date: " + date + " = current_date - " + (System.currentTimeMillis()-startDate)/1000 );
		
		
		long endDate = (long)(1228536518.423739*1000);
		date.setTime(endDate);
		System.err.println(date);
		System.err.println("simplx end date: " + date + " = current_date - " + (System.currentTimeMillis()-endDate)/1000 );
	}
}

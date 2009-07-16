package com.plectix.simulator.gui.panel;

import java.util.Timer;
import java.util.TimerTask;

import com.plectix.simulator.controller.SimulationService;
import com.plectix.simulator.controller.SimulatorStatusInterface;

public class SimulatorStatusChecker {
	
	private Timer timer = new Timer();
	private ControlPanel controlPanel = null;
	
	public SimulatorStatusChecker(final ControlPanel controlPanel, final SimulationService simulationService, final long jobID, final long period) {
		super();
		this.controlPanel = controlPanel;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				checkStatus(simulationService, jobID);
			}

		}, 0, period);
	}
	
	private void checkStatus(final SimulationService simulationService, final long jobID) {
		SimulatorStatusInterface simulatorStatus = simulationService.getSimulatorStatus(jobID);
		if (simulatorStatus == null) {
			timer.cancel();
			timer.purge();
		} else {
			if (simulatorStatus.getProgress() == 1.0) {
				timer.cancel();
				timer.purge();
			}
			// System.err.println("Progress: " + simulatorStatus.getProgress() + " " + simulatorStatus.getStatusMessage());
			controlPanel.updateStatus(simulatorStatus);
		}
	} 
	
}

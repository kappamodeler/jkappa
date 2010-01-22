package com.plectix.simulator.simulator;

import com.plectix.simulator.io.ConsoleOutputManager;
import com.plectix.simulator.util.Info;
import com.plectix.simulator.util.PlxTimer;
import com.plectix.simulator.util.Info.InfoType;

public class SimulationClock {
	private long clockStamp;
	private double step;
	private double nextStep;
	private double stepStories;
	private double nextStepStories;
	
	private final SimulationData simulationData;
	
	public SimulationClock(SimulationData simulationData) {
		this.simulationData = simulationData;
	}
	
	public final boolean isEndSimulation(double currentTime, long count) {
		ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		
		if (System.currentTimeMillis() - clockStamp > simulationArguments
				.getWallClockTimeLimit()) {
			consoleOutputManager.println("Simulation is interrupted because the wall clock time has expired");
			return true;
		}

		if (simulationArguments.isTime()) {
			if (currentTime <= simulationArguments.getTimeLimit()) {
				if (currentTime >= nextStep) {
					consoleOutputManager.outputBar();
					nextStep += step;
				}
				return false;
			} else {
				consoleOutputManager.println("#");
				return true;
			}
		} else if (count < simulationArguments.getMaxNumberOfEvents()) {
			if (count >= nextStep) {
				consoleOutputManager.outputBar();
				nextStep += step;
			}
			return false;
		} else {
			consoleOutputManager.println("#");
			return true;
		}
	}
	
	public final void resetBar() {
		nextStep = step;
	}

	private final void checkAndInitStoriesBar() {
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		if (simulationArguments.storiesModeIsOn()) {
			stepStories = simulationArguments.getIterations() * 1.0
					/ simulationArguments.getClockPrecision();
			nextStepStories = stepStories;
		}
	}

	public final void checkStoriesBar(int index) {
		if (index >= nextStepStories) {
			ConsoleOutputManager consoleOutputManager = simulationData.getConsoleOutputManager();
			SimulationArguments simulationArguments = simulationData.getSimulationArguments();
			double r;
			if (stepStories >= 1)
				r = 1;
			else
				r = simulationArguments.getClockPrecision() * 1.0
						/ simulationArguments.getIterations();
			while (r > 0) {
				consoleOutputManager.print("#");
				r = r - 1;
			}
			nextStepStories += stepStories;
		}
	}

	public final void setEvent(long event) {
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		
		checkAndInitStoriesBar();
		step = event * 1.0 / simulationArguments.getClockPrecision();
		nextStep = step;
		simulationArguments.setMaxNumberOfEvents(event);
	}

	public final void setTimeLimit(double timeLimit) {
		SimulationArguments simulationArguments = simulationData.getSimulationArguments();
		
		checkAndInitStoriesBar();
		step = timeLimit / simulationArguments.getClockPrecision();
		nextStep = step;
		simulationData.setTimeLimit(timeLimit);
	}
	
	public static final void stopTimer(SimulationData simulationData, InfoType outputType, PlxTimer timer,
			String message) {
		if (timer == null) {
			return;
		}
		timer.stopTimer();

		if (outputType == InfoType.OUTPUT) {
			message += " ";
			simulationData.getConsoleOutputManager().println(message 
					+ timer.getTimeMessage() + " sec. CPU");
		}
		// timer.getTimer();
		simulationData.addInfo(new Info(outputType, InfoType.INFO, message, timer
				.getThreadTimeInSeconds(), 1));
	}

	public final void setClockStamp(long clockStamp) {
		this.clockStamp = clockStamp;
	}
}

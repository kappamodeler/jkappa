package com.plectix.simulator.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.plectix.simulator.util.ExponentialMovingAverage;

public class SimulatorProgressMonitor implements SimulatorCallableListener {
    private static final int TIME_WINDOW = 10;
    
    private boolean aborted= false;
    private long startTimestamp;
    private int numberOfRuns;
    private double doneCount = 0;
    private ExponentialMovingAverage exponentialMovingAverage = new ExponentialMovingAverage(TIME_WINDOW);
    
    private List<SimulatorFutureTask> futureTaskList = null;
    
    private SimulatorCallableListener listener = null;
    private ExecutorCompletionService<SimulatorResultsData> executorCompletionService = null;

    //**********************************************************************************************************
    /**
     * @param simulator 
     * @param listener 
     * 
     */
    public SimulatorProgressMonitor(SimulatorInterface simulator, 
    		List<SimulatorInputData> simulationInputDataList,
			SimulatorCallableListener listener, 
			ExecutorCompletionService<SimulatorResultsData> executorCompletionService) {
    	super();

        if (simulator == null || simulationInputDataList == null || executorCompletionService == null) {
        	throw new RuntimeException("Unexpected null objects");
        }
        
        this.listener = listener;
        this.executorCompletionService = executorCompletionService;
        
        this.numberOfRuns = simulationInputDataList.size();
        
        futureTaskList = new ArrayList<SimulatorFutureTask>(numberOfRuns);

        startTimestamp = System.currentTimeMillis();
        for (SimulatorInputData simulatorInputData : simulationInputDataList) {
    		SimulatorCallable simulatorCallable = new SimulatorCallable(simulator.clone(), simulatorInputData, this);
    		SimulatorFutureTask futureTask = new SimulatorFutureTask(simulatorCallable);
        	executorCompletionService.submit(futureTask, futureTask.getSimulator().getSimulatorResultsData());
    		futureTaskList.add(futureTask);
        }
	}
	
    //**********************************************************************************************************
    /**
     * Retrieves and removes the Future representing the next completed task, 
     * waiting if necessary up to the specified wait time if none are yet present. 
     * 
     * @param timeout how long to wait before giving up, in units of <code>unit</code>
     * @param unit a <code>TimeUnit</code> determining how to interpret the <code>timeout</code> parameter 
     * 
     * @return the Future representing the next completed task or <code>null</code> 
     * if the specified waiting time elapses before one is present 
     * 
     * @throws InterruptedException - if interrupted while waiting
     */
    public Future<SimulatorResultsData> poll(long timeout, TimeUnit unit)  throws InterruptedException {
		return executorCompletionService.poll(timeout, unit); 	
    }
	
    //**********************************************************************************************************
    /**
     * Aborts all jobs. 
     */
    public void abort() {
    	for (SimulatorFutureTask f : futureTaskList) {
    		f.cancel(true);
    	}
    	aborted = true;
    }

    //**********************************************************************************************************
    /**
     * Returns [0-1], -1 for unknown
     */
	public double getAmountComplete() {
        synchronized (exponentialMovingAverage) {
            return doneCount / numberOfRuns;
        }
	}

    //**********************************************************************************************************
    /**
     * Expected Time Of Completion for this job in milliseconds, Returns 0 for unknown
     */
	public long getExpectedTimeOfCompletion() {
        synchronized (exponentialMovingAverage) {
            return (long) exponentialMovingAverage.getCurrentValue();
        }
	}

    //**********************************************************************************************************
    /**
     * Returns the start time of the process
     */
	public long getStartTimestamp() {
        return startTimestamp;
	}

    //**********************************************************************************************************
    /**
     * Returns current status, null for none
     */
	public String getStatus() {
        synchronized (exponentialMovingAverage) {
            if (doneCount < numberOfRuns) {
                if (aborted) {
                    return "Aborting...";
                }
                return "Run no: " + doneCount;
            } else {
                if (aborted) {
                    return "Aborted!";
                }
                return "Finished Simulation";
            }
        }
	}

    //**********************************************************************************************************
    /**
     * 
     */
	@Override
	public void finished(SimulatorCallable simulatorCallable) {
        synchronized (exponentialMovingAverage) {
            doneCount++;
            exponentialMovingAverage.addValue(startTimestamp + numberOfRuns*(System.currentTimeMillis()-startTimestamp)/doneCount);      
        }
        if (listener != null) {
        	listener.finished(simulatorCallable); 
        }
	}
 

}

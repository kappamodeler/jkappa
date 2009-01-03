package com.plectix.simulator.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author ecemis
 */
public class SimulationService {
	private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
	
	private static ExecutorService EXECUTOR_SERVICE = null;

	private SimulatorFactoryInterface simulatorFactory = null;
	
	private ConcurrentHashMap<Long, SimulatorFutureTask> callablesMap = new ConcurrentHashMap<Long, SimulatorFutureTask>();
	
	/**
	 * 
	 * @param simulatorInterface
	 */
	public SimulationService(SimulatorFactoryInterface simulatorFactoryInterface) {
		this(simulatorFactoryInterface, DEFAULT_NUMBER_OF_THREADS);
	}
	
	/**
	 * 
	 * @param simulatorInterface
	 */
	public SimulationService(SimulatorFactoryInterface simulatorFactoryInterface, int numberOfThreads) {
		if (simulatorFactoryInterface == null) {
			throw new RuntimeException("We need a simulator factory!");
		}
		this.simulatorFactory = simulatorFactoryInterface;
		
		if (EXECUTOR_SERVICE == null) {
			EXECUTOR_SERVICE = Executors.newFixedThreadPool(numberOfThreads);
		}
	}
	
	/**
	 * 
	 * @param simulatorInputData
	 * @return
	 */
	public long submit(SimulatorInputData simulatorInputData, SimulatorCallableListener listener) {
		SimulatorCallable simulatorCallable = new SimulatorCallable(simulatorFactory.createSimulator(), simulatorInputData, listener);
		SimulatorFutureTask futureTask = new SimulatorFutureTask(simulatorCallable);
		// submit task:
		EXECUTOR_SERVICE.submit(futureTask);
		// cash the ID number
		callablesMap.put(simulatorCallable.getId(), futureTask);
		return simulatorCallable.getId();
	}
	
	/**
	 * 
	 * @param simulationInputDataList
	 * @param listener
	 * @return
	 */
	public SimulatorProgressMonitor submit(List<SimulatorInputData> simulationInputDataList, SimulatorCallableListener listener) {
		SimulatorProgressMonitor progressMonitor = new SimulatorProgressMonitor(
				simulatorFactory, simulationInputDataList, listener,
				new ExecutorCompletionService<SimulatorResultsData>(EXECUTOR_SERVICE));
		return progressMonitor;
	}
	
	/**
	 * Waits if necessary for the computation to complete, and then retrieves its result. 
	 * 
	 * @param jobID
	 * @param timeout
	 * @param unit
	 * @return
	 */
	public SimulatorResultsData getSimulatorResultsData(long jobID, long timeout, TimeUnit unit) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return null;
		}
		
		try {
			return futureTask.get(timeout, unit);
		} catch (Exception e) {
			//SimulatorResultsData simulatorResultsData = new SimulatorResultsData();
			
			SimulatorResultsData simulatorResultsData = futureTask.getSimulator().getSimulatorResultsData();
			simulatorResultsData.getSimulatorExitReport().setException(e);
			return simulatorResultsData;
		} finally {
			callablesMap.remove(jobID);
		}
	}


	/**
	 * Returns the current simulation time for <code>jobID</code> or
	 * <code>Double.NaN</code> if the job doesn't exist.
	 * @param jobID
	 * @return
	 */
	public double getCurrentTime(long jobID) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return Double.NaN;
		}
		
		return futureTask.getSimulator().getCurrentTime();
	}
	
	/**
	 *  Attempts to cancel execution of this job. This attempt will fail 
	 *  if the task has already completed, has already been canceled, or 
	 *  could not be canceled for some other reason. <br>
	 *  If successful, and this job has not started when cancel is called, 
	 *  this job should never run. <br>
	 *  If the job has already started, then the <code>mayInterruptIfRunning</code>
	 *  parameter determines whether the thread executing this job should be interrupted 
	 *  in an attempt to stop the job.<br>
	 *  <br>
	 *  After this method returns, subsequent calls to <code>isDone()</code> will 
	 *  always return true. Subsequent calls to <code>isCancelled()</code> 
	 *  will always return <code>true</code> if this method returned true. 
	 *  
	 * @param jobID
	 * @param mayInterruptIfRunning - <code>true</code> if the thread executing this job 
	 * should be interrupted; otherwise, in-progress jobs are allowed to complete 
	 * 
	 * @return <code>false</code> if the jobId doesn't exist, the job could not 
	 * be canceled, typically because it has already completed normally; 
	 * <code>true</code> otherwise.
	 */
	public boolean cancel(long jobID, boolean mayInterruptIfRunning, boolean removeJob) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return false;
		}
		boolean ret = futureTask.cancel(mayInterruptIfRunning);
		if (removeJob) {
			callablesMap.remove(jobID);
		}
		return ret;
	}
	
	/**
	 * Returns true if this job doesn't exist or was canceled before it completed normally. 
	 * @param jobID
	 * @return
	 */
	public boolean isCancelled(long jobID) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return true;
		}
		return futureTask.isCancelled();
	}
	
	/**
	 * Returns true if this task completed. Completion may be due to normal termination, 
	 * an exception, or cancellation -- in all of these cases, this method will return true. 
	 * @param jobID
	 * @return
	 */
	public boolean isDone(long jobID) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return false;
		}
		return futureTask.isDone();
	}

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be
     * accepted. 
     * Invocation has no additional effect if already shut down.
     * @throws SecurityException if a security manager exists and
     * shutting down this ExecutorService may manipulate threads that
     * the caller is not permitted to modify because it does not hold
     * {@link java.lang.RuntimePermission}<tt>("modifyThread")</tt>,
     * or the security manager's <tt>checkAccess</tt>  method denies access.
     */
	public void shutdown() {
		EXECUTOR_SERVICE.shutdown();
	}
	
}

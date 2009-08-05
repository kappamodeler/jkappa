package com.plectix.simulator.controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.plectix.simulator.streaming.LiveData;

/**
 * This class runs jobs in a thread pool. The thread pool is created only once, when any one
 * of the constructors are called and then its size can not be changed again. The default
 * size is the number of available processors on the system. One can submit simulation
 * jobs to this class, and monitor their progress either through using call-back functions, 
 * blocking calls to this class, or looping over non-blocking calls to this class.
 * 
 * @author ecemis
 */
public class SimulationService {
	private static final int DEFAULT_NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
	
	private static ExecutorService EXECUTOR_SERVICE = null;

	private SimulatorFactoryInterface simulatorFactory = null;
	
	private ConcurrentHashMap<Long, SimulatorFutureTask> callablesMap = new ConcurrentHashMap<Long, SimulatorFutureTask>();
	
	/**
	 * Creates a SimulationService which would use the given simulatorFactoryInterface 
	 * to create new Simulators to work in parallel threads. The first call to any one
	 * of the constructors creates the thread pool whose size can not be changed.
	 * 
	 * @param simulatorFactoryInterface
	 */
	public SimulationService(SimulatorFactoryInterface simulatorFactoryInterface) {
		this(simulatorFactoryInterface, DEFAULT_NUMBER_OF_THREADS);
	}
	
	/**
	 * Creates a SimulationService which would use the given simulatorFactoryInterface 
	 * to create new Simulators to work in parallel threads. The first call to any one
	 * of the constructors creates the thread pool whose size can not be changed.
	 * 
	 * @param simulatorFactoryInterface
	 * @param numberOfThreads
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
	 * Submits simulatorInputData to get executed by the thread pool and returns an ID to query the
	 * job progress. The progress can also be monitored through the callback functions of the
	 * listener passed to this method.
	 *  
	 * @param simulatorInputData
	 * @param listener
	 * @return the job ID to query the progress
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
	 * Submits a collection of jobs to run.
	 * 
	 * @param simulationInputDataList
	 * @param listener
	 * @return a {@link SimulatorProgressMonitor}
	 */
	public SimulatorProgressMonitor submit(List<SimulatorInputData> simulationInputDataList, SimulatorCallableListener listener) {
		SimulatorProgressMonitor progressMonitor = new SimulatorProgressMonitor(
				simulatorFactory, simulationInputDataList, listener,
				new ExecutorCompletionService<SimulatorResultsData>(EXECUTOR_SERVICE));
		return progressMonitor;
	}
	
	/**
	 * Waits if necessary for the computation of jobID to complete, and then retrieves its result. 
	 * Returns null if there is no job with id jobID. Note that the jobID is removed from the queue
	 * no matter what this method returns. I.e. One can not call this function twice with the same jobID.
	 * 
	 * @param jobID
	 * @param timeout
	 * @param unit
	 * @return the result of the simulation as {@link SimulatorResultsData}
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
	 * Returns the current simulation status for <code>jobID</code> or
	 * <code>null</code> if the job doesn't exist.
	 * 
	 * @param jobID
	 * @return the current status
	 */
	public SimulatorStatusInterface getSimulatorStatus(long jobID) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return null;
		}
		
		return futureTask.getSimulator().getStatus();
	}

	/**
	 * Returns the streaming live data for <code>jobID</code> or
	 * <code>null</code> if the job doesn't exist.
	 * 
	 * @param jobID
	 * @return the current status
	 */
	public LiveData getSimulatorLiveData(long jobID) {
		SimulatorFutureTask futureTask = callablesMap.get(jobID);
		if (futureTask == null) {
			return null;
		}
		
		return futureTask.getSimulator().getLiveData();
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
	 * @param removeJob - if <code>true</code> the jobID can not be queried again.
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
	 * 
	 * @param jobID
	 * @return true if this job doesn't exist or was canceled before it completed normally. 
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
	 * 
	 * @param jobID
	 * @return true if this task completed. 
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
     * 
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

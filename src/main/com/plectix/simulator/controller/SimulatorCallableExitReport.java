package com.plectix.simulator.controller;

/**
 * 
 * @author ecemis
 */
public class SimulatorCallableExitReport {

    private long simulatorCallableId = Integer.MIN_VALUE;
    
    private long startTimestamp = Long.MIN_VALUE;
    private long endTimestamp = Long.MIN_VALUE;
   
    private Exception exception = null;

    //***********************************************************************************
    /**
     * 
     */
    public SimulatorCallableExitReport() {
    	super();
    }

    //***********************************************************************************
    /**
     * This is wall clock time.
     * 
     * @return the total runtime for this job
     */
    public int getRunTimeInMillis() {
        if (startTimestamp == Long.MIN_VALUE || endTimestamp == Long.MIN_VALUE) {
            return 0;
        }
        return (int)(endTimestamp-startTimestamp);
    }


    //***********************************************************************************
    //
    //                  GETTERS AND SETTERS
    //
    //  

	public final long getSimulatorCallableId() {
		return simulatorCallableId;
	}

	public final void setSimulatorCallableId(long simulatorRunnableId) {
		this.simulatorCallableId = simulatorRunnableId;
	}

	public final long getStartTimestamp() {
		return startTimestamp;
	}

	public final void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public final long getEndTimestamp() {
		return endTimestamp;
	}

	public final void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public final Exception getException() {
		return exception;
	}

	public final void setException(Exception exception) {
		this.exception = exception;
	}

}

package com.plectix.simulator.streaming;

import com.plectix.simulator.components.CObservables;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;

public class LiveDataStreamer {
	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(LiveDataStreamer.class);
	
	private long liveDataInterval = -1;
	private int liveDataPoints = -1;
	
	/**
	 * Default Constructor
	 */
	public LiveDataStreamer() {
		super();
	}

	/**
	 * Resets the data streaming queues and structures. 
	 * 
	 * This method is called from the main simulation thread just before a new simulation starts.
	 * 
	 * @param liveDataInterval
	 * @param liveDataPoints
	 * @param observables
	 */
	public final void reset(long liveDataInterval, int liveDataPoints, CObservables observables) {
		this.liveDataInterval = liveDataInterval;
		this.liveDataPoints = liveDataPoints;
		
		if (liveDataInterval <= 0) {
			// the live data feature is turned off so let' return
			LOGGER.debug("Live Data Streamer is turned off.");
			return;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Live Data Streamer is turned on with interval = " + liveDataInterval + " secs and point = " + liveDataPoints);
		}
		
		// TODO: Clear all the data in the buffer and other data structures
		// TODO: Copy the plot names and types from observables
		// TODO: Call the object that is responsible for compressing the data to start a thread for periodic compression task
		// TODO: The period is liveDataInterval, the approximate number of data points to return is liveDataPoints
	}
	
	/**
	 * Adds a new data point to be processed for live data.
	 * 
	 * Warning: This method should return very fast since it is called from the main simulation thread.
	 * 
	 * @param currentEventNumber
	 * @param currentTime
	 * @param observables
	 */
	public final void addNewDataPoint(long currentEventNumber, double currentTime, CObservables observables) {
		if (liveDataInterval <= 0) {
			// the live data feature is turned off so don't add the new data point
			return;
		}
		
		// TODO: Consume Data... E.g.
		// 1-) Call another object to decide whether to keep or throw this data point
		// 2-) Or Put the new data in a queue to be consumed later
	}
	
	/**
	 * Returns live data to the client.
	 * 
	 * This method is called from a thread that is separate from the main simulation thread 
	 * so that the client doesn't need to worry about returning immediately from this method. 
	 * This means that liveData that is passed to this method should be an independent copy 
	 * of the data that is not modified while this method is running. 
	 * 
	 * @param liveData
	 * @return
	 */
	public final LiveData getLiveData(LiveData liveData) {
		// return live data asynchronously, i.e. the returned data may have been prepared by the periodic consumption some time earlier...
		return null;
	}

}

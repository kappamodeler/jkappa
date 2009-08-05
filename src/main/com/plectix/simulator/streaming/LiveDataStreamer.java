package com.plectix.simulator.streaming;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;

public class LiveDataStreamer {
	
	private static final LiveDataConsumerInterface DUMMY_LIVE_DATA_CONSUMER = new LiveDataConsumerInterface() {

		@Override
		public void addDataPoint(long currentEventNumber, double currentTime) {
		}

		@Override
		public void consumeLiveData() {
		}

		@Override
		public LiveData getConsumedLiveData() {
			return null;
		}
		
	};

	private static final PlxLogger LOGGER = ThreadLocalData.getLogger(LiveDataStreamer.class);
	private static final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1);
	
	private int liveDataInterval = -1;
	private LiveDataConsumerInterface consumer;
	private ScheduledFuture<?> consumeTaskFuture;

	private final Runnable consumeTask = new Runnable() {
		@Override
		public void run() {
			consumer.consumeLiveData();
		}
	};
	
	/**
	 * Default Constructor
	 */
	public LiveDataStreamer() {
		super();
		consumer = DUMMY_LIVE_DATA_CONSUMER;
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
	public final void addNewDataPoint(long currentEventNumber, double currentTime) {
		if (liveDataInterval <= 0) {
			// the live data feature is turned off so don't add the new data point
			return;
		}
		
		consumer.addDataPoint(currentEventNumber, currentTime);
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
	public final LiveData getLiveData() {
		// return live data asynchronously, i.e. the returned data may have been prepared by the periodic consumption some time earlier...
		return consumer.getConsumedLiveData();
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
	public final void reset(final int liveDataInterval, final int liveDataPoints, final String liveDataConsumerClassname, final LiveDataSourceInterface liveDataSource) {	
		if (liveDataInterval <= 0) {
			// the live data feature is turned off so let' return
			LOGGER.debug("Live Data Streamer is turned off.");
			return;
		}
		
		this.liveDataInterval = liveDataInterval;
		stop();
		
		try {
			Class[] parameterTypes = new Class[] { LiveDataSourceInterface.class, int.class };
			Object[] initargs = new Object[] {liveDataSource, liveDataPoints};
			this.consumer = (LiveDataConsumerInterface) Class.forName(liveDataConsumerClassname).getConstructor(parameterTypes).newInstance(initargs);
		} catch (Exception exception) {
			LOGGER.debug("Could not instantiate " + liveDataConsumerClassname + " -> Live Data Streamer is turned off!");
			this.consumer = DUMMY_LIVE_DATA_CONSUMER;
			exception.printStackTrace();
			return;
		} 
		
		consumeTaskFuture = TIMER.scheduleAtFixedRate(consumeTask, liveDataInterval, liveDataInterval, TimeUnit.MILLISECONDS);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Live Data Streamer is turned on with interval = " + liveDataInterval + " milliseconds and points = " + liveDataPoints);
		}
		
	}
	
	public final void stop() {
		if (consumeTaskFuture != null) {
			consumeTaskFuture.cancel(false);
			TIMER.execute(consumeTask);
			consumer = DUMMY_LIVE_DATA_CONSUMER;
		}
	}

}

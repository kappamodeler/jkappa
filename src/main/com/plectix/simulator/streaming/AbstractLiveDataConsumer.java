package com.plectix.simulator.streaming;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*package*/ abstract class AbstractLiveDataConsumer implements LiveDataConsumerInterface {

	private final LiveDataSourceInterface liveDataSource;
	private Collection<LiveDataPoint> rawDataPoints;
	private Collection<LiveDataPoint> processedDataPoints;

	private final Lock processedDatalock = new ReentrantLock();
	private final Lock rawDataLock = new ReentrantLock();

	public AbstractLiveDataConsumer(LiveDataSourceInterface dataSource, int liveDataPoints) {
		this.liveDataSource = dataSource;
		rawDataPoints = createLiveDataBuffer();
	}

	@Override
	public final void addDataPoint(long currentEventNumber, double currentTime) {
		LiveDataPoint dataPoint = createDataPoint(currentEventNumber,currentTime);
		if (dataPoint == null) {
			return;
		}
		
		rawDataLock.lock();
		try {
			rawDataPoints.add(dataPoint);
		} finally {
			rawDataLock.unlock();
		}
	}
	
	private final LiveDataPoint createDataPoint(long currentEventNumber, double currentTime) {
		return new LiveDataPoint(currentEventNumber, currentTime, liveDataSource.getPlotValues());
	}

	@Override
	public final void consumeLiveData() {
		Collection<LiveDataPoint> collectedData;
		rawDataLock.lock();
		try {
			collectedData = rawDataPoints;
			rawDataPoints = createLiveDataBuffer();
		} finally {
			rawDataLock.unlock();
		}
		
		processedDatalock.lock();
		try {
			processedDataPoints = processRawDataPoints(collectedData);
		} finally {
			processedDatalock.unlock();
		}
	}

	protected abstract Collection<LiveDataPoint> processRawDataPoints(Collection<LiveDataPoint> rawDataPoints);

	protected final Collection<LiveDataPoint> createLiveDataBuffer() {
		return new LinkedList<LiveDataPoint>();
	}

	@Override
	public final LiveData getConsumedLiveData() {
		LiveData result = null;
		processedDatalock.lock();
		try {
			result = new LiveData(liveDataSource.getPlotNames(), liveDataSource.getPlotTypes(), processedDataPoints);
		} finally {
			processedDatalock.unlock();
		}
		return result;
	}
}

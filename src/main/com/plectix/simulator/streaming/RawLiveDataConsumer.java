package com.plectix.simulator.streaming;

import java.util.Collection;

public final class RawLiveDataConsumer extends AbstractLiveDataConsumer {
	
	private final Collection<LiveDataPoint> allDataPoints = createLiveDataBuffer();
	
	public RawLiveDataConsumer(LiveDataSourceInterface dataSource, int liveDataPoints) {
		super(dataSource, liveDataPoints);
	}

	@Override
	protected final Collection<LiveDataPoint> processRawDataPoints(Collection<LiveDataPoint> rawDataPoints) {
		allDataPoints.addAll(rawDataPoints);
		// We need to make return an independent copy:
		Collection<LiveDataPoint> copyOfAllDataPoints = createLiveDataBuffer();
		copyOfAllDataPoints.addAll(allDataPoints);
		return copyOfAllDataPoints;
	}

}

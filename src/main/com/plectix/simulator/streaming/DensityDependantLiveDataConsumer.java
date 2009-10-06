package com.plectix.simulator.streaming;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/*package*/ final class DensityDependantLiveDataConsumer extends AbstractLiveDataConsumer {
	
	private Collection<LiveDataPoint> allDataPoints = createLiveDataBuffer();
	private final int pointsLimit;
	
	public DensityDependantLiveDataConsumer(LiveDataSourceInterface dataSource, int liveDataPoints) {
		super(dataSource, liveDataPoints);
		pointsLimit = liveDataPoints;
	}

	@Override
	protected final Collection<LiveDataPoint> processRawDataPoints(Collection<LiveDataPoint> rawDataPoints) {
		allDataPoints = this.join(allDataPoints, rawDataPoints);
		// We need to make return an independent copy:
		Collection<LiveDataPoint> copyOfAllDataPoints = createLiveDataBuffer();
		copyOfAllDataPoints.addAll(allDataPoints);
		return copyOfAllDataPoints;
	}

	private final Collection<LiveDataPoint> join(Collection<LiveDataPoint> initial, Collection<LiveDataPoint> next) {
		LinkedList<LiveDataPoint> allPoints = new LinkedList<LiveDataPoint>(initial);
		allPoints.addAll(next);
		if (allPoints.size() <= pointsLimit) {
			return allPoints;
		}
		int howManyPointsWeShouldDelete = allPoints.size() - pointsLimit;
		return this.removeNumberOfPoints(allPoints, howManyPointsWeShouldDelete);
	}
	
	private final Collection<LiveDataPoint> removeNumberOfPoints(Collection<LiveDataPoint> data, int quantity) {
		Collection<LiveDataPoint> result = new LinkedList<LiveDataPoint>(data);
		SegmentCovering covering = SegmentCovering.getCovering(data);
		Set<LiveDataPoint> removedPoints = new LinkedHashSet<LiveDataPoint>();
		for (int i = 0; i < quantity; i++) {
			LiveDataPoint point = covering.pop();
			if (point != null) {
				removedPoints.add(point);
			}
		}
		result.removeAll(removedPoints);
		return result;
	}
}

package com.plectix.simulator.streaming;

import java.util.LinkedList;
import java.util.Collection;

/*package*/ class InterpolatingLiveDataConsumer extends AbstractLiveDataConsumer {
	private Collection<LiveDataPoint> allDataPoints = createLiveDataBuffer();
	private final int pointsLimit;
	
	public InterpolatingLiveDataConsumer(LiveDataSourceInterface dataSource, int liveDataPoints) {
		super(dataSource, liveDataPoints);
		pointsLimit = liveDataPoints;
	}

	@Override
	protected Collection<LiveDataPoint> processRawDataPoints(Collection<LiveDataPoint> rawDataPoints) {
		allDataPoints = this.join(allDataPoints, rawDataPoints);
		// We need to make return an independent copy:
		Collection<LiveDataPoint> copyOfAllDataPoints = createLiveDataBuffer();
		copyOfAllDataPoints.addAll(allDataPoints);
		return copyOfAllDataPoints;
	}

	private Collection<LiveDataPoint> join(Collection<LiveDataPoint> initial, Collection<LiveDataPoint> next) {
		LinkedList<LiveDataPoint> allPoints = new LinkedList<LiveDataPoint>(initial);
		allPoints.addAll(next);
		if (allPoints.size() <= pointsLimit) {
			return allPoints;
		}
		int pointsNumber = Math.min(pointsLimit, allPoints.size());
		// we think that the time is counting from zero
		double initialTimePoint = allPoints.getFirst().getEventTime();
		double lastTimePoint = allPoints.getLast().getEventTime();
		double timeStep = (lastTimePoint - initialTimePoint) / (pointsNumber - 1);
		Collection<LiveDataPoint> result = new LinkedList<LiveDataPoint>();
		
		int eventNumber = 0;
		double nextSnapshotTime = initialTimePoint;
		LiveDataPoint previousPoint = null;
		
		for (LiveDataPoint point : allPoints) {
			if (previousPoint == null) {
				double[] firstPointValues = point.getPlotValues(); 
				result.add(new LiveDataPoint(eventNumber++, nextSnapshotTime, firstPointValues));
				previousPoint = point;
				nextSnapshotTime += timeStep;
				continue;
			}
			LiveDataPoint newPointToAdd = createNewDataPoint(previousPoint, point, nextSnapshotTime, eventNumber);
			while (newPointToAdd != null) {
				result.add(newPointToAdd);
				eventNumber++;
				nextSnapshotTime += timeStep;
				newPointToAdd = createNewDataPoint(previousPoint, point, nextSnapshotTime, eventNumber);
			}
				
			previousPoint = point;
		}
		return result;
	}
	
	private LiveDataPoint createNewDataPoint(LiveDataPoint p1, LiveDataPoint p2, double time, int eventNumber) {
		if (p1.getEventTime() < time && p2.getEventTime() >= time) {
			double[] averageValues = getAverageLiveData(p1, p2, time);
			LiveDataPoint result = new LiveDataPoint(eventNumber, time, averageValues);
			return result;
		} else {
			return null;
		}
	}
	
	private double[] getAverageLiveData(LiveDataPoint p1, LiveDataPoint p2, double time) {
		double timeDeviation = p2.getEventTime() - p1.getEventTime();
		double timeProportionValue = time - p1.getEventTime();
		int observablesNumber = p1.getPlotValues().length;
		double[] result = new double[observablesNumber];
		
		for (int index = 0; index < observablesNumber; index++) {
			double observableDeviation = p2.getPlotValues()[index] - p1.getPlotValues()[index];
			result[index] = (timeProportionValue * observableDeviation / timeDeviation) + p1.getPlotValues()[index];
		}
		return result;
	}
}

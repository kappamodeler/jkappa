package com.plectix.simulator.streaming;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*package*/ final class SegmentCovering {
	/**
	 * This is the time segment with LiveDataPoints as ends. 
	 */
	private static final class Segment implements Comparable<Segment> {
		private LiveDataPoint firstPoint;
		private LiveDataPoint secondPoint;
		private double length;
		
		public Segment(LiveDataPoint firstPoint, LiveDataPoint secondPoint) {
			this.firstPoint = firstPoint;
			this.secondPoint = secondPoint;
			length = secondPoint.getEventTime() - firstPoint.getEventTime();
		}
		
		@Override
		public final int compareTo(Segment segment) {
			if (segment == this) {
				return 0;
			}
			int result = Double.compare(this.length, segment.length);
			if (result == 0) {
				return (int)(this.firstPoint.getEventNumber() - segment.secondPoint.getEventNumber());
			}
			return result;
		}
		
		@Override
		public final boolean equals(Object o) {
			return this == o;
		}
		
		public final String toString() {
			return firstPoint.getEventTime() + "||" + secondPoint.getEventTime(); 
		}
		
		public final void replaceDataWith(Segment s) {
			firstPoint = s.firstPoint;
			secondPoint = s.secondPoint;
			length = s.length;
		}
	}
	
	// pairs of segments, value following the key (they have one common point)
	private TreeMap<Segment, Segment> covering = new TreeMap<Segment, Segment>();
	
	private SegmentCovering() {
		
	}
	
	/**
	 * This method returns segment covering for the queue of LiveDataPoints (sorted by time!).
	 * <br>Input queue should contain more than 2 points.
	 * @param points input queue of LiveDataPoints 
	 * @return segment covering for this queue
	 */
	public static final SegmentCovering getCovering(Collection<LiveDataPoint> points) {
		SegmentCovering covering = new SegmentCovering();
		Iterator<LiveDataPoint> iterator = points.iterator();
		LiveDataPoint zeroPoint = iterator.next();
		LiveDataPoint firstPoint = iterator.next();
		Segment segment1 = new Segment(zeroPoint, firstPoint);
		Segment segment2 = null;
		
		// previousPoint -> point
		while (iterator.hasNext()) {
			LiveDataPoint secondPoint = iterator.next();
			segment2 = new Segment(firstPoint, secondPoint);
			if (segment1.length > 0) {
				covering.addSegments(segment1, segment2);
			}
			
			zeroPoint = firstPoint;
			firstPoint = secondPoint;
			segment1 = segment2;
		}
		return covering;
	}
	
	// segment2 is the next after the segment1
	private final void addSegments(Segment segment1, Segment segment2) {
		covering.put(segment1, segment2);
	}
		
	/**
	 * This method takes the shortest segment in the covering and removes LiveDataPoint,
	 * which is the right end of this segment. <br>
	 * One exception - we don't see the most right segment. It can be the shortest one
	 * or whatever, we never delete it's ending points.
	 * @return LiveDataPoint we want to delete
	 */
	public final LiveDataPoint pop() {
		if (covering.isEmpty()) {
			return null;
		}
		Map.Entry<Segment, Segment> firstEntry = covering.pollFirstEntry();
		Segment leftSegment = firstEntry.getKey();
		Segment rightSegment = firstEntry.getValue();
		LiveDataPoint removedPoint = leftSegment.secondPoint;
		
		covering.remove(leftSegment);
		
		Segment newBiggerSegment = new Segment(leftSegment.firstPoint, rightSegment.secondPoint);
		// leftSegment maybe left in covering as a value, so we just change it's data
		leftSegment.replaceDataWith(newBiggerSegment);
		
		Segment theOneChangedByRightSegment = covering.remove(rightSegment);
		if (theOneChangedByRightSegment != null) {
			// leftSegment is now representing new bigger segment
			covering.put(leftSegment, theOneChangedByRightSegment);
		}
		
		return removedPoint;
	}
}
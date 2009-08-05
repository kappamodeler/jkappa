package com.plectix.simulator.streaming;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.TreeMap;

/*package*/ final class SegmentCovering {
	/**
	 * This is the time segment with LiveDataPoints as ends. 
	 */
	private static final class Segment implements Comparable<Segment> {
		private LiveDataPoint p1;
		private LiveDataPoint p2;
		private double length;
		
		public Segment(LiveDataPoint p1, LiveDataPoint p2) {
			this.p1 = p1;
			this.p2 = p2;
			length = p2.getEventTime() - p1.getEventTime();
		}
		
		@Override
		public int compareTo(Segment o) {
			if (o == this) {
				return 0;
			}
			int result = Double.compare(this.length, o.length);
			if (result == 0) {
				return (int)(this.p1.getEventNumber() - o.p2.getEventNumber());
			}
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			return this == o;
		}
		
		public String toString() {
			return p1.getEventTime() + "||" + p2.getEventTime(); 
		}
		
		public void replaceDataWith(Segment s) {
			p1 = s.p1;
			p2 = s.p2;
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
	 * @param pointsCollection input queue of LiveDataPoints 
	 * @return segment covering for this queue
	 */
	public static SegmentCovering getCovering(Collection<LiveDataPoint> pointsCollection) {
		SegmentCovering covering = new SegmentCovering();
		Iterator<LiveDataPoint> iterator = pointsCollection.iterator();
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
	private void addSegments(Segment segment1, Segment segment2) {
		covering.put(segment1, segment2);
	}
		
	/**
	 * This method takes the shortest segment in the covering and removes LiveDataPoint,
	 * which is the right end of this segment. <br>
	 * One exception - we don't see the most right segment. It can be the shortest one
	 * or whatever, we never delete it's ending points.
	 * @return LiveDataPoint we want to delete
	 */
	public LiveDataPoint pop() {
		if (covering.isEmpty()) {
			return null;
		}
		Map.Entry<Segment, Segment> firstEntry = covering.pollFirstEntry();
		Segment leftSegment = firstEntry.getKey();
		Segment rightSegment = firstEntry.getValue();
		LiveDataPoint removedPoint = leftSegment.p2;
		
		covering.remove(leftSegment);
		
		Segment newBiggerSegment = new Segment(leftSegment.p1, rightSegment.p2);
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
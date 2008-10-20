package com.plectix.simulator.util;

/*package*/ class Piece {
	private long myLowerBound = 0;
	private long myUpperBound = 0;
	private int myStep = 1;
	
	public Piece() {
		myLowerBound = 0;
		myUpperBound = 0;
	}
	
	public Piece(long lu) {
		myLowerBound = lu;
		myUpperBound = lu;
	}
	
	public Piece(long l, long u) {
		myLowerBound = l;
		myUpperBound = u;
	}
	
	public Piece(long l, long u, int step) {
		myLowerBound = l;
		myUpperBound = u;
		myStep = step;
	}
	
	public long getUpper() {
		return myUpperBound;
	}
	
	public long getLower() {
		return myLowerBound;
	}
	
	public int getStep() {
		return myStep;
	}
	
}

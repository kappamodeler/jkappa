package com.plectix.simulator.probability.avl;

public enum Orientation {
	LEFT,
	RIGHT, UNKNOWN;
	
	public Orientation reflect() {
		if (this == LEFT) { 
			return RIGHT;
		} else if (this == RIGHT) {
			return LEFT;
		} else {
			return UNKNOWN;
		}
	}
}

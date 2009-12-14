package com.plectix.simulator.util;

public class Timer {

	long time;

	// constructor

	public Timer() {

		reset();

	}

	// reset timer
	public void reset() {

		time = System.currentTimeMillis();

	}

	// return elapsed time

	public long elapsed() {

		return System.currentTimeMillis() - time;

	}

	public String getPrintToString(String s) {
		return s + ": " + elapsed() + " milliseconds";
	}

	// print explanatory string and elapsed time

	public void print(String s) {
		System.out.println(getPrintToString(s));
	}

}

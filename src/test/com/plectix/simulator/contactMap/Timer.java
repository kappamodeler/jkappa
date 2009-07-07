package com.plectix.simulator.contactMap;

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

    // print explanatory string and elapsed time

    public void print(String s) {
 
    	System.out.println(s + ": " + elapsed() + " milliseconds");
    	
    }

}

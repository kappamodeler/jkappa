package com.plectix.simulator.streaming;


public interface LiveDataConsumerInterface {
	
	public void consumeLiveData();
	
	public LiveData getConsumedLiveData();
	
	public void addDataPoint(long currentEventNumber, double currentTime);
	
}

package com.plectix.simulator.components.stories.storage.graphs;


public class UniqueGraph {
	
	private StoriesGraphs graph;
	private Integer count = 1;
	private Double persent;
	private Integer allCount;
	private double time;
	private double averageTime;
	
	public UniqueGraph(StoriesGraphs _graph, Integer _allCount, double _time) {
		graph = _graph;
		allCount = _allCount;
		time  = _time;
		updateAverageTime();
		updatePercent();
		
	}
	
	public StoriesGraphs getGraph() {
		return graph;
	}

	public void setGraph(StoriesGraphs graph) {
		this.graph = graph;
	}

	public Double getPersent() {
		return persent;
	}

	private void updatePercent() {
		this.persent =  count.doubleValue() / allCount.doubleValue();
	}
	
	private void updateAverageTime() {
		this.averageTime =  time / allCount.doubleValue();
	}
	
	
	
	public void incrementCount() {
		count++;
		updatePercent();
	}

	public void addAverageTime(double time) {
		this.time += time;
		updateAverageTime(); 
		
	}
	
	public double getAverageTime() {
		return averageTime;
	}

}

package com.plectix.simulator.staticanalysis.stories.graphs;

public final class UniqueGraph {

	private final StoriesGraphs graph;
	private int number = 1;
	private double persent;
	private final int commonQuantity;
	private double time;
	private double averageTime;

	public UniqueGraph(StoriesGraphs graph, int commonQuantity, double time) {
		this.graph = graph;
		this.commonQuantity = commonQuantity;
		this.time = time;
		updateAverageTime();
		updatePercent();
	}

	public final StoriesGraphs getGraph() {
		return graph;
	}

	public final double getPersent() {
		return persent;
	}

	private final void updatePercent() {
		this.persent = number * 1.0 / commonQuantity;
	}

	private final void updateAverageTime() {
		this.averageTime = time * 1.0 / commonQuantity;
	}

	public final void incrementCount() {
		number++;
		updatePercent();
	}

	public final void addAverageTime(double time) {
		this.time += time;
		updateAverageTime();
	}

	public final double getAverageTime() {
		return averageTime;
	}
}

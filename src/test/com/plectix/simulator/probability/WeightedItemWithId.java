package com.plectix.simulator.probability;

public class WeightedItemWithId implements WeightedItem {
	private int id = 0;
	private int count = 0;
	private int numberOfItems = -1;
	private double weight = 0.0;

	public enum WeightFunction {
		LINEAR {
			@Override
			double compute(int id, int numberOfItems) {
				return id + 1;
			}
		},
		LOGARITHM {
			@Override
			double compute(int id, int numberOfItems) {
				return Math.log(id + 2);
			}
		},
		PARABOLA {
			@Override
			double compute(int id, int numberOfItems) {
				return ((id - 0.5 * numberOfItems) * (id - 0.5 * numberOfItems)
						/ numberOfItems + 0.3 * id);
			}
		},
		SINE {
			@Override
			double compute(int id, int numberOfItems) {
				return 2.0 + Math.sin(0.25 * id);
			}
		};

		abstract double compute(int id, int numberOfItems);
	}

	public WeightedItemWithId(int id, int numberOfItems,
			WeightFunction weightFunction) {
		this.id = id;
		this.numberOfItems = numberOfItems;
		setWeightFunction(weightFunction);
	}

	public final void setWeightFunction(final WeightFunction weightFunction) {
		this.weight = weightFunction.compute(id, numberOfItems);
	}

	public final void remove() {
		this.weight = 0.0;
	}

	public final void resetCount() {
		this.count = 0;
	}

	public final void incrementCount() {
		this.count++;
	}

	public double getWeight() {
		return weight;
	}

	public final int getId() {
		return id;
	}

	public final int getCount() {
		return count;
	}

}

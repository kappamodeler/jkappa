package com.plectix.simulator.simulationclasses.probability;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.plectix.simulator.simulationclasses.probability.WeightedItemWithId.WeightFunction;

public class TestSelector {
	int numberOfWeightedItems;
	int numberOfUpdates;
	int numberOfSelection;
	private static final double CONFIDENCE_BOUND = 0.07;

	List<WeightedItemWithId> weightedItemList;
	WeightedItemSelector<WeightedItemWithId> weightedItemSelector;

	// true -> using Random() false Random()
	private boolean permitRandom = false;

	public void setUp(WeightedItemSelector<WeightedItemWithId> testedSelector,
			int testedNumberOfWeightedItems, int testedNumberOfUpdates,
			int testedNumberOfSelection, WeightFunction typeOfWeight)
			throws Exception {

		weightedItemList = new ArrayList<WeightedItemWithId>();
		weightedItemSelector = testedSelector;
		numberOfWeightedItems = testedNumberOfWeightedItems;
		numberOfUpdates = testedNumberOfUpdates;
		numberOfSelection = testedNumberOfSelection;
		for (int i = 0; i < numberOfWeightedItems; i++) {
			weightedItemList.add(new WeightedItemWithId(i,
					numberOfWeightedItems, typeOfWeight));
		}
		shuffleAndUpdate();
	}

	public void setWeightedItemList(List<WeightedItemWithId> list) {
		weightedItemList = list;
	}

	public boolean testRandom() {
		shuffleAndUpdate();
		processSelection();
		return equiprobability();
	}

	public boolean testRemoveAssigneSineAndRandom(int frequencyRemoved) {
		assignSineAndParabolaWeight();
		removeSomeItems(frequencyRemoved);
		resetCounts();
		shuffleAndUpdate();
		processSelection();
		return equiprobability();

	}

	public boolean testRemoveRecalculationAndRandom(int frequencyRemoved) {

		assignOtherWeight();
		removeSomeItems(frequencyRemoved);
		resetCounts();
		shuffleAndUpdate();
		processSelection();
		return equiprobability();

	}

	public void process() {
		shuffleAndUpdate();
		processSelection();
	}

	private void removeSomeItems(int k) {
		for (int i = 0; i < numberOfWeightedItems; i++) {
			WeightedItemWithId item = weightedItemList.get(i);
			if (item.getId() % k != 0) {
				item.remove();
			}
		}
	}

	private void resetCounts() {
		for (int i = 0; i < numberOfWeightedItems; i++) {
			weightedItemList.get(i).resetCount();
		}
	}

	private void assignSineAndParabolaWeight() {
		for (int i = 0; i < numberOfWeightedItems; i++) {
			WeightedItemWithId item = weightedItemList.get(i);
			if (item.getId() % 2 == 0)
				item.setWeightFunction(WeightFunction.SINE);

			if (item.getId() % 7 == 1)
				item.setWeightFunction(WeightFunction.PARABOLA);
		}
	}

	private void assignOtherWeight() {
		for (int i = 0; i < numberOfWeightedItems; i++) {
			WeightedItemWithId item = weightedItemList.get(i);
			if (item.getId() % 5 == 0)
				item.setWeightFunction(WeightFunction.LOGARITHM);

			if (item.getId() % 7 == 1)
				item.setWeightFunction(WeightFunction.PARABOLA);
		}
	}

	private void shuffleAndUpdate() {
		if (permitRandom) {
			for (int i = 0; i < numberOfUpdates; i++) {
				Collections.shuffle(weightedItemList);
				weightedItemSelector.updatedItems(weightedItemList);
			}
		} else {
			weightedItemSelector.updatedItems(weightedItemList);

		}

	}

	private void processSelection() {
		for (int i = 0; i < numberOfSelection; i++) {
			WeightedItemWithId item = weightedItemSelector.select();
			item.incrementCount();
		}
	}

	private boolean equiprobability() {
		int errors = 0;
		double sumOfWeights = 0;
		for (int i = 0; i < numberOfWeightedItems; i++) {
			sumOfWeights += weightedItemList.get(i).getWeight();
		}
		for (int i = 0; i < numberOfWeightedItems; i++) {

			if (weightedItemList.get(i).getWeight() != 0) {
				if (!confidenceTest(weightedItemList.get(i).getCount(),
						weightedItemList.get(i).getWeight() / sumOfWeights)) {
					errors++;
				}
			} else {
				if (weightedItemList.get(i).getCount() > 0)
					fail("Item with 0 weight was selected!!!!!");
			}

		}

		return errors < CONFIDENCE_BOUND * numberOfWeightedItems;
	}

	// 1.92 - from tables for 95% confidence interval
	private boolean confidenceTest(int experimentalNumberOfEvents,
			double expectedProbability) {
		return Math.abs((experimentalNumberOfEvents - expectedProbability
				* numberOfSelection)
				/ Math.sqrt(numberOfSelection * expectedProbability
						* (1 - expectedProbability))) < 1.92;
	}

	public boolean boundaryTest() {
		for (int i = 0; i < numberOfWeightedItems; i++) {
			WeightedItemWithId wi = new WeightedItemWithId(i,
					numberOfWeightedItems, WeightFunction.ZERO);
			weightedItemList.add(wi);
		}
		for (int i = numberOfWeightedItems; i < 2 * numberOfWeightedItems; i++) {
			WeightedItemWithId wi = new WeightedItemWithId(i,
					numberOfWeightedItems, WeightFunction.BIG);
			weightedItemList.add(wi);
		}
		setWeightedItemList(weightedItemList);
		process();

		for (WeightedItemWithId wi : weightedItemList) {
			if (wi.getWeight() == 0 && wi.getCount() > 0) {
				return false;
			}
		}
		weightedItemList.clear();

		for (int i = 0; i < numberOfWeightedItems; i++) {
			WeightedItemWithId wi = new WeightedItemWithId(i,
					numberOfWeightedItems, WeightFunction.ONE);
			weightedItemList.add(wi);
		}

		for (int i = numberOfWeightedItems; i < numberOfWeightedItems + 10; i++) {
			WeightedItemWithId wi = new WeightedItemWithId(i,
					numberOfWeightedItems, WeightFunction.BIG);
			weightedItemList.add(wi);
		}
		process();
		boolean flag = false;
		for (WeightedItemWithId wi : weightedItemList) {
			if (wi.getWeight() == 1 && wi.getCount() > 0) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			System.err
					.println("very rarely event is occured in testProbability");
		}

		return true;
	}

}

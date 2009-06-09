package com.plectix.simulator.probability;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.probability.WeightedItemWithId.WeightFunction;
import com.plectix.simulator.probability.skiplist.SkipListSelector;


public class TestSelectorCompare {
	int numberOfWeightedItems; 
	int numberOfUpdates; 
	int numberOfSelection;
	private static final double confidenceBound = 0.06;
	
	List<WeightedItemWithId> weightedItemList; 
	WeightedItemSelector<WeightedItemWithId> weightedItemSelector;

	
	public void setUp(WeightedItemSelector<WeightedItemWithId> testedSelector,
			int testedNumberOfWeightedItems, int testedNumberOfUpdates,
			int testedNumberOfSelection, WeightFunction typeOfWeight) throws Exception {
		
		weightedItemList = new ArrayList<WeightedItemWithId>();		
		weightedItemSelector = testedSelector;
		numberOfWeightedItems = testedNumberOfWeightedItems;
		numberOfUpdates = testedNumberOfUpdates;
		numberOfSelection = testedNumberOfSelection;
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			weightedItemList.add(new WeightedItemWithId(i, numberOfWeightedItems, typeOfWeight)); 
			}
		shuffleAndUpdate();		
	}
	
	
	public boolean testRandom(){
		processSelection();			
		return equiprobability(); 		
	}
	
	
	public boolean testRemoveAssigneSineAndRandom(int frequencyRemoved){
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

	private void removeSomeItems(int k){
		for(int i = 0; i<numberOfWeightedItems; i++){
			WeightedItemWithId item = weightedItemList.get(i);
			if (item.getId()%k !=0){
				item.remove();
			}
		}
	}
	
	private void resetCounts(){
		for(int i = 0; i<numberOfWeightedItems;i++){
			weightedItemList.get(i).resetCount();
		}
	}
	
	private void assignSineAndParabolaWeight(){
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			WeightedItemWithId item = weightedItemList.get(i); 
			if (item.getId() % 2 == 0)		
				item.setWeightFunction(WeightFunction.SINE); 

			if (item.getId() % 7 == 1)		
				item.setWeightFunction(WeightFunction.PARABOLA); 		
		}	
	}
	
	
	private void assignOtherWeight(){
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			WeightedItemWithId item = weightedItemList.get(i); 
			if (item.getId() % 5 == 0)		
				item.setWeightFunction(WeightFunction.LOGARITHM); 

			if (item.getId() % 7 == 1)		
				item.setWeightFunction(WeightFunction.PARABOLA); 		
		}	
	}
	
	private void shuffleAndUpdate(){
		for (int i= 0; i< numberOfUpdates; i++) { 
			//Collections.shuffle(weightedItemList); 
			weightedItemSelector.updatedItems(weightedItemList); 
		} 
		
	}
	
	private void processSelection(){
		for (int i= 0; i< numberOfSelection; i++) { 
			WeightedItemWithId item = weightedItemSelector.select(); 
			item.incrementCount();
			}
	}
	
	private boolean equiprobability(){
	    int errors = 0; 
	    double sumOfWeights=0;
	    for(int i = 0;i<numberOfWeightedItems;i++){
	    	sumOfWeights+=weightedItemList.get(i).getWeight();
	    }
		for(int i = 0;i<numberOfWeightedItems;i++){
			
			if (weightedItemList.get(i).getWeight()!=0){		
				if(!confidenceTest(weightedItemList.get(i).getCount(), weightedItemList.get(i).getWeight()/sumOfWeights)){	
					errors++;
					//if(errors==1){ 
					//	System.out.println("outliers");
					//}
					//System.out.println(weightedItemList.get(i).getCount());
					//System.out.println(weightedItemList.get(i).getWeight()/sumOfWeights);
				}	
			}
			else{
				if (weightedItemList.get(i).getCount()>0) fail("Item with 0 weight was selected!!!!!");		
			}
				
		}
			
		return errors<confidenceBound*numberOfWeightedItems;	
	}
	//1.92 - from tables for 95% confidence interval
	private boolean confidenceTest(int numberOfEvents, double expectedProbability){
		return Math.abs((double)(numberOfEvents - expectedProbability*numberOfSelection)
				/Math.sqrt(numberOfSelection*expectedProbability*(1-expectedProbability))) <2;		
	}
	
	


}

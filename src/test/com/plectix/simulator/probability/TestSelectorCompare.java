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
	
	List<WeightedItemWithId> weightedItemList; 
	List<Integer> counts;
	WeightedItemSelector<WeightedItemWithId> weightedItemSelector;

	
	public void setUp(WeightedItemSelector<WeightedItemWithId> testedSelector,
			int testedNumberOfWeightedItems, int testedNumberOfUpdates,
			int testedNumberOfSelection, WeightFunction typeOfWeight) throws Exception {
		
		weightedItemList = new ArrayList<WeightedItemWithId>();
		counts = new ArrayList<Integer>();
		
		weightedItemSelector = testedSelector;
		numberOfWeightedItems = testedNumberOfWeightedItems;
		numberOfUpdates = testedNumberOfUpdates;
		numberOfSelection = testedNumberOfSelection;
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			weightedItemList.add(new WeightedItemWithId(i, numberOfWeightedItems, typeOfWeight)); 
			counts.add(0); 
			}
		shuffleAndUpdate();		
	}
	
	
	public boolean testRandom(){
		processSelection();			
		return equiprobability(); 		
	}
	
	
	public boolean testRemoveAssigneSineAndRandom(){
		assignSineAndParabolaWeight();
		removeSomeItems(3);
		resetCounts();		
		shuffleAndUpdate();
		processSelection();
		return equiprobability();
		
	}
	
	public boolean testRemoveRecalculationAndRandom() {
		
		assignOtherWeight();
		removeSomeItems(5);
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
			counts.set(i, 0);
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
			Collections.shuffle(weightedItemList); 
			weightedItemSelector.updatedItems(weightedItemList); 
		} 
		
	}
	
	private void processSelection(){
		for (int i= 0; i< numberOfSelection; i++) { 
			WeightedItemWithId item = weightedItemSelector.select(); 
			counts.set(item.getId(), counts.get(item.getId())+1); 
			}
	}
	
	private boolean equiprobability(){
	    int errors = 0; 
	    int j;
	    double sumOfWeights=0;
	    for(int i = 0;i<numberOfWeightedItems;i++){
	    	sumOfWeights+=weightedItemList.get(i).getWeight();
	    }
		for(int i = 0;i<numberOfWeightedItems;i++){
			j = weightedItemList.get(i).getId();
			if (weightedItemList.get(i).getWeight()!=0){		
				if(!confidenceTest(counts.get(j), weightedItemList.get(i).getWeight()/sumOfWeights)){	
					errors++;
					if(errors==1){ 
						//System.out.println("outliers");
					}
					//System.out.println(counts.get(j));
					//System.out.println(weightedItemList.get(i).getWeight()/sumOfWeights);
				}	
			}
			else{
				if (counts.get(j)>0) fail("Item with 0 weight was selected!!!!!");		
			}
				
		}
			
		return errors<0.06*numberOfWeightedItems;	
	}
	//1.92 - from tables for 95% confidence interval
	private boolean confidenceTest(int numberOfEvents, double expectedProbability){
		return Math.abs((double)(numberOfEvents - expectedProbability*numberOfSelection)
				/Math.sqrt(numberOfSelection*expectedProbability*(1-expectedProbability))) <2;		
	}
	
	


}

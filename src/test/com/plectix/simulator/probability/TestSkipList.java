package com.plectix.simulator.probability;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.probability.WeightedItemWithId.WeightFunction;
import com.plectix.simulator.probability.skiplist.SkipListSelector;

public class TestSkipList {

	int numberOfWeightedItems = 100; 
	int numberOfUpdates = 1000; 
	int numberOfSelection = 1000000;
	
	private static final double deviationGraph = 0.01;
	private static final int frequencyRemoved1 = 5;
	private static final int frequencyRemoved2 = 3;
	
	
	List<WeightedItemWithId> weightedItemList = new ArrayList<WeightedItemWithId>(); 
	List<Integer> counts = new ArrayList<Integer>();
	SkipListSelector<WeightedItemWithId> weightedItemSelector;
	
	@Before
	public void setUp() throws Exception {
		IRandom irandom = new CRandomJava(null, null);
		weightedItemSelector= new SkipListSelector<WeightedItemWithId>(irandom);
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			weightedItemList.add(new WeightedItemWithId(i, numberOfWeightedItems, WeightFunction.LINEAR)); 
			counts.add(0); 
			}
		shuffleAndUpdate();		
	}
	
	@Test
	public void testRandom(){
		processSelection();			
		if (!equiprobability()) 
			fail("Bad Randomize or very small operation factors!");
	}
	
	@Test
	public void testRemoveRecalculationAndRandom() {
		
		assignOtherWeight();
		removeSomeItems(frequencyRemoved1);
		resetCounts();
		
		shuffleAndUpdate();

		processSelection();
	
		if (!equiprobability()) 
			fail("Bad Recalculation");
	
	}

	private void removeSomeItems(int k){
		for(int i = 0; i<numberOfWeightedItems; i++){
			WeightedItemWithId item = weightedItemList.get(i);
			if (item.getId()%k ==0){
				item.remove();
			}
		}
	}
	
	private void resetCounts(){
		for(int i = 0; i<numberOfWeightedItems;i++){
			counts.set(i, 0);
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
		double min = -1;
		double max = -1;
		int j;
		for(int i = 0;i<numberOfWeightedItems;i++){
			j = weightedItemList.get(i).getId();
			if (weightedItemList.get(i).getWeight()!=0){
				if ((double)counts.get(j)/weightedItemList.get(i).getWeight() > max|| max==-1){
					max = counts.get(j)/weightedItemList.get(i).getWeight();
				}
				else{
					if ((double)counts.get(j)/weightedItemList.get(i).getWeight()<min|| min==-1) 
						min = counts.get(j)/weightedItemList.get(i).getWeight();
				}
			}
		}
		if (max==-1||min==-1) 
			fail("List is empty!");
		
		return testDeviationGraph(max-min);	
	}
	
	//test deviation between graphs 1)expected and 2)empirical distribution
	private boolean testDeviationGraph(double p){
		return Math.abs(p) <deviationGraph*(double)numberOfSelection/numberOfWeightedItems;		
	}
	

	@Test
	public void testSelectorCompare() throws Exception{
		TestSelectorCompare testSelector= new TestSelectorCompare();
		IRandom irandom = new CRandomJava(null, null);
		weightedItemSelector= new SkipListSelector<WeightedItemWithId>(irandom);
	
		testSelector.setUp(weightedItemSelector, numberOfWeightedItems, numberOfUpdates, numberOfSelection, WeightFunction.LINEAR);
		if (!testSelector.testRandom()){
			fail("Bad Randomize or very small operation factors!");
		}	
		
		if(!testSelector.testRemoveRecalculationAndRandom(frequencyRemoved1)){
			fail("Bad Recalculation and Random after remove and set logariphm ");		
		}

		if(!testSelector.testRemoveAssigneSineAndRandom(frequencyRemoved2)){
			fail("Bad Recalculation and Random after remove and set Sine and Parabola ");		
		}
	}
	
	

}

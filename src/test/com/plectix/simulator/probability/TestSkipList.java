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
	int numberOfUpdates = 100; 
	int numberOfSelection = 10000000;
	
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
	/*
	public TestSkipList(){
		IRandom irandom = new CRandomJava(null, null);
		weightedItemSelector= new SkipListSelector<WeightedItemWithId>(irandom);
		for (int i= 0; i< numberOfWeightedItems; i++) { 
			weightedItemList.add(new WeightedItemWithId(i, numberOfWeightedItems, WeightFunction.LINEAR)); 
			counts.add(0); 
			}
		for (int i= 0; i< numberOfUpdates; i++) { 
			Collections.shuffle(weightedItemList); 
			weightedItemSelector.updatedItems(weightedItemList); 
			}
		
	}*/
	
	@Test
	public void testRandom(){
		processSelection();			
		if (!equiprobability()) 
			fail("Bad Randomize or very small operation factors!");
	}
	
	
	@Test
	public void testRemoveRecalculationAndRandom() {
		
		assignOtherWeight();
		removeSomeItems(5);
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
		for(int i = 0;i<numberOfWeightedItems;i++){
			if (weightedItemList.get(i).getWeight()!=0){
				if ((double)counts.get(i)/weightedItemList.get(i).getWeight() > max|| max==-1){
					max = counts.get(i);
				}
				else{
					if ((double)counts.get(i)/weightedItemList.get(i).getWeight()<min|| min==-1) 
						min = counts.get(i);
				}
			}
		}
		if (max==-1||min==-1) 
			fail("List is empty!");
		
		return confidenceTest(max-min);	
	}
	//In testing equiprobability should use confidence interval?
	private boolean confidenceTest(double p){
		return Math.abs(p/numberOfSelection) <0.01;		
	}
	
	@Test
	public void testSelect() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdatedItems() {
		fail("Not yet implemented");
	}

	@Test
	public void testLevelsToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testWeightsToString() {
		fail("Not yet implemented");
	}

}

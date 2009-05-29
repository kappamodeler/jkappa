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

	int numberOfWeightedItems = 1000; 
	int numberOfUpdates = 100; 
	int numberOfSelection = 100000000;
	
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
		for (int i= 0; i< numberOfUpdates; i++) { 
			Collections.shuffle(weightedItemList); 
			weightedItemSelector.updatedItems(weightedItemList); 
			}			
	}
	
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
		
	}
	@Test
	public void testRandom(){
		for (int i= 0; i< numberOfSelection; i++) { 
			WeightedItemWithId item = weightedItemSelector.select(); 
			counts.set(item.getId(), counts.get(item.getId())+1); 
			}
		double min = (double)counts.get(0)/weightedItemList.get(0).getWeight();
		double max = (double)counts.get(0)/weightedItemList.get(0).getWeight();
		for(int i = 0;i<numberOfWeightedItems;i++){
			if ((double)counts.get(i)/weightedItemList.get(i).getWeight() >max){
				max = counts.get(i);
			}
			else{
				if ((double)counts.get(i)/weightedItemList.get(i).getWeight()<min) 
					min = counts.get(i);
			}
			
		}
		
		if ((double)(max-min)/numberOfSelection >0.01) fail("Bad Randomize!");
	}
	
	
	@Test
	public void testSkipListSelector() {
		fail("Not yet implemented");
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

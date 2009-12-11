package com.plectix.simulator.staticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.util.ObservableState;

public class TestObservables {

	@Test
	public void testCalculateObsTwoModes() {

		double[] times = new double[] {0.01, 0.09, 0.12, 0.13, 0.14, 0.17, 0.24, 0.25,
				0.34, 0.78, 0.99, 1.03 };

		int[] answer1 = new int[]{-1,2,6,8,9,9,9,9,10,10,11};
		int[] answer2 = new int[]{-1,3,7,9,10,11,12};
		double[] answerTime = new double[]{0.0,0.12, 0.24, 0.34, 0.78, 0.99, 1.03};
		double fullTime = 1;
		double initialTime = 0;
		boolean isTime = true;
		int points = 10;

		// unusing variables
		long count = 0;
		int events = 0;

		Observables obsPlainCalculate = new Observables();
		Observables obsExactCalculate = new Observables();

		List<Agent> connectedAgents = new LinkedList<Agent>();
		String name = "";
		String line = "";
		int id = 0;
		boolean isUnique = true;
		ObservableMock obsMock1 = new ObservableMock(connectedAgents, name,
				line, id, isUnique);
		
		ObservableMock obsMock2 = new ObservableMock(connectedAgents, name,
				line, id, isUnique);
		
		List<ObservableInterface> list1 = new LinkedList<ObservableInterface>();
		list1.add(obsMock1);

		List<ObservableInterface> list2 = new LinkedList<ObservableInterface>();
		list2.add(obsMock2);

		obsExactCalculate.init(fullTime, initialTime, events, points, isTime);
		obsPlainCalculate.init(fullTime, initialTime, events, points, isTime);

		

		obsExactCalculate.setComponentList(list1);
		obsPlainCalculate.setComponentList(list2);
		obsMock1.setValue(-1);
		obsMock2.setValue(-1);
		
		obsExactCalculate.addInitialState();
		obsPlainCalculate.addInitialState();

		for (int i = 0; i < times.length; i++) {
			obsExactCalculate.calculateExactSampleObs(times[i], count, isTime);
			obsMock1.setValue(i+1);
			obsMock2.setValue(i+1);
			obsPlainCalculate.calculateObs(times[i], count, isTime);
			
		}

		assertEquals(Double.valueOf(obsExactCalculate.getTimeSampleMin()),
				Double.valueOf(0.1));

		ObservableInterface outputExact = obsExactCalculate.getComponentList()
				.get(0);
		ObservableInterface outputPlain = obsPlainCalculate.getComponentList()
				.get(0);

		List<ObservableState> countTimeExact = obsExactCalculate.getCountTimeList();

		List<ObservableState> countTimePlain = obsPlainCalculate.getCountTimeList();
		
		for (int i = 0; i < countTimeExact.size(); i++) {
			assertTrue((int)outputExact.getItem(i, null)==answer1[i]);
		}
		for (int i = 0; i < countTimePlain.size(); i++) {
			assertTrue(countTimePlain.get(i).getTime()==answerTime[i]);
			assertTrue(outputPlain.getItem(i, null) == answer2[i]);		
		}

	}

}

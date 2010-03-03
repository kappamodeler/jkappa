package com.plectix.simulator.staticanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.ObservableInterface;
import com.plectix.simulator.staticanalysis.observables.ObservableState;
import com.plectix.simulator.staticanalysis.observables.Observables;

public class TestObservables {

	double[] times = new double[] { 0.01, 0.09, 0.12, 0.13, 0.14, 0.17, 0.24,
			0.25, 0.34, 0.78, 0.99, 1.03 };

	int[] answer1 = new int[] { -1, 2, 6, 8, 9, 9, 9, 9, 10, 10, 11 };
	int[] answer2 = new int[] { -1, 3, 7, 9, 10, 11, 12 };
	int[] answer3 = new int[] { -1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12 };
	double[] answerTime = new double[] { 0.0, 0.12, 0.24, 0.34, 0.78, 0.99,
			1.03 };

	List<Agent> connectedAgents = new LinkedList<Agent>();
	String name = "";
	String line = "";
	int id = 0;

	double fullTime = 1;
	double initialTime = 0;
	int points = 10;

	Observables obsPlainCalculate;
	Observables obsExactCalculate;

	List<ObservableInterface> list1;
	List<ObservableInterface> list2;
	ObservableMock obsMock1;
	ObservableMock obsMock2;
	boolean isUnique = true;

	@Test
	public void testCalculateObsTwoModes() {

		boolean isTime = true;

		// unusing variables
		int events = 0;

		init(isTime, events);
		run(isTime);
		timeCheck();

		isTime = false;
		events = 12;

		init(isTime, events);
		run(isTime);
		eventsCheck();

	}

	private void eventsCheck() {
		assertEquals(Double.valueOf(obsExactCalculate.getTimeSampleMin()),
				Double.valueOf(1.2));

		ObservableInterface outputExact = obsExactCalculate.getComponentList()
				.get(0);
		ObservableInterface outputPlain = obsPlainCalculate.getComponentList()
				.get(0);

		List<ObservableState> countTimeExact = obsExactCalculate
				.getCountTimeList();

		List<ObservableState> countTimePlain = obsPlainCalculate
				.getCountTimeList();

		// for (int i = 0; i < countTimeExact.size(); i++) {
		// //System.out.println(outputExact.getItem(i, null));
		// assertTrue((int) outputExact.getItem(i, null) == answer3[i]);
		// }
		for (int i = 0; i < countTimePlain.size(); i++) {
			// System.out.println(countTimePlain.get(i).getTime());
			// System.out.println(outputPlain.getItem(i, null));
			// assertTrue(countTimePlain.get(i).getTime() == answerTime[i]);
			assertTrue(outputPlain.getItem(i, null) == answer3[i]);
		}

	}

	private void timeCheck() {
		assertEquals(Double.valueOf(obsExactCalculate.getTimeSampleMin()),
				Double.valueOf(0.1));

		ObservableInterface outputExact = obsExactCalculate.getComponentList()
				.get(0);
		ObservableInterface outputPlain = obsPlainCalculate.getComponentList()
				.get(0);

		List<ObservableState> countTimeExact = obsExactCalculate
				.getCountTimeList();

		List<ObservableState> countTimePlain = obsPlainCalculate
				.getCountTimeList();

		for (int i = 0; i < countTimeExact.size(); i++) {
			assertTrue((int) outputExact.getItem(i, null) == answer1[i]);
		}
		for (int i = 0; i < countTimePlain.size(); i++) {
			assertTrue(countTimePlain.get(i).getTime() == answerTime[i]);
			assertTrue(outputPlain.getItem(i, null) == answer2[i]);
		}
	}

	private void run(boolean isTime) {
		for (int i = 0; i < times.length; i++) {
			obsExactCalculate.calculateExactSampleObs(times[i], i + 1, isTime);
			obsMock1.setValue(i + 1);
			obsMock2.setValue(i + 1);
			obsPlainCalculate.calculateObs(times[i], i + 1, isTime);

		}
	}

	private void init(boolean isTime, int events) {
		obsPlainCalculate = new Observables();
		obsExactCalculate = new Observables();
		list1 = new LinkedList<ObservableInterface>();
		list2 = new LinkedList<ObservableInterface>();

		obsMock1 = new ObservableMock(connectedAgents, name, line, id, isUnique);

		obsMock2 = new ObservableMock(connectedAgents, name, line, id, isUnique);

		list1.add(obsMock1);

		list2.add(obsMock2);

		obsExactCalculate.init(fullTime, initialTime, events, points, isTime);
		obsPlainCalculate.init(fullTime, initialTime, events, points, isTime);

		obsExactCalculate.setComponentList(list1);
		obsPlainCalculate.setComponentList(list2);
		obsMock1.setValue(-1);
		obsMock2.setValue(-1);

		obsExactCalculate.addInitialState();
		obsPlainCalculate.addInitialState();
	}

}

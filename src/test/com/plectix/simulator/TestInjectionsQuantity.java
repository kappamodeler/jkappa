package com.plectix.simulator;


import java.util.*;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import org.junit.runner.RunWith;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.components.*;
import com.plectix.simulator.util.*;

@RunWith(Parameterized.class)
public class TestInjectionsQuantity {
	private String myNameParameter;
	private static Map<String, Integer> myDataMap = new HashMap<String, Integer>();
	private ObservablesConnectedComponent myCurrentCC;
	private Failer myFailer = new Failer();
	
	public TestInjectionsQuantity(String name) {
		myNameParameter = name;
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		myDataMap = (new InjectionsQuantityDataParser(
			"test.data/InjectionsQuantityData")).parse();
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		int i = 0;
		for (String name : myDataMap.keySet()) {
			parameters.add( new String[] {name});
			i++;
		}
		return Collections.unmodifiableList(parameters);
	}
	
	private void createInjectionsList(String ccName) {
		Integer expectedQuantity = myDataMap.get(ccName);
		boolean exists = false;
		for (ObservablesConnectedComponent c : RunInjectionsTests.getObservatory()) {
			if (ccName.equals(c.getName())) {
				myCurrentCC = c;
				exists = true;
			}
		}
		if (!exists) {
			myFailer.fail("There's no component with name " + ccName);
		}
		List<CInjection> injectionsList = myCurrentCC.getInjectionsList();
		if (injectionsList != null) {
			myFailer.assertEquals("failed on " + ccName, expectedQuantity, (injectionsList.size()));
		} else {
			myFailer.assertEquals("failed on " + ccName, expectedQuantity, 0);
		}
	}
	
	@Test
	public void testCCInjectionsQuantity() {
		createInjectionsList(myNameParameter);
	}
}

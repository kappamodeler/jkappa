package com.plectix.simulator;


import java.util.*;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import org.junit.runner.RunWith;

import com.plectix.simulator.components.CObservables.ObservablesConnectedComponent;
import com.plectix.simulator.components.*;
import com.plectix.simulator.util.QuantityDataParser;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestInjectionsQuantity {
	private String myNameParameter;
	private static Map<String, Integer> myDataMap = new HashMap<String, Integer>();
	private ObservablesConnectedComponent myCurrentCC;
	
	public TestInjectionsQuantity(String name) {
		myNameParameter = name;
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		myDataMap = (new QuantityDataParser(
			"test.data/InjectionsQuantityData")).parse();
		Set<String> names = myDataMap.keySet();
		Object[][] parameters = new Object[names.size()][];
		int i = 0;
		for (String name : myDataMap.keySet()) {
			parameters[i] = new String[] {name};
			i++;
		}
		return Collections.unmodifiableList(Arrays.asList(parameters));
	}
	
	private void assertWithFailMessage(Object a, Object b, String testId) {
		boolean fail = false;
		if (a != null) {
			fail = !a.equals(b);
		} else {
			fail = (b != null);
		}
		if (fail) {
		    fail(testId + " : expected " + a.toString() + ", but was " + b.toString());
		}	
	}
	
	private void createInjectionsList(String ccName) {
		Integer expectedQuantity = myDataMap.get(ccName);
		boolean exists = false;
		for (ObservablesConnectedComponent c : TestInjections.getObservatory()) {
			if (ccName.equals(c.getName())) {
				myCurrentCC = c;
				exists = true;
			}
		}
		if (!exists) {
			fail("There's no component with name " + ccName);
		}
		List<CInjection> injectionsList = myCurrentCC.getInjectionsList();
		if (injectionsList != null) {
			assertWithFailMessage(expectedQuantity, (injectionsList.size()), "failed on " + ccName);
		} else {
			assertWithFailMessage(expectedQuantity, 0, "failed on " + ccName);
		}
	}
	
	@Test
	public void testCCInjectionsQuantity() {
		createInjectionsList(myNameParameter);
	}
}

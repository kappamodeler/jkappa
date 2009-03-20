package com.plectix.simulator.injections;


import java.util.*;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import org.junit.runner.RunWith;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.util.*;

@RunWith(Parameterized.class)
public class TestInjectionsQuantity extends TestInjections  {
	private String myNameParameter;
	private static Map<String, Integer> myDataMap = new TreeMap<String, Integer>();
	private IObservablesConnectedComponent myCurrentCC;
	private Failer myFailer = new Failer();
	
	public TestInjectionsQuantity(String name) {
		myNameParameter = name;
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		myDataMap = (new QuantityDataParser(
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
		for (IObservablesConnectedComponent c : getInitializator().getObservables()) {
			if (ccName.equals(c.getName())) {
				myCurrentCC = c;
				exists = true;
			}
		}
		if (!exists) {
			myFailer.fail("There's no component with name " + ccName);
		}
		Collection<IInjection> injectionsList = myCurrentCC.getInjectionsList();
		long quant = 0;
		for (IInjection injection : myCurrentCC.getInjectionsList()) {
			if (injection.isSuper()) {
				quant += injection.getSuperSubstance().getQuantity();
			} else {
				quant++;
			}
		}
			
		if (injectionsList != null) {
			myFailer.assertEquals("failed on " + ccName, (long)expectedQuantity, quant);
		} else {
			myFailer.assertEquals("failed on " + ccName, expectedQuantity, 0);
		}
	}
	
	@Test
	public void testCCInjectionsQuantity() {
		createInjectionsList(myNameParameter);
	}
}

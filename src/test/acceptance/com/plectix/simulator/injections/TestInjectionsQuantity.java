package com.plectix.simulator.injections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;

@RunWith(Parameterized.class)
public class TestInjectionsQuantity extends TestInjections {
	private static final String separator = File.separator;
	private final String nameParameter;
	private static Map<String, Integer> data = new TreeMap<String, Integer>();
	private ObservableConnectedComponentInterface myCurrentCC;
	private final Failer myFailer = new Failer();

	public TestInjectionsQuantity(String nameParameter) {
		this.nameParameter = nameParameter;
	}

	@Parameters
	public static Collection<Object[]> regExValues() throws FileNotFoundException, SimulationDataFormatException, IOException {
		data = (new QuantityDataParser("test.data" + separator
				+ "InjectionsQuantityData" + DEFAULT_EXTENSION_FILE))
				.parse();
		LinkedList<Object[]> parameters = new LinkedList<Object[]>();
		int i = 0;
		for (String name : data.keySet()) {
			parameters.add(new String[] { name });
			i++;
		}
		return parameters;
	}

	private void createInjectionsList(String ccName) {
		Integer expectedQuantity = data.get(ccName);
		boolean exists = false;
		for (ObservableConnectedComponentInterface c : getInitializator()
				.getObservables()) {
			if (ccName.equals(c.getName())) {
				myCurrentCC = c;
				exists = true;
			}
		}
		if (!exists) {
			myFailer.fail("There's no component with name " + ccName);
		}
		Collection<Injection> injectionsList = myCurrentCC.getInjectionsList();
		double quant = myCurrentCC.getInjectionsWeight();

		if (injectionsList != null) {
			myFailer.assertEquals("failed on " + ccName,
					(long) expectedQuantity, (long) quant);
		} else {
			myFailer.assertEquals("failed on " + ccName, expectedQuantity, 0);
		}
	}

	@Test
	public void testCCInjectionsQuantity() {
		createInjectionsList(nameParameter);
	}
}

package com.plectix.simulator.updates;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.parser.SimulationDataFormatException;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.staticanalysis.Site;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;

public class TestNegativeUpdate extends TestUpdate {

	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator
			+ "negativeUpdate" + separator;

	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private final Failer myFailer = new Failer();
	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator.generate(getAllTestFileNames(myPrefixFileName),true);
	}

	public TestNegativeUpdate(String fileName, Integer opMode) {
		super(fileName, opMode);

		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}


	@Test
	public void testLHS() {
		List<ConnectedComponentInterface> leftHand = getActiveRule()
				.getLeftHandSide();
		for (ConnectedComponentInterface cc : leftHand) {
			Collection<Injection> componentInjections = cc.getInjectionsList();
			if (!lhsIsEmpty(leftHand)) {

				myFailer.assertEquals("LHS injections", myLHSInjectionsQuantity
						.get(myTestFileName), (int) cc.getInjectionsWeight());
				for (Injection injection : getCurrentInjectionsList()) {

					assertFalse(cc.getInjectionsList().contains(injection));
					for (Site site : injection.getChangedSites()) {

						myFailer.assertEmpty("LHS lifts", site.getLift());
					}
				}
			} else {
				myFailer.assertTrue("LHS injections", (componentInjections
						.size() == 1)
						&& (componentInjections.contains(ThreadLocalData
								.getEmptyInjection())));
			}
		}
	}

	@Test
	public void testObs() {
		for (ObservableConnectedComponentInterface cc : getInitializator().getObservables()) {
			myFailer.assertEquals("Observables injections",
					myObsInjectionsQuantity.get(myTestFileName), (int)cc
							.getInjectionsWeight());
		}
		
	}

	@Override
	public boolean isDoingPositive() {
		return false;
	}

	@Override
	public String getPrefixFileName() {
		return myPrefixFileName;
	}

	@Override
	public void init() throws FileNotFoundException, SimulationDataFormatException, IOException {
		myObsInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "ObsInjectionsData" + DEFAULT_EXTENSION_FILE))
				.parse();
		myLHSInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "LHSInjectionsData" + DEFAULT_EXTENSION_FILE))
				.parse();
	}
}

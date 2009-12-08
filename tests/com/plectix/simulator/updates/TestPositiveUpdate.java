package com.plectix.simulator.updates;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableConnectedComponentInterface;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.Failer;
import com.plectix.simulator.util.QuantityDataParser;

public class TestPositiveUpdate extends TestUpdate {

	private static final String separator = File.separator;
	private static final String myPrefixFileName = "test.data" + separator
			+ "positiveUpdate" + separator;

	private String myTestFileName = "";
	private Map<String, Integer> myObsInjectionsQuantity;
	private Map<String, Integer> myLHSInjectionsQuantity;
	private final Failer myFailer = new Failer();

	@Parameters
	public static Collection<Object[]> regExValues() {
		return OperationModeCollectionGenerator.generate(getAllTestFileNames(myPrefixFileName),true);
	}

	public TestPositiveUpdate(String filePath, Integer opMode) {
		super(filePath, opMode);
		myTestFileName = filePath;
		myFailer.loadTestFile(myTestFileName);
	}

	@Test
	// we can only check for quantity of injections, meaning correct injections
	// setting
	public void testObs() {
		int solutionLinkingForCurrentObs = 0;

		for (ObservableConnectedComponentInterface cc : getInitializator()
				.getObservables()) {
			for (Injection injection : cc.getInjectionsList()) {
				if (injection.isSuper()) {
					solutionLinkingForCurrentObs += injection
							.getCorrespondence().size()
							* injection.getWeight();
				} else {
					solutionLinkingForCurrentObs += injection
							.getCorrespondence().size();
				}
			}
			// myFailer.assertEquals("Observables injections",
			// myObsInjectionsQuantity.get(myTestFileName),
			// cc.getCommonPower());
		}

		myFailer.assertEquals("Observatory injections", myObsInjectionsQuantity
				.get(myTestFileName), solutionLinkingForCurrentObs);
	}

	@Test
	// the same way, we're checking common injections quantity for all the cc
	// from lhs
	public void testLHS() {
		List<ConnectedComponentInterface> leftHand = getActiveRule()
				.getLeftHandSide();
		for (ConnectedComponentInterface cc : leftHand) {
			if (!lhsIsEmpty(leftHand)) {
				myFailer.assertEquals("LHS injections", myLHSInjectionsQuantity
						.get(myTestFileName), (int) cc.getInjectionsWeight());
			} else {
				myFailer.assertTrue("LHS injections",
						(cc.getInjectionsWeight() == 1)
								&& (cc.getInjectionsList()
										.contains(ThreadLocalData
												.getEmptyInjection())));
			}
		}
	}

	@Override
	public boolean isDoingPositive() {
		return true;
	}

	@Override
	public String getPrefixFileName() {
		return myPrefixFileName;
	}

	@Override
	public void init() {
		myObsInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "ObsInjectionsData" + DEFAULT_EXTENSION_FILE))
				.parse();
		myLHSInjectionsQuantity = (new QuantityDataParser(myPrefixFileName
				+ "LHSInjectionsData" + DEFAULT_EXTENSION_FILE))
				.parse();
	}
}

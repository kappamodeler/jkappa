package com.plectix.simulator.cyclesdetection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.cycledetection.Detector;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

@RunWith(value = Parameterized.class)
public class TestDetectionOfCycles {
	
	private static final String prefixSourceModel = InitData.pathForSourceModel;

	private AllSubViewsOfAllAgentsInterface subViews;
	private ContactMap contactMap;

	private InitTestDetectionOfCycles initTestDetectionOfCycles = new InitTestDetectionOfCycles();

	@Parameters
	public static Collection<Object[]> configs() {
		return FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(prefixSourceModel,
						"~kappa");
	}

	public TestDetectionOfCycles(String count, String patch) {
		initTestDetectionOfCycles.initializeSimulation(patch, count);
	}

	@Before
	public void setUp() {
		subViews = initTestDetectionOfCycles.getSubViews();
		contactMap = initTestDetectionOfCycles.getContactMap();
	}

	@Test
	public void testTotalAmountLocalViews() {
		List<AbstractAgent> list = new LinkedList<AbstractAgent>();
		list.addAll(contactMap.getAbstractSolution().getAgentNameToAgent()
				.values());
		Detector detector = new Detector(subViews, list);
		System.out.println(detector.extractCycles().size());
	}
}

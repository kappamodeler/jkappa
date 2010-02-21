package com.plectix.simulator.simulationdata;

import junit.framework.Assert;

import org.junit.Test;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;

public class TestSimulationData {
	private final SimulationArguments simulationArguments = new SimulationArguments(); 
	
	public TestSimulationData() {
		
	}
	
	@Test
	public void setXmlOutputDestination() {
		String value = "qwerty";
		simulationArguments.setXmlOutputDestination(value);
		Assert.assertTrue(value.equals(simulationArguments.getXmlOutputDestination()));
	}
	
	@Test
	public void setSimulationType() {
		SimulationType value = SimulationType.COMPILE;
		simulationArguments.setSimulationType(value);
		Assert.assertTrue(value.equals(simulationArguments.getSimulationType()));
	}
	
	@Test
	public void setInputFilename() {
		String value = "new new new file name";
		simulationArguments.setInputFileName(value);
		Assert.assertTrue(value.equals(simulationArguments.getInputFileName()));
	}
	
	@Test
	public void setMaxNumberOfEvents() {
		int value = 1967;
		simulationArguments.setMaxNumberOfEvents(value);
		Assert.assertTrue(value == simulationArguments.getMaxNumberOfEvents());
		Assert.assertFalse(simulationArguments.isTime());
	}

	@Test
	public void setTimeLimit() {
		double value = 2010;
		simulationArguments.setTimeLimit(value);
		Assert.assertTrue(value == simulationArguments.getTimeLimit());
		Assert.assertTrue(simulationArguments.isTime());
	}

	@Test
	public void setInputCharArray() {
		char[] value = new char[]{'1', 'f', 'Z'};
		simulationArguments.setInputCharArray(value);
		Assert.assertEquals(value, simulationArguments.getInputCharArray());
	}

	@Test
	public void setStorify() {
		simulationArguments.setStorifyFlag(true);
		Assert.assertTrue(simulationArguments.needToStorify());
		simulationArguments.setStorifyFlag(false);
		Assert.assertFalse(simulationArguments.needToStorify());
	}
	
	@Test
	public void setEnumerationOfSpecies() {
		simulationArguments.setEnumerationOfSpecies(true);
		Assert.assertTrue(simulationArguments.needToEnumerationOfSpecies());
		simulationArguments.setEnumerationOfSpecies(false);
		Assert.assertFalse(simulationArguments.needToEnumerationOfSpecies());
	}
	

	@Test
	public void setIterations() {
		int value = 2010000;
		simulationArguments.setIterations(value);
		Assert.assertTrue(value == simulationArguments.getIterations());
	}

	@Test
	public void setAllowIncompleteSubstance() {
		simulationArguments.setAllowIncompleteSubstance(true);
		Assert.assertTrue(simulationArguments.incompletesAllowed());
		simulationArguments.setAllowIncompleteSubstance(false);
		Assert.assertFalse(simulationArguments.incompletesAllowed());
	}
}

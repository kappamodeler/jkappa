package com.plectix.simulator.simulator.api.steps;

import com.plectix.simulator.io.xml.SimulationDataXMLWriter;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.api.OperationType;

public class XMLOutputOperation extends AbstractOperation<Object> {
	private final SimulationData simulationData;
	private final String destination;
	
	public XMLOutputOperation(SimulationData simulationData, String destination) {
		super(simulationData, OperationType.XML_OUTPUT);
		this.simulationData = simulationData;
		this.destination = destination;
	}
	
	/**
	 * Returns a set of dead rules' ids 
	 * @param simulator
	 * @return
	 * @throws Exception 
	 */
	protected Object performDry() throws Exception {
		simulationData.getSimulationArguments().setXmlOutputDestination(destination);
		(new SimulationDataXMLWriter(simulationData)).outputXMLData();
		return null;
	}

	@Override
	protected boolean noNeedToPerform() {
		return false;
	}

	@Override
	protected Object retrievePreparedResult() {
		return null;
	}

}

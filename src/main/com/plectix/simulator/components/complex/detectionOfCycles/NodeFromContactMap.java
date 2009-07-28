package com.plectix.simulator.components.complex.detectionOfCycles;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.abstracting.CAbstractSite;
import com.plectix.simulator.graphs.Vertex;

public class NodeFromContactMap extends Vertex{
	
	CAbstractAgent agent;
	CAbstractSite site;
	
	public NodeFromContactMap(CAbstractAgent a, CAbstractSite abstractSite){
		agent = a;
		site = abstractSite;
	}

}

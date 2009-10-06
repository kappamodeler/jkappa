package com.plectix.simulator.component.complex.cyclesdetection;

import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.graphs.Vertex;

//TODO should we add some methods here or what?
public final class NodeFromContactMap extends Vertex{
	private final AbstractAgent agent;
	private final AbstractSite site;
	
	public NodeFromContactMap(AbstractAgent a, AbstractSite abstractSite){
		agent = a;
		site = abstractSite;
	}
}

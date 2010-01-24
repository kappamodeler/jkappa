package com.plectix.simulator.staticanalysis.cycledetection;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.abstracting.AbstractSite;
import com.plectix.simulator.staticanalysis.graphs.Vertex;

//TODO should we add some methods here or what?
final class NodeFromContactMap extends Vertex{
	private final AbstractAgent agent;
	private final AbstractSite site;
	
	public NodeFromContactMap(AbstractAgent a, AbstractSite abstractSite){
		agent = a;
		site = abstractSite;
	}
}

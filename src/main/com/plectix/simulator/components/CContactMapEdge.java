package com.plectix.simulator.components;

public class CContactMapEdge {
	private ChangedSite node1;
	private ChangedSite node2;
	
	
	
	
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CContactMapEdge)) {
			return false;
		}

		CContactMapEdge edge = (CContactMapEdge) obj;

		if (!this.node1.isLinkState()){
			if(edge.node1.isLinkState()){
				return false;
			}else{
				return this.node1.getSite().equals(edge.node1.getSite());
			}
		} else 
			
			/*
			nameId != agent.getNameId()) {
			return false;*/
		//}
		return true;
	}

	
}

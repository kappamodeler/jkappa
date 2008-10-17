package com.plectix.simulator.components;

import java.util.List;

public class CSnapshot {

	private CSolution solution;
	
	private int totalAgents;
	
	private int totalConnectedComponents;
	
	private int smollestConnectedComponent;
	
	private int largestConnectedComponent;
	
	private int uniqueConnectedComponent;
	
	private List<SnapshotElement> snapshotElements;
	
	private class SnapshotElement{
		private int count;
		private CConnectedComponent cc; 
	}
	
	
	public CSnapshot (CSolution solution){
		
		clone(solution);
	}

	private void clone(CSolution solution) {
		
	}
	
	
}

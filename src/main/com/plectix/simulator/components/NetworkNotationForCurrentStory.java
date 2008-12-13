package com.plectix.simulator.components;

import java.util.ArrayList;
import java.util.List;

class NetworkNotationForCurrentStory {
	private List<CNetworkNotation> networkNotationList;
	private boolean endOfStory;
	private double averageTime;
	
	public double getAverageTime(){
		return averageTime;
	}

	public void setAverageTime(double averageTime){
		this.averageTime = averageTime;
	}

	public final boolean isEndOfStory() {
		return endOfStory;
	}

	public final void setEndOfStory(boolean endOfStory) {
		this.endOfStory = endOfStory;
	}

	public final List<CNetworkNotation> getNetworkNotationList() {
		return networkNotationList;
	}

	public final CNetworkNotation getNetworkNotation(int traceID) {
		for(CNetworkNotation nn: networkNotationList)
			if (nn.getStep()==traceID)
				return nn;
		return null;
	}
	
	public NetworkNotationForCurrentStory() {
		networkNotationList = new ArrayList<CNetworkNotation>();
		endOfStory = false;
	}

	public void addToNetworkNotationList(CNetworkNotation networkNotation) {
		if (networkNotation.isOpposite(networkNotationList))
			networkNotationList.add(networkNotation);
	}

	public void addToNetworkNotationListStorifyRule(
			CNetworkNotation networkNotation) {
		networkNotationList.add(networkNotation);
	}

	// TODO separate
	private class CStoryVertexes {
		AgentSites aSites;
	}

	public void handling() {
		List<CNetworkNotation> nnList = new ArrayList<CNetworkNotation>();
		nnList.add(networkNotationList.get(networkNotationList.size() - 1));
		for (int i = networkNotationList.size() - 2; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			if (isIntersects(nn, nnList)) {
				nnList.add(nn);
			}
		}
		this.networkNotationList = nnList;
	}

	private final boolean isIntersects(CNetworkNotation nn,
			List<CNetworkNotation> nnList) {

		for (CNetworkNotation nnFromList : nnList) {
			if (!((nnFromList.isIntersects(nn) == CNetworkNotation.HAS_NO_INTERSECTION) && (nn
					.isIntersects(nnFromList) == CNetworkNotation.HAS_NO_INTERSECTION)))
				return true;
		}

		return false;
	}

	public final void clearList() {
		networkNotationList.clear();
	}

}

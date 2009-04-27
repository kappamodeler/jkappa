package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.stories.CNetworkNotation.IntersectionType;

class NetworkNotationForCurrentStory {
	private List<CNetworkNotation> networkNotationList;
	private boolean endOfStory = false;;
	private double averageTime;

	public NetworkNotationForCurrentStory() {
		networkNotationList = new ArrayList<CNetworkNotation>();
	}
	
	public double getAverageTime(){
		return averageTime;
	}

	public void setAverageTime(double averageTime){
		this.averageTime = averageTime;
	}

	public final boolean isEndOfStory() {
		return endOfStory;
	}

	public final void setEndOfStory() {
		this.endOfStory = true;
	}

	public final List<CNetworkNotation> getNetworkNotationList() {
		return networkNotationList;
	}

	public final CNetworkNotation getNetworkNotation(int traceID) {
		for (CNetworkNotation nn : networkNotationList)
			if (nn.getStep() == traceID)
				return nn;
		return null;
	}

	public static void addToNetworkNotationList(CNetworkNotation networkNotation,
			List<CNetworkNotation> networkNotationList) {
		if (networkNotation.isNotOpposite(networkNotationList) == null)
			networkNotationList.add(networkNotation);
	}

	public void addToNetworkNotationListStorifyRule(
			CNetworkNotation networkNotation) {
			networkNotationList.add(networkNotation);
	}

	public void handling() {
		List<CNetworkNotation> nnList = new ArrayList<CNetworkNotation>();
		nnList.add(networkNotationList.get(networkNotationList.size() - 1));
		//nnList.add(networkNotationList.get(networkNotationList.size() - 2));
		for (int i = networkNotationList.size() - 2; i >= 0; i--) {
			CNetworkNotation nn = networkNotationList.get(i);
			if (isIntersects(nn, nnList)) {
				//addToNetworkNotationList(nn, nnList);
				nnList.add(nn);
			}else{
				//nn.clearAgentsForDeletedOppositeRules();
			}
				
		}
		this.networkNotationList = nnList;
	}

	private final boolean isIntersects(CNetworkNotation nn,
			List<CNetworkNotation> nnList) {

		int counter =0;
		for (CNetworkNotation nnFromList : nnList) {
			
			if (((nnFromList.isIntersects(nn, true) != IntersectionType.NO_INTERSECTION) || (nn
						.isIntersects(nnFromList, true) != IntersectionType.NO_INTERSECTION)))
				counter++;
		}
		if (counter!=0)
			return true;

		return false;
	}

	public final void clearList() {
		networkNotationList.clear();
	}

}

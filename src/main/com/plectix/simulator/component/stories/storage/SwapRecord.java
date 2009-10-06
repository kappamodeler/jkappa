package com.plectix.simulator.component.stories.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.plectix.simulator.component.stories.compressions.ExtensionData;

public final class SwapRecord {
	
	private List<Long> agentsIdsFirstList;
	private List<Long> agentsIdsSecondList;
	private long firstEventId;
	private boolean swapTop;
	private Event otherSide;
	private ArrayList<ExtensionData> extensionLinks;
	public final List<Map<WireHashKey,WireHashKey>> mapWire
		= new LinkedList<Map<WireHashKey,WireHashKey>>();;
	
	public SwapRecord(){
		otherSide = null;
	}

	public final void setAgents1(List<Long> agents1) {
		this.agentsIdsFirstList = agents1;
	}

	public final List<Long> getAgents1() {
		return agentsIdsFirstList;
	}

	public final void setAgents2(List<Long> agents2) {
		this.agentsIdsSecondList = agents2;
	}

	public final List<Long> getAgents2() {
		return agentsIdsSecondList;
	}

	public void setFirstEventId(Long firstEventId) {
		this.firstEventId = firstEventId;
	}

	public final long getFirstEventId() {
		return firstEventId;
	}

	public final void setSwapTop(boolean swapTop) {
		this.swapTop = swapTop;
	}

	public final boolean isSwapTop() {
		return swapTop;
	}

	public final void setOtherSide(Event otherSide) {
		this.otherSide = otherSide;
	}

	public final Event getOtherSide() {
		return otherSide;
	}


	public final void setExtensionLinks(ArrayList<ExtensionData> extensionLinks) {
		this.extensionLinks = extensionLinks;
	}


	public final ArrayList<ExtensionData> getExtensionLinks() {
		return extensionLinks;
	}
}

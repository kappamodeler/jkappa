package com.plectix.simulator.components.stories.storage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SwapRecord {
	
	private List<Long> agents1;
	private List<Long> agents2;

	private Long firstEventId;
	private boolean swapTop;
	
	private CEvent otherSide;
	
	public List<Map<WireHashKey,WireHashKey>> mapWire;
	
	public SwapRecord(){
		otherSide = null;
		mapWire = new LinkedList<Map<WireHashKey,WireHashKey>>();
	}
	

	public void setAgents1(List<Long> agents1) {
		this.agents1 = agents1;
	}

	public List<Long> getAgents1() {
		return agents1;
	}

	public void setAgents2(List<Long> agents2) {
		this.agents2 = agents2;
	}

	public List<Long> getAgents2() {
		return agents2;
	}

	public void setFirstEventId(Long firstEventId) {
		this.firstEventId = firstEventId;
	}

	public Long getFirstEventId() {
		return firstEventId;
	}

	public void setSwapTop(boolean swapTop) {
		this.swapTop = swapTop;
	}

	public boolean isSwapTop() {
		return swapTop;
	}

	public void setOtherSide(CEvent otherSide) {
		this.otherSide = otherSide;
	}

	public CEvent getOtherSide() {
		return otherSide;
	}
	
	
}

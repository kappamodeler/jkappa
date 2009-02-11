package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.List;

public class CStoryIntro {
	String notation;
	List<Integer> traceIDs;

	public String getNotation() {
		return notation;
	}

	public List<Integer> getTraceIDs() {
		return traceIDs;
	}

	public CStoryIntro(String notation) {
		this.notation = notation;
		traceIDs = new ArrayList<Integer>();
	}

	public void addToTraceID(int traceID) {
		this.traceIDs.add(traceID);
	}
}

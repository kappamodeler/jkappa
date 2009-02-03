package com.plectix.simulator.components.stories;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.IStates;

/**
 * Will be separated soon. Do not replace it.
 * */
class StoryChangeStateWithTrace {
	private List<Integer> traceIDList;
	private List<IStates> storyStatesList;

	public StoryChangeStateWithTrace() {
		traceIDList = new ArrayList<Integer>();
		storyStatesList = new ArrayList<IStates>();
	}

	public void addToStoryChangeStateWithTraceLists(int traceID,
			IStates state) {
		traceIDList.add(traceID);
		storyStatesList.add(state);
	}

	public boolean isLastUpdate(int id) {
		int index;
		for (index = 0; index < traceIDList.size(); index++) {
			if (traceIDList.get(index) > id)
				break;
		}
		if (index == traceIDList.size())
			return true;
		return false;
	}

	public IStates getStoryStateByTraceID(int id, boolean isEmpty) {
		int index;
		for (index = 0; index < traceIDList.size(); index++) {
			if (traceIDList.get(index) >= id)
				break;
		}
		if (index == traceIDList.size())
			return CStoryState.EMPTY_STATE;

		return storyStatesList.get(index);
	}
}
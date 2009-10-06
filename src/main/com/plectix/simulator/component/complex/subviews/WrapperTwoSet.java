package com.plectix.simulator.component.complex.subviews;

import java.util.LinkedHashSet;
import java.util.Set;

// TODO please rename
public final class WrapperTwoSet {
	private final LinkedHashSet<Integer> first = new LinkedHashSet<Integer>();
	private final LinkedHashSet<Integer> second = new LinkedHashSet<Integer>();

	public final LinkedHashSet<Integer> getFirst() {
		return first;
	}

	public final void firstSetAddAll(Set<Integer> integers) {
		first.addAll(integers);
	}
	
	public final void secondSetAddAll(Set<Integer> integers) {
		first.addAll(integers);
	}
	
	public final LinkedHashSet<Integer> getSecond() {
		return second;
	}

	public final boolean isEmpty() {
		// TODO Auto-generated method stub
		return (first.isEmpty() && second.isEmpty());
	}

}

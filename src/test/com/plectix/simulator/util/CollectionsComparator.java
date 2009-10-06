package com.plectix.simulator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public abstract class CollectionsComparator {
	public abstract boolean equals(Object a, Object b);

	public <E> boolean areEqual(Collection<E> a, Collection<E> b) {
		Stack<E> bStack = new Stack<E>();
		bStack.addAll(b);

		ArrayList<E> aArrayList = new ArrayList<E>();
		aArrayList.addAll(a);

		int foundIndex = -1;
		E bElement;
		E elementA;

		while (!bStack.isEmpty()) {
			boolean contains = false;
			bElement = bStack.pop();
			for (int i = 0; i < aArrayList.size(); i++) {
				elementA = aArrayList.get(i);
				if (elementA != null) {
					if (equals(elementA, bElement)) {
						foundIndex = i;
						contains = true;
						break;
					}
				} else {
					contains = (bElement == null);
				}
			}
			if (!contains) {
				return false;
			} else {
				aArrayList.remove(foundIndex);
			}

		}

		if (!aArrayList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}

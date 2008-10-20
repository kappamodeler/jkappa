package com.plectix.simulator.util;

import java.util.Set;
import java.util.TreeSet;

/*package*/ class SetUtilities {
	public static <E> Set<E> and(Set<E> a, Set<E> b) {
		Set<E> res = new TreeSet<E>();
		for (E element : a) {
			if (b.contains(element)) {
				res.add(element);
			}
		}
		return res;
	}
}

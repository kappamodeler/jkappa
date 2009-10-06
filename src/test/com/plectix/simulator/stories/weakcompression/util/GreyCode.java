package com.plectix.simulator.stories.weakcompression.util;

import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.component.stories.MarkOfEvent;

public class GreyCode {

	public static Set<MarkOfEvent[]> generateGreyLists(int n) {
		Set<MarkOfEvent[]> set = new LinkedHashSet<MarkOfEvent[]>();
		MarkOfEvent marks[] = new MarkOfEvent[n];
		for (int i = 0; i < marks.length; i++) {
			marks[i] = MarkOfEvent.DELETED;
		}
		int tmp;
		for (int i = 0; i < Math.pow(2, n) - 2; i++) {
			tmp = getIndex(i + 1);
			marks[tmp] = inverse(marks[tmp]);
			MarkOfEvent m[] = new MarkOfEvent[n];
			m = marks.clone();
			if (isGood(m))
				set.add(m);

		}

		return set;

	}

	private static boolean isGood(MarkOfEvent[] m) {
		for (MarkOfEvent markOfEvent : m) {
			if (markOfEvent.equals(MarkOfEvent.DELETED))
				return true;
		}
		return false;
	}

	private static MarkOfEvent inverse(MarkOfEvent markOfEvent) {
		if (markOfEvent.equals(MarkOfEvent.DELETED))
			return MarkOfEvent.KEPT;
		else
			return MarkOfEvent.DELETED;
	}

	private static int getIndex(int i) {
		int index = 0;
		while (i % 2 == 0) {
			i = i / 2;
			index++;
		}
		// System.out.println("index = " + index);
		return index;
	}

	public static void main(String[] args) {
		System.out.println("hello");
		Set<MarkOfEvent[]> mymarks = generateGreyLists(3);
		for (MarkOfEvent[] markOfEvents : mymarks) {
			for (int i = 0; i < markOfEvents.length; i++) {
				System.out.print(markOfEvents[i] + "\t");
			}
			System.out.println();
		}

	}
}

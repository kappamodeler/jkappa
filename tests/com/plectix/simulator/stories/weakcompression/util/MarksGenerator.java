package com.plectix.simulator.stories.weakcompression.util;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.staticanalysis.stories.MarkOfEvent;

public class MarksGenerator {

	public static List<List<MarkOfEvent>> generateLists(int n) {

		List<List<MarkOfEvent>> list = new ArrayList<List<MarkOfEvent>>();
		for (int i = 0; i < Math.pow(2, n); i++) {
			List<MarkOfEvent> tmp = new ArrayList<MarkOfEvent>();
			list.add(tmp);
		}
		list = generate(n, list);
		list.remove(list.size() - 1);
		list.remove(0);
		return list;
	}

	private static List<List<MarkOfEvent>> generate(int n,
			List<List<MarkOfEvent>> list) {

		int i = 0;
		if (n == 1) {
			for (List<MarkOfEvent> list2 : list) {
				list2
						.add((i % 2 == 0) ? MarkOfEvent.KEPT
								: MarkOfEvent.DELETED);
				i++;
			}
			return list;
		} else {
			for (List<MarkOfEvent> list2 : list) {
				list2
						.add(((i % Math.pow(2, n)) < Math.pow(2, n - 1)) ? MarkOfEvent.KEPT
								: MarkOfEvent.DELETED);
				i++;
			}
			return generate(--n, list);
		}
	}

	public static void main(String[] args) {
		generateLists(20);
	}
}

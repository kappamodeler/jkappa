package com.plectix.simulator.stories.weakCompression.util;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;

public class MarksGenerator {
	
	
	public static List<List<EMarkOfEvent>> generateLists(int n) {
		
		List<List<EMarkOfEvent>> list = new ArrayList<List<EMarkOfEvent>>();
		for (int i = 0; i < Math.pow(2, n); i++) {
			List<EMarkOfEvent> tmp = new ArrayList<EMarkOfEvent>();
			list.add(tmp);
		}
		list = generate(n, list);
		list.remove(list.size() - 1);
		list.remove(0);
		return list;
	}
	

	private static List<List<EMarkOfEvent>> generate(int n,
			List<List<EMarkOfEvent>> list) {
		
		int i = 0;
		if (n == 1) {
			for (List<EMarkOfEvent> list2 : list) {
				list2.add((i % 2 == 0) ? EMarkOfEvent.KEPT
						: EMarkOfEvent.DELETED);
				i++;
			}
			return list;
		} else {
			for (List<EMarkOfEvent> list2 : list) {
				list2
						.add(((i % Math.pow(2, n)) < Math.pow(2, n - 1)) ? EMarkOfEvent.KEPT
								: EMarkOfEvent.DELETED);
				i++;
			}
			return generate(--n, list);
		}
	}
	

	
	public static void main(String[] args) {
		generateLists(20);
	}
}

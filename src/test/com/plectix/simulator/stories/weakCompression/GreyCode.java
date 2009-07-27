package com.plectix.simulator.stories.weakCompression;

import java.util.LinkedHashSet;
import java.util.Set;

import com.plectix.simulator.components.stories.enums.EMarkOfEvent;

public class GreyCode {

	
	
	public static Set<EMarkOfEvent []> generateGreyLists(int n) {
		Set<EMarkOfEvent []> set = new LinkedHashSet<EMarkOfEvent []>();
		EMarkOfEvent marks [] = new EMarkOfEvent[n];
		for (int i = 0; i < marks.length; i++) {
			marks[i] = EMarkOfEvent.DELETED;
		}
		int tmp;
		for (int i = 0; i < Math.pow(2, n) - 2; i++) {
			tmp = getIndex(i +1);
			marks[tmp] = inverse(marks[tmp]);
			EMarkOfEvent m [] = new EMarkOfEvent[n];
			m = marks.clone();
			if (isGood(m))
				set.add(m);
			
		}
		
		
		
		return set;
		
	}

	private static boolean isGood(EMarkOfEvent[] m) {
		for (EMarkOfEvent markOfEvent : m) {
			if (markOfEvent.equals(EMarkOfEvent.DELETED))
					return true;
		}
		return false;
	}

	private static EMarkOfEvent inverse(EMarkOfEvent markOfEvent) {
		if (markOfEvent.equals(EMarkOfEvent.DELETED))
			return EMarkOfEvent.KEPT;
		else
			return EMarkOfEvent.DELETED;
	}

	private static int getIndex(int i) {
		int index = 0;
		while(i%2 == 0){
			i = i/2;
			index++;
		}
//		System.out.println("index = " + index);
		return index;
	}
	
	
	public static void main(String[] args) {
		System.out.println("hello");
		Set<EMarkOfEvent[]> mymarks = generateGreyLists(3);
		for (EMarkOfEvent[] markOfEvents : mymarks) {
			for (int i = 0; i < markOfEvents.length; i++) {
				System.out.print(markOfEvents[i] + "\t");
			}
			System.out.println();
		}
			
	}
}

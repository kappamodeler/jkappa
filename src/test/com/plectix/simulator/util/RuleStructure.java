package com.plectix.simulator.util;

import java.util.*;

public class RuleStructure {
	private List<String> myLHS;
	private List<String> myRHS;
	
	public RuleStructure(List<String> lhs, List<String> rhs) {
		myLHS = lhs;
		myRHS = rhs;
	}
	
	public List<String> getLHS() {
		return myLHS;
	}
	
	public List<String> getRHS() {
		return myRHS;
	}
	
	@Override
	public boolean equals(Object a) {
		if (a == this) {
			return true;
		}
		
		if (!(a instanceof RuleStructure)) {
			return false;
		}
		
		RuleStructure aa = (RuleStructure)a;
		CollectionsComparator comparator = new CollectionsComparator() {
			@Override
			public boolean equals(Object a, Object b) {
				if (a != null) {
					return a.equals(b);
				} else {
					return b == null;
				}
				
			}
		};
		
		return comparator.areEqual(myLHS, aa.myLHS) && comparator.areEqual(myRHS, aa.myRHS);
	}
	
	@Override
	public String toString() {
		return myLHS.toString() + " -> " + myRHS.toString();
	}
}

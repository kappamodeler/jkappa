package com.plectix.simulator.components.complex.subviews;

import java.util.LinkedHashSet;

public class WrapperTwoSet {
	
	private LinkedHashSet<Integer> first;
	private LinkedHashSet<Integer> second;
	
	public WrapperTwoSet(){
		first = new LinkedHashSet<Integer>();
		second = new LinkedHashSet<Integer>();
	}
	public void setFirst(LinkedHashSet<Integer> first) {
		this.first = first;
	}
	public LinkedHashSet<Integer> getFirst() {
		return first;
	}
	public void setSecond(LinkedHashSet<Integer> second) {
		this.second = second;
	}
	public LinkedHashSet<Integer> getSecond() {
		return second;
	}
	public LinkedHashSet<Integer> toOneSet() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (first.isEmpty()&&second.isEmpty());
	}
	

}

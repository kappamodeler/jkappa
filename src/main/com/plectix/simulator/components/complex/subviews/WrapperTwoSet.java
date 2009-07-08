package com.plectix.simulator.components.complex.subviews;

import java.util.HashSet;

public class WrapperTwoSet {
	
	private HashSet<Integer> first;
	private HashSet<Integer> second;
	
	public WrapperTwoSet(){
		first = new HashSet<Integer>();
		second = new HashSet<Integer>();
	}
	public void setFirst(HashSet<Integer> first) {
		this.first = first;
	}
	public HashSet<Integer> getFirst() {
		return first;
	}
	public void setSecond(HashSet<Integer> second) {
		this.second = second;
	}
	public HashSet<Integer> getSecond() {
		return second;
	}
	public HashSet<Integer> toOneSet() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (first.isEmpty()&&second.isEmpty());
	}
	

}

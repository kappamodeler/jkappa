package com.plectix.simulator.parser.abstractmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO implement
public class AbstractStories implements IAbstractComponent {

	private List<String> myNames = new ArrayList<String>();
	
	public void addName(String name) {
		myNames.add(name);
	}

	public List<String> getStorifiedNames() {
		return myNames;
	}
	
	public String toString() {
		Collections.sort(myNames);
		StringBuffer sb = new StringBuffer();
		for (String name : myNames) {
			sb.append("%story: '" + name + "'\n");
		}
		return sb.toString();
	}
}

package com.plectix.simulator.staticanalysis;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.subviews.SubViewClass;

public class StaticAnalysisException extends Exception {

	public StaticAnalysisException(String string) {
		System.err.println(string);
		printStackTrace();
	}
	
	
	private static final String mess = "Unexpected current SubViews class and given agent. ";

	public StaticAnalysisException(SubViewClass subViewsClass, AbstractAgent agent) {
		super(mess + "SubViewsClass = " + subViewsClass.toString()
				+ " GivenAgent = " + agent.toString() + " sites:"
				+ agent.getSitesMap().toString());
	}

}

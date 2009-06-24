package com.plectix.simulator.components.complex.subviews.storage;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.subviews.CSubViewClass;

public class SubViewsExeption extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String mess = "Unexpected current SubViews class and given agent. ";

	public SubViewsExeption(CSubViewClass subViewsClass, CAbstractAgent agent) {
		super(mess + "SubViewsClass = " + subViewsClass.toString()
				+ " GivenAgent = " + agent.toString() + " sites:"
				+ agent.getSitesMap().toString());
	}
}

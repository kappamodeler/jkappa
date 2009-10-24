package com.plectix.simulator.staticanalysis.subviews.storage;

import com.plectix.simulator.staticanalysis.abstracting.AbstractAgent;
import com.plectix.simulator.staticanalysis.subviews.SubViewClass;

@SuppressWarnings("serial")
public final class SubViewsExeption extends Exception {
	private static final String mess = "Unexpected current SubViews class and given agent. ";

	public SubViewsExeption(SubViewClass subViewsClass, AbstractAgent agent) {
		super(mess + "SubViewsClass = " + subViewsClass.toString()
				+ " GivenAgent = " + agent.toString() + " sites:"
				+ agent.getSitesMap().toString());
	}
}

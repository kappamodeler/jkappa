package com.plectix.simulator.component.complex.subviews.storage;

import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.subviews.SubViewClass;

@SuppressWarnings("serial")
public final class SubViewsExeption extends Exception {
	private static final String mess = "Unexpected current SubViews class and given agent. ";

	public SubViewsExeption(SubViewClass subViewsClass, AbstractAgent agent) {
		super(mess + "SubViewsClass = " + subViewsClass.toString()
				+ " GivenAgent = " + agent.toString() + " sites:"
				+ agent.getSitesMap().toString());
	}
}

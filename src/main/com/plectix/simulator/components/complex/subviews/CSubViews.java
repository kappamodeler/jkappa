package com.plectix.simulator.components.complex.subviews;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;

public class CSubViews {
	private CSubViewClass subViewClass;
	private ISubViewsStorage storage;

	public CSubViews() {
		subViewClass = new CSubViewClass(-1);
	}
}

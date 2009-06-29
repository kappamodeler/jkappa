package com.plectix.simulator.components.bologna;

import java.util.*;

import com.plectix.simulator.components.injections.CInjection;

public class Reaction {
	private CInjection firstInjection;
	private CInjection secondInjection;
	private List<CInjection> list;
	
	public Reaction(CInjection firstInjection, CInjection secondInjection) {
		this.firstInjection = firstInjection;
		this.secondInjection = secondInjection;
	}
	
	public List<CInjection> getInjectionsList() {
		if (list == null) {
			list = new ArrayList<CInjection>();
			list.add(this.firstInjection);
			list.add(this.secondInjection);
		}
		return list;
	}
}

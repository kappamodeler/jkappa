package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.solution.SuperSubstance;

public interface IConnectedComponent extends ISolutionComponent {

	public String  getHash();

	public void setSuperSubstance(SuperSubstance superSubstance);

	public SuperSubstance getSubstance();

	public boolean isEmpty();
}

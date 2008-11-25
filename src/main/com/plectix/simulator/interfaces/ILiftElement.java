package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CConnectedComponent;

public interface ILiftElement {

	public CConnectedComponent getConnectedComponent();

	public IInjection getInjection();

}

package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CObservables;

public interface IObservablesComponent {

	public final static byte TYPE_CONNECTED_COMPONENT = 0;
	public final static byte TYPE_RULE_COMPONENT = 1;

	public void calculate();

	public String getName();

	public String getLine();

	public int getNameID();

	public byte getType();

	public double getSize();
	
	public String getItem(int index,CObservables obs);
	
}

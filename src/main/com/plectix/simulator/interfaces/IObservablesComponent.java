package com.plectix.simulator.interfaces;

import com.plectix.simulator.components.CObservables;

public interface IObservablesComponent {
//	public final static byte TYPE_CONNECTED_COMPONENT = 0;
//	public final static byte TYPE_RULE_COMPONENT = 1;

	public final static boolean CALCULATE_WITH_REPLASE_LAST = true;
	public final static boolean CALCULATE_WITH_NOT_REPLASE_LAST = false;

	public void calculate(boolean replaceLast);

	public String getName();

	public String getLine();

	public int getNameID();

//	public byte getType();

	public double getSize();

	public String getItem(int index, CObservables obs);

	public long getValue(int index, CObservables obs);

	public void updateLastValue();
	
	public int getMainAutomorphismNumber();

	public void setMainAutomorphismNumber(int index);

	public void addAutomorphicObservables(int index);
	
	public boolean isUnique();
}

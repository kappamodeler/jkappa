//package com.plectix.simulator.parser.abstractmodel;
//
//import java.util.List;
//
//import com.plectix.simulator.interfaces.IConnectedComponent;
//
//public class AbstractPerturbationRule extends AbstractRule {
//	private int myCount;
//	private boolean inf = false;
//	
//	public AbstractPerturbationRule(List<IConnectedComponent> left,
//			List<IConnectedComponent> right, String name, double ruleRate,
//			int ruleID, boolean isStorify) {
//		super(left, right, name, ruleRate, ruleID, isStorify);
//	}
//	
//	public void setCount(double countToFile) {
//		if (countToFile == Double.MAX_VALUE) {
//			inf = true;
//			myCount = -1;
//		} else {
//			myCount = (int) countToFile;
//		}
//	}
//
//}

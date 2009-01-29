//package com.plectix.simulator.parser.util;
//
//import java.util.List;
//
//import com.plectix.simulator.interfaces.IAgent;
//import com.plectix.simulator.interfaces.IConnectedComponent;
//import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
//import com.plectix.simulator.parser.abstractmodel.AbstractConnectedComponent;
//import com.plectix.simulator.parser.abstractmodel.AbstractRule;
//import com.plectix.simulator.simulator.SimulationUtils;
//
//public class BuildUtil {
//	public static final AbstractRule buildRule(List<AbstractAgent> left, List<IAgent> right,
//			String name, double activity, int ruleID, boolean isStorify) {
//		return new AbstractRule(SimulationUtils.buildConnectedComponents(left),
//				SimulationUtils.buildConnectedComponents(right), name, activity, ruleID,
//				isStorify);
//	}
//}

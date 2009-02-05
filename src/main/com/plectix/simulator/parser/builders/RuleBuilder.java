package com.plectix.simulator.parser.builders;

import java.util.List;

import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;

public class RuleBuilder {
	private final SubstanceBuilder mySubstanceBuilder;
	private final SimulationData myData;
	
	public RuleBuilder(SimulationData data) {
		myData = data;
		mySubstanceBuilder = new SubstanceBuilder(data);
	}
	
	public IRule build(AbstractRule rule) {
		String name = rule.getName();
		List<AbstractAgent> lhs = rule.getLHS();
		List<AbstractAgent> rhs = rule.getRHS();
		double rate = rule.getRate();
		int id = rule.getID();
		boolean isStorify = rule.isStorify();
		
		List<IAgent> lhsAgents = mySubstanceBuilder.buildAgents(lhs);
		List<IAgent> rhsAgents = mySubstanceBuilder.buildAgents(rhs);
//		List<IConnectedComponent> lhsCCList = SimulationUtils.buildConnectedComponents(lhsAgents);
//		List<IConnectedComponent> rhsCCList = SimulationUtils.buildConnectedComponents(rhsAgents);
//		CRule newRule = new CRule(lhsCCList, rhsCCList, name, rate, id, isStorify);
		IRule newRule = SimulationUtils.buildRule(lhsAgents, rhsAgents, name, rate, id, isStorify);
		myData.generateNextRuleId();
//		String one = ruleToString(rule);
//		String two = ruleToString(newRule);
//		if	(!one.equals(two)) { 
//			System.out.println("OLD - - - " + one);
//			System.out.println("New - - - " + two);
//		}
		return newRule;
	}
	
//	public String handSideToString(List<AbstractAgent> list) {
//		List<IAgent> agents = mySubstanceBuilder.buildAgents(list);
//		StringBuffer sb = new StringBuffer();
//		boolean first = true;
//		if (agents == null) {
//			return "";
//		}
//		for (IAgent cc : agents) {
//			if (!first) {
//				sb.append(", ");
//			} else {
//				first = false;
//			}
//			sb.append(Converter.toString(cc));
//		}
//		return sb.toString();
//	}
//	
//	private String handToString(List<IConnectedComponent> list) {
//		StringBuffer sb = new StringBuffer();
//		boolean first = true;
//		if (list == null) {
//			return "";
//		}
//		for (IConnectedComponent cc : list) {
//			if (!first) {
//				sb.append(", ");
//			} else {
//				first = false;
//			}
//			sb.append(Converter.toString(cc));
//		}
//		return sb.toString();
//	}
//	
//	public String ruleToString(IRule rule) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(handToString(rule.getLeftHandSide()));
//		sb.append(" -> ");
//		sb.append(handToString(rule.getRightHandSide()));
//		sb.append(" @ ");
//		sb.append(rule.getRuleRate());
//		return sb.toString();
//	}
//
//	public String ruleToString(AbstractRule rule) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(handSideToString(rule.getLHS()));
//		sb.append(" -> ");
//		sb.append(handSideToString(rule.getRHS()));
////		sb.append(" (");
////		sb.append(")");
//		sb.append(" @ ");
//		sb.append(rule.getRate());
//		return sb.toString();
//	}
}

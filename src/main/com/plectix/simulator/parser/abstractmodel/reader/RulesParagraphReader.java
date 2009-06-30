package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.*;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractRule;
import com.plectix.simulator.parser.abstractmodel.KappaModel;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.exceptions.ParseErrorMessage;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

public class RulesParagraphReader extends KappaParagraphReader<Collection<AbstractRule>>{

	private final SimulationArguments myArguments;
	
	public RulesParagraphReader(KappaModel model, SimulationArguments arguments,
			AgentFactory factory) {
		super(model, arguments, factory);
		myArguments = getArguments();
	}
	
	public Collection<AbstractRule> readComponent(
			KappaFileParagraph rulesParagraph) throws ParseErrorException,
			DocumentFormatException {
		List<AbstractRule> rules = new ArrayList<AbstractRule>();
		int ruleID = 0;
		boolean isStorify = myArguments.isStorify();
		
		for (KappaFileLine ruleLine : rulesParagraph.
				getLines()) {

			String rulesStr = ruleLine.getLine();
			double activity = 1.;
			double activity2 = 1.;

			rulesStr = rulesStr.trim();
			String name = null;
			if (rulesStr.indexOf("'") != -1) {
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1);
				if (rulesStr.indexOf("'") == -1)
					throw new ParseErrorException(ruleLine,
							ParseErrorMessage.UNEXPECTED_RULE_NAME, rulesStr);
				name = rulesStr.substring(0, rulesStr.indexOf("'")).trim();
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1,
						rulesStr.length()).trim();

			}
			double binaryRate = -1;
			int index = rulesStr.lastIndexOf("@");
			if (index != -1) {
				try {
					String activStr = rulesStr.substring(index + 1).trim();
					binaryRate = extractBinaryRate(activStr);
					int bracketIndex = activStr.indexOf("(");
					if (bracketIndex != -1) {
						activStr = activStr.substring(0, activStr.indexOf("(")).trim();
					}
//					System.out.println(activStr);
//					System.out.println(binaryRate);
					String inf = new String(new Double(Double.POSITIVE_INFINITY)
							.toString());
					activStr = activStr.replaceAll("\\$INF", inf);
					if (activStr.indexOf(",") != -1) {
						activity = Double.valueOf(activStr.substring(0,
								activStr.indexOf(",")));
						activity2 = Double.valueOf(activStr.substring(activStr.indexOf(",") + 1));
					} else {
						activity = Double.valueOf(activStr);
					}
				} catch (Exception e) {
					String details = rulesStr.substring(index).trim();
					e.printStackTrace();
					System.exit(0);
					throw new ParseErrorException(ruleLine,
							ParseErrorMessage.UNEXPECTED_RULE_RATE, details);
				}
				rulesStr = rulesStr.substring(0, index).trim();
			}

			index = -1;
			final byte RULE_TWO_WAY = 1;
			byte typeRule = 0;
			if (rulesStr.indexOf("<->") != -1) {
				typeRule = RULE_TWO_WAY;
				rulesStr = rulesStr.replace("<", "");
				activity2 = myArguments.isForwardOnly() ? 0. : activity2;
			}

			rulesStr = rulesStr.trim();
			int y = rulesStr.indexOf("->");
			if (y == -1) {
				throw new ParseErrorException(ruleLine, ParseErrorMessage.ARROW_EXPECTED);
			}
			
			final byte CC_RHS = 0;
			final byte CC_LHS = 1;
			// TODO CC_ALL is never copied
			final byte CC_ALL = -1;

			if (y == 0) {
				index = CC_RHS;
			}
			if (y == rulesStr.length() - 2) {
				if (index == -1) {
					index = CC_LHS;
				} else {
					throw new ParseErrorException(ruleLine, ParseErrorMessage.ARROW_EXPECTED,
							ruleLine.getLine());
				}
			}

			String[] result = rulesStr.split("\\->");

			String lhs = result[0];
			String rhs;

			if (result.length < 2) {
				rhs = "";
			} else {
				rhs = result[1];
			}

			List<AbstractAgent> left = null;
			List<AbstractAgent> right = null;
			String nameOp = null;
			
			
			if (name != null)
				nameOp = name + "_op";
			try {
				switch (index) {
				case CC_LHS: {
					left = parseAgent(lhs.trim());
					rules.add(new AbstractRule(left, right, name, activity, binaryRate, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						 rules.add(new AbstractRule(right,
						 parseAgent(lhs.trim()), nameOp, activity2, binaryRate, ruleID,
						 isStorify));
					}
					break;
				}
				case CC_RHS: {
					right = parseAgent(rhs.trim());
					rules.add(new AbstractRule(left, right, name,
							 activity, binaryRate, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(new AbstractRule(parseAgent(rhs.trim()), 
								left, name, activity2, binaryRate, ruleID, isStorify));
					}
					break;
				}
				case CC_ALL: {
					left = parseAgent(lhs.trim());
					right = parseAgent(rhs.trim());
					rules.add(new AbstractRule(left, right, name,
							 activity, binaryRate, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(new AbstractRule(parseAgent(rhs.trim()), 
								parseAgent(lhs.trim()), nameOp, activity2, binaryRate, 
								ruleID, isStorify));
					}
					break;
				}
				}
			} catch (ParseErrorException e) {
				e.setLineDescription(ruleLine);
				throw e;
			}
			ruleID++;

		}
		return rules;
	}

	/**
	 * this method modifies it's parameter by replacing binary rate part if there's one!
	 * @param activStr
	 * @return binary rate as double
	 */
	private double extractBinaryRate(String activStr) {
		int indexOpen = activStr.lastIndexOf("(");
		if (indexOpen != -1) {
			activStr = activStr.substring(indexOpen + 1);
			int indexClose = activStr.indexOf(")");
			activStr = activStr.substring(0, indexClose).trim();
			try {
				return Double.parseDouble(activStr);
			} catch(NumberFormatException e) {
				return -1;
			}
		} else {
			return -1;
		}
	}
}

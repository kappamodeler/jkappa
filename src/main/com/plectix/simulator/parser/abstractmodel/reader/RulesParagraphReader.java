package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.SimulationArguments;

public final class RulesParagraphReader extends KappaParagraphReader<Collection<ModelRule>>{
	private final SimulationArguments simulationArguments;
	
	public RulesParagraphReader(SimulationArguments simulationArguments, AgentFactory agentFactory) {
		super(simulationArguments, agentFactory);
		this.simulationArguments = getArguments();
	}
	
	@Override
	public final Collection<ModelRule> readComponent(
			KappaFileParagraph rulesParagraph) throws ParseErrorException,
			DocumentFormatException {
		List<ModelRule> rules = new ArrayList<ModelRule>();
		int ruleID = 0;
		boolean isStorify = simulationArguments.needToStorify();
		
		for (KappaFileLine ruleLine : rulesParagraph.
				getLines()) {

			ruleID = fullRule(rules, ruleID, isStorify, ruleLine);

		}
		return rules;
	}

	public int fullRule(List<ModelRule> rules, int ruleID,
			boolean isStorify, KappaFileLine ruleLine)
			throws ParseErrorException, DocumentFormatException,
			IncompletesDisabledException {
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
		double binaryRateOpposite = -1;
		int index = rulesStr.lastIndexOf("@");
		if (index != -1) {
			try {
				String activStr = rulesStr.substring(index + 1).trim();
				String inf = new String(new Double(Double.POSITIVE_INFINITY)
						.toString());
				activStr = activStr.replaceAll("\\$INF", inf);
				if (activStr.indexOf(",") != -1) {
					String[] activities = activStr.split(",");
					
					// reading first part
					int bracketIndex = activities[0].indexOf("(");
					if (bracketIndex != -1) {
						activity = readRateInBrackets(activities[0]);
						activStr = activStr.substring(0, activities[0].indexOf("(")).trim();
						binaryRate = Double.valueOf(activStr);
					} else {
						activity = Double.valueOf(activities[0]);
					}
					
					bracketIndex = activities[1].indexOf("(");
					if (bracketIndex != -1) {
						activity2 = readRateInBrackets(activities[1]);
						activStr = activStr.substring(0, activities[1].indexOf("(")).trim();
						binaryRateOpposite = Double.valueOf(activStr);
					} else {
						activity2 = Double.valueOf(activities[1]);
					}
					
//						activity = Double.valueOf(activities[0]);
//						activity2 = Double.valueOf(activStr.substring(activStr.indexOf(",") + 1));
				} else {
					int bracketIndex = activStr.indexOf("(");
					if (bracketIndex != -1) {
						activity = readRateInBrackets(activStr);
						activStr = activStr.substring(0, activStr.indexOf("(")).trim();
						binaryRate = Double.valueOf(activStr);
					} else {
						activity = Double.valueOf(activStr);
					}
				}
			} catch (Exception e) {
				String details = rulesStr.substring(index).trim();
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
			activity2 = simulationArguments.isForwardOnly() ? 0. : activity2;
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

		List<ModelAgent> left = null;
		List<ModelAgent> right = null;
		String nameOp = null;
		
		
		if (name != null)
			nameOp = name + "_op";
		try {
			switch (index) {
			case CC_LHS: {
				left = parseAgents(lhs.trim());
				rules.add(new ModelRule(left, right, name, activity, binaryRate, ruleID, isStorify));
				if (typeRule == RULE_TWO_WAY) {
					ruleID++;
					 rules.add(new ModelRule(right, parseAgents(lhs.trim()), 
							 nameOp, activity2, binaryRateOpposite, ruleID,
					 isStorify));
				}
				break;
			}
			case CC_RHS: {
				right = parseAgents(rhs.trim());
				rules.add(new ModelRule(left, right, name,
						 activity, binaryRate, ruleID, isStorify));
				if (typeRule == RULE_TWO_WAY) {
					ruleID++;
					rules.add(new ModelRule(parseAgents(rhs.trim()), 
							left, name, activity2, binaryRateOpposite, ruleID, isStorify));
				}
				break;
			}
			case CC_ALL: {
				left = parseAgents(lhs.trim());
				right = parseAgents(rhs.trim());
				rules.add(new ModelRule(left, right, name,
						 activity, binaryRate, ruleID, isStorify));
				if (typeRule == RULE_TWO_WAY) {
					ruleID++;
					rules.add(new ModelRule(parseAgents(rhs.trim()), 
							parseAgents(lhs.trim()), nameOp, activity2, binaryRateOpposite, 
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
		return ruleID;
	}

	/**
	 * this method modifies it's parameter by replacing binary rate part if there's one!
	 * @param line
	 * @return binary rate as double
	 */
	private final double readRateInBrackets(String line) {
		int indexOpen = line.lastIndexOf("(");
		if (indexOpen != -1) {
			line = line.substring(indexOpen + 1);
			int indexClose = line.indexOf(")");
			line = line.substring(0, indexClose).trim();
			try {
				return Double.parseDouble(line);
			} catch(NumberFormatException e) {
				return -1;
			}
		} else {
			return -1;
		}
	}
}

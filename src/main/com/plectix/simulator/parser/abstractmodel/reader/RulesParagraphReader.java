package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.*;

import com.plectix.simulator.components.ConstraintData;
import com.plectix.simulator.components.ConstraintExpression;
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
import com.plectix.simulator.simulator.ThreadLocalData;

public class RulesParagraphReader extends KappaParagraphReader<Collection<AbstractRule>>{

	private final static byte RULE_TWO_WAY = 1;

	private static final byte CC_RHS = 0;
	private static final byte CC_LHS = 1;
	private static final byte CC_ALL = -1;

	private static final String CONSTRAINT_PATTERN_ITEM = "((\\$)+[0-9]+)+";
	private static final String CONSTRAINT_PATTERN_EXPRESSION = "(("
			+ CONSTRAINT_PATTERN_ITEM + "\\=\\=" + CONSTRAINT_PATTERN_ITEM
			+ ")|(" + CONSTRAINT_PATTERN_ITEM + "\\<\\>"
			+ CONSTRAINT_PATTERN_ITEM + ")|(" + CONSTRAINT_PATTERN_ITEM
			+ "\\/\\/([0-9[a-zA-Z]]+[0-9[a-zA-Z]*\\_\\^\\-]*)+))+";

	private static final String CONSTRAINT_EXPRESSION_EQUALS = "==";
	private static final String CONSTRAINT_EXPRESSION_NOT_EQUALS = "<>";
	private static final String CONSTRAINT_EXPRESSION_ID = "//";
	
	private final static String NO_POLY = "NO_POLY";
	private final static String NO_HELIX = "NO_HELIX";
	
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
			ConstraintData constraintLeftToRight = null;
			ConstraintData constraintRightToLeft = null;
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
			int index = rulesStr.lastIndexOf("@");
			if (index != -1) {
				try {
					String activStr = rulesStr.substring(index + 1).trim();
					String inf = new String(new Double(Double.POSITIVE_INFINITY)
							.toString());
					activStr = activStr.replaceAll("\\$INF", inf);
					if (activStr.indexOf(",") != -1) {
						constraintLeftToRight = parseConstraint(activStr
								.substring(0, activStr.indexOf(",")));
//						activity = constraintLeftToRight.getActivity();
						 activity = Double.valueOf(activStr.substring(0,
						 activStr.indexOf(",")));
						constraintRightToLeft = parseConstraint(activStr
								.substring(activStr.indexOf(",") + 1));
//						activity2 = constraintRightToLeft.getActivity();
						 activity2 =
						 Double.valueOf(activStr.substring(activStr
						 .indexOf(",") + 1));
					} else {
//						constraintLeftToRight = parseConstraint(activStr);
//						activity = constraintLeftToRight.getActivity();
						 activity = Double.valueOf(activStr);
					}
				} catch (Exception e) {
					String details = rulesStr.substring(index).trim();
					throw new ParseErrorException(ruleLine,
							ParseErrorMessage.UNEXPECTED_RULE_RATE, details);
				}
				rulesStr = rulesStr.substring(0, index).trim();
			}
			if (constraintLeftToRight == null)
				constraintLeftToRight = new ConstraintData(activity);
			if (constraintRightToLeft == null)
				constraintRightToLeft = new ConstraintData(activity2);

			rulesStr = checkConstraintsInRule(ruleLine, rulesStr, rulesStr
					.indexOf("->"), constraintLeftToRight,
					constraintRightToLeft);
			rulesStr = checkConstraintsInRule(ruleLine, rulesStr, rulesStr
					.indexOf("->"), constraintLeftToRight,
					constraintRightToLeft);

			index = -1;
			byte typeRule = 0;
			if (rulesStr.indexOf("<->") != -1) {
				typeRule = RULE_TWO_WAY;
				rulesStr = rulesStr.replace("<", "");
				activity2 = myArguments.isForwardOnly() ? 0. : activity2;
				constraintRightToLeft.setActivity(activity2);
			}

			rulesStr = rulesStr.trim();
			int y = rulesStr.indexOf("->");
			if (y == -1) {
				throw new ParseErrorException(ruleLine, ParseErrorMessage.ARROW_EXPECTED);
			}
			
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
					rules.add(new AbstractRule(left, right, name, activity, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						 rules.add(new AbstractRule(right,
						 parseAgent(lhs.trim()), nameOp, activity2, ruleID,
						 isStorify));
					}
					break;
				}
				case CC_RHS: {
					right = parseAgent(rhs.trim());
					rules.add(new AbstractRule(left, right, name,
							 activity, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(new AbstractRule(parseAgent(rhs.trim()), 
								left, name, activity2, ruleID, isStorify));
					}
					break;
				}
				case CC_ALL: {
					left = parseAgent(lhs.trim());
					right = parseAgent(rhs.trim());
					rules.add(new AbstractRule(left, right, name,
							 activity, ruleID, isStorify));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(new AbstractRule(parseAgent(rhs.trim()), 
								parseAgent(lhs.trim()), nameOp, activity2, ruleID, isStorify));
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
	
	//TODO such a shame! :-P redo!! .. orevenremove 
	private String checkConstraintsInRule(KappaFileLine rulesDS, String rulesStr,
			int index, ConstraintData constraintLeftToRight,
			ConstraintData constraintRightToLeft) throws ParseErrorException {
		if (index == -1)
			throw new ParseErrorException(rulesDS, ParseErrorMessage.ARROW_EXPECTED,
					rulesDS.getLine());
		int indexConstraintStart = rulesStr.indexOf("{");
		int indexConstraintEnd = rulesStr.indexOf("}");

		if (indexConstraintEnd == -1 && indexConstraintStart == -1)
			return rulesStr;
		if (indexConstraintEnd == -1 || indexConstraintStart == -1)
			throw new ParseErrorException(rulesDS, ParseErrorMessage.UNEXPECTED_LINE,
					rulesDS.getLine());

		String st = rulesStr.substring(indexConstraintStart,
				indexConstraintEnd + 1);
		if (st.length() == 2)
			throw new ParseErrorException(rulesDS, ParseErrorMessage.UNEXPECTED_LINE,
					rulesDS.getLine());

		// System.out.println(st);
		rulesStr = rulesStr.substring(0, indexConstraintStart)
				+ rulesStr.substring(indexConstraintEnd + 1);

		st = st.substring(1, st.length() - 1);

		st = st.trim().replaceAll("[ 	]", "");

		ConstraintData constraintData;
		if (indexConstraintEnd < index)
			constraintData = constraintLeftToRight;
		else
			constraintData = constraintRightToLeft;
		StringTokenizer line = new StringTokenizer(st, ",");
		String constr;
		while (line.hasMoreTokens()) {
			constr = line.nextToken().trim();
			if (!constr.matches(CONSTRAINT_PATTERN_EXPRESSION))
				throw new ParseErrorException(rulesDS, ParseErrorMessage.UNEXPECTED_LINE,
						rulesDS.getLine());
			byte type = -1;
			long idLeft = -1;
			long idRight = -1;
			constr = constr.replaceAll("\\$", "");
			try {
				if (constr.contains(CONSTRAINT_EXPRESSION_EQUALS)) {
					type = ConstraintExpression.TYPE_CONSTRAINT_EXPRESSION_EQUALS;
					int i = constr.indexOf(CONSTRAINT_EXPRESSION_EQUALS);
					idLeft = Integer.valueOf(constr.substring(0, i));
					idRight = Integer.valueOf(constr.substring(i).replace(CONSTRAINT_EXPRESSION_EQUALS, ""));
				}
				if (constr.contains(CONSTRAINT_EXPRESSION_NOT_EQUALS)) {
					type = ConstraintExpression.TYPE_CONSTRAINT_EXPRESSION_NOT_EQUALS;
					int i = constr.indexOf(CONSTRAINT_EXPRESSION_NOT_EQUALS);
					idLeft = Integer.valueOf(constr.substring(0, i));
					idRight = Integer.valueOf(constr.substring(i).replace(CONSTRAINT_EXPRESSION_NOT_EQUALS, ""));
				}
				if (constr.contains(CONSTRAINT_EXPRESSION_ID)) {
					type = ConstraintExpression.TYPE_CONSTRAINT_EXPRESSION_ID;
					int i = constr.indexOf(CONSTRAINT_EXPRESSION_ID);
					idLeft = Integer.valueOf(constr.substring(0, i));
					String agentStr = constr.substring(i).replace(CONSTRAINT_EXPRESSION_ID, "");
					idRight = ThreadLocalData.getNameDictionary().addName(agentStr);
					//idRight = Integer.valueOf(constr.substring(i).replace(CONSTRAINT_EXPRESSION_ID, ""));
				}
			} catch (Exception e) {
				throw new ParseErrorException(rulesDS, ParseErrorMessage.UNEXPECTED_LINE,
						rulesDS.getLine());
			}
			constraintData.addExpression(new ConstraintExpression(type, idLeft,
					idRight));
		}

		return rulesStr;
	}

	private ConstraintData parseConstraint(String activStr) throws Exception {
		byte activityType = ConstraintData.TYPE_CONSTRAINT_NORMAL;
		double activity = 1.;
		double activityConstraint = 1.;
		if (activStr.indexOf(NO_POLY) != -1 && activStr.indexOf("[") != -1
				&& activStr.indexOf("]") != -1) {
			activityType = ConstraintData.TYPE_CONSTRAINT_NO_POLY;
			String constr = activStr.substring(activStr.indexOf("["), activStr
					.indexOf("]") + 1);
			activStr = activStr.replace(constr, "");
		}
		if (activityType == ConstraintData.TYPE_CONSTRAINT_NORMAL
				&& activStr.indexOf(NO_HELIX) != -1) {
			activityType = ConstraintData.TYPE_CONSTRAINT_NO_HELIX;
			String constr = activStr.substring(activStr.indexOf("["), activStr
					.indexOf("]") + 1);
			activStr = activStr.replace(constr, "");
		}
		if (activStr.indexOf("(") != -1 && activStr.indexOf(")") != -1) {
			String constr = activStr.substring(activStr.indexOf("("), activStr
					.indexOf(")") + 1);
			activStr = activStr.replace(constr, "");
			constr = constr.substring(1, constr.length() - 1);
			activityConstraint = Double.valueOf(constr);
		} else {
			activityConstraint = -1;
		}
		activity = Double.valueOf(activStr);
		ConstraintData ct = new ConstraintData(activityType, activity,
				activityConstraint);
		// ct.setActivity(activity);
		// ct.setActivityConstraint(activityConstraint);
		// ct.setType(activityType);
		return ct;
	}
}

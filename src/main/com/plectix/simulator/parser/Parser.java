package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CDataString;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRulePerturbation;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.RateExpression;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.interfaces.IRule;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.simulator.SimulationArguments.SimulationType;
import com.plectix.simulator.util.Info;

public class Parser {

	private final static String SYMBOL_STATE = "~";
	private final static int KEY_STATE = 1;
	private final static String SYMBOL_CONNECTED = "!_";
	private final static String SYMBOL_CONNECTED_TRUE_VALUE = "_";
	private final static int KEY_CONNECTED_TRUE = 2;
	private final static String SYMBOL_CONNECTED_MAY_BE = "?";
	private final static int KEY__MAY_BE = 3;
	private final static String SYMBOL_CONNECT = "!";
	private final static int KEY_CONNECT = 4;

	private final static byte RULE_TWO_WAY = 1;

	private static final byte CC_RHS = 0;
	private static final byte CC_LHS = 1;
	private static final byte CC_ALL = -1;

	private static final byte CREATE_INIT = 0;
	private static final byte CREATE_OBS = 1;
	private static final byte CREATE_STORY = 2;

	private static final String PATTERN_AGENT_SITE = "^[0-9[a-zA-Z]]+[0-9[a-zA-Z]\\_\\^\\-]*";
	private static final String PATTERN_STATE = "^[0-9[a-zA-Z]]+";

	private static final String PATTERN_LINE_AGENT_SITE = "([0-9[a-zA-Z]]+[0-9[a-zA-Z]*\\_\\^\\-]*)";
	private static final String PATTERN_LINE_STATE = "([0-9[a-zA-Z]]+)";
	private static final String PATTERN_LINE_CONNECTED = "((!_)|(![0-9]+)|(\\?))*";
	private static final String PATTERN_LINE_SITE_STATE = "(("
			+ PATTERN_LINE_AGENT_SITE + PATTERN_LINE_CONNECTED + ")+|("
			+ PATTERN_LINE_AGENT_SITE + "(~)" + PATTERN_LINE_STATE
			+ PATTERN_LINE_CONNECTED + ")+)";

	private static final String PATTERN_LINE_AGENT = "("
			+ PATTERN_LINE_AGENT_SITE + "(\\()(" + PATTERN_LINE_SITE_STATE
			+ "*|(" + PATTERN_LINE_SITE_STATE + "((\\,)"
			+ PATTERN_LINE_SITE_STATE + ")*)*)" + "(\\))" + ")";

	public static final String PATTERN_LINE = "(" + PATTERN_LINE_AGENT
			+ "((\\,)" + PATTERN_LINE_AGENT + ")*)";

	private DataReading data;

	private double perturbationRate;
	private boolean isForwarding;
	private final SimulationData simulationData;

	private class DataString {
		private String st1 = null;
		private String st2 = null;

		public String getSt1() {
			return st1;
		}

		public String getSt2() {
			return st2;
		}

		public void setSt1(String st1) {
			this.st1 = st1;
		}

		public void setSt2(String st2) {
			this.st2 = st2;
		}

		DataString(String st1) {
			this.st1 = st1;
		}

	}

	public Parser(DataReading data, SimulationData simulationData) {
		this.data = data;
		this.simulationData = simulationData;
	}

	public final void parse() throws ParseErrorException {
		simulationData.addInfo(Info.TYPE_INFO,"--Computing initial state");
		
		if (simulationData.isParseSolution())
			createSimData(data.getInits(), CREATE_INIT);
		List<IRule> rules = createRules(data.getRules());
		simulationData.setRules(rules);
		if ((simulationData.getStories() == null)
				&& (simulationData.getSimulationArguments().getSimulationType() == SimulationArguments.SimulationType.STORIFY)) {
			simulationData.setStories(new CStories(simulationData));
			createSimData(data.getStory(), CREATE_STORY);
		} else
			createSimData(data.getObservables(), CREATE_OBS);
		List<CPerturbation> perturbations = createPertubations(data.getMods());
		simulationData.setPerturbations(perturbations);
	}

	private final IObservablesComponent checkInObservables(String obsName)
			throws ParseErrorException {
		IObservablesComponent obsId = null;
		for (IObservablesComponent cc : simulationData.getObservables()
				.getComponentList()) {
			if ((cc.getName() != null) && (cc.getName().equals(obsName))) {
				obsId = cc;
				break;
			}
		}
		if (obsId != null) {
			return obsId;
		} else {
			throw new ParseErrorException("Rule '" + obsName
					+ "' must be in observables!");
		}
	}

	private final void parseExpressionMonomeBeforeDo(String arg,
			CDataString perturbationStr, List<Double> parameters,
			List<IObservablesComponent> obsID) throws ParseErrorException {

		String item = arg;
		if (item.indexOf("*") == -1) {
			try {
				parameters.add(new Double(Double.valueOf(item)));
			} catch (NumberFormatException e) {
				throw new ParseErrorException(perturbationStr,
						"Real number expected instead of '" + item + "'");
			}
		} else {
			double parameter = 0.0;
			String number = item.substring(0, item.indexOf("*")).trim();
			try {
				parameter = Double.valueOf(number);
			} catch (NumberFormatException e) {
				throw new ParseErrorException(perturbationStr,
						"Real number expected instead of '" + number + "'");
			}
			parameters.add(parameter);
			item = item.substring(item.indexOf("*") + 1).trim();

			checkString("[", item, perturbationStr);
			item = item.substring(item.indexOf("[") + 1).trim();
			checkString("'", item, perturbationStr);
			item = item.substring(item.indexOf("'") + 1).trim();

			String obsName = getName(item);

			IObservablesComponent obsId = checkInObservables(obsName);
			obsID.add(obsId);
		}
	}

	private final List<CPerturbation> createPertubations(List<CDataString> mods)
			throws ParseErrorException {
		List<CPerturbation> perturbations = new ArrayList<CPerturbation>();
		int pertubationID = 0;
		for (CDataString perturbationStr : mods) {
			try {
				String st = perturbationStr.getLine();
				st = st.trim();

				List<IPerturbationExpression> rateExpression = new ArrayList<IPerturbationExpression>();

				if (st.indexOf("$T") == 0) {
					boolean greater = getGreater(st, 2);
					st = st.substring(2).trim();
					st = st.substring(1).trim();

					int index = st.indexOf("do");
					String timeStr = "";
					if (index != -1) {
						timeStr = st.substring(0, index).trim();
					} else {
						throw new ParseErrorException(perturbationStr,
								"'do' expected : " + st);
					}
					double time = 0;

					try {
						time = Double.valueOf(timeStr);
					} catch (NumberFormatException e) {
						throw new ParseErrorException(perturbationStr,
								"Wrong 'time' modifier (real number expected) : "
										+ timeStr);
					}

					st = st.substring(index + 2).trim();

					this.perturbationRate = -1.;
					IRule rule = null;
					if (!checkOnce(st, perturbationStr, perturbations,
							pertubationID, time, greater)) {
						rule = getGreaterRule(st, perturbationStr,
								rateExpression);
						if (rule != null) {
							perturbations.add(new CPerturbation(
									pertubationID++, time,
									CPerturbation.TYPE_TIME, perturbationRate,
									rule, greater, rateExpression));
						}
					}
				} else {
					checkString("[", st, perturbationStr);

					st = st.substring(st.indexOf("[") + 1).trim();
					checkString("'", st, perturbationStr);
					st = st.substring(st.indexOf("'") + 1).trim();
					String obsName = getName(st);

					checkString("]", st, perturbationStr);
					st = st.substring(st.indexOf("]") + 1).trim();

					boolean greater = getGreater(st, 0);
					st = st.substring(1).trim();

					int obsNameID = checkInObservables(obsName).getNameID();

					checkString("do", st, perturbationStr);
					String pertStr = st.substring(st.indexOf("do") + 2);
					this.perturbationRate = -1.;
					IRule rule = getGreaterRule(pertStr, perturbationStr,
							rateExpression);

					st = st.substring(0, st.indexOf("do")).trim();

					List<Double> parameters = new ArrayList<Double>();
					List<IObservablesComponent> obsID = new ArrayList<IObservablesComponent>();
					if (st.indexOf("+") == -1) {
						parseExpressionMonomeBeforeDo(st, perturbationStr,
								parameters, obsID);
					} else {

						StringTokenizer sTok = new StringTokenizer(st, "+");
						while (sTok.hasMoreTokens()) {
							String item = sTok.nextToken().trim();
							parseExpressionMonomeBeforeDo(item,
									perturbationStr, parameters, obsID);
						}

					}

					if (rule != null) {

						CPerturbation pertubation = new CPerturbation(
								pertubationID++, obsID, parameters, obsNameID,
								CPerturbation.TYPE_NUMBER, perturbationRate,
								rule, greater, rateExpression, simulationData
										.getObservables());
						perturbations.add(pertubation);

					}
				}
			} catch (ParseErrorException e) {
				e.setLineDescription(perturbationStr);
				throw e;
			}

		}
		return perturbations;
	}

	private final boolean checkOnce(String st, CDataString perturbationStr,
			List<CPerturbation> perturbations, int pertubationID, double time,
			boolean greater) throws ParseErrorException {
		int indexAdd = st.indexOf("$ADDONCE");
		int indexDel = st.indexOf("$DELETEONCE");
		if (indexAdd == -1 && indexDel == -1)
			return false;
		if (indexAdd != -1 && indexDel != -1)
			throw new ParseErrorException(perturbationStr,
					"perturbation expected after 'do'");
		String line = new String(st);
		if (indexAdd != -1)
			line = line.substring(indexAdd + 8);
		else
			line = line.substring(indexDel + 11);

		int indexCount = line.indexOf("*");
		String strCount = line.substring(0, indexCount).trim();
		double countToFile;
		String inf = new String(new Double(Double.MAX_VALUE).toString());
		strCount = strCount.replaceAll("\\$INF", inf);
		try {
			countToFile = Double.valueOf(strCount);
		} catch (NumberFormatException e) {
			throw new ParseErrorException(perturbationStr,
					"Quantity must have numerical format: " + strCount);
		}
		line = line.substring(indexCount + 1);

		List<IAgent> agentList = parseAgent(line);
		List<IConnectedComponent> ccList = SimulationUtils.buildConnectedComponents(agentList);

		List<CRulePerturbation> ruleList = new ArrayList<CRulePerturbation>();
		// public CRulePerturbation(List<IConnectedComponent> left,
		// List<IConnectedComponent> right, String name, double ruleRate,
		// int ruleID, boolean isStorify) {
		// super(left, right, name, ruleRate, ruleID, isStorify);
		// }
		for (IConnectedComponent cc : ccList) {
			List<IConnectedComponent> ccL = new ArrayList<IConnectedComponent>();
			ccL.add(cc);
			CRulePerturbation rp;
			if (indexAdd != -1) {
				if (countToFile == Double.MAX_VALUE)
					throw new ParseErrorException(perturbationStr,
							"$ADDONCE has not used with $INF");
				rp = new CRulePerturbation(null, ccL, "", 0, ruleID++,
						simulationData.isStorify());
			} else {
				rp = new CRulePerturbation(ccL, null, "", 0, ruleID++,
						simulationData.isStorify());
			}
			ruleList.add(rp);

		}

		for (CRulePerturbation rule : ruleList) {
			rule.setCount(countToFile);
			simulationData.addRule(rule);

			CPerturbation perturbation = new CPerturbation(pertubationID, time,
					CPerturbation.TYPE_ONCE, rule, greater);
			perturbations.add(perturbation);
		}

		return true;
	}

	private final double parseExpressionMonomeAfterDo(String arg,
			CDataString perturbationStr,
			List<IPerturbationExpression> rateExpression)
			throws ParseErrorException {
		String item = arg;
		double freeTerm = 0;
		if (item.indexOf("*") == -1) {
			// free term as double
			try {
				freeTerm += Double.valueOf(item);
			} catch (NumberFormatException e) {

				checkString("'", item, perturbationStr);
				addFreeRule(rateExpression, item);
			}
		} else {
			double curValue = 0;
			try {
				curValue = Double.valueOf(item.substring(0, item.indexOf("*"))
						.trim());
			} catch (NumberFormatException e) {
				throw new ParseErrorException(perturbationStr,
						"Perturbations sum parse error: value parameter expected before rule name");
			}
			item = item.substring(item.indexOf("*") + 1).trim();

			checkString("'", item, perturbationStr);
			item = item.substring(item.indexOf("'") + 1).trim();

			IRule curRule = getRuleWithEqualName(getName(item));

			rateExpression.add(new RateExpression(curRule, curValue));
		}
		return freeTerm;
	}

	private final IRule getGreaterRule(String st, CDataString perturbationStr,
			List<IPerturbationExpression> rateExpression)
			throws ParseErrorException {
		boolean fail = false;
		if (st.length() > 0) {
			fail = Character.isLetter(st.charAt(0));
		} else {
			throw new ParseErrorException(perturbationStr,
					"perturbation expected after 'do'");
		}
		checkString("'", st, perturbationStr);
		st = st.substring(st.indexOf("'") + 1).trim();
		if (fail) {
			throw new ParseErrorException(perturbationStr,
					"'do' expected before [" + st + "]");
		}
		checkString("'", st, perturbationStr);
		String ruleName = st.substring(0, st.indexOf("'")).trim();

		// st = st.replace("[ 	]", "");
		int index = st.indexOf(":=");
		checkString(":=", st, perturbationStr);
		st = st.substring(index + 2);

		double freeTerm = 0;

		if (st.indexOf("+") == -1) {
			freeTerm += parseExpressionMonomeAfterDo(st, perturbationStr,
					rateExpression);
		} else {
			StringTokenizer sTok = new StringTokenizer(st, "+");
			while (sTok.hasMoreTokens()) {
				String item = sTok.nextToken().trim();
				freeTerm += parseExpressionMonomeAfterDo(item, perturbationStr,
						rateExpression);
			}
		}
		if (freeTerm > 0.0)
			rateExpression.add(new RateExpression(null, freeTerm));

		return getRuleWithEqualName(ruleName);
	}

	private final void addFreeRule(
			List<IPerturbationExpression> rateExpression, String item)
			throws ParseErrorException {
		item = item.substring(item.indexOf("'") + 1).trim();
		IRule curRule = getRuleWithEqualName(getName(item));
		if (curRule != null) {
			rateExpression.add(new RateExpression(curRule, 1.0));
		}
	}

	private final IRule getRuleWithEqualName(String ruleName)
			throws ParseErrorException {
		for (IRule rule : simulationData.getRules())
			if ((rule.getName() != null) && (rule.getName().equals(ruleName))) {
				return rule;
			}

		// TODO decide if error message is not necessary
		throw new ParseErrorException("No such rule : " + ruleName);
		// return null;
	}

	private final boolean getGreater(String st, int index)
			throws ParseErrorException {
		if (st.indexOf(">") == index) {
			return true;

		} else if (st.indexOf("<") == index) {
			return false;
		} else
			throw new ParseErrorException("Expected '>' or '<' (after '"
					+ st.substring(0, index) + "') in [" + st + "]");
	}

	private final String getName(String line) throws ParseErrorException {
		// Example: "abc']..."
		String name = null;
		int index = line.indexOf("'");
		if (index != -1) {
			name = line.substring(0, line.indexOf("'"));
		} else {
			throw new ParseErrorException("Rule name or number expected : "
					+ line);
		}
		return name;
	}

	private void checkString(String ch, String st, CDataString perturbationStr)
			throws ParseErrorException {
		int index = st.indexOf(ch);
		if (index == -1) {
			String quote = "'";
			if ("'".equals(ch)) {
				quote = "\"";
			}
			throw new ParseErrorException(perturbationStr, quote + ch + quote
					+ " expected : " + st + "");
		}
	}

	private int ruleID = 0;

	public final List<IRule> createRules(List<CDataString> list)
			throws ParseErrorException {

		List<IRule> rules = new ArrayList<IRule>();
		for (CDataString rulesDS : list) {

			String rulesStr = rulesDS.getLine();
			double activity = 1.;
			double activity2 = 1.;

			rulesStr = rulesStr.trim();
			String name = null;
			if (rulesStr.indexOf("'") != -1) {
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1);
				if (rulesStr.indexOf("'") == -1)
					throw new ParseErrorException(rulesDS,
							"Unexpected rule name (please use apostrophes) : "
									+ rulesStr);
				name = rulesStr.substring(0, rulesStr.indexOf("'")).trim();
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1,
						rulesStr.length()).trim();

			}
			int index = rulesStr.lastIndexOf("@");
			if (index != -1) {
				try {
					String activStr = rulesStr.substring(index + 1).trim();
					String inf = new String(new Double(Double.MAX_VALUE)
							.toString());
					activStr = activStr.replaceAll("\\$INF", inf);
					if (activStr.indexOf(",") != -1) {
						activity = Double.valueOf(activStr.substring(0,
								activStr.indexOf(",")));
						activity2 = Double.valueOf(activStr.substring(activStr
								.indexOf(",") + 1));
					} else
						activity = Double.valueOf(activStr);
				} catch (Exception e) {
					String details = rulesStr.substring(index).trim();
					throw new ParseErrorException(rulesDS,
							"Unexpected rule rate : " + details);
				}
				rulesStr = rulesStr.substring(0, index).trim();
			}

			index = -1;
			byte typeRule = 0;
			if (rulesStr.indexOf("<->") != -1) {
				typeRule = RULE_TWO_WAY;
				rulesStr = rulesStr.replace("<", "");
				activity2 = isForwarding() ? 0. : activity2;
			}

			rulesStr = rulesStr.trim();
			int y = rulesStr.indexOf("->");
			if (y == 0) {
				index = CC_RHS;
			}
			if (y == rulesStr.length() - 2) {
				if (index == -1) {
					index = CC_LHS;
				} else {
					throw new ParseErrorException(rulesDS, "Unexpected line : "
							+ rulesDS.getLine());
				}
			}

			if (!rulesStr.contains("->")) {
				throw new ParseErrorException(rulesDS,
						"Rule should have '->' as separator");
			}

			String[] result = rulesStr.split("\\->");

			String lhs = result[0];
			String rhs;

			if (result.length < 2) {
				rhs = "";
			} else {
				rhs = result[1];
			}

			List<IAgent> left = null;
			List<IAgent> right = null;
			String nameOp = null;
			if (name != null)
				nameOp = name + "_op";
			try {
				switch (index) {
				case CC_LHS: {
					left = parseAgent(lhs.trim());
					rules.add(SimulationUtils.buildRule(left, right, name, activity,
							ruleID, simulationData.isStorify()));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationUtils.buildRule(right, parseAgent(lhs
								.trim()), nameOp, activity2, ruleID,
								simulationData.isStorify()));
					}
					break;
				}
				case CC_RHS: {
					right = parseAgent(rhs.trim());
					rules.add(SimulationUtils.buildRule(left, right, name, activity,
							ruleID, simulationData.isStorify()));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationUtils.buildRule(parseAgent(rhs.trim()),
								left, nameOp, activity2, ruleID, simulationData
										.isStorify()));
					}
					break;
				}
				case CC_ALL: {
					left = parseAgent(lhs.trim());
					right = parseAgent(rhs.trim());
					rules.add(SimulationUtils.buildRule(left, right, name, activity,
							ruleID, simulationData.isStorify()));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationUtils.buildRule(parseAgent(rhs.trim()),
								parseAgent(lhs.trim()), nameOp, activity2,
								ruleID, simulationData.isStorify()));
					}
					break;
				}
				}
			} catch (ParseErrorException e) {
				e.setLineDescription(rulesDS);
				throw e;
			}
			ruleID++;

		}

		// return Collections.unmodifiableList(rules);
		return rules;
	}

	private final void createSimData(List<CDataString> list, byte code)
			throws ParseErrorException {
		long count;
		String line;
		String[] result;
		int obsNameID = 0;
		for (CDataString itemDS : list) {
			String item = itemDS.getLine();
			count = 1;
			result = item.split("\\*");
			int length = result.length;
			result[0] = result[0].trim();
			count = 1;
			double countInFile = 1;
			if (length != 1) {
				double rescale = simulationData.getSimulationArguments().getRescale();
				if (rescale < 0 || Double.isNaN(rescale)) {
					rescale = 1.;
				}

				try {
					countInFile = Double.valueOf(result[0]) * rescale;
				} catch (NumberFormatException e) {
					throw new ParseErrorException(itemDS,
							"Quantity must have numerical format: " + result[0]);
				}

				// if (countInFile - Math.floor(countInFile) < 1e-16)
				long round = Math.round(countInFile);
				if (Math.abs(countInFile - round) < 1e-12) {
					// count = (long) countInFile;
					count = round;
				} else {
					throw new ParseErrorException(itemDS,
							"Integer quantity expected, use '--rescale' option");
				}
			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without
			// parse "count" once "line"
			try {
				switch (code) {
				case CREATE_INIT: {
					if (countInFile > 0) {
						line = line.replaceAll("[ 	]", "");
						List<IAgent> listAgent = parseAgent(line);
						simulationData.getSolution().addAgents(listAgent);
						if (simulationData.getSimulationArguments().getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
							simulationData.getContactMap()
									.addAgentFromSolution(listAgent);
							simulationData.getContactMap().setSimulationData(simulationData);
						} else {
							for (int i = 1; i < count; i++) {
								simulationData.getSolution().addAgents(
										simulationData.getSolution().cloneAgentsList(listAgent, simulationData));
							}
						}
						if (simulationData.getSimulationArguments().getSimulationType() == SimulationArguments.SimulationType.COMPILE) {
							((CSolution) simulationData.getSolution())
									.checkSolutionLinesAndAdd(line, count);

						}
					}
					break;
				}
				case CREATE_STORY: {
					String name = null;
					if (line.indexOf("'") != -1) {
						line = line.substring(line.indexOf("'") + 1);
						name = line.substring(0, line.indexOf("'")).trim();
						line = line.substring(line.indexOf("'") + 1,
								line.length()).trim();
					}
					simulationData.addStories(name);
					break;
				}
				case CREATE_OBS: {
					String name = null;
					if (line.indexOf("'") != -1) {
						line = line.substring(line.indexOf("'") + 1);
						int index = line.indexOf("'");
						if (index != -1) {
							name = line.substring(0, index).trim();
							line = line.substring(index + 1, line.length())
									.trim();
						}
					}

					if (line.length() == 0) {
						simulationData.getObservables().addRulesName(name,
								obsNameID, simulationData.getRules());
					} else
						simulationData
								.getObservables()
								.addConnectedComponents(
										SimulationUtils.buildConnectedComponents(parseAgent(line)),
										name, line, obsNameID);
					obsNameID++;
					break;
				}

				}
			} catch (ParseErrorException e) {
				e.setLineDescription(itemDS);
				throw e;
			}
		}

	}

	private boolean testLine(String line) {
		while (line.indexOf("(") == 0) {
			line = line.substring(1);
			if (line.indexOf(")") == -1)
				return false;
			line = line.substring(0, line.length() - 1);
		}

		if (!line.matches(PATTERN_LINE)) {
			return false;
		}
		return true;
	}

	public final List<IAgent> parseAgent(String line)
			throws ParseErrorException {
		line = line.replaceAll("[ 	]", "");
		// if (!testLine(line))
		// throw new ParseErrorException();

		StringTokenizer st = new StringTokenizer(line, "),");
		Map<Integer, CSite> map = new HashMap<Integer, CSite>();
		StringTokenizer agent;
		String ccomp;
		String site;
		List<IAgent> listAgent = new ArrayList<IAgent>();
		CAgent cagent = null;
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				agent = new StringTokenizer(ccomp, "(");
				if (agent.countTokens() == 0)
					throw new ParseErrorException("Unexpected line : " + line);
				ccomp = agent.nextToken(); // Agent name.
				if (!ccomp.trim().matches(PATTERN_AGENT_SITE))
					throw new ParseErrorException("Unexpected agent name : "
							+ ccomp);

				cagent = new CAgent(ThreadLocalData.getNameDictionary().addName(ccomp), simulationData.generateNextAgentId());
				
				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSome(site, map)); // <-------Agent
				}
			} else {
				if (cagent == null)
					throw new ParseErrorException("Unexpected agent name : "
							+ ccomp);
				cagent.addSite(parseSome(ccomp, map)); // <------Agent
			}
		}
		if (!map.isEmpty()) {
			CSite errSite = (CSite) map.values().toArray()[0];
			throw new ParseErrorException("Unexpected Link State: '"
					+ errSite.getName() + "' from Agent: '"
					+ errSite.getAgentLink().getName() + "'");
		}
		if (!testLine(line))
			throw new ParseErrorException("Unexpected line : " + line);
		return listAgent;
		// return Collections.unmodifiableList(listAgent);
	}

	private final CSite parseSome(String site, Map<Integer, CSite> map)
			throws ParseErrorException {
		String line = site;
		String state = null;
		String connect = null;
		DataString dt = null;
		CSite csite = null;

		dt = parseLine(site, KEY_STATE);
		site = dt.getSt1();
		state = dt.getSt2();
		if (state != null) {
			dt = parseLine(state, KEY_CONNECT);
			state = dt.getSt1();
			if (!state.matches(PATTERN_LINE_SITE_STATE))
				throw new ParseErrorException(
						"Unexpected internal state name : " + line);
		} else {
			dt = parseLine(site, KEY_CONNECT);
			site = dt.getSt1();
		}
		connect = dt.getSt2();
		if (!site.trim().matches(PATTERN_AGENT_SITE))
			throw new ParseErrorException("Unexpected site name : " + line);

		final int siteNameId = ThreadLocalData.getNameDictionary().addName(site);
		csite = new CSite(siteNameId);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				final int nameId = ThreadLocalData.getNameDictionary().addName(state);
				csite.setInternalState(new CInternalState(nameId));
			} else {
				throw new ParseErrorException(
						"Unexpected internal state name : " + line);
			}

		if (connect != null)
			if (connect.length() == 0) {
				csite.getLinkState().setStatusLink(
						CLinkState.STATUS_LINK_WILDCARD);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				csite.getLinkState()
						.setStatusLink(CLinkState.STATUS_LINK_BOUND);
			} else {
				try {
					int index = Integer.valueOf(connect);
					CSite isite = map.get(index);
					if (isite != null) {
						isite.getLinkState().setSite(csite);
						csite.getLinkState().setSite(isite);

						isite.setLinkIndex(index);
						csite.setLinkIndex(index);
						map.remove(index);
					} else {
						map.put(index, csite);
					}
				} catch (Exception e) {
					throw new ParseErrorException("Unexpected link state : "
							+ line);
				}

			}
		if (csite == null)
			throw new ParseErrorException("Unexpected line : " + line);
		return csite;
	}

	private final DataString parseLine(String st, int key)
			throws ParseErrorException {
		String id = null;
		DataString ds = new DataString(st);
		int i = -1;
		switch (key) {
		case KEY_STATE: {
			id = SYMBOL_STATE;
			i = st.indexOf(id);
			break;
		}
		case KEY_CONNECT: {
			id = SYMBOL_CONNECT;
			i = st.indexOf(id);
		}
		case KEY_CONNECTED_TRUE: {
			if (i != -1) {
				id = SYMBOL_CONNECTED;
				i = st.indexOf(id);
				if (i == -1) {
					i = st.indexOf(SYMBOL_CONNECT);
					String test = new String(st);
					test = test.substring(i + 1);
					if (test.length() == 0)
						throw new ParseErrorException(
								"Unexpected link state : " + st);
				}
				break;
			}
		}
		case KEY__MAY_BE: {
			if (i == -1) {
				id = SYMBOL_CONNECTED_MAY_BE;
				i = st.indexOf(id);
			}
			break;
		}
		}

		if (i != -1) {
			String content = st.substring(i + 1).trim();
			st = st.substring(0, i).trim();
			ds.setSt1(st);
			ds.setSt2(content);
		}
		return ds;
	}

	public void setForwarding(boolean isForwarding) {
		this.isForwarding = isForwarding;
	}

	public boolean isForwarding() {
		return isForwarding;
	}

}

package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CDataString;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CPerturbation;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CSolution;
import com.plectix.simulator.components.CStories;
import com.plectix.simulator.components.RateExpression;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionInternalState;
import com.plectix.simulator.parser.Exeptions.ParseErrorExceptionLine;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionAgent;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionLinkState;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionRuleName;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionRuleRate;
import com.plectix.simulator.parser.Exeptions.ParseErrorExeptionSite;
import com.plectix.simulator.parser.Exeptions.ParseExeptionInternalState;
import com.plectix.simulator.parser.Exeptions.ParseErrorException;
import com.plectix.simulator.parser.Exeptions.ParserExceptionHandler;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.SimulationData;

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

	private ParserExceptionHandler myErrorHandler = new ParserExceptionHandler();

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

	public Parser(DataReading data) {
		this.data = data;
	}

	public Parser() {
	}

	public final void parse() throws ParseErrorException {
		createSimData(data.getInits(), CREATE_INIT);
		List<CRule> rules = createRules(data.getRules());
		SimulationMain.getSimulationManager().setRules(rules);
		if ((SimulationMain.getSimulationManager().getSimulationData()
				.getStories() == null)
				&& (SimulationMain.getSimulationManager().getSimulationData()
						.isStorify())) {
			SimulationMain.getSimulationManager().getSimulationData()
					.setStories(new CStories());
			createSimData(data.getStory(), CREATE_STORY);
		} else
			createSimData(data.getObservables(), CREATE_OBS);
		List<CPerturbation> perturbations = createPertubations(data.getMods());
		SimulationMain.getSimulationManager().getSimulationData()
				.setPerturbations(perturbations);
	}

	private final List<CPerturbation> createPertubations(List<CDataString> mods)
			throws ParseErrorException {
		List<CPerturbation> perturbations = new ArrayList<CPerturbation>();
		int pertubationID = 0;

		for (CDataString perturbationStr : mods) {
			String st = perturbationStr.getLine();
			st = st.trim();

			List<IPerturbationExpression> rateExpression = new ArrayList<IPerturbationExpression>();

			if (st.indexOf("$T") == 0) {
				st = st.substring(2).trim();
				boolean greater = getGreater(st, perturbationStr);
				st = st.substring(1).trim();

				int index = st.indexOf("do");
				String timeStr = st.substring(0, index).trim();
				double time = Double.valueOf(timeStr);
				st = st.substring(index + 2).trim();

				this.perturbationRate = -1.;
				CRule rule = getGreaterRule(st, perturbationStr, rateExpression);
				if (rule != null) {
					perturbations.add(new CPerturbation(pertubationID++, time,
							CPerturbation.TYPE_TIME, perturbationRate, rule,
							greater, rateExpression));
				} else
					throw new ParseErrorException(myErrorHandler
							.formMessage(perturbationStr));
			} else {
				checkString("[", st, perturbationStr);

				st = st.substring(st.indexOf("[") + 1).trim();
				checkString("'", st, perturbationStr);
				st = st.substring(st.indexOf("'") + 1).trim();
				String obsName = getName(st);

				checkString("]", st, perturbationStr);
				st = st.substring(st.indexOf("]") + 1).trim();

				boolean greater = getGreater(st, perturbationStr);
				st = st.substring(1).trim();

				int obsNameID = -1;
				for (IObservablesComponent cc : SimulationMain
						.getSimulationManager().getSimulationData()
						.getObservables().getComponentList()) {
					if ((cc.getName() != null)
							&& (cc.getName().equals(obsName))) {
						obsNameID = cc.getNameID();
						break;
					}
				}

				checkString("do", st, perturbationStr);
				String pertStr = st.substring(st.indexOf("do") + 2).trim();
				this.perturbationRate = -1.;
				CRule rule = getGreaterRule(pertStr, perturbationStr,
						rateExpression);

				st = st.substring(0, st.indexOf("do")).trim();

				List<Double> parameters = new ArrayList<Double>();
				List<IObservablesComponent> obsID = new ArrayList<IObservablesComponent>();
				if (st.indexOf("+") == -1) {
					parameters.add(new Double(Double.valueOf(st)));
				} else {

					StringTokenizer sTok = new StringTokenizer(st, "+");
					while (sTok.hasMoreTokens()) {
						String item = sTok.nextToken().trim();

						if (item.indexOf("*") == -1)
							parameters.add(new Double(Double.valueOf(item)));
						else {
							Double parameter = Double.valueOf(item.substring(0,
									item.indexOf("*")).trim());
							parameters
									.add(new Double(Double.valueOf(parameter)));
							item = item.substring(item.indexOf("*") + 1).trim();

							checkString("[", item, perturbationStr);
							item = item.substring(item.indexOf("[") + 1).trim();
							checkString("'", item, perturbationStr);
							item = item.substring(item.indexOf("'") + 1).trim();

							obsName = getName(item);

							IObservablesComponent obsId = null;
							for (IObservablesComponent cc : SimulationMain
									.getSimulationManager().getSimulationData()
									.getObservables().getComponentList()) {
								if ((cc.getName() != null)
										&& (cc.getName().equals(obsName))) {
									obsId = cc;
									break;
								}
							}
							if (obsId != null)
								obsID.add(obsId);
						}
					}

				}

				if (rule != null) {

					CPerturbation pertubation = new CPerturbation(
							pertubationID++, obsID, parameters, obsNameID,
							CPerturbation.TYPE_NUMBER, perturbationRate, rule,
							greater, rateExpression);
					perturbations.add(pertubation);

				}
			}

		}

		return perturbations;
	}

	private final CRule getGreaterRule(String st, CDataString perturbationStr,
			List<IPerturbationExpression> rateExpression)
			throws ParseErrorException {
		checkString("'", st, perturbationStr);
		st = st.substring(st.indexOf("'") + 1).trim();
		checkString("'", st, perturbationStr);
		String ruleName = st.substring(0, st.indexOf("'")).trim();

		// st = st.replace("[ 	]", "");
		int index = st.indexOf(":=");
		checkString(":=", st, perturbationStr);
		st = st.substring(index + 2);

		double freeTerm = 0;

		if (st.indexOf("+") == -1) {
			try {
				rateExpression
						.add(new RateExpression(null, Double.valueOf(st)));
			} catch (NumberFormatException e) {
				addFreeRule(rateExpression, st);
			}
		} else {
			StringTokenizer sTok = new StringTokenizer(st, "+");
			while (sTok.hasMoreTokens()) {
				String item = sTok.nextToken().trim();

				if (item.indexOf("*") == -1) {
					// free term as double
					try {
						freeTerm += Double.valueOf(item);
					} catch (NumberFormatException e) {

						checkString("'", item, perturbationStr);
						addFreeRule(rateExpression, item);
						/*
						 * item = item.substring(item.indexOf("'") + 1).trim();
						 * CRule curRule = getRuleWithEqualName(getName(item));
						 * if (curRule == null) return null;
						 * rateExpression.add(new RateExpression(curRule, 1.0));
						 */
					}
				} else {
					double curValue = 0;
					try {
						curValue = Double.valueOf(item.substring(0,
								item.indexOf("*")).trim());
					} catch (NumberFormatException e) {
						throw new ParseErrorException(
								"Perturbations sum parse error: value parameter needs to be before rule name");
					}
					item = item.substring(item.indexOf("*") + 1).trim();

					checkString("'", item, perturbationStr);
					item = item.substring(item.indexOf("'") + 1).trim();

					CRule curRule = getRuleWithEqualName(getName(item));

					if (curRule == null)
						return null;
					rateExpression.add(new RateExpression(curRule, curValue));
				}
			}
		}
		if (freeTerm > 0.0)
			rateExpression.add(new RateExpression(null, freeTerm));

		return getRuleWithEqualName(ruleName);
	}

	private final void addFreeRule(
			List<IPerturbationExpression> rateExpression, String item) {
		item = item.substring(item.indexOf("'") + 1).trim();
		CRule curRule = getRuleWithEqualName(getName(item));
		if (curRule != null)
			rateExpression.add(new RateExpression(curRule, 1.0));
	}

	private final CRule getRuleWithEqualName(String ruleName) {
		for (CRule rule : SimulationMain.getSimulationManager().getRules())
			if ((rule.getName() != null) && (rule.getName().equals(ruleName))) {
				return rule;
			}

		return null;
	}

	private final boolean getGreater(String st, CDataString perturbationStr)
			throws ParseErrorException {
		if (st.indexOf(">") == 0) {
			return true;

		} else if (st.indexOf("<") == 0) {
			return false;
		} else
			throw new ParseErrorException(myErrorHandler
					.formMessage(perturbationStr));
	}

	private final String getName(String line) {
		// Example: "abc']..."
		String name = null;
		name = line.substring(0, line.indexOf("'"));
		return name;
	}

	private void checkString(String ch, String st, CDataString perturbationStr)
			throws ParseErrorException {
		int index = st.indexOf(ch);
		if (index == -1)
			throw new ParseErrorException(myErrorHandler
					.formMessage(perturbationStr));
	}

	public final List<CRule> createRules(List<CDataString> list)
			throws ParseErrorException {

		List<CRule> rules = new ArrayList<CRule>();
		int ruleID = 0;
		for (CDataString rulesDS : list) {

			String rulesStr = rulesDS.getLine();
			double activity = 1.;
			double activity2 = 1.;
			String input = rulesStr;
			rulesStr = rulesStr.trim();
			String name = null;
			if (rulesStr.indexOf("'") != -1) {
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1);
				if (rulesStr.indexOf("'") == -1)
					throw new ParseErrorExeptionRuleName(myErrorHandler
							.formMessage(rulesDS), rulesStr);
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
					throw new ParseErrorExeptionRuleRate(myErrorHandler
							.formMessage(rulesDS), rulesStr.substring(index)
							.trim());
				}
				rulesStr = rulesStr.substring(0, index).trim();
			}

			index = -1;
			byte typeRule = 0;
			if (rulesStr.indexOf("<->") != -1) {
				typeRule = RULE_TWO_WAY;
				rulesStr = rulesStr.replace("<", "");
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
					throw new ParseErrorException(myErrorHandler
							.formMessage(rulesDS));
				}
			}

			String[] result = rulesStr.split("\\->");

			List<CAgent> left = null;
			List<CAgent> right = null;
			String nameOp = null;
			if (name != null)
				nameOp = name + "_op";
			try {
				switch (index) {
				case CC_LHS: {
					left = parseAgent(result[0].trim());
					rules.add(SimulationMain.getSimulationManager().buildRule(
							left, right, name, activity, ruleID));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationMain.getSimulationManager()
								.buildRule(right, parseAgent(result[0].trim()),
										nameOp, activity2, ruleID));
					}
					break;
				}
				case CC_RHS: {
					right = parseAgent(result[1].trim());
					rules.add(SimulationMain.getSimulationManager().buildRule(
							left, right, name, activity, ruleID));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationMain.getSimulationManager()
								.buildRule(parseAgent(result[1].trim()), left,
										nameOp, activity2, ruleID));
					}
					break;
				}
				case CC_ALL: {
					left = parseAgent(result[0].trim());
					right = parseAgent(result[1].trim());
					rules.add(SimulationMain.getSimulationManager().buildRule(
							left, right, name, activity, ruleID));
					if (typeRule == RULE_TWO_WAY) {
						ruleID++;
						rules.add(SimulationMain.getSimulationManager()
								.buildRule(parseAgent(result[1].trim()),
										parseAgent(result[0].trim()), nameOp,
										activity2, ruleID));
					}
					break;
				}
				}
			} catch (ParseErrorException e) {
				e.setMessage(myErrorHandler.formMessage(rulesDS)
						+ e.getMyMessage());
				throw e;
			}
			ruleID++;

		}

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
			if (length != 1) {
				double rescale = SimulationMain.getSimulationManager()
						.getSimulationData().getRescale();
				if (rescale < 0)
					rescale = 1.;
				double countInFile = Double.valueOf(result[0]) * rescale;

				// if (countInFile - Math.floor(countInFile) < 1e-16)
				long round = Math.round(countInFile);
				if (Math.abs(countInFile - round) < 1e-12)

					// count = (long) countInFile;
					count = round;
				else
					throw new ParseErrorException(myErrorHandler
							.formMessage(itemDS)
							+ " use '--rescale' option.");

			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without
			// parse "count" once "line"
			SimulationData simulationData = SimulationMain
					.getSimulationManager().getSimulationData();
			try {
				switch (code) {
				case CREATE_INIT: {
					line = line.replaceAll("[ 	]", "");
					List<CAgent> listAgent = parseAgent(line);
					simulationData.getSolution().addAgents(listAgent);
					for (int i = 1; i < count; i++) {
						simulationData.getSolution().addAgents(
								cloneAgentsList(listAgent));
					}
					if (SimulationMain.getSimulationManager()
							.getSimulationData().isCompile()) {
						((CSolution) SimulationMain.getSimulationManager()
								.getSimulationData().getSolution())
								.checkSolutionLinesAndAdd(line, count);

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
						name = line.substring(0, line.indexOf("'")).trim();
						line = line.substring(line.indexOf("'") + 1,
								line.length()).trim();
					}

					if (line.length() == 0) {
						simulationData.getObservables().addRulesName(
								name,
								obsNameID,
								SimulationMain.getSimulationManager()
										.getRules());
					} else
						simulationData.getObservables().addConnectedComponents(
								SimulationMain.getSimulationManager()
										.buildConnectedComponents(
												parseAgent(line)), name, line,
								obsNameID);
					obsNameID++;
					break;
				}

				}
			} catch (ParseErrorException e) {
				e.setMessage(myErrorHandler.formMessage(itemDS)
						+ e.getMyMessage());
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

	public final List<CAgent> parseAgent(String line)
			throws ParseErrorException {
		line = line.replaceAll("[ 	]", "");
		// if (!testLine(line))
		// throw new ParseErrorException();

		StringTokenizer st = new StringTokenizer(line, "),");
		Map<Integer, CSite> map = new HashMap<Integer, CSite>();
		StringTokenizer agent;
		String ccomp;
		String site;
		List<CAgent> listAgent = new ArrayList<CAgent>();
		CAgent cagent = null;
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				agent = new StringTokenizer(ccomp, "(");
				if (agent.countTokens() == 0)
					throw new ParseErrorExceptionLine(line);
				ccomp = agent.nextToken(); // Agent name.
				if (!ccomp.trim().matches(PATTERN_AGENT_SITE))
					throw new ParseErrorExeptionAgent(ccomp);

				cagent = new CAgent(SimulationMain.getSimulationManager()
						.getNameDictionary().addName(ccomp));
				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSome(site, map)); // <-------Agent
				}
			} else {
				if (cagent == null)
					throw new ParseErrorExeptionAgent(ccomp);
				cagent.addSite(parseSome(ccomp, map)); // <------Agent
			}
		}
		if (!map.isEmpty())
			throw new ParseErrorExeptionLinkState((CSite) map.values()
					.toArray()[0]);
		if (!testLine(line))
			throw new ParseErrorExceptionLine(line);
		return listAgent;
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
				throw new ParseErrorExeptionInternalState(line);
		} else {
			dt = parseLine(site, KEY_CONNECT);
			site = dt.getSt1();
		}
		connect = dt.getSt2();
		if (!site.trim().matches(PATTERN_AGENT_SITE))
			throw new ParseErrorExeptionSite(line);

		final int siteNameId = SimulationMain.getSimulationManager()
				.getNameDictionary().addName(site);
		csite = new CSite(siteNameId);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				final int nameId = SimulationMain.getSimulationManager()
						.getNameDictionary().addName(state);
				csite.setInternalState(new CInternalState(nameId));
			} else {
				throw new ParseExeptionInternalState(line);
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
					throw new ParseErrorExeptionLinkState(line);
				}

			}
		if (csite == null)
			throw new ParseErrorExceptionLine(line);
		return csite;
	}

	private final DataString parseLine(String st, int key)
			throws ParseErrorException {
		String line = new String(st);
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
						throw new ParseErrorExeptionLinkState(st);
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

	private final List<CAgent> cloneAgentsList(List<CAgent> agentList) {
		List<CAgent> newAgentsList = new ArrayList<CAgent>();
		for (CAgent agent : agentList) {
			CAgent newAgent = new CAgent(agent.getNameId());
			for (CSite site : agent.getSites()) {
				CSite newSite = new CSite(site.getNameId(), newAgent);
				newSite.setLinkIndex(site.getLinkIndex());
				newSite.setInternalState(new CInternalState(site
						.getInternalState().getNameId()));
				// newSite.getInternalState().setNameId(
				// site.getInternalState().getNameId());
				newAgent.addSite(newSite);
			}
			newAgentsList.add(newAgent);
		}
		for (int i = 0; i < newAgentsList.size(); i++) {
			for (CSite siteNew : newAgentsList.get(i).getSites()) {
				CLinkState lsNew = siteNew.getLinkState();
				CLinkState lsOld = agentList.get(i)
						.getSite(siteNew.getNameId()).getLinkState();
				lsNew.setStatusLink(lsOld.getStatusLink());
				if (lsOld.getSite() != null) {
					CSite siteOldLink = (CSite) lsOld.getSite();
					int j = 0;
					for (j = 0; j < agentList.size(); j++) {
						if (agentList.get(j) == siteOldLink.getAgentLink())
							break;
					}
					int index = j;
					lsNew.setSite(newAgentsList.get(index).getSite(
							siteOldLink.getNameId()));
				}

			}

		}

		return newAgentsList;
	}

}

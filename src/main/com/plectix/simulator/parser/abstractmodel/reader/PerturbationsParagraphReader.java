//package com.plectix.simulator.parser.abstractmodel.reader;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//
//import com.plectix.simulator.components.CPerturbation;
//import com.plectix.simulator.components.CPerturbationType;
//import com.plectix.simulator.components.CRulePerturbation;
//import com.plectix.simulator.components.ConstraintData;
//import com.plectix.simulator.components.RateExpression;
//import com.plectix.simulator.interfaces.IAgent;
//import com.plectix.simulator.interfaces.IConnectedComponent;
//import com.plectix.simulator.interfaces.IObservablesComponent;
//import com.plectix.simulator.interfaces.IPerturbationExpression;
//import com.plectix.simulator.interfaces.IRule;
//import com.plectix.simulator.parser.KappaFileLine;
//import com.plectix.simulator.parser.KappaFileParagraph;
//import com.plectix.simulator.parser.ParseErrorException;
//import com.plectix.simulator.parser.abstractmodel.*;
//import com.plectix.simulator.parser.util.AgentFactory;
//import com.plectix.simulator.parser.util.IdGenerator;
//import com.plectix.simulator.parser.util.StringUtil;
//import com.plectix.simulator.simulator.SimulationArguments;
//import com.plectix.simulator.simulator.SimulationData;
//import com.plectix.simulator.simulator.SimulationUtils;
//
///*package*/ class PerturbationsParagraphReader extends KappaParagraphReader<List<AbstractPerturbation>> {
//	private double perturbationRate;
//	
//	private final SimulationArguments myArguments;
//	private final KappaModel myModel;
//	
//	public PerturbationsParagraphReader(KappaModel model, SimulationArguments arguments,
//			AgentFactory factory) {
//		super(model, arguments, factory);
//		myArguments = getArguments();
//		myModel = model;
//	}
//	
//	public final List<AbstractPerturbation> addComponent(
//			KappaFileParagraph perturbationsParagraph)
//			throws ParseErrorException {
//		List<AbstractPerturbation> perturbations = new ArrayList<AbstractPerturbation>();
//		int pertubationID = 0;
//		for (KappaFileLine perturbationStr : perturbationsParagraph.getLines()) {
//			try {
//				String st = perturbationStr.getLine();
//				st = st.trim();
//
//				List<AbstractRateExpression> rateExpression = new ArrayList<AbstractRateExpression>();
//
//				if (st.indexOf("$T") == 0) {
//					boolean greater = getGreater(st, 2);
//					st = st.substring(2).trim();
//					st = st.substring(1).trim();
//
//					int index = st.indexOf("do");
//					String timeStr = "";
//					if (index != -1) {
//						timeStr = st.substring(0, index).trim();
//					} else {
//						throw new ParseErrorException(perturbationStr,
//								"'do' expected : " + st);
//					}
//					double time = 0;
//
//					try {
//						time = Double.valueOf(timeStr);
//					} catch (NumberFormatException e) {
//						throw new ParseErrorException(perturbationStr,
//								"Wrong 'time' modifier (real number expected) : "
//										+ timeStr);
//					}
//
//					st = st.substring(index + 2).trim();
//
//					this.perturbationRate = -1.;
//					String ruleName = null;
//					if (!checkOnce(st, perturbationStr, perturbations,
//							pertubationID, time, greater)) {
//						ruleName = getGreaterRuleName(st, perturbationStr,
//								rateExpression);
//						if (ruleName != null) {
//							perturbations.add(new AbstractPerturbation(
//									pertubationID++, time,
//									CPerturbationType.TIME, perturbationRate,
//									ruleName, greater, rateExpression));
//						}
//					}
//				} else {
//					StringUtil.checkString("[", st, perturbationStr);
//
//					st = st.substring(st.indexOf("[") + 1).trim();
//					StringUtil.checkString("'", st, perturbationStr);
//					st = st.substring(st.indexOf("'") + 1).trim();
//					String obsName = getName(st);
//
//					StringUtil.checkString("]", st, perturbationStr);
//					st = st.substring(st.indexOf("]") + 1).trim();
//
//					boolean greater = getGreater(st, 0);
//					st = st.substring(1).trim();
//
//					int obsNameID = checkInObservables(obsName).getNameID();
//
//					StringUtil.checkString("do", st, perturbationStr);
//					String pertStr = st.substring(st.indexOf("do") + 2);
//					this.perturbationRate = -1.;
//					String ruleName = getGreaterRuleName(pertStr, perturbationStr,
//							rateExpression);
//
//					st = st.substring(0, st.indexOf("do")).trim();
//
//					List<Double> parameters = new ArrayList<Double>();
//					List<String> obsID = new ArrayList<String>();
//					if (st.indexOf("+") == -1) {
//						parseExpressionMonomeBeforeDo(st, perturbationStr,
//								parameters, obsID);
//					} else {
//
//						StringTokenizer sTok = new StringTokenizer(st, "+");
//						while (sTok.hasMoreTokens()) {
//							String item = sTok.nextToken().trim();
//							parseExpressionMonomeBeforeDo(item,
//									perturbationStr, parameters, obsID);
//						}
//
//					}
//
//					if (ruleName != null) {
//
//						AbstractPerturbation pertubation = new AbstractPerturbation(
//								pertubationID++, obsID, 
//								parameters, obsNameID,
//								CPerturbationType.NUMBER, perturbationRate,
//								ruleName, greater, rateExpression);
////								, myModel.getObservables());
//						perturbations.add(pertubation);
//
//					}
//				}
//			} catch (ParseErrorException e) {
//				e.setLineDescription(perturbationStr);
//				throw e;
//			}
//
//		}
//		return perturbations;
//	}
//
//	private final IObservablesComponent checkInObservables(String obsName)
//			throws ParseErrorException {
//		IObservablesComponent obsId = null;
//		for (IObservablesComponent cc : myModel.getObservables()
//				.getComponentList()) {
//			if ((cc.getName() != null) && (cc.getName().equals(obsName))) {
//				obsId = cc;
//				break;
//			}
//		}
//		if (obsId != null) {
//			return obsId;
//		} else {
//			throw new ParseErrorException("Rule '" + obsName
//					+ "' must be in observables!");
//		}
//	}
//
//	private final void parseExpressionMonomeBeforeDo(String arg,
//			KappaFileLine perturbationStr, List<Double> parameters,
//			List<String> obsNames) throws ParseErrorException {
//
//		String item = arg;
//		if (item.indexOf("*") == -1) {
//			try {
//				parameters.add(new Double(Double.valueOf(item)));
//			} catch (NumberFormatException e) {
//				throw new ParseErrorException(perturbationStr,
//						"Real number expected instead of '" + item + "'");
//			}
//		} else {
//			double parameter = 0.0;
//			String number = item.substring(0, item.indexOf("*")).trim();
//			try {
//				parameter = Double.valueOf(number);
//			} catch (NumberFormatException e) {
//				throw new ParseErrorException(perturbationStr,
//						"Real number expected instead of '" + number + "'");
//			}
//			parameters.add(parameter);
//			item = item.substring(item.indexOf("*") + 1).trim();
//
//			StringUtil.checkString("[", item, perturbationStr);
//			item = item.substring(item.indexOf("[") + 1).trim();
//			StringUtil.checkString("'", item, perturbationStr);
//			item = item.substring(item.indexOf("'") + 1).trim();
//
//			String obsName = getName(item);
//
////			IObservablesComponent obsId = checkInObservables(obsName);
//			obsNames.add(obsName);
//		}
//	}
//
//	private final double parseExpressionMonomeAfterDo(String arg,
//			KappaFileLine perturbationStr,
//			List<AbstractRateExpression> rateExpression)
//			throws ParseErrorException {
//		String item = arg;
//		double freeTerm = 0;
//		if (item.indexOf("*") == -1) {
//			// free term as double
//			try {
//				freeTerm += Double.valueOf(item);
//			} catch (NumberFormatException e) {
//
//				StringUtil.checkString("'", item, perturbationStr);
//				addFreeRule(rateExpression, item);
//			}
//		} else {
//			double curValue = 0;
//			try {
//				curValue = Double.valueOf(item.substring(0, item.indexOf("*"))
//						.trim());
//			} catch (NumberFormatException e) {
//				throw new ParseErrorException(perturbationStr,
//						"Perturbations sum parse error: value parameter expected before rule name");
//			}
//			item = item.substring(item.indexOf("*") + 1).trim();
//
//			StringUtil.checkString("'", item, perturbationStr);
//			item = item.substring(item.indexOf("'") + 1).trim();
//
//			String curRuleName = getName(item);
//
//			//TODO!!!
//			rateExpression.add(new AbstractRateExpression(curRuleName, curValue));
//		}
//		return freeTerm;
//	}
//
//	private final String getGreaterRuleName(String st,
//			KappaFileLine perturbationStr,
//			List<AbstractRateExpression> rateExpression)
//			throws ParseErrorException {
//		boolean fail = false;
//		if (st.length() > 0) {
//			fail = Character.isLetter(st.charAt(0));
//		} else {
//			throw new ParseErrorException(perturbationStr,
//					"perturbation expected after 'do'");
//		}
//		StringUtil.checkString("'", st, perturbationStr);
//		st = st.substring(st.indexOf("'") + 1).trim();
//		if (fail) {
//			throw new ParseErrorException(perturbationStr,
//					"'do' expected before [" + st + "]");
//		}
//		StringUtil.checkString("'", st, perturbationStr);
//		String ruleName = st.substring(0, st.indexOf("'")).trim();
//
//		// st = st.replace("[ 	]", "");
//		int index = st.indexOf(":=");
//		StringUtil.checkString(":=", st, perturbationStr);
//		st = st.substring(index + 2);
//
//		double freeTerm = 0;
//
//		if (st.indexOf("+") == -1) {
//			freeTerm += parseExpressionMonomeAfterDo(st, perturbationStr,
//					rateExpression);
//		} else {
//			StringTokenizer sTok = new StringTokenizer(st, "+");
//			while (sTok.hasMoreTokens()) {
//				String item = sTok.nextToken().trim();
//				freeTerm += parseExpressionMonomeAfterDo(item, perturbationStr,
//						rateExpression);
//			}
//		}
//		if (freeTerm > 0.0)
//			rateExpression.add(new AbstractRateExpression(null, freeTerm));
//
//		return ruleName;
//	}
//
//	private final boolean getGreater(String st, int index)
//			throws ParseErrorException {
//		if (st.indexOf(">") == index) {
//			return true;
//
//		} else if (st.indexOf("<") == index) {
//			return false;
//		} else
//			throw new ParseErrorException("Expected '>' or '<' (after '"
//					+ st.substring(0, index) + "') in [" + st + "]");
//	}
//
//	private final String getName(String line) throws ParseErrorException {
//		// Example: "abc']..."
//		String name = null;
//		int index = line.indexOf("'");
//		if (index != -1) {
//			name = line.substring(0, line.indexOf("'"));
//		} else {
//			throw new ParseErrorException("Rule name or number expected : "
//					+ line);
//		}
//		return name;
//	}
//
//	private final void addFreeRule(
//			List<AbstractRateExpression> rateExpression, String item)
//			throws ParseErrorException {
//		item = item.substring(item.indexOf("'") + 1).trim();
//		String curRuleName = getName(item);
//		if (curRuleName != null) {
//			//TODO!!!
//			rateExpression.add(new AbstractRateExpression(curRuleName, 1.0));
//		}
//	}
//	
//	private final boolean checkOnce(String st, KappaFileLine perturbationStr,
//			List<AbstractPerturbation> perturbations, int pertubationID, double time,
//			boolean greater) throws ParseErrorException {
//		int indexAdd = st.indexOf("$ADDONCE");
//		int indexDel = st.indexOf("$DELETEONCE");
//		IdGenerator ruleIDGen = myModel.getRuleIdGenerator();
//		if (indexAdd == -1 && indexDel == -1)
//			return false;
//		if (indexAdd != -1 && indexDel != -1)
//			throw new ParseErrorException(perturbationStr,
//					"perturbation expected after 'do'");
//		String line = new String(st);
//		if (indexAdd != -1)
//			line = line.substring(indexAdd + 8);
//		else
//			line = line.substring(indexDel + 11);
//
//		int indexCount = line.indexOf("*");
//		String strCount = line.substring(0, indexCount).trim();
//		double countToFile;
//		String inf = new String(new Double(Double.MAX_VALUE).toString());
//		strCount = strCount.replaceAll("\\$INF", inf);
//		try {
//			countToFile = Double.valueOf(strCount);
//		} catch (NumberFormatException e) {
//			throw new ParseErrorException(perturbationStr,
//					"Quantity must have numerical format: " + strCount);
//		}
//		line = line.substring(indexCount + 1);
//
//		List<IAgent> agentList = parseAgent(line);
//		List<IConnectedComponent> ccList = SimulationUtils
//				.buildConnectedComponents(agentList);
//
//		List<AbstractPerturbationRule> ruleList = new ArrayList<AbstractPerturbationRule>();
//
//		for (IConnectedComponent cc : ccList) {
//			List<IConnectedComponent> ccL = new ArrayList<IConnectedComponent>();
//			ccL.add(cc);
//			AbstractPerturbationRule rp;
//			if (indexAdd != -1) {
//				if (countToFile == Double.MAX_VALUE)
//					throw new ParseErrorException(perturbationStr,
//							"$ADDONCE has not used with $INF");
//				
//				//TODO type safety 
//				rp = new AbstractPerturbationRule(null, ccL, "",
//						0, (int)ruleIDGen.generateNextAgentId(), 
//						myArguments.isStorify());
//			} else {
//				rp = new AbstractPerturbationRule(ccL, null, "",
//						0, (int)ruleIDGen.generateNextAgentId(), 
//						myArguments.isStorify());
//			}
//			ruleList.add(rp);
//
//		}
//
//		for (AbstractPerturbationRule rule : ruleList) {
//			rule.setCount(countToFile);
//			myModel.addRule(rule);
//
//			AbstractPerturbation perturbation = new AbstractPerturbation(pertubationID, time,
//					CPerturbationType.ONCE, rule, greater);
//			perturbations.add(perturbation);
//		}
//
//		return true;
//	}
//
//}

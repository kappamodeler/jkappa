package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
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

	private static final String PATTERN_AGENT_SITE = "^[0-9[a-zA-Z]]+[0-9[a-zA-Z]\\_\\^\\-]*";
	private static final String PATTERN_STATE = "^[0-9[a-zA-Z]]+";
	
	
	private static final String PATTERN_LINE_AGENT_SITE = "([0-9[a-zA-Z]]+[0-9[a-zA-Z]*\\_\\^\\-]*)";
	private static final String PATTERN_LINE_STATE = "([0-9[a-zA-Z]]+)";
	private static final String PATTERN_LINE_CONNECTED = "((!_)|(![0-9]+)|(\\?))*";
	private static final String PATTERN_LINE_SITE_STATE = "(("
			+ PATTERN_LINE_AGENT_SITE + PATTERN_LINE_CONNECTED + ")+|("
			+ PATTERN_LINE_AGENT_SITE + "(~)" + PATTERN_LINE_STATE
			+ PATTERN_LINE_CONNECTED + ")+)";

	private static final String PATTERN_LINE_AGENT = "(" + PATTERN_LINE_AGENT_SITE
			+ "(\\()(" + PATTERN_LINE_SITE_STATE + "*|("
			+ PATTERN_LINE_SITE_STATE + "((\\,)" + PATTERN_LINE_SITE_STATE
			+ ")*)*)" + "(\\))" + ")";

	public static final String PATTERN_LINE = "("+PATTERN_LINE_AGENT+"((\\,)"+PATTERN_LINE_AGENT+")*)";
	

	private DataReading data;

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

	// TODO needs to throw our own exception of wrong strings to parse
	public final void doParse() throws ParseErrorException {
		System.out.println("Start parsing...");

			 createSimData(data.getInits(), CREATE_INIT);
			 List<CRule> rules = createRules(data.getRules());
			 SimulationMain.getSimulationManager().setRules(rules);
			createSimData(data.getObservables(), CREATE_OBS);
		

	}
	
	public final List<CRule> createRules(List<String> list) throws ParseErrorException {

		List<CRule> rules = new ArrayList<CRule>();
		Double activity = null;
		Double activity2 = null;
		for (String rulesStr : list) {
			String input = rulesStr;
			rulesStr = rulesStr.trim();
			String name = null;
			if (rulesStr.indexOf("'") != -1) {
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1);
				name = rulesStr.substring(0, rulesStr.indexOf("'")).trim();
				rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1,
						rulesStr.length()).trim();
			}
			int index = rulesStr.lastIndexOf("@");
			if (index == -1)
				throw new ParseErrorException("Error in Rules: " + input);

			try {
				String activStr = rulesStr.substring(index + 1).trim();
				if (activStr.indexOf(",") != -1) {
					activity = Double.valueOf(activStr.substring(0, activStr
							.indexOf(",") - 1));
					activity2 = Double.valueOf(activStr.substring(activStr
							.indexOf(",") + 1));
				} else
					activity = Double.valueOf(activStr);
			} catch (Exception e) {
				throw new ParseErrorException("Error in Rules: " + input);
			}
			rulesStr = rulesStr.substring(0, index).trim();

			index = -1;
			byte typeRule = 0;
			if (rulesStr.indexOf("<->") != -1) {
				typeRule = RULE_TWO_WAY;
				rulesStr = rulesStr.replace("<", "");
				if(activity2==null)
					throw new ParseErrorException("Error in Rules: " + input);
			}else
				if(activity2!=null)
					throw new ParseErrorException("Error in Rules: " + input);
			
			rulesStr = rulesStr.trim();
			int y = rulesStr.indexOf("->");
			if (y == 0) {
				index = CC_RHS;
			}
			if (y == rulesStr.length() - 2) {
				if (index == -1) {
					index = CC_LHS;
				} else {
					throw new ParseErrorException("Error in Rules: " + input);
				}
			}

			String[] result = rulesStr.split("\\->");

			List<CAgent> left = null;
			List<CAgent> right = null;

			switch (index) {
			case CC_LHS: {
				if (typeRule == RULE_TWO_WAY)
					rules.add(SimulationMain.getSimulationManager().buildRule(
							right, parseAgent(result[0].trim()), name,
							activity2));
				left = parseAgent(result[0].trim());
				break;
			}
			case CC_RHS: {
				if (typeRule == RULE_TWO_WAY)
					rules.add(SimulationMain.getSimulationManager()
							.buildRule(parseAgent(result[1].trim()), left,
									name, activity2));
				right = parseAgent(result[1].trim());
				break;
			}
			case CC_ALL: {
				if (typeRule == RULE_TWO_WAY)
					rules.add(SimulationMain.getSimulationManager().buildRule(
							parseAgent(result[1].trim()),
							parseAgent(result[0].trim()), name, activity2));
				left = parseAgent(result[0].trim());
				right = parseAgent(result[1].trim());
				break;
			}
			}

			rules.add(SimulationMain.getSimulationManager().buildRule(left,
					right, name, activity));

		}

		return rules;
	}

	private final void createSimData(List<String> list, byte code)
			throws ParseErrorException {
		long count;
		String line;
		String[] result;
		for (String item : list) {
			count = 1;
			result = item.split("\\*");
			int length = result.length;
			result[0] = result[0].trim();
			count = 1;
			if (length != 1) {
				try {
					count = Long.valueOf(result[0]);
				} catch (NumberFormatException e) {
					throw new ParseErrorException("Error in Initial Conditions.");
				}
			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without
			// parse "count" once "line"
			SimulationData simulationData = SimulationMain
					.getSimulationManager().getSimulationData();
			switch (code) {
			case CREATE_INIT: {
				for (int i = 0; i < count; i++) {
					simulationData.getSolution().addAgents(parseAgent(line));
				}
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
				
				simulationData.getObservables().addConnectedComponents(
						SimulationMain.getSimulationManager()
								.buildConnectedComponents(parseAgent(line)),name);
				break;
			}

			}
		}

	}
	
	private boolean testLine(String line){
		line=line.replaceAll("[ 	]", "");
		while (line.indexOf("(")==0) {
			line=line.substring(1);
			if(line.indexOf(")")==-1)
				return false;
			line=line.substring(0, line.length()-1);
		}
		
		if(!line.matches(PATTERN_LINE))
			return false;
		return true;
	}

	public final List<CAgent> parseAgent(String line) throws ParseErrorException {
			if (!testLine(line))
				throw new ParseErrorException("Error in line: "+line);

		
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
				ccomp = agent.nextToken(); // Agent name.
				if (!ccomp.trim().matches(PATTERN_AGENT_SITE))
					throw new ParseErrorException("Error in 'agent' name: " + ccomp);

				cagent = new CAgent(SimulationMain.getSimulationManager()
						.getNameDictionary().addName(ccomp));
				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSome(site, map)); // <-------Agent
				}
			} else {
				cagent.addSite(parseSome(ccomp, map)); // <------Agent
			}
		}
		if (!map.isEmpty())
			throw new ParseErrorException("Error in 'connected': " + line);
		return listAgent;
	}

	private final CSite parseSome(String site, Map<Integer, CSite> map)
			throws ParseErrorException {
		String state = null;
		String connect = null;
		DataString dt = null;
		CSite csite = null;

		dt = parseLine(site, KEY_STATE);
		site = dt.getSt1();
		state = dt.getSt2();
		if (state != null) {
			dt = parseLine(state, KEY_CONNECT);
			connect = dt.getSt2();
			state = dt.getSt1();
		} else {
			dt = parseLine(site, KEY_CONNECT);
			connect = dt.getSt2();
			site = dt.getSt1();
		}

		if (!site.trim().matches(PATTERN_AGENT_SITE))
			throw new  ParseErrorException("Error in 'site' name: " + site);

		final int siteNameId = SimulationMain.getSimulationManager()
				.getNameDictionary().addName(site);
		csite = new CSite(siteNameId);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				final int nameId = SimulationMain.getSimulationManager()
						.getNameDictionary().addName(state);
				csite.setInternalState(new CInternalState(nameId));
			} else {
				throw new ParseErrorException("Error in name 'state': " + state);
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
					throw new ParseErrorException("Error in 'connected': " + connect);
				}

			}
		return csite;
	}

	private final DataString parseLine(String st, int key) {
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
				if (i == -1)
					i = st.indexOf(SYMBOL_CONNECT);
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

}

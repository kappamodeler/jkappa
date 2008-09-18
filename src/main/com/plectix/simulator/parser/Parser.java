package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkState;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulatorManager;

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

	private static final byte CC_RHS = 0;
	private static final byte CC_LHS = 1;
	private static final byte CC_ALL = -1;

	private static final byte CREATE_INIT = 0;
	private static final byte CREATE_OBS = 1;

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
	public void doParse() throws IOException {
		System.out.println("Start parsing...");

		try {
			createSimData(data.getInits(), CREATE_INIT);
			List<CRule> rules = createRules(data.getRules());
			SimulatorManager.getInstance().setRules(rules);
			createSimData(data.getObservables(), CREATE_OBS);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}

	}

	private List<CRule> createRules(List<String> list) throws IOException {

		List<CRule> rules = new ArrayList<CRule>();

		int index;
		String[] result;
		Double activity;
		String input;
		for (String rulesStr : list) {
			input = rulesStr;
			rulesStr = rulesStr.trim();
			rulesStr = rulesStr.substring(rulesStr.indexOf("'") + 1);
			String name = rulesStr.substring(0, rulesStr.indexOf("'")).trim();
			rulesStr = rulesStr.substring(rulesStr.indexOf("'"),
					rulesStr.length()).trim();
			index = rulesStr.lastIndexOf("@");
			try {
				activity = Double.valueOf(rulesStr.substring(index + 1).trim());
			} catch (Exception e) {
				throw new IOException("Error in Rules: " + input);
			}
			rulesStr = rulesStr.substring(1, index).trim();

			index = -1;
			int y = rulesStr.indexOf("->");
			if (y == 1) {
				index = CC_RHS;
			}
			if (y == rulesStr.length() - 1) {
				if (index == -1) {
					index = CC_LHS;
				} else {
					throw new IOException("Error in Rules.");
				}
			}

			result = rulesStr.split("\\->");

			List<CAgent> left = null;
			List<CAgent> right = null;

			switch (index) {
			case CC_LHS: {
				left = parceAgent(result[0].trim());
				break;
			}
			case CC_RHS: {
				right = parceAgent(result[1].trim());
				break;
			}
			case CC_ALL: {
				left = parceAgent(result[0].trim());
				right = parceAgent(result[1].trim());
				break;
			}
			}

			rules.add(SimulatorManager.getInstance().buildRule(left, right,
					name, activity));

		}

		return rules;
	}

	private void createSimData(List<String> list, byte code) throws IOException {
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
					throw new IOException("Error in Initial Conditions.");
				}
			}
			line = result[length - 1].trim();

			// In the future will be create another addAgents to Solution,
			// without
			// parce "count" once "line"
			SimulationData simulationData = SimulatorManager.getInstance()
					.getSimulationData();
			switch (code) {
			case CREATE_INIT: {
				for (int i = 0; i < count; i++) {
					simulationData.getSolution().addAgents(parceAgent(line));
				}
				break;
			}
			case CREATE_OBS: {
				simulationData.getObservables().addConnectedComponents(
						SimulatorManager.getInstance()
								.buildConnectedComponents(parceAgent(line)));
				break;
			}

			}
		}

	}

	public List<CAgent> parceAgent(String line) {
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
				cagent = new CAgent(ccomp);
				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSome(site, map)); // <-------Agent
				}
			} else {
				cagent.addSite(parseSome(ccomp, map)); // <------Agent
			}
		}
		return listAgent;
	}

	private CSite parseSome(String site, Map<Integer, CSite> map) {
		String state = null;
		String connect = null;
		DataString dt = null;
		CSite csite = null;

		dt = parceLine(site, KEY_STATE);
		site = dt.getSt1();
		state = dt.getSt2();
		if (state != null) {
			dt = parceLine(state, KEY_CONNECT);
			connect = dt.getSt2();
			state = dt.getSt1();
		} else {
			dt = parceLine(site, KEY_CONNECT);
			connect = dt.getSt2();
			site = dt.getSt1();
		}

		csite = new CSite(site);

		if (state != null) {
			csite.setInternalState(new CInternalState(state));
		}
		if (connect != null)
			if (connect.length() == 0) {
				csite.getLinkState().setStatusLink(
						CLinkState.STATUS_LINK_MAY_BE);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				csite.getLinkState().setStatusLink(
						CLinkState.STATUS_LINK_CONNECTED);
			} else {
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

			}
		return csite;
	}

	private DataString parceLine(String st, int key) {
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

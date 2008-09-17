package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CState;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.simulator.SimulationData;

public class Parser {

	private SimulationData simData = null;

	private DataReading data = null;

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

	public Parser() {

	}

	public Parser(DataReading data, SimulationData simData) {
		this.simData = simData;
		this.data = data;
	}

	// needs to throw our own exception of wrong strings to parse
	public void doParse() throws IOException {
		System.out.println("Start parsing...");

		try {
			System.out.println("<<<<<<INITS>>>>>>");
			createSimData(data.getInits());
			 System.out.println("<<<<<<RULES>>>>>>");
			 createRules(data.getRules());
			 System.out.println("<<<<<<OBS>>>>>>");
			 createSimData(data.getObservables());
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}

	}

	private void createRules(List<String> list) throws IOException {
		int index;
		String[] result;
		String name;
		Double activity;
		String input;
		for (String rules : list) {
			input = rules;
			rules = rules.trim();
			rules = rules.substring(rules.indexOf("'") + 1);
			name = rules.substring(0, rules.indexOf("'")).trim();
			rules = rules.substring(rules.indexOf("'"), rules.length()).trim();
			index = rules.lastIndexOf("@");
			try {
				activity = Double.valueOf(rules.substring(index + 1).trim());
			} catch (Exception e) {
				throw new IOException("Error in Rules: " + input);
			}
			rules = rules.substring(1, index).trim();

			System.out.println("-----------------------");
			System.out.println("Name=" + name);
			System.out.println(rules);
			System.out.println(activity);

			index = -1;
			int y = rules.indexOf("->");
			if (y == 1) {
				index = CC_RHS;
			}
			if (y == rules.length() - 1) {
				if (index == -1) {
					index = CC_LHS;
				} else {
					throw new IOException("Error in Rules.");
				}
			}

			result = rules.split("\\->");
			switch (index) {
				case CC_LHS: {
					System.out.println("LHS:");
					parceAgent(result[0].trim());
					break;
				}
				case CC_RHS: {
					System.out.println("RHS:");
					parceAgent(result[1].trim());
					break;
				}
				case CC_ALL: {
					System.out.println("LHS:");
					parceAgent(result[0].trim());
					System.out.println("RHS:");
					parceAgent(result[1].trim());
					break;
				}
			}
		}
	}

	private void createSimData(List<String> list) throws IOException {
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
			System.out.println("====================");
			System.out.println("count=" + count);
			System.out.println("====");
			line = result[length - 1].trim();
			parceAgent(line);
		}

	}

	private List<IAgent> parceAgent(String line) throws IOException {
		StringTokenizer st = new StringTokenizer(line, "),");
		Map<Integer, ISite> map = new HashMap<Integer, ISite>();
		StringTokenizer agent;
		String ccomp;
		String site;
		List<IAgent> listAgent = new ArrayList<IAgent>(1);
		CAgent cagent = null;
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				System.out.print("Agent:");
				agent = new StringTokenizer(ccomp, "(");
				ccomp = agent.nextToken(); // Agent name.
				cagent = new CAgent(ccomp);
				listAgent.add(cagent);
				System.out.println(ccomp);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parceSome(site, map)); // <-------Agent
				}
			} else {
				cagent.addSite(parceSome(ccomp, map)); // <------Agent
			}
		}
		return listAgent;
	}

	private ISite parceSome(String site, Map<Integer, ISite> map) throws IOException {
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

		System.out.println("-" + site);
		if (state != null) {
			System.out.println("--" + state);
			csite.setState(new CState(state));
		}
		if (connect != null)
			if (connect.length() == 0) {
				System.out.println("---" + "MAY_BE");
				csite.setStatusLink(CSite.STATUS_LINK_MAY_BE);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				System.out.println("---" + "CONNECT_TRUE");
				csite.setStatusLink(CSite.STATUS_LINK_CONNECTED);
			} else {
				System.out.println("---" + connect);
				int index = Integer.valueOf(connect);
				ISite isite = map.get(index);
				if(isite != null){
					isite.setLink(csite);
					csite.setLink(isite);
					map.remove(index);
				} else
					map.put(Integer.valueOf(connect), csite);
				
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

		// i = st.indexOf(id);
		if (i != -1) {
			String content = st.substring(i + 1).trim();
			st = st.substring(0, i).trim();
			ds.setSt1(st);
			ds.setSt2(content);
		}
		return ds;
	}

}

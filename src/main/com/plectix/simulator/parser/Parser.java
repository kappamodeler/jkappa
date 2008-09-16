package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import com.plectix.simulator.DataReading;
import com.plectix.simulator.SimulationData;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.interfaces.ISite;
import com.plectix.simulator.interfaces.IState;

public class Parser {

	private SimulationData simData = null;

	private DataReading data = null;

	private IAgent currentAgent = null;
	private IState currentState = null;
	private ISite currentSite = null;
	private String currentAgentS = null;
	private String currentStateS = null;
	private String currentSiteS = null;

	private final static String SYMBOL_STATE = "~";
	private final static int KEY_STATE = 1;
	private final static String SYMBOL_CONNECT_TRUE = "!_";
	private final static int KEY_CONNECT_TRUE = 2;
	private final static String SYMBOL_CONNECT_NOT_KNOWN = "?";
	private final static int KEY_NOT_KNOWN = 3;
	private final static String SYMBOL_CONNECT = "!";
	private final static int KEY_CONNECT = 4;

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
	public void doParse() {
		System.out.println("Start parsing...");

		try {
			System.out.println("<<<<<<INITS>>>>>>");
			createSimData(data.getInits());
//			System.out.println("<<<<<<RULES>>>>>>");
//			createSimData(data.getRules());
			System.out.println("<<<<<<OBS>>>>>>");
			createSimData(data.getObservables());
		} catch (IOException e) {
			System.err.println("Error in Inits.");
			return;
		}

	}

	private void createSimData(List<String> list) throws IOException {

		StringTokenizer st;
		long count;
		String line;
		String[] result;
		for (String inits : list) {
			count = 1;
			result = inits.split("\\*");
			int length = result.length;
			for (int x = 0; x < length; x++) {
				result[x] = result[x].trim();
				count = 1;
				if (length != 1) {
					count = Long.valueOf(result[x]);
					x++;
				}
				System.out.println("====================");
				System.out.println("count=" + count);
				System.out.println("====");
				line = result[x].trim();
				// line = line.replace(" ", ""); // Delete all " ".

				st = new StringTokenizer(line, "),");
				parceAgent(st);

			}
		}

	}

	private void parceAgent(StringTokenizer st) {
		StringTokenizer agent;
		String ccomp;
		String site;
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				System.out.print("Agent:");
				agent = new StringTokenizer(ccomp, "(");
				ccomp = agent.nextToken(); // Agent name.
				System.out.println(ccomp);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					parceSome(site);	// <-------Agent
				}
			} else {
				parceSome(ccomp);		// <------Agent
			}
		}
	}

	private void parceSome(String site) {
		String state = null;
		String connect = null;
		DataString dt = null;
		
		dt = parceLine(site, KEY_STATE);
		site = dt.getSt1();
		state = dt.getSt2();
		if(state!=null){
			dt = parceLine(state, KEY_CONNECT);
			connect = dt.getSt2();
			state = dt.getSt1();
		}else{
			dt = parceLine(site, KEY_CONNECT);
			connect = dt.getSt2();
			site = dt.getSt1();
		}
		
		System.out.println("-" + site.trim());
		if (state != null)
			System.out.println("--" + state.trim());
		if (connect != null)
			System.out.println("---" + connect.trim());
	}

	private DataString parceLine(String st, int key) {
		String id = null;
		DataString ds = new DataString(st);
		int i=-1;
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
		case KEY_CONNECT_TRUE: {
			if(i!=-1){
				id = SYMBOL_CONNECT_TRUE;
				i = st.indexOf(id);
				if(i ==-1)
					i = st.indexOf(SYMBOL_CONNECT);
				break;
			}
		}
		case KEY_NOT_KNOWN: {
			if(i==-1){
				id = SYMBOL_CONNECT_NOT_KNOWN;
				i = st.indexOf(id);
			}
			break;
		}
		}

//		i = st.indexOf(id);
		if (i != -1) {
			String content = st.substring(i + 1);
			st = st.substring(0, i);
			ds.setSt1(st);
			ds.setSt2(content);
		}
		return ds;
	}

}

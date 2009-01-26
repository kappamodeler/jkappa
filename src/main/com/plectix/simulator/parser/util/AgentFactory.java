package com.plectix.simulator.parser.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.interfaces.IAgent;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 *	It's not a factory, as a OOP-pattern, just named such 
 */
public class AgentFactory {

	private static final String PATTERN_AGENT_SITE = "^[0-9[a-zA-Z]]+[0-9[a-zA-Z]\\_\\^\\-]*";
	private static final String PATTERN_STATE = "^[0-9[a-zA-Z]]+";

	private final static String SYMBOL_CONNECTED_TRUE_VALUE = "_";
	
	private final IdGenerator myIdGenerator;
	
	public AgentFactory(IdGenerator idGenerator) {
		myIdGenerator = idGenerator;
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

				cagent = new CAgent(ThreadLocalData.getNameDictionary()
						.addName(ccomp), myIdGenerator.generateNextAgentId());

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
		if (!AgentFormatChecker.check(line))
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

		dt = parseLine(site, SitePropertyKey.INTERNAL_STATE);
		site = dt.getSt1();
		state = dt.getSt2();
		if (state != null) {
			dt = parseLine(state, SitePropertyKey.CONNECTION);
			state = dt.getSt1();
			if (!AgentFormatChecker.checkState(state))
				throw new ParseErrorException(
						"Unexpected internal state name : " + line);
		} else {
			dt = parseLine(site, SitePropertyKey.CONNECTION);
			site = dt.getSt1();
		}
		connect = dt.getSt2();
		if (!site.trim().matches(PATTERN_AGENT_SITE))
			throw new ParseErrorException("Unexpected site name : " + line);

		final int siteNameId = ThreadLocalData.getNameDictionary()
				.addName(site);
		csite = new CSite(siteNameId);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				final int nameId = ThreadLocalData.getNameDictionary().addName(
						state);
				csite.setInternalState(new CInternalState(nameId));
			} else {
				throw new ParseErrorException(
						"Unexpected internal state name : " + line);
			}

		if (connect != null)
			if (connect.length() == 0) {
				csite.getLinkState().setStatusLink(CLinkStatus.WILDCARD);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				csite.getLinkState().setStatusLink(CLinkStatus.BOUND);
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

	private final DataString parseLine(String st, SitePropertyKey key)
			throws ParseErrorException {
		String id = key.getSymbol();
		DataString ds = new DataString(st);
		int i = -1;
		switch (key) {
		case INTERNAL_STATE: {
			i = st.indexOf(id);
			break;
		}
		case CONNECTION: {
			i = st.indexOf(id);
		}
		case BLIND_CONNECTION: {
			if (i != -1) {
				id = SitePropertyKey.BLIND_CONNECTION.getSymbol();
				i = st.indexOf(id);
				if (i == -1) {
					i = st.indexOf(SitePropertyKey.CONNECTION.getSymbol());
					String test = new String(st);
					test = test.substring(i + 1);
					if (test.length() == 0)
						throw new ParseErrorException(
								"Unexpected link state : " + st);
				}
				break;
			}
		}
		case WILDCARD: {
			if (i == -1) {
				id = SitePropertyKey.WILDCARD.getSymbol();
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

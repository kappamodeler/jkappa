package com.plectix.simulator.parser.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.components.CLinkStatus;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSite;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.exceptions.ParseErrorMessage;
import com.plectix.simulator.simulator.ThreadLocalData;

/**
 *	It's not a factory, as a OOP-pattern, just named such 
 */
public class AgentFactory {

	private static final String PATTERN_AGENT_SITE = "^[0-9[a-zA-Z]]+[0-9[a-zA-Z]\\_\\^\\-]*";
	private static final String PATTERN_STATE = "^[0-9[a-zA-Z]]+";

	private final static String SYMBOL_CONNECTED_TRUE_VALUE = "_";
	
	public AgentFactory() {
	}
	
	public final List<AbstractAgent> parseAgent(String line)
			throws ParseErrorException, DocumentFormatException {
		line = line.replaceAll("[ 	]", "");
		// if (!testLine(line))
		// throw new ParseErrorException();

		StringTokenizer st = new StringTokenizer(line, "),");
		Map<Integer, AbstractSite> map = new HashMap<Integer, AbstractSite>();
		StringTokenizer agent;
		String ccomp;
		String site;
		List<AbstractAgent> listAgent = new LinkedList<AbstractAgent>();
		AbstractAgent cagent = null;
		
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				agent = new StringTokenizer(ccomp, "(");
				if (agent.countTokens() == 0)
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
				ccomp = agent.nextToken(); // Agent name.
				if (!ccomp.trim().matches(PATTERN_AGENT_SITE))
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_AGENT_NAME, ccomp);

				cagent = new AbstractAgent(ThreadLocalData.getNameDictionary()
						.addName(ccomp));

				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSome(site, map)); // <-------Agent
				}
			} else {
				if (cagent == null)
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_AGENT_NAME, ccomp);
				cagent.addSite(parseSome(ccomp, map)); // <------Agent
			}
		}
		if (!map.isEmpty()) {
			throw new DocumentFormatException(ParseErrorMessage.BAD_CONNECTIONS_COORDINATION, line);
		}
		if (!AgentFormatChecker.check(line))
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
		return listAgent;
	}

	private final AbstractSite parseSome(String line, Map<Integer, AbstractSite> map)
			throws ParseErrorException {
		String state = null;
		String connect = null;
		DataString dt = null;

		dt = parseLine(line, SitePropertyKey.INTERNAL_STATE);
		line = dt.getSt1();
		state = dt.getSt2();
		if (state != null) {
			dt = parseLine(state, SitePropertyKey.CONNECTION);
			state = dt.getSt1();
			if (!AgentFormatChecker.checkState(state))
				throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_INTERNAL_STATE, line);
		} else {
			dt = parseLine(line, SitePropertyKey.CONNECTION);
			line = dt.getSt1();
		}
		connect = dt.getSt2();
		if (!line.trim().matches(PATTERN_AGENT_SITE))
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_SITE_NAME, line);

		final int siteNameId = ThreadLocalData.getNameDictionary()
				.addName(line);
		AbstractSite csite = new AbstractSite(siteNameId);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				final int nameId = ThreadLocalData.getNameDictionary().addName(
						state);
				csite.setInternalState(nameId);
			} else {
				throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_INTERNAL_STATE, line);
			}

		if (connect != null)
			if (connect.length() == 0) {
				csite.getLinkState().setStatusLink(CLinkStatus.WILDCARD);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				csite.getLinkState().setStatusLink(CLinkStatus.BOUND);
			} else {
				try {
					int index = Integer.valueOf(connect);
					AbstractSite isite = map.get(index);
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
					throw new ParseErrorException(ParseErrorMessage.CONNECTION_SYMBOL_EXPECTED, line);
				}
			}
		if (csite == null)
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
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
						throw new ParseErrorException(ParseErrorMessage.CONNECTION_SYMBOL_EXPECTED, st);
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

//	public long getCurrentId() {
//		return myIdGenerator.check();
//	}

//	public List<AbstractAgent> parseAgent(String line, long count) throws ParseErrorException {
//		List<AbstractAgent> result = parseAgent(line);
////		myIdGenerator.shift(result.size() * (count - 1));
//		return result;
//	}
}

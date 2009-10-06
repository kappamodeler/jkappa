package com.plectix.simulator.parser.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.plectix.simulator.component.LinkStatus;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelSite;

/**
 *	It's not a factory, as a OOP-pattern, just named such 
 */
public final class AgentFactory {
	private static final String PATTERN_AGENT_SITE = "^[0-9[a-zA-Z]]+[0-9[a-zA-Z]\\_\\^\\-]*";
	private static final String PATTERN_STATE = "^[0-9[a-zA-Z]]+";
	private static final String SYMBOL_CONNECTED_TRUE_VALUE = "_";

	private final boolean allowIncompletes;
	
	public AgentFactory(boolean allowIncompletes) {
		this.allowIncompletes = allowIncompletes;
	}
	
	public final List<ModelAgent> parseAgent(String line)
			throws ParseErrorException, DocumentFormatException, IncompletesDisabledException {
		line = line.replaceAll("[ 	]", "");
		// if (!testLine(line))
		// throw new ParseErrorException();

		StringTokenizer st = new StringTokenizer(line, "),");
		Map<Integer, ModelSite> map = new LinkedHashMap<Integer, ModelSite>();
		StringTokenizer agent;
		String ccomp;
		String site;
		List<ModelAgent> listAgent = new LinkedList<ModelAgent>();
		ModelAgent cagent = null;
		
		while (st.hasMoreTokens()) {
			ccomp = st.nextToken().trim();
			if (ccomp.indexOf("(") != -1) {
				agent = new StringTokenizer(ccomp, "(");
				if (agent.countTokens() == 0)
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
				ccomp = agent.nextToken(); // Agent name.
				if (!ccomp.trim().matches(PATTERN_AGENT_SITE))
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_AGENT_NAME, ccomp);

				cagent = new ModelAgent(ccomp);

				listAgent.add(cagent);
				while (agent.hasMoreTokens()) {
					site = agent.nextToken().trim(); // Site name or State name.
					cagent.addSite(parseSite(site, map)); // <-------Agent
				}
			} else {
				if (cagent == null)
					throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_AGENT_NAME, ccomp);
				cagent.addSite(parseSite(ccomp, map)); // <------Agent
			}
		}
		if (!map.isEmpty()) {
			throw new DocumentFormatException(ParseErrorMessage.BAD_CONNECTIONS_COORDINATION, line);
		}
		if (!AgentFormatChecker.check(line))
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
		return listAgent;
	}

	private final ModelSite parseSite(String line, Map<Integer, ModelSite> connectionsData)
			throws ParseErrorException, IncompletesDisabledException {
		String state = null;
		String connect = null;
		SiteProperty dt = null;

		dt = parseLine(line, SitePropertyKey.INTERNAL_STATE);
		line = dt.getSiteLine();
		state = dt.getPropertyLine();
		if (state != null) {
			dt = parseLine(state, SitePropertyKey.CONNECTION);
			state = dt.getSiteLine();
			if (!AgentFormatChecker.checkState(state))
				throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_INTERNAL_STATE, line);
		} else {
			dt = parseLine(line, SitePropertyKey.CONNECTION);
			line = dt.getSiteLine();
		}
		connect = dt.getPropertyLine();
		if (!line.trim().matches(PATTERN_AGENT_SITE))
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_SITE_NAME, line);

		final String siteName = line;
		ModelSite csite = new ModelSite(siteName);

		if (state != null)
			if ((state.length() != 0) && state.trim().matches(PATTERN_STATE)) {
				csite.setInternalState(state);
			} else {
				throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_INTERNAL_STATE, line);
			}

		if (connect != null)
			if (connect.length() == 0) {
				if (!allowIncompletes) {
					throw new IncompletesDisabledException(ParseErrorMessage.INCOMPLETES_DISABLED, line);
				}
				csite.getLinkState().setStatusLink(LinkStatus.WILDCARD);
			} else if (connect.equals(SYMBOL_CONNECTED_TRUE_VALUE)) {
				if (!	allowIncompletes) {
					throw new IncompletesDisabledException(ParseErrorMessage.INCOMPLETES_DISABLED, line);
				}
				csite.getLinkState().setStatusLink(LinkStatus.BOUND);
			} else {
				try {
					int index = Integer.valueOf(connect);
					ModelSite abstractSite = connectionsData.get(index);
					if (abstractSite != null) {
						abstractSite.getLinkState().setSite(csite);
						csite.getLinkState().setSite(abstractSite);

						abstractSite.setLinkIndex(index);
						csite.setLinkIndex(index);
						connectionsData.remove(index);
					} else {
						connectionsData.put(index, csite);
					}
				} catch (Exception e) {
					throw new ParseErrorException(ParseErrorMessage.CONNECTION_SYMBOL_EXPECTED, line);
				}
			}
		if (csite == null)
			throw new ParseErrorException(ParseErrorMessage.UNEXPECTED_LINE, line);
		return csite;
	}

	private final SiteProperty parseLine(String line, SitePropertyKey dividingSymbol)
			throws ParseErrorException {
		String id = dividingSymbol.getSymbol();
		SiteProperty ds = new SiteProperty(line);
		int i = -1;
		switch (dividingSymbol) {
		case INTERNAL_STATE: {
			i = line.indexOf(id);
			break;
		}
		case CONNECTION: {
			i = line.indexOf(id);
		}
		case BLIND_CONNECTION: {
			if (i != -1) {
				id = SitePropertyKey.BLIND_CONNECTION.getSymbol();
				i = line.indexOf(id);
				if (i == -1) {
					i = line.indexOf(SitePropertyKey.CONNECTION.getSymbol());
					String test = new String(line);
					test = test.substring(i + 1);
					if (test.length() == 0)
						throw new ParseErrorException(ParseErrorMessage.CONNECTION_SYMBOL_EXPECTED, line);
				}
				break;
			}
		}
		case WILDCARD: {
			if (i == -1) {
				id = SitePropertyKey.WILDCARD.getSymbol();
				i = line.indexOf(id);
			}
			break;
		}
		}

		if (i != -1) {
			String content = line.substring(i + 1).trim();
			line = line.substring(0, i).trim();
			ds.setSiteLine(line);
			ds.setPropertyLine(content);
		}
		return ds;
	}
}

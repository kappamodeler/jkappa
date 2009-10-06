package com.plectix.simulator.bestiary;

import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.mocks.MockKappaSystemForRules;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.builders.SubstanceBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.KappaSystemInterface;

public final class LibraryOfLocalViews {
	private final SubstanceBuilder substanceBuilder;
	private final KappaSystemInterface mockKappaSystem = new MockKappaSystemForRules();

	public LibraryOfLocalViews() {
		substanceBuilder = new SubstanceBuilder(mockKappaSystem);
	}

	private final List<Agent> getAgentListByString(String agentSrt)
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {
		AgentFactory factory = new AgentFactory(true);
		List<ModelAgent> ag = factory.parseAgent(agentSrt);
		return substanceBuilder.buildAgents(ag);
	}

	public final LocalViewsMain getLocalViews(List<String> initial)
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {
		LocalViewsMain views = new LocalViewsMain(null);
		for (String s : initial) {
			for (Agent a : getAgentListByString(s)) {
				views.addLocalView(new AbstractAgent(a));
			}
		}
		return views;
	}
}

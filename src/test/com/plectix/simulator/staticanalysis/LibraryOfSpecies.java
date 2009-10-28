package com.plectix.simulator.staticanalysis;

import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.builders.SubstanceBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.KappaSystemInterface;
import com.plectix.simulator.simulator.MockKappaSystemForRules;

public class LibraryOfSpecies {

	private final SubstanceBuilder substanceBuilder;
	private final KappaSystemInterface mockKappaSystem = new MockKappaSystemForRules();

	public LibraryOfSpecies() {
		substanceBuilder = new SubstanceBuilder(mockKappaSystem);
	}

	public final List<Agent> getAgentListByString(String agentSrt)
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {
		AgentFactory factory = new AgentFactory(true);
		List<ModelAgent> ag = factory.parseAgent(agentSrt);
		return substanceBuilder.buildAgents(ag);
	}
	
	public ConnectedComponentInterface getConnectedComponent(String agentSrt) throws IncompletesDisabledException, ParseErrorException, DocumentFormatException{
		return new ConnectedComponent(getAgentListByString(agentSrt));
	}
}

package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.CState;
import com.plectix.simulator.interfaces.IAgent;

public class ParserTest extends TestCase {
	
	@Test
	public void testParseAgent() {
		Parser parser = new Parser();
		List<IAgent> actualAgentsList;
		List<IAgent> expectedAgentsList = new ArrayList<IAgent>();
		
		actualAgentsList = parser.parceAgent("Ras(S1S2~gtp), MEK(s,S222~p,S218~p), MEK(s!1,S218~p,S222~p), ERK(Y187!1)");
		
		CAgent agent = new CAgent("Ras");
		CSite site = new CSite("S1S2");
		site.setState(new CState("gtp"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("MEK");
		site = new CSite("s");
		agent.addSite(site);
		site = new CSite("S222");
		site.setState(new CState("p"));
		agent.addSite(site);
		site = new CSite("S218");
		site.setState(new CState("p"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("MEK");
		site = new CSite("s");
		agent.addSite(site);
		site = new CSite("S218");
		site.setState(new CState("p"));
		agent.addSite(site);
		site = new CSite("S222");
		site.setState(new CState("p"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("ERK");
		site = new CSite("Y187");
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		assertEquals(actualAgentsList, expectedAgentsList);
		// let's check if there is a site link between last two agents
		assertTrue(actualAgentsList.get(2).getSites().get(0).getLink() == actualAgentsList.get(3).getSites().get(0));
		assertTrue(actualAgentsList.get(3).getSites().get(0).getLink() == actualAgentsList.get(2).getSites().get(0));
	}
}

package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;

public class ParserTest extends TestCase {
	
	@Test
	public void testParseAgent() {
		Parser parser = new Parser();
		List<CAgent> actualAgentsList=null;
		List<CAgent> expectedAgentsList = new ArrayList<CAgent>();
		
		try {
			actualAgentsList = parser.parseAgent("Ras(S1S2~gtp), MEK(s,S222~p,S218~p), MEK(s!1,S218~p,S222~p), ERK(Y187!1)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CAgent agent = new CAgent("Ras");
		CSite site = new CSite("S1S2");
		site.setInternalState(new CInternalState("gtp"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("MEK");
		site = new CSite("s");
		agent.addSite(site);
		site = new CSite("S222");
		site.setInternalState(new CInternalState("p"));
		agent.addSite(site);
		site = new CSite("S218");
		site.setInternalState(new CInternalState("p"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("MEK");
		site = new CSite("s");
		agent.addSite(site);
		site = new CSite("S218");
		site.setInternalState(new CInternalState("p"));
		agent.addSite(site);
		site = new CSite("S222");
		site.setInternalState(new CInternalState("p"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent("ERK");
		site = new CSite("Y187");
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
//		assertEquals(actualAgentsList, expectedAgentsList);
		// let's check if there is a site link between last two agents
		assertTrue(actualAgentsList.get(2).getSite("s").getLinkState().getSite() == actualAgentsList.get(3).getSite("Y187"));
		assertTrue(actualAgentsList.get(3).getSite("Y187").getLinkState().getSite() == actualAgentsList.get(2).getSite("s"));
	}
}

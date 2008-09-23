package com.plectix.simulator.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.NameDictionary;

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
		final NameDictionary nameDictionary = SimulationMain.getSimulationManager().getNameDictionary();
		
		CAgent agent = new CAgent(nameDictionary.addName("Ras"));
		CSite site = new CSite(nameDictionary.addName("S1S2"));
		site.setInternalState(new CInternalState(nameDictionary.addName("gtp")));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent(nameDictionary.addName("MEK"));
		site = new CSite(nameDictionary.addName("s"));
		agent.addSite(site);
		site = new CSite(nameDictionary.addName("S222"));
		site.setInternalState(new CInternalState(nameDictionary.addName("p")));
		agent.addSite(site);
		site = new CSite(nameDictionary.addName("S218"));
		site.setInternalState(new CInternalState(nameDictionary.addName("p")));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent(nameDictionary.addName("MEK"));
		site = new CSite(nameDictionary.addName("s"));
		agent.addSite(site);
		site = new CSite(nameDictionary.addName("S218"));
		site.setInternalState(new CInternalState(nameDictionary.addName("p")));
		agent.addSite(site);
		site = new CSite(nameDictionary.addName("S222"));
		site.setInternalState(new CInternalState(nameDictionary.addName("p")));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		agent = new CAgent(nameDictionary.addName("ERK"));
		site = new CSite(nameDictionary.addName("Y187"));
		agent.addSite(site);
		expectedAgentsList.add(agent);
		
		assertEquals(actualAgentsList, expectedAgentsList);
		// let's check if there is a site link between last two agents
		assertTrue(actualAgentsList.get(2).getSite(nameDictionary.addName("s")).getLinkState().getSite() == actualAgentsList.get(3).getSite(nameDictionary.addName("Y187")));
		assertTrue(actualAgentsList.get(3).getSite(nameDictionary.addName("Y187")).getLinkState().getSite() == actualAgentsList.get(2).getSite(nameDictionary.addName("s")));
	}
}

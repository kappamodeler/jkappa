package com.plectix.simulator.parser;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CDataString;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.NameDictionary;

public class ParserTest extends TestCase {

	public void testParseRulesLine(String line) throws ParseErrorException {
		Parser parser = new Parser();

		List<CDataString> listRules = new ArrayList<CDataString>();
		listRules.add(new CDataString(0, line));
		parser.createRules(listRules);
	}

	@Test
	public void testParseRules() {
		try {
			testParseRulesLine("aa() -> a(x) @ 1.0");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("a(x),b(x) <-> a(x!1),b(x!1) @ 10.0,1.0");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("'cy_phos' a(x),c(x~p,y~u) -> a(x),c(x~p,y~p) @ 1.0");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("R(l,r),E(r) <-> R(l!1,r),E(r!1) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("R(l!_,r),R(l!_,r) -> R(l!_,r!1),R(l!_,r!1) @ 1.0");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("R(r!1),R(r!1) -> R(r),R(r) @ 1.0");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("'Grb_Shc' Sh(Y7~p),G(a) <-> Sh(Y7~p!1),G(a!1) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("'Grb_R' R(Y68~p),G(a) <-> R(Y68~p!1),G(a!1) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("So(d),G(b) <-> So(d!1),G(b!1) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("'Shc_R' R(Y48~p),Sh(pi) <-> R(Y48~p!1),Sh(pi!1) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("R(Y48~p),Sh(pi) <-> R(Y48~p?) @ 1.0,3");
		} catch (ParseErrorException e) {
			fail("Should throw an exception!");
		}

		try {
			testParseRulesLine("A(X,) <-> A(X) @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A(), <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A(X~) <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A() <-> A( @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A( <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A(x~u~p) <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A(x!1!2) <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}

		try {
			testParseRulesLine("A <-> A() @ 1.0,3");
			fail("Should throw an exception!");
		} catch (ParseErrorException e) {
		}
	}

	@Test
	public void testParseAgent() {
		Parser parser = new Parser();
		List<CAgent> actualAgentsList = null;
		List<CAgent> expectedAgentsList = new ArrayList<CAgent>();

		try {
			actualAgentsList = parser
					.parseAgent("Ras(S1S2~gtp), MEK(s,S222~p,S218~p), MEK(s!1,S218~p,S222~p), ERK(Y187!1)");
		} catch (ParseErrorException e) {
			e.printStackTrace();
		}
		final NameDictionary nameDictionary = SimulationMain
				.getSimulationManager().getNameDictionary();

		CAgent agent = new CAgent(nameDictionary.addName("Ras"));
		CSite site = new CSite(nameDictionary.addName("S1S2"));
		site
				.setInternalState(new CInternalState(nameDictionary
						.addName("gtp")));
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
		assertTrue(actualAgentsList.get(2).getSite(nameDictionary.addName("s"))
				.getLinkState().getSite() == actualAgentsList.get(3).getSite(
				nameDictionary.addName("Y187")));
		assertTrue(actualAgentsList.get(3).getSite(
				nameDictionary.addName("Y187")).getLinkState().getSite() == actualAgentsList
				.get(2).getSite(nameDictionary.addName("s")));
	}
}

package com.plectix.simulator.xmlmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.util.io.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestContactMap {

	private static final String separator = File.separator;
	private static final String prefixAll = "test.data" + separator
			+ "contact_map" + separator;
	private static final String prefixSourceRules = prefixAll + "rules"
			+ separator;
	private static final String prefixSourceModel = prefixAll + "model"
			+ separator;

	private static String currentXMLData;

	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionSimplex;
	private SAXHandler handler;
	private ArrayList<Agent> agentsSimplex;
	private ArrayList<Agent> agentsJava;
	private ArrayList<Bond> bondsSimplex;
	private ArrayList<Bond> bondsJava;

	private final InitTestContactMap initTestContactMap = new InitTestContactMap();

	@Parameters
	public static Collection<Object[]> configs() {

		FileNameCollectionGenerator.getAllFileNamesWithPathWithModifyName(
				prefixSourceRules, "~kappa");

		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator
				.addAllFileNamesWithPathWithModifyName(prefixSourceModel,
						"~kappa"),false);
	}

	public TestContactMap(String count, String patch, Integer opMode) throws Exception {
		currentXMLData = initTestContactMap.generateXML(patch, count, opMode);
	}

	@Before
	public void prepare() {
		parserFactory = SAXParserFactory.newInstance();

		try {

			sessionSimplex = new File(initTestContactMap.getComparePath());

			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			agentsSimplex = handler.getAgents();
			bondsSimplex = handler.getBonds();

			parserxml.parse(new InputSource(new StringBufferReader(
					currentXMLData)), handler);
			agentsJava = handler.getAgents();
			bondsJava = handler.getBonds();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContactMap() {

		agentsContactMapTest();
		bondsContactMapTest();

	}

	public void agentsContactMapTest() {
		
		assertEquals("[Error] Agents in XML (JAVA,SIMPLEX) ",
				agentsJava.size(), agentsSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Agent agent : agentsSimplex) {
			if (!contains(agent, agentsJava))
				errors.append("there is no agent " + agent.getName()
						+ " in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
			return;
		}
		
		errors = new StringBuffer();

	}

	public void bondsContactMapTest() {
	
		StringBuffer errors = new StringBuffer();

		for (Bond bond : bondsSimplex) {
			if (!contains(bond, bondsJava))
				errors.append("there is no bond \n < bond " + "fromAgent = "
						+ bond.getFromAgent() + "fromSite = "
						+ bond.getFromSite() + "toAgent = " + bond.getToAgent()
						+ "toSite = " + bond.getToSite() + " >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}
	}

	private boolean contains(Bond bond, ArrayList<Bond> list) {
		for (Bond b : list) {
			if (b.equals(bond)) {
				list.remove(bond);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Agent agent, ArrayList<Agent> list) {
		for (Agent a : list) {
			if (a.equals(agent)) {
				list.remove(a);
				return true;
			}
		}
		return false;
	}
}

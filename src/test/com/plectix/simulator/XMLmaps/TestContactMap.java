package com.plectix.simulator.XMLmaps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

@RunWith(value = Parameterized.class)
public class TestContactMap {

	private static final String separator = File.separator;
	private static final String prefixSourseRules = "test.data" + separator
			+ "contact_map" + separator + "rules" + separator;
	private static final String prefixSourseModel = "test.data" + separator
			+ "contact_map" + separator + "model" + separator;
	private static final String prefixSourseAgents = "test.data" + separator
			+ "contact_map" + separator + "agents" + separator;
//	private static final String prefixResult = "test.data" + separator
//			+ "contact_map" + separator + "results" + separator;
	private static final String prefixResultRules = "test.data" + separator
			+ "contact_map" + separator + "resultsRules" + separator;
	private static final String prefixResultModel = "test.data" + separator
			+ "contact_map" + separator + "resultsModel" + separator;
	
	private static int length = 20;
	private static int lengthModel = 4;
	

	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionJava;
	private File sessionSimplex;
	private SAXHandler handler;
	private ArrayList<Agent> agentsSimplex;
	private ArrayList<Agent> agentsJava;
	private ArrayList<Bond> bondsSimplex;
	private ArrayList<Bond> bondsJava;

	@Parameters

	public static Collection<Object[]> configs() {
		String str = new String();
		Object[][] object = new Object[length + lengthModel][2];
		for (Integer i = 1; i <= length; i++) {
			if (i < 10)
				str = "0" + "0" + i;
			else if (i < 100)
				str = "0" + i;
			else
				str = i.toString();
			object[i - 1][0] = str;
			object[i - 1][1] = prefixSourseRules;// str;
		}
		
        for (Integer i = 1; i <= lengthModel ; i++) {
  			if(i<10) str = "0" + "0" + i;
  			else
  				if (i<100) str = "0" + i;
 	 			else 
 	 				str = i.toString();
  			object[length + i - 1][0] = str;
  			object[length + i - 1][1] = prefixSourseModel;         
          }
		


//		object[length][0] = "001";
//		object[length][1] = prefixSourseModel;

		return Arrays.asList(object);
	}

     public TestContactMap(String count, String patch){
     	if(patch.equals(prefixSourseModel)) { 
  		  InitTestContactMap.init(patch, prefixResultModel,  count);
      	} if (patch.equals(prefixSourseRules)) {
      	  InitTestContactMap.init(patch, prefixResultRules,  count);
  		}
    	 
//		InitTestContactMap.init(patch, prefixResult,  count);
//		InitTestContactMap.init(prefixSourseRules, prefixResult,  count);
     }


	@Before
	public void prepare() {
		parserFactory = SAXParserFactory.newInstance();

		try {
			
			sessionSimplex = new File(InitTestContactMap.getComparePath());
			sessionJava = new File(InitTestContactMap.getSessionPath());
			
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			agentsSimplex = handler.getAgents();
			bondsSimplex = handler.getBonds();

			parserxml.parse(sessionJava, handler);
			agentsJava = handler.getAgents();
			bondsJava = handler.getBonds();

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAgentsContactMap() {
		// if (agentsSimplex.size()agentsJava.size()){
		// fail("wrong size of agents list");
		//			
		// }
		// for (Agent agent : agentsSimplex) {
		// if(!contains(agent, agentsJava))
		
		assertEquals("[Error] Agents in XML (JAVA,SIMPLEX) ", agentsJava.size(), agentsSimplex.size());

		
		StringBuffer errors = new StringBuffer();
		
		for (Agent agent : agentsSimplex) {
			if (!contains(agent, agentsJava))
				errors.append("there is no agent " + agent.getName() + " in Java \n");
		}
		
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
		
		
	}

	@Test
	public void testBondsContactMap() {
		// if (bondsSimplex.size()!=bondsJava.size()){
		// fail("wrong size of bonds list");
		// }
		
		//assertEquals("[Error] Bonds in XML (JAVA,SIMPLEX) ", bondsJava.size(), bondsSimplex.size());

		
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

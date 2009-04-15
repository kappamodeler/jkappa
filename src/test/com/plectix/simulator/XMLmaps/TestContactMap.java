package com.plectix.simulator.XMLmaps;

import static org.junit.Assert.*;

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
public class TestContactMap{
	
	private static final String separator = File.separator;
	private static final String prefixSourse= "test.data" + separator + "contact_map" + separator + "rules" + separator;
	private static final String prefixResult= "test.data" + separator + "contact_map" + separator + "results" + separator;
	
	private static int length = 20;
	
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
         Object[][] object = new Object[length][1];
         for (Integer i = 1; i <= length ; i++) {
 			if(i<10) str = "0" + "0" + i;
 			else
 				if (i<100) str = "0" + i;
	 			else 
	 				str = i.toString();
 			object[i-1][0] = str;
         }
         return Arrays.asList(object);
    }

	public TestContactMap(String count){
		InitTestContactMap.init(prefixSourse, prefixResult,  count);
	}

	@Before
	public void prepare(){
		parserFactory = SAXParserFactory.newInstance();
		sessionSimplex = new File(InitTestContactMap.getComparePath());
		
		sessionJava = new File(InitTestContactMap.getSessionPath());
		
		try {
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			agentsSimplex = handler.getAgents();
			bondsSimplex = handler.getBonds();
			
			
			parserxml.parse(sessionJava, handler);
			agentsJava = handler.getAgents();
			bondsJava = handler.getBonds();
			
		} catch (ParserConfigurationException e) {
			fail();
			e.printStackTrace();
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		}catch (IOException e) {
//			fail();
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testAgentsContactMap(){
		if (agentsSimplex.size()!=agentsJava.size()){
			fail("wrong size of agents list");
			
		}
		for (Agent agent : agentsSimplex) {
			if(!contains(agent, agentsJava))
				fail("there is no agent " + agent.getName() + " in Java");
		}
	}
	
	@Test
	public void testBondsContactMap(){
//		if (bondsSimplex.size()!=bondsJava.size()){
//			fail("wrong size of bonds list");
//		}
		for (Bond bond : bondsSimplex) {
			if(!contains(bond, bondsJava))
				fail("there is no bond \n < bond " +
						"fromAgent = " + bond.getFromAgent() + 
						"fromSite = " + bond.getFromSite() +
						"toAgent = " + bond.getToAgent() + 
						"toSite = " + bond.getToSite() + 
						" >  in Java");
		}
	}


	private boolean contains(Bond bond, ArrayList<Bond> list) {
		for (Bond b : list) {
			if (b.equals(bond)){
				list.remove(bond);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Agent agent, ArrayList<Agent> list) {
		for (Agent a : list) {
			if (a.equals(agent)){
				list.remove(a);
				return true;
			}
		}
		return false;
	}
}

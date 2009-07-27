package com.plectix.simulator.XMLmaps;

import static org.junit.Assert.fail;

import java.io.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;

import org.junit.*;
import org.xml.sax.SAXException;

import com.plectix.simulator.Initializator;
import com.plectix.simulator.SimulationMain;

import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.SimulationUtils;
import com.plectix.simulator.simulator.Simulator;
import com.plectix.simulator.simulator.SimulatorCommandLine;
import com.plectix.simulator.util.Info.InfoType;


@RunWith(value = Parameterized.class)
public class TestInfluenceMap {
	private static final String separator = File.separator;
	//private static final String prefixSourseRules= "test.data" + separator + "influenceMap" + separator + "rules" + separator;
	private static final String prefixSourseModel= "test.data" + separator + "influenceMap" + separator + "model" + separator;
	//private static final String prefixSourseAgents= "test.data" + separator + "influenceMap" + separator + "agents" + separator;
	private static final String prefixResult= "test.data" + separator + "influenceMap" + separator + "results" + separator;
	
	private static int length = 0;
	private static int lengthModel = 3;
	
	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionJava;
	private File sessionSimplex;
	private SAXHandler handler;
	private ArrayList<Node> nodesSimplex;
	private ArrayList<Node> nodesJava;
	private ArrayList<Connection> connectionsSimplex;
	private ArrayList<Connection> connectionsJava;
	

	@Parameters
     public static Collection<Object[]> configs() {  
         String str = new String();
         Object[][] object = new Object[length + lengthModel][2];

         for (Integer i = 1; i <= lengthModel ; i++) {
  			if(i<10) str = "0" + "0" + i;
  			else
  				if (i<100) str = "0" + i;
 	 			else 
 	 				str = i.toString();
  			object[length + i - 1][0] = str;
  			object[length + i - 1][1] = prefixSourseModel;         
          }

         return Arrays.asList(object);
    }

	public TestInfluenceMap(String count, String patch){
		InitTestInfluenceMap.init(patch, prefixResult,  count);
	}

	@Before
	public void prepare(){
		parserFactory = SAXParserFactory.newInstance();
		
		try {
			
			sessionSimplex = new File(InitTestInfluenceMap.getComparePath());
			sessionJava = new File(InitTestInfluenceMap.getSessionPath());
			
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			nodesSimplex = handler.getNodes();
			connectionsSimplex = handler.getConnections();
			
			
			parserxml.parse(sessionJava, handler);
			nodesJava = handler.getNodes();
			connectionsJava = handler.getConnections();
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testNodesInfluenceMap(){

		Assert.assertEquals("[Error] Nodes in XML (JAVA,SIMPLEX) ", nodesJava.size(), nodesSimplex.size());
		
		StringBuffer errors = new StringBuffer();
		
		for (Node node : nodesSimplex) {
			if(!contains(node, nodesJava))
				errors.append("there is no Node = '" + node.getName() + "' >  in Java \n");
		}
		
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
		
	}
	
	@Test
	public void testConnectionsInfluenceMap(){

		//TODO: THINK!!!!!
//		Assert.assertEquals("[Error] Connections in XML (JAVA,SIMPLEX) ", connectionsJava.size(), connectionsSimplex.size());
		
		StringBuffer errors = new StringBuffer();
		
		for (Connection connection : connectionsSimplex) {
			if(!contains(connection, connectionsJava))
				errors.append("there is no connection \n < connection " +
						"fromNode = " + connection.getFromNode() + 
						" toNode = " + connection.getToNode() +
						" with Relation = " + connection.getRelation() +
						" >  in Java \n");
		}
		
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
		
	}


	private boolean contains(Connection connection, ArrayList<Connection> list) {
		for (Connection c : list) {
			if (c.equals(connection)){
				list.remove(connection);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Node node, ArrayList<Node> list) {
		for (Node n : list) {
			if (n.equals(node)){
				list.remove(n);
				return true;
			}
		}
		return false;
	}
	
	
}

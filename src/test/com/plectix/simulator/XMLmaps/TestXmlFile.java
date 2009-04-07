package com.plectix.simulator.XMLmaps;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

//import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
//import javax.xml.validation.SchemaFactory;
//import javax.xml.validation.Schema;

import org.junit.Test;
import org.xml.sax.SAXException;

public class TestXmlFile {
	
	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private String prefix= "C:\\Documents and Settings\\lopatkinat\\workspace\\simulator\\";
	private File xmlFileJava;
	private File xmlFilePlectix;
	private ArrayList<Node> nodesMustInclude;
	private ArrayList<Node> nodesToFind;
	private ArrayList<Connection> connectionsToFind;
	private ArrayList<Connection> connectionsMustInclude;
	private SAXHandler handler;
	private boolean flag = false;

	@Test
	public void Test(){
	System.out.println("Test:");
		parserFactory = SAXParserFactory.newInstance();
		xmlFileJava = new File(prefix + "simplexTest.xml");
		xmlFilePlectix = new File(prefix + "plectix\\windows\\simplx.xml");
		connectionsToFind = new ArrayList<Connection>();
		connectionsMustInclude = new ArrayList<Connection>();
		try {
			System.out.println("xmlFilePlectix: connectionsToFind");
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(xmlFilePlectix, handler);
			nodesToFind =  handler.getNodes();
			connectionsToFind =  handler.getConnections();
			
			
			System.out.println("xmlFileJava:connectionsMustInclude");
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(xmlFileJava, handler);
			nodesMustInclude =  handler.getNodes();
			connectionsMustInclude =  handler.getConnections();
			
		} catch (ParserConfigurationException e) {
			fail();
			e.printStackTrace();
		} catch (SAXException e) {
			fail();
			e.printStackTrace();
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
		checkNodes();	
		
		checkConnections();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("in java");
		checkConnections2();
		if (flag){
			fail();
		}
	}



	private void checkNodes() {
		for (Node node : nodesToFind) {
			if (!findNode(node, nodesMustInclude)){
//				fail();
				printNode(node);
				findNodeById(node.getId());
			}
		}
	}

	
	private void checkConnections() {
		for (Connection connection : connectionsToFind) {
//			if (!connection.getRelation().equals("POSITIVE")){
				flag = !findConnection(connection, connectionsMustInclude);
//			}
		}
		
		
	}
	private void checkConnections2() {
		for (Connection connection : connectionsMustInclude) {
//			if (!connection.getRelation().equals("POSITIVE")){
				flag = !findConnection(connection, connectionsToFind);
//			}
		}
		
		
	}
	



//	private void xmlParse(String title, File xmlFile)
//							throws ParserConfigurationException, SAXException, IOException {
//		System.out.println(title);
//		parserxml = parserFactory.newSAXParser();
//		handler = new SAXHandler();
//		parserxml.parse(xmlFile, handler);
////		nodesList =  handler.getNodes();
////		connectionList =  handler.getConnections();
////		printNodes(nodesList);
////		printConnections(connectionList);
//	}

	

	private boolean findNode(Node node, ArrayList<Node> nodes) {
		for (Node tnode : nodes) {
			if (tnode.equals(node))
				return true;
		}
//		System.out.println("not found node");
		return false;
	}

	private boolean findConnection(Connection connection,
			ArrayList<Connection> connections) {
		for (Connection tconnection : connections) {
			if (tconnection.equals(connection))
				return true;
		}
//		if (!flag){
//			flag = true;
//		}
		printConnection(connection);
		return false;
	}

	private void printNodes(ArrayList<Node> nodes) {
		System.out.println();
		for (Node node : nodes) {
			printNode(node);
		}
	}

	private void printNode(Node node) {
		System.out.println("" +
				  "<node " 
				+ " ID='" + node.getId()
				+ "' Data='"+ node.getData() 
				+ "' Name='" + node.getName()
				+ "' Text='" + node.getText()
				+ "' Type='" + node.getType()
				+"'>");
	}
	
	private void findNodeById(String id){
		boolean flag = false;
		for (Node node : nodesMustInclude) {
			if (node.getId().equals(id)){
//				System.out.print(" --");
				printNode(node);
				flag = true;
			}
		}
		if (!flag)	System.out.println("there is no such node");
	}
//
	private void printConnections(ArrayList<Connection> connectionList) {
		System.out.println();
		for (Connection connection : connectionList) {
			printConnection(connection);
		}
		
	}
	
	private void printConnection(Connection connection) {
		System.out.println("<connection"
				+ " FromNode='" + connection.getFromNode()
				+ "' ToNode='" + connection.getToNode()
				+ "' Relation='" + connection.getRelation()
				+ "'>");
	}

//	private void printXMLFile(String path) {
//		BufferedReader in;
//		try {
//		in = new BufferedReader(new InputStreamReader(
//				new FileInputStream(path)));
//		while (in.ready()) {
//		String s = in.readLine();
//		System.out.println(s);
//		}
//		} catch (IOException e) {
//		}
//	}
}

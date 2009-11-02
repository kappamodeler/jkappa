package com.plectix.simulator.xmlmap;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.OperationModeCollectionGenerator;
import com.plectix.simulator.util.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestInfluenceMap {
	private static final String separator = File.separator;
	private static final String prefixSourseModel = "test.data" + separator
			+ "influenceMap" + separator + "model" + separator;



	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionSimplex;
	private SAXHandler handler;
	private ArrayList<Node> nodesSimplex;
	private ArrayList<Node> nodesJava;
	private ArrayList<Connection> connectionsSimplex;
	private ArrayList<Connection> connectionsJava;
	private static String currentXMLData;

	private InitTestInfluenceMap initTestInfluenceMap = new InitTestInfluenceMap();
	
	
	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator.getAllFileNamesWithPathWithModifyName(
				prefixSourseModel, "~kappa"));
	}

	public TestInfluenceMap(String count, String patch, Integer opMode) {
		currentXMLData = initTestInfluenceMap.generateXML(patch, count, opMode);
	}

	@Before
	public void prepare() {
		parserFactory = SAXParserFactory.newInstance();

		try {
			sessionSimplex = new File(initTestInfluenceMap.getComparePath());
			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			nodesSimplex = handler.getNodes();
			connectionsSimplex = handler.getConnections();

			parserxml.parse(new InputSource(new StringBufferReader(currentXMLData)), handler);
			nodesJava = handler.getNodes();
			connectionsJava = handler.getConnections();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNodesInfluenceMap() {

		Assert.assertEquals("[Error] Nodes in XML (JAVA,SIMPLEX) ", nodesJava
				.size(), nodesSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Node node : nodesSimplex) {
			if (!contains(node, nodesJava))
				errors.append("there is no Node = '" + node.getName()
						+ "' >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
			return;
		}
		
		errors = new StringBuffer();

		for (Connection connection : connectionsSimplex) {
			if (!contains(connection, connectionsJava))
				errors.append("there is no connection \n < connection "
						+ "fromNode = " + connection.getFromNode()
						+ " toNode = " + connection.getToNode()
						+ " with Relation = " + connection.getRelation()
						+ " >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}
	}

	private boolean contains(Connection connection, ArrayList<Connection> list) {
		for (Connection c : list) {
			if (c.equals(connection)) {
				list.remove(connection);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Node node, ArrayList<Node> list) {
		for (Node n : list) {
			if (n.equals(node)) {
				list.remove(n);
				return true;
			}
		}
		return false;
	}

}

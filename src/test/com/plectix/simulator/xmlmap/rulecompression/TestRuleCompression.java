package com.plectix.simulator.xmlmap.rulecompression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;

import com.plectix.simulator.FileNameCollectionGenerator;
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestRuleCompression {

	private static final String separator = File.separator;
	private static final String allPath = "test.data" + separator
			+ "ruleCompressions" + separator;
	public static final String pathForSourseModel = allPath + "model"
			+ separator + "quantitative" + separator;

	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionSimplex;
	private SAXHandler handler;
	private Set<RuleTag> rulesSimplex;
	private Set<Association> associationsSimplex;
	private Set<RuleTag> rulesJava;
	private Set<Association> associationsJava;
	private String currentXMLData;

	private InitTestRuleCompression initTestRuleCompression = new InitTestRuleCompression();

	@Parameters
	public static Collection<Object[]> configs() {

		return FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(pathForSourseModel,
						"~kappa");

	}

	public TestRuleCompression(String count, String patch)
			throws ParseException {
			currentXMLData = initTestRuleCompression.generateXML(patch, count);
	}

	@Before
	public void prepare() {
		parserFactory = SAXParserFactory.newInstance();

		try {

			sessionSimplex = new File(initTestRuleCompression.getComparePath());

			parserxml = parserFactory.newSAXParser();
			handler = new SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			rulesSimplex = handler.getRules();
			associationsSimplex = handler.getAssociations();

			parserxml.parse(new InputSource(new StringBufferReader(
					currentXMLData)), handler);
			rulesJava = handler.getRules();
			associationsJava = handler.getAssociations();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testRuleCompressionQuantativeXML() {
		
		quantativeMapTest();
		associationsMapTest();
		
	}
	
	public void quantativeMapTest() {

		assertEquals("[Error] Agents in XML (JAVA,SIMPLEX) ", rulesJava.size(),
				rulesSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (RuleTag rule : rulesSimplex) {
			if (!contains(rule, rulesJava))
				errors.append("there is no rule " + rule.toString()
						+ " in Java \n");
		}
		for (RuleTag rule : rulesJava) {
			if (!contains(rule, rulesSimplex))
				errors.append("there is no rule " + rule.toString()
						+ " in Simplex \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	public void associationsMapTest() {

		assertEquals("[Error] Associations in XML (JAVA,SIMPLEX) ",
				associationsJava.size(), associationsSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Association as : associationsSimplex) {
			if (!contains(as, associationsJava))
				errors.append("there is no bond \n" + as.toString()
						+ "in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}
	}

	private boolean contains(Association ass, Set<Association> set) {
		return set.contains(ass);
	}

	private boolean contains(RuleTag rule, Set<RuleTag> set) {
		return set.contains(rule);
	}
}

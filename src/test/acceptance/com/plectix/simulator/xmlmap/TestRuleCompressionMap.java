package com.plectix.simulator.xmlmap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.Set;

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
import com.plectix.simulator.xmlmap.rulecompression.Association;
import com.plectix.simulator.xmlmap.rulecompression.InitTestRuleCompression;
import com.plectix.simulator.xmlmap.rulecompression.RuleTag;
import com.plectix.simulator.xmlmap.rulecompression.SAXHandler;

@RunWith(value = Parameterized.class)
public class TestRuleCompressionMap {

	
	private static final String separator = File.separator;
	private static final String prefixSourseModel = "test.data" + separator
			+ "ruleCompressions" + separator + "model" + separator + "quantitative" + separator;

	private static String currentXMLData;
	private final InitTestRuleCompression init = new InitTestRuleCompression();
	

	private SAXParserFactory parserFactory;
	private SAXParser parserxml;
	private File sessionSimplex;
	private SAXHandler handler;
	private Set<Association> complxAss;
	private Set<Association> jsimAss;
	private Set<RuleTag> rules;
	
	
	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator.getAllFileNamesWithPathWithModifyName(
				prefixSourseModel, "~kappa"),false);
	}
	
	
	public TestRuleCompressionMap(String count, String patch, Integer opMode) throws Exception {
		currentXMLData = init.generateXML(patch, count, opMode);
	}
	
	@Before
	public void prepare() {
		parserFactory = SAXParserFactory.newInstance();

		try {
			sessionSimplex = new File(init.getComparePath());
			parserxml = parserFactory.newSAXParser();
			handler = new com.plectix.simulator.xmlmap.rulecompression.SAXHandler();
			parserxml.parse(sessionSimplex, handler);
			jsimAss = handler.getAssociations();
			
			parserxml.parse(new InputSource(new StringBufferReader(currentXMLData)), handler);
			complxAss = handler.getAssociations();
			
			rules = handler.getRules();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test(){
		assertTrue(jsimAss.size()==complxAss.size());
		
		for(Association as : jsimAss){
			assertTrue(complxAss.contains(as));
		}
		
	}
	
	
}

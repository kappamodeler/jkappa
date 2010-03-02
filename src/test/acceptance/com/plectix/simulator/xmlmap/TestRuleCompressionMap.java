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
	private Set<Association> complxQuantitativeAss;
	private Set<Association> complxQualitativeAss;
	
	private Set<Association> jsimQuantitativeAss;
	private Set<Association> jsimQualitativeAss;
	
	private Set<RuleTag> jsimQuantitativeRules;
	private Set<RuleTag> jsimQualitativeRules;
	
	private Set<RuleTag> complxQuantitativeRules;
	private Set<RuleTag> complxQualitativeRules;
	
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
			
			complxQuantitativeAss = handler.getQuantitativeAssociations();
			complxQualitativeAss = handler.getQualitativeAssociations();
			
			complxQuantitativeRules = handler.getQuantitativeRules();
			complxQualitativeRules = handler.getQualitativeRules();
			
			parserxml.parse(new InputSource(new StringBufferReader(currentXMLData)), handler);
			
			jsimQuantitativeAss = handler.getQuantitativeAssociations();
			jsimQualitativeAss = handler.getQualitativeAssociations();
			
			jsimQuantitativeRules = handler.getQuantitativeRules();
			jsimQualitativeRules = handler.getQualitativeRules();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAssociation(){
		String message = "Fail in:" + sessionSimplex.getName();
		assertTrue(message + " jsimQuantitativeSize=" + jsimQuantitativeAss.size() + 
				" complxQuantitativeSize=" + complxQuantitativeAss.size() + 
				" jsimQualitativeSize=" + jsimQualitativeAss.size() + 
				" complxQualitativeSize=" + complxQualitativeAss.size(),
					(
						jsimQuantitativeAss.size()==complxQuantitativeAss.size() &&
						jsimQualitativeAss.size()==complxQualitativeAss.size()
					)
				);
		boolean isFail = false;
		StringBuffer sb = new StringBuffer(message);
		checkSets(sb, isFail, jsimQuantitativeAss, complxQuantitativeAss);
		checkSets(sb, isFail, jsimQualitativeAss, complxQualitativeAss);
		
		if(isFail){
			System.err.println(sb.toString());
			fail(message);
		}
	}
	
	private void checkSets(StringBuffer sb, boolean isFail, Set<Association> jsimAss, Set<Association> complxAss){
		sb.append("\n");
		for(Association as : jsimAss){
			if(!complxAss.contains(as)){
				sb.append("JSim:");
				sb.append(as);
				sb.append("\n");
//				System.err.println("JSim:" + as);
				isFail = true;
			}
//			assertTrue(complxAss.contains(as));
		}
	}

	@Test
	public void testRules(){
		String message = "Fail in:" + sessionSimplex.getName();
		assertTrue(message + " jsimQuantitativeSize=" + jsimQuantitativeRules.size() + 
				" complxQuantitativeSize=" + complxQuantitativeRules.size() +
				" jsimQualitativeSize=" + jsimQualitativeRules.size() + 
				" complxQualitativeSize=" + complxQualitativeRules.size(),
					(
							jsimQuantitativeRules.size()==complxQuantitativeRules.size() &&
							jsimQualitativeRules.size()==complxQualitativeRules.size()
							
					)
				);
				
		
		
		boolean isFail = false;
		StringBuffer sb = new StringBuffer(message);
		sb.append("\n");
		for(RuleTag rt : jsimQuantitativeRules){
			if(!complxQuantitativeRules.contains(rt)){
				sb.append("\nJSim:");
				sb.append(rt);
				sb.append("\n");
				sb.append("OSim:");
				sb.append(getComplxRuleById(rt.getId()));
				sb.append("\n---------------------------");
//				System.err.println("JSim:" + rt);
				isFail = true;
			}
//			assertTrue(complxRules.contains(rt));
		}
		
		if(isFail){
			System.err.println(sb.toString());
			fail(message);
		}
	}
	
	private RuleTag getComplxRuleById(int id){
		for(RuleTag rt : complxQuantitativeRules){
			if(rt.getId() == id)
				return rt;
		}
		return null;
	}
	
	
}

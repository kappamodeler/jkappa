package com.plectix.simulator.localviews;

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
import com.plectix.simulator.subviews.util.Entry;
import com.plectix.simulator.subviews.util.Set;
import com.plectix.simulator.util.io.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestLocalViewsCompareXML {

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	InitTestLocalViewsCompareXML initTestLocalViewsCompareXML = new InitTestLocalViewsCompareXML();

	private ArrayList<Set> setsSimplex;
	private ArrayList<Entry> entresSimplex;

	private ArrayList<Set> setsJava;
	private ArrayList<Entry> entresJava;
	private final String currentXMLData;

	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
						"~kappa"),false);
	}

	public TestLocalViewsCompareXML(String prefixFile, String path, Integer opMode) {
		currentXMLData = initTestLocalViewsCompareXML.generateXML(path, prefixFile, opMode);
	}

	@Before
	public void prepare() {

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		try {

			File sessionSimplex = new File(initTestLocalViewsCompareXML
					.getComparePath());

			SAXParser parserxml = parserFactory.newSAXParser();
			LocalViewsParserXMLHandler handler = new LocalViewsParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			setsSimplex = handler.getSets();
			entresSimplex = handler.getEntres();

			parserxml.parse(new InputSource(new StringBufferReader(currentXMLData)), handler);
			setsJava = handler.getSets();
			entresJava = handler.getEntres();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testLocalViewsCompareXML() {
		setAgentTest();
		entryDataTest();
	}
	
	public void setAgentTest() {
		Assert.assertEquals("[Error] Set in XML (JAVA,SIMPLEX) ", setsJava
				.size(), setsSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Set set : setsSimplex) {
			if (!contains(set, setsJava))
				errors.append("[ERROR] No Agent = '" + set.getAgent()
						+ "' >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	public void entryDataTest() {

		// TODO: THINKING....
		// Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ",
		// entresJava.size(), entresSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Entry entry : entresSimplex) {
			if (!contains(entry, entresJava))
				errors.append("[ERROR] No Entry Data = '" + entry.getData()
						+ "' >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	private boolean contains(Set set, ArrayList<Set> list) {
		for (Set setList : list) {
			if (setList.equals(set)) {
				list.remove(setList);
				return true;
			}
		}
		return false;
	}

	private boolean contains(Entry entry, ArrayList<Entry> list) {
		for (Entry entryList : list) {
			if (entryList.equals(entry)) {
				list.remove(entryList);
				return true;
			}
		}
		return false;
	}

}

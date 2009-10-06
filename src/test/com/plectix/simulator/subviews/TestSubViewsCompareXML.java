package com.plectix.simulator.subviews;

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
import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.subviews.util.Entry;
import com.plectix.simulator.subviews.util.Set;
import com.plectix.simulator.subviews.util.Tag;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestSubViewsCompareXML {

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	private InitTestSubViewsCompareXML initTestSubViewsCompareXML = new InitTestSubViewsCompareXML();

	private ArrayList<Set> setsSimplex;
	private ArrayList<Tag> tagsSimplex;
	private ArrayList<Entry> entresSimplex;

	private ArrayList<Set> setsJava;
	private ArrayList<Tag> tagsJava;
	private ArrayList<Entry> entresJava;
	private String currentXMLData;

	@Parameters
	public static Collection<Object[]> configs() {
		return FileNameCollectionGenerator
		.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
				"~kappa");
	}

	public TestSubViewsCompareXML(String prefixFile, String path) {
		currentXMLData = initTestSubViewsCompareXML.generateXML(path, prefixFile);
	}

	@Before
	public void prepare() {

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		File sessionSimplex = new File(initTestSubViewsCompareXML
				.getComparePath());

		try {
			SAXParser parserxml = parserFactory.newSAXParser();
			SubViewsParserXMLHandler handler = new SubViewsParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			setsSimplex = handler.getSets();
			tagsSimplex = handler.getTags();
			entresSimplex = handler.getEntres();

			parserxml.parse(new InputSource(new StringBufferReader(currentXMLData)), handler);
			setsJava = handler.getSets();
			tagsJava = handler.getTags();
			entresJava = handler.getEntres();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testSubViewsCompareXML() {
		setAgentTest();
		tagDataTest();
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

	public void tagDataTest() {
		Assert.assertEquals("[Error] Tag in XML (JAVA,SIMPLEX) ", tagsJava
				.size(), tagsSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Tag tag : tagsSimplex) {
			if (!contains(tag, tagsJava))
				errors.append("[ERROR] No Tag Data = '" + tag.getData()
						+ "' >  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	public void entryDataTest() {
		Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ", entresJava
				.size(), entresSimplex.size());

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

	private boolean contains(Tag tag, ArrayList<Tag> list) {
		for (Tag tagList : list) {
			if (tagList.equals(tag)) {
				list.remove(tagList);
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

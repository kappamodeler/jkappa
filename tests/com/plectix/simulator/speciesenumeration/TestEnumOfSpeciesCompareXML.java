package com.plectix.simulator.speciesenumeration;

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
import com.plectix.simulator.speciesenumeration.util.Entry;
import com.plectix.simulator.speciesenumeration.util.Reachables;
import com.plectix.simulator.speciesenumeration.util.Set;
import com.plectix.simulator.util.io.StringBufferReader;

@RunWith(value = Parameterized.class)
public class TestEnumOfSpeciesCompareXML {

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	private InitTestEnumOfSpeciesCompareXML initTestEnumOfSpeciesCompareXML = new InitTestEnumOfSpeciesCompareXML();

	private ArrayList<Reachables> reachablesSimplex;
	private ArrayList<Set> setsSimplex;
	private ArrayList<Entry> entriesSimplex;

	private ArrayList<Reachables> reachablesJava;
	private ArrayList<Set> setsJava;
	private ArrayList<Entry> entriesJava;

	private int count;
	private final String currentXMLData;

	@Parameters
	public static Collection<Object[]> configs() {
		return OperationModeCollectionGenerator.generate(FileNameCollectionGenerator
				.getAllFileNamesWithPathWithModifyName(prefixSourseModel,
						"~kappa"));
	}

	public TestEnumOfSpeciesCompareXML(String prefixFile, String path, Integer opMode) {
		currentXMLData = initTestEnumOfSpeciesCompareXML.generateXML(path,
				prefixFile, opMode);
	}

	@Before
	public void prepare() {

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();

		File sessionSimplex = new File(initTestEnumOfSpeciesCompareXML
				.getComparePath());

		try {
			SAXParser parserxml = parserFactory.newSAXParser();
			EnumOfSpeciesParserXMLHandler handler = new EnumOfSpeciesParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			reachablesSimplex = handler.getReachables();
			setsSimplex = handler.getSets();
			entriesSimplex = handler.getEntres();

			parserxml.parse(new InputSource(new StringBufferReader(
					currentXMLData)), handler);
			reachablesJava = handler.getReachables();
			setsJava = handler.getSets();
			entriesJava = handler.getEntres();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testEnumOfSpeciesCompareXML() {
		reachablesTest();
		setTest();
		entryTest();
	}

	public void reachablesTest() {

		Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ",
				reachablesJava.size(), reachablesSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Reachables reachables : reachablesSimplex) {
			if (!contains(reachables, reachablesJava))
				errors.append("[ERROR] No Reachables '<Reachables Name = \""
						+ reachables.getName() + "\" Cardinal = \""
						+ reachables.getCordinal() + "\"/>' ==>  in Java \n");

		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	public void setTest() {

		Assert.assertEquals("[Error] Set in XML (JAVA,SIMPLEX) ", setsJava
				.size(), setsSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Set set : setsSimplex) {
			if (!contains(set, setsJava))
				errors.append("[ERROR] No Set = '<Set Name = \""
						+ set.getName() + "\">' ==>  in Java \n");
		}

		if (errors.length() > 0) {
			fail(errors.toString());
		}

	}

	public void entryTest() {

		Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ",
				entriesJava.size(), entriesSimplex.size());

		StringBuffer errors = new StringBuffer();

		for (Entry entry : entriesSimplex) {
			if (!contains(entry, entriesJava))
				errors.append("[ERROR] No Entry '<Entry Type = \""
						+ entry.getType() + "\" Weight = \""
						+ entry.getWeight() + "\" Data = \"" + entry.getData()
						+ "/>' ==>  in Java \n");
			// errors.append(entry.getData() + "\n");
		}

		if (errors.length() > 0) {
			System.out.println("Count = " + count);
			fail(errors.toString());
		}

	}

	private boolean contains(Reachables set, ArrayList<Reachables> list) {
		for (Reachables setList : list) {
			if (setList.equals(set)) {
				list.remove(setList);
				return true;
			}
		}
		return false;
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
				count++;
				list.remove(entryList);
				return true;
			}
		}
		return false;
	}

}

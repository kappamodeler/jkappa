package com.plectix.simulator.subViews;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

import com.plectix.simulator.subViews.UtilsForParserXML.Entry;
import com.plectix.simulator.subViews.UtilsForParserXML.Set;
import com.plectix.simulator.subViews.UtilsForParserXML.Tag;


@RunWith(value = Parameterized.class)
public class TestSubViewsCompareXML {
	
	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;
	
	private InitTestSubViewsCompareXML initTestSubViewsCompareXML = new InitTestSubViewsCompareXML(InitData.LOG4J_PROPERTIES_FILENAME);

	private ArrayList<Set> setsSimplex; 
	private ArrayList<Tag> tagsSimplex;
	private ArrayList<Entry> entresSimplex;
	
	private ArrayList<Set> setsJava; 
	private ArrayList<Tag> tagsJava;
	private ArrayList<Entry> entresJava;
	

	@Parameters
    public static Collection<Object[]> configs() { 
    	
    	Object[][] object = new Object[length+1][2];

    	String str = new String();
    	
    	for (Integer i = 0; i <= length ; i++) {
 			
    		if(i<10) str = "0" + "0" + i;
 			else
 				if (i<100) str = "0" + i;
	 			else 
	 				str = i.toString();
 			
 			object[i][0] = str;
 			object[i][1] = prefixSourseModel; 			
         }
     
        return Arrays.asList(object);
    }
    
    public TestSubViewsCompareXML(String prefixFile, String path) {
    	
    	initTestSubViewsCompareXML.generateXML(path, prefixResult, prefixFile);

	}
    
	@Before
	public void prepare(){
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		
		File sessionSimplex = new File(initTestSubViewsCompareXML.getComparePath());
		File sessionJava = new File(initTestSubViewsCompareXML.getSessionPath());
		
		try {
			SAXParser parserxml = parserFactory.newSAXParser();
			SubViewsParserXMLHandler handler = new SubViewsParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			setsSimplex = handler.getSets();
			tagsSimplex = handler.getTags(); 
			entresSimplex = handler.getEntres();
			
			
			parserxml.parse(sessionJava, handler);
			setsJava = handler.getSets();
			tagsJava = handler.getTags(); 
			entresJava = handler.getEntres();

			
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
	public void testSetAgent(){
    	Assert.assertEquals("[Error] Set in XML (JAVA,SIMPLEX) ", setsJava.size(), setsSimplex.size());
		
    	StringBuffer errors = new StringBuffer();
    	
    	for (Set set : setsSimplex) {
			if(!contains(set, setsJava))
				errors.append("[ERROR] No Agent = '" + set.getAgent() + "' >  in Java \n");
		}
    	
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
    	
    }
    
    @Test
	public void testTagData(){
    	Assert.assertEquals("[Error] Tag in XML (JAVA,SIMPLEX) ", tagsJava.size(), tagsSimplex.size());
		
    	StringBuffer errors = new StringBuffer();
    	
    	for (Tag tag : tagsSimplex) {
			if(!contains(tag, tagsJava))
				errors.append("[ERROR] No Tag Data = '" + tag.getData() + "' >  in Java \n");
		}
    	
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
    	
    }
    
    @Test
	public void testEntryData(){
    	Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ", entresJava.size(), entresSimplex.size());
		
    	StringBuffer errors = new StringBuffer();
    	
    	for (Entry entry : entresSimplex) {
			if(!contains(entry, entresJava))
				errors.append("[ERROR] No Entry Data = '" + entry.getData() + "' >  in Java \n");
		}
    	
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
    	
    }


	private boolean contains(Set set, ArrayList<Set> list) {
		for (Set setList : list) {
			if (setList.equals(set)){
				list.remove(setList);
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(Tag tag, ArrayList<Tag> list) {
		for (Tag tagList : list) {
			if (tagList.equals(tag)){
				list.remove(tagList);
				return true;
			}
		}
		return false;
	}
	
	private boolean contains(Entry entry, ArrayList<Entry> list) {
		for (Entry entryList : list) {
			if (entryList.equals(entry)){
				list.remove(entryList);
				return true;
			}
		}
		return false;
	}
	
	
}

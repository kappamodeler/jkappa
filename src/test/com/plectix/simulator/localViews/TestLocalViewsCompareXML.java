package com.plectix.simulator.localViews;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.plectix.simulator.localViews.InitData;
import com.plectix.simulator.localViews.InitTestLocalViewsCompareXML;
import com.plectix.simulator.subViews.UtilsForParserXML.Entry;
import com.plectix.simulator.subViews.UtilsForParserXML.Set;
import com.plectix.simulator.subViews.UtilsForParserXML.Tag;

@RunWith(value = Parameterized.class)
public class TestLocalViewsCompareXML {
	
	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;
	
	InitTestLocalViewsCompareXML initTestLocalViewsCompareXML = new InitTestLocalViewsCompareXML(InitData.LOG4J_PROPERTIES_FILENAME);

	private ArrayList<Set> setsSimplex; 
	private ArrayList<Entry> entresSimplex;
	
	private ArrayList<Set> setsJava; 
	private ArrayList<Entry> entresJava;
	

	@Parameters
    public static Collection<Object[]> configs() { 
    	
    	Object[][] object = new Object[length][2];

    	String str = new String();
    	
    	for (Integer i = 1; i <= length ; i++) {
 			
    		if(i<10) str = "0" + "0" + i;
 			else
 				if (i<100) str = "0" + i;
	 			else 
	 				str = i.toString();
 			
 			object[i - 1][0] = str;
 			object[i - 1][1] = prefixSourseModel; 			
         }
     
        return Arrays.asList(object);
    }
    
    public TestLocalViewsCompareXML(String prefixFile, String path) {
    	initTestLocalViewsCompareXML.generateXML(path, prefixResult, prefixFile);
	}
    
	@Before
	public void prepare(){
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	
		try {
			
			File sessionSimplex = new File(initTestLocalViewsCompareXML.getComparePath());
			File sessionJava = new File(initTestLocalViewsCompareXML.getSessionPath());
			
			
			SAXParser parserxml = parserFactory.newSAXParser();
			LocalViewsParserXMLHandler handler = new LocalViewsParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			setsSimplex = handler.getSets();
			entresSimplex = handler.getEntres();
			
			
			parserxml.parse(sessionJava, handler);
			setsJava = handler.getSets();
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
	public void testEntryData(){
    	
    	//TODO: THINKING....
//    	Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ", entresJava.size(), entresSimplex.size());
		
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

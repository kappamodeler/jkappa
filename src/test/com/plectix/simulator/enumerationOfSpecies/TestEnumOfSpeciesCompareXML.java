package com.plectix.simulator.enumerationOfSpecies;

import static org.junit.Assert.fail;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Entry;
import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Reachables;
import com.plectix.simulator.enumerationOfSpecies.UtilsForParserXML.Set;


@RunWith(value = Parameterized.class)
public class TestEnumOfSpeciesCompareXML {
	
	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;
	
	private InitTestEnumOfSpeciesCompareXML initTestEnumOfSpeciesCompareXML = new InitTestEnumOfSpeciesCompareXML(InitData.LOG4J_PROPERTIES_FILENAME);

	private ArrayList<Reachables> reachablesSimplex;
	private ArrayList<Set> setsSimplex; 
	private ArrayList<Entry> entresSimplex;
	
	private ArrayList<Reachables> reachablesJava;
	private ArrayList<Set> setsJava; 
	private ArrayList<Entry> entresJava;
	
	private int count;
	

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
    
    public TestEnumOfSpeciesCompareXML(String prefixFile, String path) {
    	System.out.println("Test " +  prefixFile);
    	initTestEnumOfSpeciesCompareXML.generateXML(path, prefixResult, prefixFile);

	}
    
	@Before
	public void prepare(){
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		
		File sessionSimplex = new File(initTestEnumOfSpeciesCompareXML.getComparePath());
		File sessionJava = new File(initTestEnumOfSpeciesCompareXML.getSessionPath());
		
		try {
			SAXParser parserxml = parserFactory.newSAXParser();
			EnumOfSpeciesParserXMLHandler handler = new EnumOfSpeciesParserXMLHandler();
			parserxml.parse(sessionSimplex, handler);
			reachablesSimplex = handler.getReachables(); 
			setsSimplex = handler.getSets();
			entresSimplex = handler.getEntres();
			
			
			parserxml.parse(sessionJava, handler);
			reachablesJava = handler.getReachables();
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
	public void testReachables(){
    	
    	
    	
    	 List<Entry> listData = new ArrayList<Entry>();
         
    	 
         listData.add(new Entry("11", "21", "31"));
         listData.add(new Entry("12", "22", "32"));
         listData.add(new Entry("13", "23", "33"));
         listData.add(new Entry("14", "24", "34"));
         

//         for (Entry element : listData) {
//       
//        	 if(element.equals(new Entry("14", "24", "34"))) {
//        		 listData.remove(element);
//             }
//        	 
//         }
         
         int count = 0;
         
         System.out.println("1 = " + listData);
        
         Iterator<Entry> iter = listData.iterator();
         
         while (iter.hasNext()) {
        	 
        	 Entry type = (Entry) iter.next();
             
        	 if(type.equals(new Entry("14", "24", "34"))) {
            	 iter.remove();
            	 count++;
            	 System.out.println("2 = " + listData);
             }
             
         }
     	
         System.out.println("3 = " + count);

         
    	Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ", reachablesJava.size(), reachablesSimplex.size());
		
    	StringBuffer errors = new StringBuffer();
    	
		for (Reachables reachables : reachablesSimplex) {
			if(!contains(reachables, reachablesJava))
				errors.append("[ERROR] No Reachables '<Reachables Name = \"" + reachables.getName()
													 + "\" Cardinal = \"" + reachables.getCordinal() 
													 +"\"/>' ==>  in Java \n");
			
		}
    	
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
    	
    }
	
    @Test
	public void testSet(){
    	
    	Assert.assertEquals("[Error] Set in XML (JAVA,SIMPLEX) ", setsJava.size(), setsSimplex.size());
		
    	StringBuffer errors = new StringBuffer();
    	
    	for (Set set : setsSimplex) {
			if(!contains(set, setsJava))
				errors.append("[ERROR] No Set = '<Set Name = \"" + set.getName() 
												+ "\">' ==>  in Java \n");
		}
    	
    	if (errors.length() > 0) {
			fail(errors.toString());
		}
    	
    }
    
    //TODO: THINKING....
//    @Test
//	public void testEntry(){
//    	Assert.assertEquals("[Error] Entres in XML (JAVA,SIMPLEX) ", entresJava.size(), entresSimplex.size());
//		
//    	StringBuffer errors = new StringBuffer();
//    	
//    	for (Entry entry : entresSimplex) {
//			if(!contains(entry, entresJava))
//				errors.append("[ERROR] No Entry '<Entry Type = \"" + entry.getType() 
//												+ "\" Weight = \"" + entry.getWeight() 
//												+ "\" Data = \"" + entry.getData() 
//												+ "/>' ==>  in Java \n");
//		}
//		
//    	if (errors.length() > 0) {
//    		System.out.println(count);
//			fail(errors.toString());
//		}
//	
//    }


	private boolean contains(Reachables set, ArrayList<Reachables> list) {
		for (Reachables setList : list) {
			if (setList.equals(set)){
				list.remove(setList);
				return true;
			}
		}
		return false;
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

		if (entry.getData().equals("EGFR(dimer!1,ligand,tail~p),EGFR(dimer!1,ligand,tail~u)")) {
			boolean is = true;
			is = is && is;
			System.out.println(is);
		}
		
		for (Entry entryList : list) {
			if (entryList.equals(entry)){
				System.out.println("1 = " + entry.getData());
				System.out.println("2 = " + entryList.getData());
				System.out.println("1 1 = " + entry.getDataList());
				System.out.println("2 2= " + entryList.getDataList());
				count++;
				list.remove(entryList);
				return true;
			}
		}
		return false;
	}
	
	
}

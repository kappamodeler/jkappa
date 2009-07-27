package com.plectix.simulator.subViews;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.components.complex.subviews.storage.ISubViews;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;
import com.plectix.simulator.utilsForTest.FileWrapper;
	


@RunWith(value = Parameterized.class)
public class TestSubViews {
		
	
		private static final PlxLogger LOGGER = ThreadLocalData.getLogger(TestSubViews.class);
		
		private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
		
		private static FileWrapper fileLog = null; 

		private static final String prefixSourseModel = InitData.pathForSourseModel;
		private static final String prefixResult = InitData.pathForResult;

		private static int length = InitData.length;
		
		private IAllSubViewsOfAllAgents subViews;
		
		private Map<String, Integer> resultNumClassesForAgentsMap;
		private Map<String, Integer> resultNumSubViewsForClassesMap;
		
		private Map<String, Integer> testNumClassesForAgentsMap;
		private Map<String, Integer> testNumSubViewsForClassesMap;
		
		private static boolean isConsole = InitData.isPrintinConsoleAndFile; 
			
		
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

		public TestSubViews(String count, String patch){
			printConsole("Test " + count);
			InitTestSubView.init(patch, prefixResult,  count);
			//initNumClassesForAgents(getSoursePath(count));
			//initNumSubViewsForClasses(getSoursePath(count));
			init(getSoursePath(count));

		}
		
		
		private void init(String soursePath) {
			resultNumClassesForAgentsMap = new LinkedHashMap<String, Integer>();
			resultNumSubViewsForClassesMap = new LinkedHashMap<String, Integer>();
			ParserFileTesterSubViews parser = new ParserFileTesterSubViews(soursePath);
			parser.parseFile(resultNumClassesForAgentsMap, resultNumSubViewsForClassesMap);
			
		}

		private String getSoursePath(String count) {
			return InitData.pathForSource + "source" + count + FILENAME_EXTENSION; //".txt";
		}

		private void initNumClassesForAgents(String path) {
			resultNumClassesForAgentsMap = new LinkedHashMap<String, Integer>();
			/*
			resultNumClassesForAgentsMap.put("A", 2);
			resultNumClassesForAgentsMap.put("B", 2);
			resultNumClassesForAgentsMap.put("C", 1);
			resultNumClassesForAgentsMap.put("D", 2);
			resultNumClassesForAgentsMap.put("E", 1);
			resultNumClassesForAgentsMap.put("F", 1);
			resultNumClassesForAgentsMap.put("G", 1);
			resultNumClassesForAgentsMap.put("H", 2);
			resultNumClassesForAgentsMap.put("M", 2);
			resultNumClassesForAgentsMap.put("N", 4);
			resultNumClassesForAgentsMap.put("P", 1);
			resultNumClassesForAgentsMap.put("Q", 1);
			*/

		}
		
		private void initNumSubViewsForClasses(String path) {
			
			
			resultNumSubViewsForClassesMap = new LinkedHashMap<String, Integer>();
			/*
			resultNumSubViewsForClassesMap.put("Aab",3);
			resultNumSubViewsForClassesMap.put("Abc", 3);
			resultNumSubViewsForClassesMap.put("Bab", 2);
			resultNumSubViewsForClassesMap.put("Bbcd", 10);
			resultNumSubViewsForClassesMap.put("Cabcd", 3);
			resultNumSubViewsForClassesMap.put("Dab", 1);
			resultNumSubViewsForClassesMap.put("Dc", 1);
			resultNumSubViewsForClassesMap.put("Eabc", 7);
			resultNumSubViewsForClassesMap.put("Fabc", 6);
			resultNumSubViewsForClassesMap.put("Gabc", 1);
			resultNumSubViewsForClassesMap.put("Habc", -1);
			resultNumSubViewsForClassesMap.put("Hcde", -1);
			resultNumSubViewsForClassesMap.put("Mac", -1);
			resultNumSubViewsForClassesMap.put("Mbd", -1);
			resultNumSubViewsForClassesMap.put("Na", -1);
			resultNumSubViewsForClassesMap.put("Nb", -1);
			resultNumSubViewsForClassesMap.put("Nc", -1);
			resultNumSubViewsForClassesMap.put("Nd", -1);
			resultNumSubViewsForClassesMap.put("Qab", -1);
			resultNumSubViewsForClassesMap.put("Qc", -1);
			resultNumSubViewsForClassesMap.put("Qd", -1);
			resultNumSubViewsForClassesMap.put("Pabcd", -1);
			*/
		}

		@Before
		public void setUp() {
			if (isConsole) {
				instanseFileLog();
			}
			subViews = InitTestSubView.getSubViews();
		}

		private static void instanseFileLog() {
			if (fileLog == null) {
				fileLog = new FileWrapper(InitTestSubView.getResultPath());
			}
		}

		@AfterClass
		public static void exit() {
			if (isConsole) {
				fileLog.closeFile();
			}
		}
		
		
		
		@Test
		public void testNumClassesForAgents(){
			
			Iterator<Integer> allTypesOfAgents = subViews.getAllTypesIdOfAgents();
			//Map<Integer, CAbstractAgent> mapOfAgents = subViews.getFullMapOfAgents();
			
			initTestNumClassesForAgentMap(allTypesOfAgents);
			
			StringBuffer errorStr = new StringBuffer(); 
			
			for (String agentName : resultNumClassesForAgentsMap.keySet()) {
				
				Integer resultNumClasses = resultNumClassesForAgentsMap.get(agentName);
				
				if(testNumClassesForAgentsMap.containsKey(agentName)) {
					
					Integer testNumClasses = testNumClassesForAgentsMap.get(agentName);
										
					if(resultNumClasses.intValue() == testNumClasses.intValue()) {

						String okSrtring = "[OK] Agent " + agentName + " | ClassNum = " + testNumClasses;
						
						printConsole(okSrtring);
						
					} else {
						
						String errorString = "[Error] Agent " + agentName 
				           					 + " | [file] ClassNum = " + resultNumClasses
				           					 + " <> "
				           					 + " [test] ClassNum = " + testNumClasses + "\n";
						
						//printConsoleOrFail(errorString);
						
						errorStr.append(errorString);
						
					}

				} else {
					
					String errorString = "[Error] In test has not Agent " + agentName 
										 + " ClassNum = " + resultNumClasses + "\n";
					
					errorStr.append(errorString);
					//printConsoleOrFail(errorString);
					
				}
			}
			
			if(errorStr.length() > 0) {
				printConsoleOrFail(errorStr.toString());
			}
			
		}

		private void initTestNumClassesForAgentMap(Iterator<Integer> allTypesOfAgents) {
			
			testNumClassesForAgentsMap = new LinkedHashMap<String, Integer>();
			
			while (allTypesOfAgents.hasNext()) {
			
				Integer agentTypeID = allTypesOfAgents.next();
				String agent = subViews.getFullMapOfAgents().get(agentTypeID).toString();
				Integer numAllSubViews = subViews.getAllSubViewsByTypeId(agentTypeID.intValue()).size();
				testNumClassesForAgentsMap.put(agent, numAllSubViews);
				
				String srtInfo = "[Info] Test Agent " + agent + " Amount = " + numAllSubViews;
				printConsole(srtInfo);
				

			}
		}
		
		@Test
		public void testNumSubViewsForClass(){
			
			Iterator<Integer> allTypesOfAgents = subViews.getAllTypesIdOfAgents();
			
			initTestNumSubViewsForClassesMap(allTypesOfAgents);
			
			StringBuffer errorStr = new StringBuffer();
			
			for (String subViewName : resultNumSubViewsForClassesMap.keySet()) {
				
				Integer resultNumSubView = resultNumSubViewsForClassesMap.get(subViewName);
				
				if(testNumSubViewsForClassesMap.containsKey(subViewName)) {
					
					Integer testNumSubView = testNumSubViewsForClassesMap.get(subViewName);
										
					if(resultNumSubView.intValue() == testNumSubView.intValue()) {

						String okSrtring = "[OK] SubViews " + subViewName + " | SubViewsNum = " + testNumSubView;
						
						printConsole(okSrtring);
						
					} else {
						
						String errorString = "[Error] SubViews " + subViewName 
				           					 + " | [file] SubViewsNum = " + resultNumSubView
				           					 + " <> "
				           					 + "[test] SubViewsNum = " + testNumSubView + "\n";
						
						//printConsoleOrFail(errorString);
						errorStr.append(errorString);
					}

				} else {
					
					String errorString = "[Error] In test has not SubViews " + subViewName 
										 + " SubViewsNum = " + resultNumSubView + "\n";
					
					//printConsoleOrFail(errorString);
					errorStr.append(errorString);
				}
			}
			
			if(errorStr.length() > 0) {
				printConsoleOrFail(errorStr.toString());
			}
			
			
		}

		private void initTestNumSubViewsForClassesMap(Iterator<Integer> allTypesOfAgents) {
			
			testNumSubViewsForClassesMap = new LinkedHashMap<String, Integer>();
			
			while (allTypesOfAgents.hasNext()) {
			
				Integer agentTypeID = allTypesOfAgents.next();
				
				String agent = subViews.getFullMapOfAgents().get(agentTypeID).toString();
				
				if(resultNumClassesForAgentsMap.containsKey(agent)) {
					
					List<ISubViews> allSubViewsClasses = subViews.getAllSubViewsByTypeId(agentTypeID.intValue());
		
					Integer numSubClass = 0;
					
					for(ISubViews subViewsClass : allSubViewsClasses) {
						
						//String subClassName = getClassForAgent(subViewsClass);
						numSubClass = numSubClass + subViewsClass.getAllSubViews().size();
						/*
						testNumSubViewsForClassesMap.put(subClassName, numSubClass);
						String srtInfo = "[Info] Test SubView " + subClassName + " Amount = " + numSubClass;
						printConsole(srtInfo);
						*/
					}
					testNumSubViewsForClassesMap.put(agent, numSubClass);
					String srtInfo = "[Info] Test AllSubViews for Agent " + agent + " Amount = " + numSubClass;
					printConsole(srtInfo);
				}
			}
		}
		
		
		private void printConsole(String okString) {
			if (isConsole) {
				print(okString);
			}
		}

		
		private void printConsoleOrFail(String errorString) {
			if (isConsole) {
				print(errorString);
			} else {
				fail(errorString);
			}
		}

		private String getClassForAgent(ISubViews subViewsClass) {
			StringBuffer subClassTest = new StringBuffer();
			subClassTest.append(ThreadLocalData.getNameDictionary().getName(subViewsClass.getSubViewClass().getAgentTypeId()));
			for(Integer id : subViewsClass.getSubViewClass().getSitesId()) {
				subClassTest.append(ThreadLocalData.getNameDictionary().getName(id));
			}
			return subClassTest.toString();
		}
		
		
		private static void print(String message) {
			System.out.println(message);
			writeInFile(message);
		}
		
		private static void writeInFile(String str) {
			instanseFileLog();
			fileLog.writeInFile(str);
		}
		
}


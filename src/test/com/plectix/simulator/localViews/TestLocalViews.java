package com.plectix.simulator.localViews;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.utilsForTest.FileWrapper;
import com.plectix.simulator.utilsForTest.ReadAndParserFile;



@RunWith(value = Parameterized.class)
public class TestLocalViews {

	private static FileWrapper fileLog = null; 

	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;
	
	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	
	private static boolean isConsole = InitData.isPrintinConsoleAndFile;
	
	private static final String SPLITER = " ";
	private static final String  TOTAL = "TOTAL";
	
	private IAllSubViewsOfAllAgents subViews;
	private CLocalViewsMain localViews;
	
	private Map<Integer, List<CAbstractAgent>> localViewsSimplex = new LinkedHashMap<Integer, List<CAbstractAgent>>();
	
	private Map<String, Integer> amountsLocalViewsSimplexMap = new LinkedHashMap<String, Integer>();
	private Map<String, Integer> amountsLocalViewsJavaMap = new LinkedHashMap<String, Integer>();
	
	
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

	public TestLocalViews(String count, String patch){
		printConsole("Test " + count);
		InitTestLocalViews.init(patch, prefixResult,  count);
		init(getSoursePath(count));
	}
	
	private void init(String soursePath) {
		ReadAndParserFile parser = new ReadAndParserFile(soursePath, SPLITER);
		parser.addTypeData("#TOTALLOCALVIEWS", amountsLocalViewsSimplexMap);
		parser.addTypeData("#LOCALVIEWS", amountsLocalViewsSimplexMap);
		parser.parseFile();
	}
	
	@Before
	public void setUp() {
		if (isConsole) {
			instanseFileLog();
		}
		subViews = InitTestLocalViews.getSubViews();
		initLocalViews(subViews);
		
	}

	@AfterClass
	public static void exit() {
		if (isConsole) {
			fileLog.closeFile();
		}
	}
	
	private void initLocalViews(IAllSubViewsOfAllAgents subViews) {
	
		if (localViews == null) {
			localViews = new CLocalViewsMain(subViews);
			localViews.buildLocalViews();
		}
	}
	
	@Test
	public void testTotalAmountLocalViews() {
		
		//TODO: THINKING    FILE SOURSE003 "TOTAL 13" in SIMPLEX.
		int amountlocalViewsJava = localViews.getLocalViews().size();
		
		if (!amountsLocalViewsSimplexMap.get(TOTAL).equals(Integer.valueOf(amountlocalViewsJava))) {
			
			String errorString = "[Error] Test Total AmountLocalViews = " + amountlocalViewsJava 
								 + " | Simplex Total AmountLocalViews = " + amountsLocalViewsSimplexMap.get(TOTAL) + "\n";
			printConsoleOrFail(errorString);
			
		} else {
			String okSrtring = "[OK] Total AmountLocalViews = " + amountlocalViewsJava;
			printConsole(okSrtring);
		}
		
	}
	
	@Test
	public void testAmountLocalViews() {
		
		initAmountLocalViewsJavaMap();
		
		StringBuffer errorStr = new StringBuffer();
		
		for(String keyNameSimplex : amountsLocalViewsSimplexMap.keySet()) {
			
			if(keyNameSimplex.equals(TOTAL)) {
				continue;
			}
			
			if (amountsLocalViewsJavaMap.containsKey(keyNameSimplex)) {
				
				if(amountsLocalViewsSimplexMap.get(keyNameSimplex).equals
						(amountsLocalViewsJavaMap.get(keyNameSimplex))) {
					
					String okSrtring = "[OK] LocalViews for Agent = " + keyNameSimplex 
									 + " AmountLocalViews = " + amountsLocalViewsJavaMap.get(keyNameSimplex);
					
					printConsole(okSrtring);
				
				} else {
					
					String errorString = "[Error] LocalViews for Agent = " + keyNameSimplex 
					 					 + " | [file - Simplex] AmountLocalViews = " + amountsLocalViewsSimplexMap.get(keyNameSimplex)
					 					 + " <> "
					 					 + "[test - Java] AmountLocalViews = " + amountsLocalViewsJavaMap.get(keyNameSimplex) + "\n";
					
					errorStr.append(errorString);
					
				}
				
			} else {
				
				
				String errorString = "[Error] LocalViews for Agent = " + keyNameSimplex 
					 				 + " AmountLocalViews = " + amountsLocalViewsSimplexMap.get(keyNameSimplex)
					 				 + " > NO in JAVA \n";

				
				errorStr.append(errorString);

			}

		}
		
		if(errorStr.length() > 0) {
			printConsoleOrFail(errorStr.toString());
		}
	}
	
	
	private void initAmountLocalViewsJavaMap() {
		
		Map<Integer, List<CAbstractAgent>> localViewsJava = localViews.getLocalViews();
		
		for(Integer keyId : localViewsJava.keySet()) {
			
			List<CAbstractAgent> agents = localViewsJava.get(keyId);
	
			amountsLocalViewsJavaMap.put(agents.iterator().next().getName(), agents.size());
		
		}
		
	}

	private String getSoursePath(String count) {
		return InitData.pathForSource + "source" + count + FILENAME_EXTENSION; //".txt";
	}
	
	private void printConsoleOrFail(String errorString) {
		if (isConsole) {
			print(errorString);
		} else {
			fail(errorString);
		}
	}
	
	private void printConsole(String okString) {
		if (isConsole) {
			print(okString);
		}
	}
	
	private static void print(String message) {
		System.out.println(message);
		writeInFile(message);
	}
	
	private static void writeInFile(String str) {
		instanseFileLog();
		fileLog.writeInFile(str);
	}
	
	private static void instanseFileLog() {
		if (fileLog == null) {
			fileLog = new FileWrapper(InitTestLocalViews.getResultPath());
		}
	}
}

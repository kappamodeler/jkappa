package com.plectix.simulator.detectionOfCycles;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.detectionOfCycles.Detector;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.utilsForTest.FileWrapper;



@RunWith(value = Parameterized.class)
public class TestDetectionOfCycles {

	private static FileWrapper fileLog = null; 

	private static final String prefixSourseModel = InitData.pathForSourseModel;

	private static int length = InitData.length;
	
	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	
	private static boolean isConsole = InitData.isPrintinConsoleAndFile;
	
	private static final String SPLITER = " ";
	private static final String  TOTAL = "TOTAL";
	
	private IAllSubViewsOfAllAgents subViews;
	private CContactMap contactMap;
	
	
	
	
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

	public TestDetectionOfCycles(String count, String patch){
		printConsole("Test " + count);
		InitTestDetectionOfCycles.init(patch, count);
		//init(getSoursePath(count));
	}
	

	
	@Before
	public void setUp() {
		if (isConsole) {
			instanseFileLog();
		}
		subViews = InitTestDetectionOfCycles.getSubViews();
		contactMap = InitTestDetectionOfCycles.getContactMap();
		
		
	}

	@AfterClass
	public static void exit() {
		if (isConsole) {
			fileLog.closeFile();
		}
	}
	

	
	@Test
	public void testTotalAmountLocalViews() {
		
		List<CAbstractAgent> list = new LinkedList<CAbstractAgent>();
		list.addAll(contactMap.getAbstractSolution().getAgentNameIdToAgent().values());
		Detector detector = new Detector(subViews,list);
		System.out.println(detector.extractCycles().size());

		

		
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
			fileLog = new FileWrapper(InitTestDetectionOfCycles.getResultPath());
		}
	}
}

//package com.plectix.simulator.contactMap;
//
//import static org.junit.Assert.*;
//
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//import org.junit.runners.Parameterized.Parameters;
//
//
//import com.plectix.simulator.components.CRule;
//import com.plectix.simulator.components.complex.contactMap.CContactMap;
//import com.plectix.simulator.components.complex.subviews.CMainSubViews;
//import com.plectix.simulator.interfaces.ISolution;
//import com.plectix.simulator.simulator.SimulationArguments;
//import com.plectix.simulator.simulator.ThreadLocalData;
//import com.plectix.simulator.subViews.FileWrapper;
//import com.plectix.simulator.util.PlxLogger;
//
//	
//
//
//@RunWith(value = Parameterized.class)
//public class TestContactMap{
//		
//	
//		private static final PlxLogger LOGGER = ThreadLocalData.getLogger(TestContactMap.class);
//
//		private static final String prefixSourseModel = InitData.pathForSourseModel;
//		private static final String prefixResult = InitData.pathForResult;
//
//		private static int length = InitData.length;
//		
//		private static FileWrapper fileLog = null;
//	
//
//		@Parameters
//	    public static Collection<Object[]> configs() { 
//	    	
//	    	Object[][] object = new Object[length+1][2];
//
//	    	String str = new String();
//	    	
//	    	for (Integer i = 0; i <= length ; i++) {
//	 			
//	    		if(i<10) str = "0" + "0" + i;
//	 			else
//	 				if (i<100) str = "0" + i;
//		 			else 
//		 				str = i.toString();
//	 			
//	 			object[i][0] = str;
//	 			object[i][1] = prefixSourseModel; 			
//	         }
//
//	        return Arrays.asList(object);
//	    }
//
//		public TestContactMap(String count, String patch){
//			InitTestContactMap.init(patch, prefixResult,  count);
//		}
//		
//		@Before
//		public void setUp() {
//			instanseFileLog();
//		}
//
//		private static void instanseFileLog() {
//			if (fileLog == null) {
//				fileLog = new FileWrapper(InitTestContactMap.getResultPath());
//			}
//		}
//
//		@AfterClass
//		public static void exit() {
//				fileLog.closeFile();
//		}
//		
//		
//		@Test
//		public void test(){
//			
//			
//			SimulationArguments args = InitTestContactMap.getSimulationArguments();
//
//			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP
//					|| args.isSubViews()) {
//
//				Memory memory = new Memory();
//				Timer timer = new Timer();
//				CMainSubViews subViews = new CMainSubViews();
//				printConsole(timer.getPrintToString("Init CMainSubViews.class "));
//				printConsole(memory.getPrintToString("CMainSubViews.class"));
//				
//				
//				ISolution solution = InitTestContactMap.getSolution();
//				List<CRule> rules = InitTestContactMap.getRules();
//				
//				memory.reset();
//				timer.reset();
//				subViews.build(solution, rules);
//				printConsole(timer.getPrintToString("CMainSubViews.build "));
//				printConsole(memory.getPrintToString("CMainSubViews.build"));
//				
//				if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
//					
//					memory.reset();
//					timer.reset();
//					CContactMap contactMap = new CContactMap();
//					printConsole(timer.getPrintToString("Init CContactMap.class "));
//					printConsole(memory.getPrintToString("Init CContactMap.class"));
//										
//					contactMap.setSimulationData(InitTestContactMap.getSimulationData());
//					
//					memory.reset();
//					timer.reset();
//					contactMap.initAbstractSolution();
//					printConsole(timer.getPrintToString("CContactMap.initAbstractSolution "));
//					printConsole(memory.getPrintToString("CContactMap.initAbstractSolution"));
//					
//					memory.reset();
//					timer.reset();
//					contactMap.constructAbstractRules(InitTestContactMap.getRules());
//					printConsole(timer.getPrintToString("CContactMap.constructAbstractRules "));
//					printConsole(memory.getPrintToString("CContactMap.constructAbstractRules"));
//					
//					memory.reset();
//					timer.reset();
//					contactMap.constructAbstractContactMapFromSubViews(subViews);
//					printConsole(timer.getPrintToString("CContactMap.constructAbstractContactMapFromSubViews "));
//					printConsole(memory.getPrintToString("CContactMap.constructAbstractContactMapFromSubViews"));
//					
//				} else {
//					
//					noArgContactMap();
//				
//				}
//
//				
//			} else {
//			
//				noArgContactMap();
//				
//			}
//			
//			
//
//		}
//		
//
//		private void noArgContactMap() {
//			
//			fail("No arg = '--contact-map'");
//			
//		}
//		
//		private void printConsole(String str) {
//				print(str);
//		}
//		
//		private static void print(String message) {
//			System.out.println(message);
//			writeInFile(message);
//		}
//		
//		private static void writeInFile(String str) {
//			instanseFileLog();
//			fileLog.writeInFile(str);
//		}
//
//}
//
package com.plectix.simulator.harness.contactmap;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;
import com.plectix.simulator.util.Memory;
import com.plectix.simulator.util.Timer;

@RunWith(value = Parameterized.class)
public class TestContactMap {

	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;

	@Parameters
	public static Collection<Object[]> configs() {
		Object[][] object = new Object[length][2];

		String str;

		for (Integer i = 1; i <= length; i++) {

			if (i < 10)
				str = "0" + "0" + i;
			else if (i < 100)
				str = "0" + i;
			else
				str = i.toString();

			object[i - 1][0] = str;
			object[i - 1][1] = prefixSourseModel;
		}

		return Arrays.asList(object);
	}

	public TestContactMap(String count, String patch) {
		InitTestContactMap.init(patch, prefixResult, count);
	}

	@Test
	public void test() {

		SimulationArguments args = InitTestContactMap.getSimulationArguments();

		if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP
				|| args.createSubViews()) {

			Memory memory = new Memory();
			Timer timer = new Timer();
			MainSubViews subViews = new MainSubViews();
			printConsole(timer.getPrintToString("Init CMainSubViews.class "));
			printConsole(memory.getPrintToString("CMainSubViews.class"));

			SolutionInterface solution = InitTestContactMap.getSolution();
			List<Rule> rules = InitTestContactMap.getRules();

			memory.reset();
			timer.reset();
			subViews.build(solution, rules);
			printConsole(timer.getPrintToString("CMainSubViews.build "));
			printConsole(memory.getPrintToString("CMainSubViews.build"));

			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {

				memory.reset();
				timer.reset();
				ContactMap contactMap = new ContactMap();
				printConsole(timer.getPrintToString("Init CContactMap.class "));
				printConsole(memory.getPrintToString("Init CContactMap.class"));

				contactMap.setSimulationData(InitTestContactMap
						.getSimulationData().getKappaSystem());

				memory.reset();
				timer.reset();
				contactMap.initAbstractSolution();
				printConsole(timer
						.getPrintToString("CContactMap.initAbstractSolution "));
				printConsole(memory
						.getPrintToString("CContactMap.initAbstractSolution"));

				memory.reset();
				timer.reset();
				contactMap.constructAbstractContactMapFromSubViews(subViews,
						InitTestContactMap.getRules());
				printConsole(timer
						.getPrintToString("CContactMap.constructAbstractContactMapFromSubViews "));
				printConsole(memory
						.getPrintToString("CContactMap.constructAbstractContactMapFromSubViews"));

			} else {

				noArgContactMap();

			}

		} else {

			noArgContactMap();

		}

	}

	private void noArgContactMap() {
		fail("No arg = '--contact-map'");
	}

	private void printConsole(String str) {
		print(str);
	}

	private static void print(String message) {
		// TODO use logger!
//		System.out.println(message);
	}
}

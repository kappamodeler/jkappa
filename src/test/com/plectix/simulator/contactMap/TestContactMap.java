package com.plectix.simulator.contactMap;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.contactMap.CContactMap;
import com.plectix.simulator.components.complex.subviews.CMainSubViews;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.ThreadLocalData;
import com.plectix.simulator.util.PlxLogger;
	


@RunWith(value = Parameterized.class)
public class TestContactMap{
		
	
		private static final PlxLogger LOGGER = ThreadLocalData.getLogger(TestContactMap.class);

		private static final String prefixSourseModel = InitData.pathForSourseModel;
		private static final String prefixResult = InitData.pathForResult;

		private static int length = InitData.length;
	

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
	    	
            //object[length][0] = InitData.nameFileWithOutKappa;
            
	        //object[length][1] = prefixSourseModel;         
	         
	        return Arrays.asList(object);
	    }

		public TestContactMap(String count, String patch){
			InitTestContactMap.init(patch, prefixResult,  count);
		}
		
		
		@Test
		public void test(){
			
			
			SimulationArguments args = InitTestContactMap.getSimulationArguments();

			if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP
					|| args.isSubViews()) {

				Timer timer = new Timer();
				CMainSubViews subViews = new CMainSubViews();
				timer.print("Init CMainSubViews.class ");
				
				
				ISolution solution = InitTestContactMap.getSolution();
				List<CRule> rules = InitTestContactMap.getRules();
				
				timer.reset();
				subViews.build(solution, rules);
				timer.print("CMainSubViews.build ");
				
				if (args.getSimulationType() == SimulationArguments.SimulationType.CONTACT_MAP) {
					
					timer.reset();
					CContactMap contactMap = new CContactMap();
					
					
					timer.print("Init CContactMap.class ");
					
					//contactMap.setSimulationData(simulationData);
					timer.reset();
					contactMap.initAbstractSolution();
					timer.print("CContactMap.initAbstractSolution ");
					
					timer.reset();
					contactMap.constructAbstractRules(InitTestContactMap.getRules());
					timer.print("CContactMap.constructAbstractRules ");
					
					timer.reset();
					contactMap.constructAbstractContactMapFromSubViews(subViews);
					timer.print("CContactMap.constructAbstractContactMapFromSubViews ");
					
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

}


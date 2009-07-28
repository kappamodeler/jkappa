package com.plectix.simulator.enumerationOfSpecies;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.complex.enumerationOfSpecies.GeneratorSpecies;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
	


@RunWith(value = Parameterized.class)
public class TestEnumOfSpecies {
		
		private static final String prefixSourseModel = InitData.pathForSourseModel;
		private static final String prefixResult = "";

		private static int length = InitData.length;
		
		private CLocalViewsMain localViews;
		
		private GeneratorSpecies generatorSpecies;
		
		private Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		private static String currentCount;
		
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

		public TestEnumOfSpecies(String count, String patch){
			currentCount = count;
			System.out.println("Test " + count);
			InitTestEnumOfSpecies.init(patch, prefixResult,  count);
			initResult();
		}
		
		private void initResult() {

			resultMap.put("001", 6);
			resultMap.put("002", 356);
			resultMap.put("003", 8);
			resultMap.put("004", 0);
			resultMap.put("005", 83);
			resultMap.put("006", 62);
			resultMap.put("007", 8);
	
		}

		@Before
		public void setUp() {
			localViews = new CLocalViewsMain(InitTestEnumOfSpecies.getSubViews());
			localViews.buildLocalViews();
		}
		
		@Test
		public void test(){
			generatorSpecies = new GeneratorSpecies(localViews.getLocalViews());
			generatorSpecies.enumerate();
			Assert.assertEquals("[Error] Species", generatorSpecies.getSpecies().keySet().size(), resultMap.get(currentCount).intValue());
		}



		
}


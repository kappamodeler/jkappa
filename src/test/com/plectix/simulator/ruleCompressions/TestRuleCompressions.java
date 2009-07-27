package com.plectix.simulator.ruleCompressions;

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

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CRule;
import com.plectix.simulator.components.complex.abstracting.CAbstractAgent;
import com.plectix.simulator.components.complex.localviews.CLocalViewsMain;
import com.plectix.simulator.components.complex.subviews.IAllSubViewsOfAllAgents;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.rulecompression.QualitativeCompressor;
import com.plectix.simulator.utilsForTest.FileWrapper;
import com.plectix.simulator.utilsForTest.ReadAndParserFile;



@RunWith(value = Parameterized.class)
public class TestRuleCompressions {

	private static FileWrapper fileLog = null; 

	private static final String prefixSourseModel = InitData.pathForSourseModel;
	private static final String prefixResult = InitData.pathForResult;

	private static int length = InitData.length;
	
	private static final String FILENAME_EXTENSION = InitData.FILENAME_EXTENSION;
	
	private static boolean isConsole = InitData.isPrintinConsoleAndFile;
	
	private static final String SPLITER = " ";
	private static final String  TOTAL = "TOTAL";
	
	private CLocalViewsMain localViews;
	private List<CRule> rules;
	
	
	
	
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

	public TestRuleCompressions(String count, String patch){
		System.out.println("Test " + count);
		InitTestRuleCompressions.init(patch, prefixResult,  count);
	}
	
	@Before
	public void setUp() {

		initLocalViews(InitTestRuleCompressions.getSubViews());
		this.rules = InitTestRuleCompressions.getRules();
		
	}


	
	private void initLocalViews(IAllSubViewsOfAllAgents subViews) {
	
		if (localViews == null) {
			localViews = new CLocalViewsMain(subViews);
			localViews.buildLocalViews();
		}
	}
	
	@Test
	public void testQualitativeCompression() {
		for(CRule rule : rules){
			QualitativeCompressor q = new QualitativeCompressor(localViews);
			q.compress(rule);
			CRule comp = q.getCompressedRule();
			for(IConnectedComponent ic : comp.getLeftHandSide()){
				if(ic.getAgents().isEmpty())
					continue;
				System.out.println(((CConnectedComponent)(ic)).getHash());
			}
			for(IConnectedComponent ic : comp.getRightHandSide()){
				if(ic.getAgents().isEmpty())
					continue;
				System.out.println(((CConnectedComponent)(ic)).getHash());
			}
			
		}
		
		
	}

	


	private String getSoursePath(String count) {
		return InitData.pathForSource + "source" + count + FILENAME_EXTENSION; //".txt";
	}
	

	

	

	


}

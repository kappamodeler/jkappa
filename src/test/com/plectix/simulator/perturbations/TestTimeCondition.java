package com.plectix.simulator.perturbations;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.CRule;
import com.plectix.simulator.util.Failer;

public class TestTimeCondition extends TestPerturbation {
	
	private String myTestFileName = "";
	private static final String myPrefixFileName = "test.data/perturbations/";
	private Failer myFailer = new Failer(); 
	private CRule myActiveRule;
	
	public TestTimeCondition(String fileName) {
		super(fileName);
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		return getAllTestFileNames(myPrefixFileName);
	}
	
	public String getPrefixFileName() {
		return myPrefixFileName;
	}
	
	public void init() {
	}
	
	@Test
	public void test() {
		myActiveRule = getRuleByName("intro_a");
		System.out.println(myActiveRule.getRuleRate());
	}
	                    
}

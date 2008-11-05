package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.util.*;
import com.plectix.simulator.components.*;

@RunWith(Parameterized.class)
public class TestParseAgents extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix() + "agents/";
	private Parser myParser;
	private final SubstanceConstructor mySubstanceConstructor = new SubstanceConstructor();
	private String myTestFileName;
	private EasyFileReader myReader;
	private List<CAgent> myExpectedAgent = new ArrayList<CAgent>();
	private List<CAgent> myActualAgent = new ArrayList<CAgent>();
	private CConnectedComponent myCC;
	private String myExpectedCC;
	
	private class SiteCollectionsComparator extends CollectionsComparator {
		public boolean equals(Object a, Object b) {
			if (a == b) {
				return true;
			}
			
			if (!(a instanceof CSite) || !(b instanceof CSite)) {
				return false;
			}
			
			CSite aa = (CSite)a;
			CSite bb = (CSite)b;
			
			return (aa.getName().equals(bb.getName())
					&& aa.getInternalState().equals(bb.getInternalState())
					&& (aa.getLinkIndex() == bb.getLinkIndex()));
		}
	}
	
	private final Failer myFailer = new Failer() {
		public boolean collectionElementEquals(Object a, Object b) {
			if (a == b) {
				return true;
			}
			
			if (!(a instanceof CAgent) || !(b instanceof CAgent)) {
				return false;
			}
			CAgent aa = (CAgent)a;
			CAgent bb = (CAgent)b;

			if (!(aa.equals(bb))) {
				return false;
			}

			return (new SiteCollectionsComparator()).areEqual(aa.getSiteMap().values(), 
					bb.getSiteMap().values());
		}
	};
	
	
	public TestParseAgents(String fileName) {
		super();
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
		try {
			myReader = new EasyFileReader(myTestFileNamePrefix + myTestFileName);
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
	
	@Parameters
	public static Collection<Object[]> regExValues() {
		return DirectoryTestsRunner.getAllTestFileNames(myTestFileNamePrefix);
	}
	
	@Before
	public void readData() {
		myParser = new Parser(new DataReading(myTestFileName));
		
		String line = "";
		CAgent currentAgent;
		List<CSite> currentSites  = new ArrayList<CSite>(); 
		String agentName = "";
		
		while (line != null) {
			if (!"".equals(line)) {
				if (line.startsWith("#substance")) {
					line = line.substring(11);
					myExpectedCC = line;
					try {
						myExpectedAgent.addAll(myParser.parseAgent(line));
					} catch(ParseErrorException e) {
						myFailer.fail(e.getMessage());
					}
				} else if (line.startsWith("#site")) {
					line = line.substring(6);
					String[] split = line.split("~");
					CSite site;
					String[] linkSplit;
					if (split.length > 1) {
						linkSplit =  split[1].split("!");
						site = mySubstanceConstructor.createSite(split[0], linkSplit[0]);
					} else {
						linkSplit =  split[0].split("!");
						site = mySubstanceConstructor.createSite(linkSplit[0], null);
					}
					
					currentSites.add(site);
					
					if (linkSplit.length > 1) {
						site.setLinkIndex(Integer.parseInt(linkSplit[1]));
					}
				} else if (line.startsWith("#agent")) {
					line = line.substring(7);
					if (!"".equals(agentName)) {
						currentAgent = mySubstanceConstructor.createAgent(agentName, currentSites);
						myActualAgent.add(currentAgent);
						currentSites.clear();
					}
					agentName = line;
				}
			} 
			line = myReader.getStringFromFile();
		}
		currentAgent = mySubstanceConstructor.createAgent(agentName, currentSites);
		myActualAgent.add(currentAgent);
		myCC = mySubstanceConstructor.createCC(new LinkedList<CAgent>(myActualAgent));
	}
	
	@After
	public void clear() {
		myExpectedAgent.clear();
		myActualAgent.clear();
	}
	
	@Test
	public void testParseAgent() {
		myFailer.loadTestFile(myTestFileName);
		myFailer.assertEquals("", myExpectedAgent, myActualAgent);
	}
	
	@Test
	public void testCCCorrection() {
		myFailer.loadTestFile(myTestFileName);
		myFailer.assertEquals("", Converter.toString(myCC), myExpectedCC);
	}
	
	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}
}

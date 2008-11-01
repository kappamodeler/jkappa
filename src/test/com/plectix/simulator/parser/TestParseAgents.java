package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;
import com.plectix.simulator.simulator.DataReading;
import com.plectix.simulator.util.*;
import com.plectix.simulator.components.*;

@RunWith(Parameterized.class)
public class TestParseAgents extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests.getFileNamePrefix() + "agents/";
	private Parser myParser;
	//private final MessageConstructor myMC = new MessageConstructor();
	
	private final Failer myFailer = new Failer() {
		public boolean myEquals(Object a, Object b) {
			if (!(a instanceof CAgent) || !(b instanceof CAgent)) {
				return false;
			}
			CAgent aa = (CAgent)a;
			CAgent bb = (CAgent)b;
			
			if (!(aa.equals(bb))) {
				return false;
			}
			
			for (CSite site : aa.getSiteMap().values()) {
				boolean contains = false;
				for (CSite site2 : bb.getSiteMap().values()) {
					if (site.getName().equals(site2.getName())
							&& site.getInternalState().equals(site2.getInternalState())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					return false;
				}
			}
			
			for (CSite site : bb.getSiteMap().values()) {
				boolean contains = false;
				for (CSite site2 : aa.getSiteMap().values()) {
					if (site.getName().equals(site2.getName())
							&& site.getInternalState().equals(site2.getInternalState())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					return false;
				}
			}
			
			return true;
		}
	};
	private final SubstanceConstructor mySubstanceConstructor = new SubstanceConstructor();
	
	private String myTestFileName;
	private EasyFileReader myReader;
	private Set<CAgent> myExpectedAgent = new HashSet<CAgent>();
	private Set<CAgent> myActualAgent = new HashSet<CAgent>();
	
	
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
					try {
						myExpectedAgent.addAll(myParser.parseAgent(line));
					} catch(ParseErrorException e) {
						myFailer.fail(e.getMessage());
					}
				} else if (line.startsWith("#site")) {
					line = line.substring(6);
					String[] split = line.split("~");
					if (split.length > 1) {
						currentSites.add(mySubstanceConstructor.createSite(split[0], split[1]));
					} else {
						currentSites.add(mySubstanceConstructor.createSite(split[0], null));
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
	
	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}
}

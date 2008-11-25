package com.plectix.simulator.parser;

import java.io.FileNotFoundException;
import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import com.plectix.simulator.DirectoryTestsRunner;
import com.plectix.simulator.util.*;
import com.plectix.simulator.components.*;
import com.plectix.simulator.interfaces.*;

@RunWith(Parameterized.class)
public class TestParseAgents extends DirectoryTestsRunner {
	private static final String myTestFileNamePrefix = RunParserTests
			.getFileNamePrefix()
			+ "agents/";
	private final SubstanceConstructor mySubstanceConstructor = new SubstanceConstructor();
	private String myTestFileName;
	private EasyFileReader myReader;
	private List<IAgent> myActualAgents = new ArrayList<IAgent>();
	private IConnectedComponent myCC;
	private String myExpectedCC;

	private class SiteCollectionsComparator extends CollectionsComparator {
		@Override
		public boolean equals(Object a, Object b) {
			if (a == b) {
				return true;
			}

			if (!(a instanceof CSite) || !(b instanceof CSite)) {
				return false;
			}

			CSite aa = (CSite) a;
			CSite bb = (CSite) b;

			return (aa.getName().equals(bb.getName())
					&& aa.getInternalState().equals(bb.getInternalState()) && (aa
					.getLinkIndex() == bb.getLinkIndex()));
		}
	}

	private final Failer myFailer = new Failer() {
		@Override
		public boolean collectionElementEquals(Object a, Object b) {
			if (a == b) {
				return true;
			}

			if (!(a instanceof CAgent) || !(b instanceof CAgent)) {
				return false;
			}
			CAgent aa = (CAgent) a;
			CAgent bb = (CAgent) b;

			if (!(aa.equals(bb))) {
				return false;
			}

			return (new SiteCollectionsComparator()).areEqual(aa.getSiteMap()
					.values(), bb.getSiteMap().values());
		}
	};

	public TestParseAgents(String fileName) {
		super();
		clear();
		myTestFileName = fileName;
		myFailer.loadTestFile(myTestFileName);
		try {
			myReader = new EasyFileReader(myTestFileNamePrefix + myTestFileName);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		readData();
	}

	@Parameters
	public static Collection<Object[]> regExValues() {
		return DirectoryTestsRunner.getAllTestFileNames(myTestFileNamePrefix);
	}

	private ISite parseSite(String line) {
		boolean wildcard = line.endsWith("?");
		boolean bounded = line.contains("!");
		boolean internal = line.contains("~");

		String siteName = "";
		String internalStateName = null;
		String linkIndex = null;

		String[] boundSplit = line.split("!");
		
		if (internal) {
			String[] split = line.split("~");
			siteName = split[0];
			if (wildcard) {
				int length = split[1].length();
				internalStateName = split[1].substring(0, length - 1);
			} else {
				internalStateName = split[1].split("!")[0];
			}
		} else {
			if (wildcard) {
				int length = line.length();
				siteName = line.substring(0, length - 1);
			} else {
				siteName = boundSplit[0];
			}
		}
		
		if (wildcard) {
			linkIndex = "?"; 
		} else if (bounded) {
			linkIndex = boundSplit[1];
		}
		
		return mySubstanceConstructor.createSite(siteName, internalStateName,
				linkIndex);

	}

	private void readData() {
		myFailer.loadTestFile(myTestFileName);

		String line = "";
		IAgent currentAgent;
		List<ISite> currentSites = new ArrayList<ISite>();
		String agentName = "";

		while (line != null) {
			if (!"".equals(line)) {
				if (line.startsWith("#substance")) {
					line = line.substring(11);
					myExpectedCC = line;
				} else if (line.startsWith("#site")) {
					line = line.substring(6);
					currentSites.add(parseSite(line));
				} else if (line.startsWith("#agent")) {
					line = line.substring(7);
					if (!"".equals(agentName)) {
						currentAgent = mySubstanceConstructor.createAgent(
								agentName, currentSites);
						myActualAgents.add(currentAgent);
						currentSites.clear();
					}
					agentName = line;
				}
			}
			line = myReader.getStringFromFile();
		}
		currentAgent = mySubstanceConstructor.createAgent(agentName,
				currentSites);
		myActualAgents.add(currentAgent);
		myCC = mySubstanceConstructor.createCC(myActualAgents);
	}

	private void clear() {
		myActualAgents.clear();
	}

	@Test
	public void testCCCorrection() {
		myFailer.assertEquals("", Converter.toString(myCC), myExpectedCC);
	}

	@Override
	public String getPrefixFileName() {
		return myTestFileNamePrefix;
	}
}

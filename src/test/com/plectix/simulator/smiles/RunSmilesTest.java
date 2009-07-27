package com.plectix.simulator.smiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.RunAllTests;
import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSite;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.IncompletesDisabledException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.AgentFactory;

@RunWith(Parameterized.class)
public class RunSmilesTest {

	private static final String separator = File.separator;
	private static final String prefix = "test.data" + separator + "smiles"
			+ separator;

	private static long id = 0;

	private String filepath;

	@Parameters
	public static Collection<Object[]> data() {
		String[] files = new String[] { 
				"twoagents" + RunAllTests.FILENAME_EXTENSION, 
				"ring" + RunAllTests.FILENAME_EXTENSION, 
				"tworings" + RunAllTests.FILENAME_EXTENSION,
				"polymer" + RunAllTests.FILENAME_EXTENSION, 
				"branchstructure" + RunAllTests.FILENAME_EXTENSION, 
				"cubane" + RunAllTests.FILENAME_EXTENSION, 
				"polyhedron" + RunAllTests.FILENAME_EXTENSION,
				"fullerene" + RunAllTests.FILENAME_EXTENSION, 
				"fusedrings" + RunAllTests.FILENAME_EXTENSION};
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String string : files) {
			Object[] obj = new Object[1];
			obj[0] = string;
			data.add(obj);
		}
		return data;
	}

	public RunSmilesTest(String filename) {
		filepath = prefix + filename;
		SimulationMain.initializeLogging();
	}

	@Test
	public void test() {
		String line = "";
		int lineCounter = 0;
		AgentFactory af = new AgentFactory(false);
		CConnectedComponent cc;
		StringBuffer fails;
		SmilesTest smilestest;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			line = br.readLine();
			
			while (line != null) {
				lineCounter++;
				cc = new CConnectedComponent(buildAgents(af.parseAgent(line)));
				fails = new StringBuffer();
				smilestest = new SmilesTest(cc);
				fails.append(smilestest.test());
				if (fails.length() > 0)
					org.junit.Assert.fail("\nfile: " + filepath + "\nline: "
							+ lineCounter + fails.toString());

				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			org.junit.Assert.fail("wrong file path: " + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			org.junit.Assert.fail("parse error in line:" + line);
		} catch (DocumentFormatException e) {
			e.printStackTrace();
		}
	}

	// / SubstanceBuilder
	public static List<CAgent> buildAgents(List<AbstractAgent> agents) {
		if (agents == null) {
			return null;
		}

		List<CAgent> result = new LinkedList<CAgent>();

		for (AbstractAgent agent : agents) {
			CAgent newAgent = buildAgent(agent);
			result.add(newAgent);
		}

		Map<Integer, CSite> map = new LinkedHashMap<Integer, CSite>();
		for (CAgent agent : result) {
			for (CSite site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index == -1) {
					continue;
				}
				CSite connectedSite = map.get(index);
				if (connectedSite == null) {
					map.put(index, site);
				} else {
					connectedSite.getLinkState().connectSite(site);
					site.getLinkState().connectSite(connectedSite);
					map.remove(site.getLinkIndex());
				}
			}
		}

		return result;
	}

	private static CAgent buildAgent(AbstractAgent agent) {
		CAgent resultAgent = new CAgent(agent.getNameId(),
				generateNextAgentId());
		for (AbstractSite site : agent.getSites()) {
			CSite newSite = buildSite(site);
			resultAgent.addSite(newSite);
		}

		return resultAgent;
	}

	private static long generateNextAgentId() {
		return ++id;
	}

	private static CSite buildSite(AbstractSite site) {
		CSite newSite = new CSite(site.getNameId());
		newSite.getLinkState().setStatusLink(
				site.getLinkState().getStatusLink());
		newSite.setLinkIndex(site.getLinkIndex());
		int internalStateNameId = site.getInternalStateNameId();
		if (internalStateNameId != CSite.NO_INDEX) {
			newSite.setInternalState(new CInternalState(internalStateNameId));
		}
		return newSite;
	}

}
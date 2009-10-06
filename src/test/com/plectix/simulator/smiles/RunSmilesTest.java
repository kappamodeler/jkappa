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
import com.plectix.simulator.component.Agent;
import com.plectix.simulator.component.ConnectedComponent;
import com.plectix.simulator.component.InternalState;
import com.plectix.simulator.component.Site;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelSite;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.util.DefaultPropertiesForTest;
import com.plectix.simulator.util.NameDictionary;

@RunWith(Parameterized.class)
public class RunSmilesTest extends DefaultPropertiesForTest {

	private static final String separator = File.separator;
	private static final String prefix = "test.data" + separator + "smiles"
			+ separator;

	private static long id = 0;

	private String filepath;

	@Parameters
	public static Collection<Object[]> data() {
		String[] files = new String[] {
				"twoagents" + DEFAULT_EXTENSION_FILE,
				"ring" + DEFAULT_EXTENSION_FILE,
				"tworings" + DEFAULT_EXTENSION_FILE,
				"polymer" + DEFAULT_EXTENSION_FILE,
				"branchstructure" + DEFAULT_EXTENSION_FILE,
				"cubane" + DEFAULT_EXTENSION_FILE,
				"polyhedron" + DEFAULT_EXTENSION_FILE,
				"fullerene" + DEFAULT_EXTENSION_FILE,
				"fusedrings" + DEFAULT_EXTENSION_FILE };
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
		ConnectedComponent cc;
		StringBuffer fails;
		SmilesTest smilestest;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			line = br.readLine();

			while (line != null) {
				lineCounter++;
				cc = new ConnectedComponent(buildAgents(af.parseAgent(line)));
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
	public static List<Agent> buildAgents(List<ModelAgent> agents) {
		if (agents == null) {
			return null;
		}

		List<Agent> result = new LinkedList<Agent>();

		for (ModelAgent agent : agents) {
			Agent newAgent = buildAgent(agent);
			result.add(newAgent);
		}

		Map<Integer, Site> map = new LinkedHashMap<Integer, Site>();
		for (Agent agent : result) {
			for (Site site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index == -1) {
					continue;
				}
				Site connectedSite = map.get(index);
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

	private static Agent buildAgent(ModelAgent agent) {
		Agent resultAgent = new Agent(agent.getName(), generateNextAgentId());
		for (ModelSite site : agent.getSites()) {
			Site newSite = buildSite(site);
			resultAgent.addSite(newSite);
		}

		return resultAgent;
	}

	private static long generateNextAgentId() {
		return ++id;
	}

	private static Site buildSite(ModelSite site) {
		Site newSite = new Site(site.getName());
		newSite.getLinkState().setStatusLink(
				site.getLinkState().getStatusLink());
		newSite.setLinkIndex(site.getLinkIndex());
		String internalStateName = site.getInternalStateName();
		if (!NameDictionary.isDefaultInternalStateName(internalStateName)) {
			newSite.setInternalState(new InternalState(internalStateName));
		}
		return newSite;
	}

}
package com.plectix.simulator.smiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CInternalState;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.parser.abstractmodel.AbstractAgent;
import com.plectix.simulator.parser.abstractmodel.AbstractSite;
import com.plectix.simulator.parser.abstractmodel.AbstractSolution;
import com.plectix.simulator.parser.abstractmodel.SolutionLineData;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.util.AgentFactory;

@RunWith(Parameterized.class)
public class RunSmilesTest{
	
	private String filepath;
	private static long id = 0;
	private final String LOG4J_PROPERTIES_FILENAME = "config/log4j.properties";
	
	
	private List<CConnectedComponent> cclist;
	 @Parameters
     public static Collection<Object[]> data() {
    	 Object[][] data = new Object[][] { 
    			 {"test.data\\smiles\\singleagent"}, 
             	 {"test.data\\smiles\\twoagents"}, 
             	 {"test.data\\smiles\\ring"}, 
             	 {"test.data\\smiles\\tworings"}  
//             	 {"test\\data\\smiles\\polymer"} 
             	 };
             return Arrays.asList(data);
     }


     public RunSmilesTest(String filePath) {
    	 filepath = filePath;
	 }

    @Before
	public void readFile() {
    	PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
    	String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			line = br.readLine();
			AgentFactory af = new AgentFactory();
			List<AbstractAgent> list = new ArrayList<AbstractAgent>();
			AbstractSolution asolution = new AbstractSolution();
			cclist = new ArrayList<CConnectedComponent>();
			while(line != null){
				if (line.charAt(0)!='#'){
					list = af.parseAgent(line);
					asolution.addAgents(1, list);
					for (SolutionLineData lineData : asolution.getAgents()) {
						List<CAgent> agents = buildAgents(lineData.getAgents());
						cclist.add(new CConnectedComponent(agents));
					}
				}
				line = br.readLine();
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("wrong file path: " + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			System.err.println("parse error in line:" + line);
		} catch (DocumentFormatException e) {
			e.printStackTrace();
		}
		
	}
		
    
    @Test
    public void test(){
    	for (int i = 0; i < cclist.size(); i++) {
    		SmilesTest smilestest = new SmilesTest(cclist.get(i), i);
    		smilestest.test();
		}
    }
    
	/// SubstanceBuilder
	public List<CAgent> buildAgents(List<AbstractAgent> agents) {
		if (agents == null) {
			return null;
		}

		List<CAgent> result = new LinkedList<CAgent>();
		
		for (AbstractAgent agent : agents) {
			CAgent newAgent = buildAgent(agent);
			result.add(newAgent);
		}

		Map<Integer, CSite> map = new HashMap<Integer, CSite>();
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
 
	private CAgent buildAgent(AbstractAgent agent) {
		CAgent resultAgent = new CAgent(agent.getNameId(), generateNextAgentId());
		for (AbstractSite site : agent.getSites()) {
			CSite newSite = buildSite(site);
			resultAgent.addSite(newSite);
		} 
		
		return resultAgent;
	}
	
	private static long generateNextAgentId() {
		return ++id;
	}


	private CSite buildSite(AbstractSite site) {
		CSite newSite = new CSite(site.getNameId());
		newSite.getLinkState().setStatusLink(site.getLinkState().getStatusLink());
		newSite.setLinkIndex(site.getLinkIndex());
		int internalStateNameId = site.getInternalStateNameId();
		if (internalStateNameId != CSite.NO_INDEX) {
			newSite.setInternalState(new CInternalState(internalStateNameId));
		}
		return newSite;
	}
		

}

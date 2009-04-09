package com.plectix.simulator.smiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.plectix.simulator.SimulationMain;
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
	
	private static final String separator = File.separator;
	private static final String prefix = "test.data"+ separator + "smiles" + separator;

	private static long id = 0;

	private String filepath;
	private CConnectedComponent cc;
	private int line;
	
	 @Parameters
     public static Collection<Object[]> data() {
    	 String[] files = new String[] { 
    			 "singleagent", 
             	 "twoagents", 
             	 "ring", 
             	 "tworings",  
             	 "polymer", 
             	 "branchstructure",
             	 "polyhedron"
             	 };
    	 List<CConnectedComponent> cclist;
    	 Collection<Object[]> data = new ArrayList<Object[]>();
    	 for (String string : files) {
			cclist = readFile(prefix + string);
			for (int i = 0; i < cclist.size(); i++) {
				Object[] obj = new Object[3];
				obj[0] = cclist.get(i);
				obj[1] = i;
				obj[2] = string;
				data.add(obj);
			}
		}
             return data;
     }


     public RunSmilesTest(CConnectedComponent c, int linecount, String filename) {
    	 cc = c;
    	 line = linecount;
    	 filepath = filename;
    	 SimulationMain.initializeLogging();
	 }

	public static List<CConnectedComponent> readFile(String filepath) {
    	String line = "";
    	List<CConnectedComponent> cclist = new ArrayList<CConnectedComponent>();
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
				}
				line = br.readLine();
			}
		
			for (SolutionLineData lineData : asolution.getAgents()) {
				List<CAgent> agents = buildAgents(lineData.getAgents());
				cclist.add(new CConnectedComponent(agents));
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
		return cclist;
		
	}
		
    
    @Test
    public void test(){
    	StringBuffer fails = new StringBuffer();
		SmilesTest smilestest = new SmilesTest(cc);
		fails.append(smilestest.test());
    	if (fails.length()>0)
    		org.junit.Assert.fail("\nfile: " + filepath + "\nline: " + line + fails.toString());
    }
    
	/// SubstanceBuilder
	public static List<CAgent> buildAgents(List<AbstractAgent> agents) {
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
 
	private static CAgent buildAgent(AbstractAgent agent) {
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


	private static CSite buildSite(AbstractSite site) {
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
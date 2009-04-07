package com.plectix.simulator.smiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CSite;
import com.plectix.simulator.components.string.ConnectedComponentToSmilesString;
import com.plectix.simulator.util.Failer;

public class SmilesTest {

	private CConnectedComponent ccomponent;
	private String uniqueKappaString;
	private int line;
	private ConnectedComponentToSmilesString connectedComponentToSmilesString;
	private Failer failer = new Failer();

	public SmilesTest(CConnectedComponent cc, int i) {
		ccomponent = cc;
		line = i + 1;
	}

	public void test() {
		int size = ccomponent.getAgents().size();
		connectedComponentToSmilesString = ConnectedComponentToSmilesString.getInstance();
		uniqueKappaString = connectedComponentToSmilesString
				.toUniqueString(ccomponent);
		if(size == 1) 
			testSites();
		else {
			testAgents(size);
//			testLinkIndexes();
		}
	}


	
	private void testAgents(int size) {
		List<CAgent> list = copy(ccomponent.getAgents());

		if (size == 2) {
			list = reverse(list);
			String smilesString = connectedComponentToSmilesString
					.toUniqueString(new CConnectedComponent(list));
			failer.assertEquals(line + ": \n" + uniqueKappaString + ",\n"
					+ smilesString, uniqueKappaString, smilesString);

		} else if (size > 2) {
			for (int i = 0; i < size; i++) {
				Collections.shuffle(list);
				String smilesString = connectedComponentToSmilesString
						.toUniqueString(new CConnectedComponent(list));
				failer.assertEquals("line " + line + ": ", uniqueKappaString, smilesString);
			}
		}

	}
	
	private void testSites() {
		int max = 0;
		for (CAgent agent : ccomponent.getAgents()) {
			if (max < agent.getSites().size())
				max = agent.getSites().size();
		}
		for (int i = 0; i < max; i++) {
			List<CAgent> list = new ArrayList<CAgent>();
			for (CAgent agent : ccomponent.getAgents()) {
				List<CSite> sites = copy(new ArrayList<CSite>(agent.getSites()));
				Collections.shuffle(sites);
				CAgent a = new CAgent(agent.getNameId(), 1);

				for (int j = 0; j < sites.size(); j++) {
					a.addSite(sites.get(j));
				}
				list.add(a);
			}
			String smilesString = connectedComponentToSmilesString
					.toUniqueString(new CConnectedComponent(list));
			failer.assertEquals(line + ": \n" + uniqueKappaString + ",\n"
					+ smilesString, uniqueKappaString, smilesString);
		}

	}

	
	private void testLinkIndexes() {
		HashMap<Integer, List<CSite>> links = new HashMap<Integer, List<CSite>>();
		List<CSite> sites = new ArrayList<CSite>();
		for (CAgent agent : ccomponent.getAgents()) {
			for (CSite site : agent.getSites()) {
				int index = site.getLinkIndex();
				if (index!=-1){
					if (links.containsKey(index)){
						sites = links.get(links.get(index));
					} else {
						sites = new ArrayList<CSite>();
					}
					sites.add(site);
					links.put(index, sites);
				}
			}
		}
		Random rnd = new Random();
		for (Integer linkIndex : links.keySet()) {
			Integer r = rnd.nextInt(1000);
			while (links.containsKey(r))
				r = rnd.nextInt(1000);
			sites = links.get(linkIndex);
			links.remove(linkIndex);
			links.put(r, sites);
		}
		for (Integer linkIndex : links.keySet()) {
			for (CSite site : links.get(linkIndex)) {
				site.setLinkIndex(linkIndex);
			}
		}
		
		String smilesString = connectedComponentToSmilesString
			.toUniqueString(ccomponent);
		failer.assertEquals(line + ": \n" + uniqueKappaString + ",\n"
				+ smilesString, uniqueKappaString, smilesString);
		
		
	}


	private List<CAgent> reverse(List<CAgent> list) {
		ArrayList<CAgent> reverse = new ArrayList<CAgent>(2);
		reverse.add(list.get(1));
		reverse.add(list.get(0));
		return reverse;
	}

	private <E> List<E> copy(List<E> agents) {
		List<E> copy = new ArrayList<E>();
		for (E agent : agents) {
			copy.add(agent);
		}
		return copy;
	}

}

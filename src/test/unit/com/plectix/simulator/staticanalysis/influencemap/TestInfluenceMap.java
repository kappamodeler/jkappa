package com.plectix.simulator.staticanalysis.influencemap;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.staticanalysis.Agent;
import com.plectix.simulator.staticanalysis.LibraryOfRules;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.influencemap.future.InfluenceMapWithFuture;
import com.plectix.simulator.staticanalysis.subviews.MainSubViews;
import com.plectix.simulator.staticanalysis.subviews.base.AbstractionRule;


public class TestInfluenceMap {
	
	static LibraryOfRules library = new LibraryOfRules();
	List<Rule> rules = new LinkedList<Rule>();
	HashMap<Integer, List<Integer>> activations = new HashMap<Integer,List<Integer>>();
	HashMap<Integer, List<Integer>> inhibitions = new HashMap<Integer,List<Integer>>();
	MainSubViews subViews;
	
	@Before
	public void testI() throws IncompletesDisabledException, ParseErrorException, DocumentFormatException{
		
		subViews = new MainSubViews();
		

		
	}

	@After
	public void check() {
		subViews.constructAbstractRules(rules);
		List<AbstractionRule> list = subViews.getAbstractRules();
		subViews.fillModelMapOfAgents(new LinkedList<Agent>(), rules);
		subViews.constructClasses(list);
		InfluenceMap im = new InfluenceMapWithFuture();
		
		im.initInfluenceMap(list, null, null, subViews.getAgentNameToAgent());
		
		for(Integer i: activations.keySet()){
			assertTrue(concord(im.getActivationByRule(i),activations.get(i)));
		}
		
		for(Integer i: inhibitions.keySet()){
			assertTrue(concord(im.getInhibitionByRule(i),inhibitions.get(i)));
		}
	}


	private boolean concord(List<Integer> list1, List<Integer> list2) {
		
		if(list1==null&&(list2==null||list2.isEmpty()))
				return true;
		if(list2==null&&list1.isEmpty())
				return true;
		
		
		if(list1.size()!=list2.size())
			return false;
		
		for(int i: list1){
			if(!list2.contains(i))
				return false;
		}
		return true;
	}

	@Test
	public void fillData() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		
		int i1=1;
		init(i1);
		int i2=2;
		init(i2);
		int i3=3;
		init(i3);
		int i4=4;
		init(i4);
		
		Rule r1 = library.getRuleByString("A(x!1),B(x!1) -> A(x),B(x)");
		r1.setRuleID(i1);
		Rule r2 = library.getRuleByString("A(x),B(x) -> A(x!1),B(x!1)");
		r2.setRuleID(i2);
		
		Rule r3 = library.getRuleByString("A(x),B(x) -> A(x!1),C(x!1)");
		r3.setRuleID(i3);
		
		Rule r4 = library.getRuleByString("C(x) -> A(x),B(x)");
		r4.setRuleID(i4);
		
		
		activations.get(i1).add(i2);
		activations.get(i2).add(i1);
		inhibitions.get(i1).add(i1);
		inhibitions.get(i2).add(i2);

		
		
		
		
		
		
		rules.add(r1);
		rules.add(r2);
	}


	private void init(int i) {
		activations.put(i, new LinkedList<Integer>());
		inhibitions.put(i, new LinkedList<Integer>());
		
	}

}

package com.plectix.simulator.staticanalysis;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;


public class TestRule {
	public static LibraryOfSpecies libraryOfSpecies = new LibraryOfSpecies();

	@Test
	public void testAutomorphismNumber() throws IncompletesDisabledException, ParseErrorException, DocumentFormatException{
		ConnectedComponentInterface c1 = libraryOfSpecies.getConnectedComponent("A()");
		ConnectedComponentInterface c2 = libraryOfSpecies.getConnectedComponent("A()");
		ConnectedComponentInterface c3 = libraryOfSpecies.getConnectedComponent("A()");
		ConnectedComponentInterface c4 = libraryOfSpecies.getConnectedComponent("A(x)");
		ConnectedComponentInterface c5 = libraryOfSpecies.getConnectedComponent("A(x~u)");
		ConnectedComponentInterface c6 = libraryOfSpecies.getConnectedComponent("A(x~u)");
		ConnectedComponentInterface c7 = libraryOfSpecies.getConnectedComponent("A(x~u,y~p)");
		ConnectedComponentInterface c8 = libraryOfSpecies.getConnectedComponent("A(x!1),B(x!1)");
		ConnectedComponentInterface c9 = libraryOfSpecies.getConnectedComponent("A(x!1),B(x!1)");
		ConnectedComponentInterface c10 = libraryOfSpecies.getConnectedComponent("A(x!1),B(x!1)");
		ConnectedComponentInterface c11 = libraryOfSpecies.getConnectedComponent("A(x!1),B(x!1)");
		ConnectedComponentInterface c12 = libraryOfSpecies.getConnectedComponent("A(x!1),B(x!1,y~p)");
		
		
		List<ConnectedComponentInterface> list1 = new LinkedList<ConnectedComponentInterface>();
		list1.add(c1);
		list1.add(c2);
		list1.add(c3);
		list1.add(c4);
		
		
		List<ConnectedComponentInterface> list2 = new LinkedList<ConnectedComponentInterface>();
		list2.add(c3);
		list2.add(c4);
		list2.add(c5);
		list2.add(c6);
		
		
		
		List<ConnectedComponentInterface> list3 = new LinkedList<ConnectedComponentInterface>();
		list3.add(c7);
		list3.add(c8);
		list3.add(c9);
		list3.add(c10);
	
		List<ConnectedComponentInterface> list4 = new LinkedList<ConnectedComponentInterface>();
		list4.add(c1);
		list4.add(c2);
		list4.add(c10);
		list4.add(c11);
		list4.add(c12);
	
		Rule rule1 = new Rule(list1, null, "", 1, 0, false);
		Rule rule2 = new Rule(list2, null, "", 1, 0, false);
		Rule rule3 = new Rule(list3, null, "", 1, 0, false);
		Rule rule4 = new Rule(list4, null, "", 1, 0, false);
		
		assertTrue(rule1.getAutomorphismNumber()==6);
		assertTrue(rule2.getAutomorphismNumber()==2);
		assertTrue(rule3.getAutomorphismNumber()==6);
		assertTrue(rule4.getAutomorphismNumber()==4);
		
		
	}
}

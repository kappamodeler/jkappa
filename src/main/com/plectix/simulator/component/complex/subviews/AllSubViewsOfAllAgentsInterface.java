package com.plectix.simulator.component.complex.subviews;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.subviews.base.AbstractionRule;
import com.plectix.simulator.component.complex.subviews.storage.SubViewsInterface;
import com.plectix.simulator.interfaces.SolutionInterface;

// TODO please rename
public interface AllSubViewsOfAllAgentsInterface {

	public void build(SolutionInterface solution, List<Rule> rules);

	public Iterator<String> getAllTypesIdOfAgents();

	public List<SubViewsInterface> getAllSubViewsByType(String type);

	public Map<String, AbstractAgent> getFullMapOfAgents();

	public void initDeadRules();

	public List<AbstractionRule> getRules();

	public Map<String, AbstractAgent> getAgentNameToAgent();

	public void createXML(XMLStreamWriter writer) throws XMLStreamException;
}
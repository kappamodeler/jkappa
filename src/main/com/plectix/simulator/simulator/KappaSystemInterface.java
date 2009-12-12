package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.io.xml.RuleCompressionXMLWriter;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.staticanalysis.Observables;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.util.Info.InfoType;

public interface KappaSystemInterface {

	public void initialize(InfoType outputType);

	public void doPositiveUpdate(Rule rule,
			List<Injection> currentInjectionsList);

	public void setRules(List<Rule> rules);

	public void checkPerturbation(double currentTime);

	public List<Rule> getRules();

	public Rule getRuleById(int ruleId);

	public SolutionInterface getSolution();

	public Observables getObservables();

	public Stories getStories();

	public ContactMap getContactMap();

	public AllSubViewsOfAllAgentsInterface getSubViews();

	public long generateNextRuleId();

	public long generateNextAgentId();

	public List<ComplexPerturbation<?,?>> getPerturbations();

	public void addRule(Rule rule);

	public void setSolution(SolutionInterface solution);

	public void setObservables(Observables observables);

	public void setStories(Stories stories);

	public void addStories(String name);

	public void setPerturbations(List<ComplexPerturbation<?,?>> perturbations);

	public void resetIdGenerators();

	public void clearRules();

	public void clearPerturbations();

	public Rule getRandomRule();

	public double getTimeValue();

	public InfluenceMap getInfluenceMap();

	public LocalViewsMain getLocalViews();

	public SpeciesEnumeration getEnumerationOfSpecies();

	public RuleCompressionXMLWriter getRuleCompressionBuilder();

}
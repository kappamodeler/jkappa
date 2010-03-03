package com.plectix.simulator.simulator;

import java.util.List;

import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.io.xml.RuleCompressionXMLWriter;
import com.plectix.simulator.simulationclasses.injections.Injection;
import com.plectix.simulator.simulationclasses.perturbations.ComplexPerturbation;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.contactmap.ContactMap;
import com.plectix.simulator.staticanalysis.influencemap.InfluenceMap;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.staticanalysis.observables.Observables;
import com.plectix.simulator.staticanalysis.speciesenumeration.SpeciesEnumeration;
import com.plectix.simulator.staticanalysis.stories.Stories;
import com.plectix.simulator.staticanalysis.subviews.AllSubViewsOfAllAgentsInterface;

public class MockKappaSystemForRules implements KappaSystemInterface {

	@Override
	public void addRule(Rule rule) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addStories(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPerturbation(double currentTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearPerturbations() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearRules() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPositiveUpdate(Rule rule,
			List<Injection> currentInjectionsList) {
		// TODO Auto-generated method stub

	}

	@Override
	public long generateNextAgentId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long generateNextRuleId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ContactMap getContactMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpeciesEnumeration getEnumerationOfSpecies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfluenceMap getInfluenceMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalViewsMain getLocalViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observables getObservables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComplexPerturbation<?, ?>> getPerturbations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rule getRandomRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rule getRuleById(int ruleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuleCompressionXMLWriter getRuleCompressionBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Rule> getRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolutionInterface getSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stories getStories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllSubViewsOfAllAgentsInterface getSubViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTimeValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetIdGenerators() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObservables(Observables observables) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPerturbations(List<ComplexPerturbation<?, ?>> perturbations) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRules(List<Rule> rules) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSolution(SolutionInterface solution) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStories(Stories stories) {
		// TODO Auto-generated method stub

	}

}

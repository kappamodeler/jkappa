package com.plectix.simulator.mocks;

import java.util.List;

import com.plectix.simulator.component.Observables;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.contactmap.ContactMap;
import com.plectix.simulator.component.complex.influencemap.InfluenceMap;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.component.complex.speciesenumeration.GeneratorSpecies;
import com.plectix.simulator.component.complex.subviews.AllSubViewsOfAllAgentsInterface;
import com.plectix.simulator.component.injections.Injection;
import com.plectix.simulator.component.perturbations.Perturbation;
import com.plectix.simulator.component.stories.Stories;
import com.plectix.simulator.interfaces.SolutionInterface;
import com.plectix.simulator.rulecompression.writer.RuleCompressionXMLWriter;
import com.plectix.simulator.simulator.KappaSystemInterface;
import com.plectix.simulator.util.Info.InfoType;

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
	public GeneratorSpecies getEnumerationOfSpecies() {
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
	public List<Perturbation> getPerturbations() {
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
	public void initialize(InfoType outputType) {
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
	public void setPerturbations(List<Perturbation> perturbations) {
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

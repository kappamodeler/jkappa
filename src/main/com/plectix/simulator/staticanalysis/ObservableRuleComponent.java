package com.plectix.simulator.staticanalysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.interfaces.ObservableRuleInterface;

/**
 * This class implements rule observable. This one is the object which affected by fixed rule,
 * which lets us keep an eye on that rule during the simulation.
 * In general we have kappa file line like
 * <br><br>
 * <code>%obs 'observableRuleName'</code>
 * <br><br>
 * where <code>observableRuleName</code> is the name of the fixed rule.<br>
 * This line means that the rule with that name is now in observables.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class ObservableRuleComponent implements ObservableRuleInterface, Serializable {
	private final Rule rule;
	private final int idAsObservable;
	private final List<Double> stateHistory = new ArrayList<Double>();
	private double lastState = -1;
	
	/**
	 * Constructor. Creates observable rule using existing rule.
	 * @param rule rule to create component from
	 * @param id id of this component in observables
	 */
	public ObservableRuleComponent(Rule rule, int id) {
		this.rule = rule;
		this.idAsObservable = id;
	}
	
	public final void updateLastValue() {
		lastState = getState();
	}

	/**
	 * This method calculates current state of observables. Util method.
	 * @return current state of observables.
	 */
	private final double getState() {
		double count = rule.getRate();

		for (ConnectedComponentInterface cc : rule.getLeftHandSide())
			count *= cc.getInjectionsWeight();
		return count/rule.getAutomorphismNumber();
	}

	/**
	 * This method writes the latest state (i.e. injections' quantity) of this observable
	 * into the special states list
	 */
	public final void fixState(boolean replaceLast) {
		if (replaceLast)
			stateHistory.set(stateHistory.size() - 1, getState());
		else
			stateHistory.add(lastState);
	}

	public final String getLine() {
		return rule.getName();
	}

	public final String getName() {
		return rule.getName();
	}

	public final int getId() {
		return idAsObservable;
	}

	public double getCurrentState(Observables observable) {
		return rule.getRate();
	}

	public String getStringItem(int index, Observables observable) {
		return stateHistory.get(index).toString();
	}

	public boolean isUnique() {
		return true;
	}

	public double getItem(int index, Observables obs) {
		return stateHistory.get(index);
	}

	@Override
	public double getLastValue() {
		return lastState;
	}
}

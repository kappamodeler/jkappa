package com.plectix.simulator.parser.abstractmodel;

import java.util.List;

import com.plectix.simulator.components.CPerturbationType;

public class AbstractPerturbation implements IAbstractComponent {// implements IAbstractComponent<IPerturbation> {
	private final int myId;
	private double myTime;
	private final CPerturbationType myType;
	private double myRate;
	private final String myRuleName;
	private final boolean myGreater;
	private List<AbstractRateExpression> myExpression;
	private List<AbstractRateExpression> parametersRHS;
	private int myObsNameID;
	private AbstractPerturbationRule myAbstractRule;
	private List<String> myObsNames;
	
	public AbstractPerturbation(int id, double time, CPerturbationType time2,
			double rate, String ruleName, boolean greater,
			List<AbstractRateExpression> rateExpression) {
		myId = id;
		myTime = time;
		myType = time2;
		myRate = rate;
		myRuleName = ruleName;
		myGreater = greater;
		myExpression = rateExpression;
	}

	public AbstractPerturbation(int perturbationID, List<String> obsNames,
			List<Double> parameters, int obsNameID, CPerturbationType type,
			double perturbationRate, String ruleName, boolean greater,
			List<AbstractRateExpression> rateParameters) {
		myId = perturbationID;
		myObsNameID = obsNameID;
		myObsNames = obsNames;
//		AbstractObservables obs = observables;
		//TODO!!!!!!
		//fillParameters(obsID, parameters, obs);
		myType = type;
		myRate = perturbationRate;
		myRuleName = ruleName;
		myGreater = greater;
		parametersRHS = rateParameters;
	}

	public AbstractPerturbation(int perturbationID, double time, CPerturbationType type,
			AbstractPerturbationRule rule, boolean greater) {
		myId = perturbationID;
		myTime = time;
		myType = type;
		myAbstractRule = rule;
		myRuleName = rule.getName();
		myGreater = greater;
	}

//	@Override
//	public IPerturbation convert() {
//		switch (myType) {
//		case NUMBER:{
//			return new CPerturbations();
//			break;
//		}
//		case ONCE:{
//			break;
//		}
//		case TIME:{
//			break;
//		}
//		}
//		return null;
//	}
}

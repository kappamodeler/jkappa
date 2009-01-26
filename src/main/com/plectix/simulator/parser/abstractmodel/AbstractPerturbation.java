package com.plectix.simulator.parser.abstractmodel;

import java.util.List;

import com.plectix.simulator.components.CPerturbationType;
import com.plectix.simulator.interfaces.IObservablesComponent;
import com.plectix.simulator.interfaces.IPerturbationExpression;

public class AbstractPerturbation implements IAbstractComponent {
//	private final int myId;
//	private double myTime;
//	private final CPerturbationType myType;
//	private double myRate;
//	private final AbstractRule myRule;
//	private final boolean myGreater;
//	private List<IPerturbationExpression> myExpression;
//	private List<IPerturbationExpression> parametersRHS;
//	private int myObsNameID;
	
	public AbstractPerturbation(int id, double time, CPerturbationType time2,
			double rate, AbstractRule rule, boolean greater,
			List<IPerturbationExpression> rateExpression) {
//		myId = id;
//		myTime = time;
//		myType = time2;
//		myRate = rate;
//		myRule = rule;
//		myGreater = greater;
//		myExpression = rateExpression;
	}

	public AbstractPerturbation(int perturbationID, List<IObservablesComponent> obsID,
			List<Double> parameters, int obsNameID, CPerturbationType type,
			double perturbationRate, AbstractRule rule, boolean greater,
			List<IPerturbationExpression> rateParameters, AbstractObservables observables) {
//		myId = perturbationID;
//		myObsNameID = obsNameID;
//		AbstractObservables obs = observables;
//		//TODO!!!!!!
//		//fillParameters(obsID, parameters, obs);
//		myType = type;
//		myRate = perturbationRate;
//		myRule = rule;
//		myGreater = greater;
//		parametersRHS = rateParameters;
	}

	public AbstractPerturbation(int perturbationID, double time, CPerturbationType type,
			AbstractPerturbationRule rule, boolean greater) {
//		myId = perturbationID;
//		myTime = time;
//		myType = type;
//		myRule = rule;
//		myGreater = greater;
	}

}

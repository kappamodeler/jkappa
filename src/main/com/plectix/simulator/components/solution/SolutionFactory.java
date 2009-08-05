package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.ISolution;
import com.plectix.simulator.simulator.KappaSystem;

/**
 * This class contains the one and only method produce(), which 
 * creates ISolution implementation according to which operation mode we use 
 */
public final class SolutionFactory {
	/**
	 * Creates ISolution implementation according to which operation mode we use
	 * @param mode operation mode we use
	 * @param system KappaSystem object we work with
	 * @return new solution
	 */
	public final ISolution produce(OperationMode mode, KappaSystem system) {
		switch(mode) {
		case FIRST: {
			return new CFirstSolution(system);
		}
		case SECOND: {
			return new CSecondSolution(system);
		}
		case THIRD: {
			return new CThirdSolution(system);
		}
		case FOURTH: {
			// TODO
			return new CFourthSolution(system);
		}
		case FIFTH: {
			// TODO
			return new CFifthSolution(system);
		}
		default : {
			// TODO
			return new CFirstSolution(system);
		}
		}
	}
}

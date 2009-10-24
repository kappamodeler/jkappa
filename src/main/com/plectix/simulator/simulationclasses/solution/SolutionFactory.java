package com.plectix.simulator.simulationclasses.solution;

import com.plectix.simulator.interfaces.SolutionInterface;
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
	public final SolutionInterface produce(OperationMode mode, KappaSystem system) {
		switch(mode) {
		case FIRST: {
			return new SolutionFirstMode(system);
		}
		case SECOND: {
			return new SolutionSecondMode(system);
		}
		case THIRD: {
			return new SolutionThirdMode(system);
		}
		case FOURTH: {
			// TODO
			return new SolutionFourthMode(system);
		}
		case FIFTH: {
			// TODO
			return new SolutionFifthMode(system);
		}
		default : {
			// TODO
			return new SolutionFirstMode(system);
		}
		}
	}
}

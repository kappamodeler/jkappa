package com.plectix.simulator.components.solution;

import com.plectix.simulator.interfaces.*;
import com.plectix.simulator.simulator.KappaSystem;

public final class SolutionFactory {
	public final ISolution produce(OperationMode mode, KappaSystem system) {
		switch(mode) {
		case FIRST: {
			return new CSolution(system);
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
			return new CSolution(system);
		}
		}
	}
}

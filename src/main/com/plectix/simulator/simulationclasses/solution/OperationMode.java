package com.plectix.simulator.simulationclasses.solution;

/**
 * Operation mode is the strategy of substances keeping.
 * <br>Example : %init: 1000000 * A(x)
 * <p><i>Operation Mode 1:</i> The initial condition above makes us allocate 
 * 1 million agent A(x) in memory. Uses a lot of memory, initialization takes some time.
 * But theoretically OM1 shows the fastest performance.</p>
 * <p><i>Operation Mode 2:</i> In this mode, we will have "mega-species" (i.e. SuperSubstance) 
 * for the initial state. For the example above, a single SuperSubstance A(x) with 1 injection. 
 * Initial memory requirements are very low, the initialization is very fast. 
 * <br>In OM2 we do the grouping only for the initial condition. 
 * E.g. if we obtain new agent A(x) during the simulation, it wouldn't be contained in
 * SuperSubstance. So, we can decrement the number of species in a SuperSubstance, but 
 * can never increment it.</p>
 * <p><i>Operation Mode 3:</i> This mode is an extension to OM2. In addition to OM2, 
 * we would add items to the SuperSubstances created by the initial condition. 
 * But we will never create new groups.</p>
 * <p><i>Operation Mode 4:</i> Extension to OM3. In addition to Mode 3, 
 * we would create new groups as new complexes are created and delete groups 
 * if they don't include items anymore.</p> 
 * <p><i>Operation Mode 5:</i> This is some kind of mixture of OM3 and OM4.
 * We create SuperSubstances only for connected components which aren't too long. 
 * Otherwise we put it into the StraightStorage, i.e. use them like if we work in OM1.
 * Critical length of the component in SuperSolution defined in SimulatorOptions.</p>
 * @author evlasov
 *
 */
public enum OperationMode {
	FIRST("1"),
	SECOND("2"),
	THIRD("3"),
	FOURTH("4"),
	FIFTH("5"),
	DEFAULT("DEFAULT");
	
	private final String string;
	
	private OperationMode(String string) {
		this.string = string;
	}
	
	public static OperationMode getValue(String string) {
		for (OperationMode mode : values()) {
			if (mode.string.equals(string)) {
				return mode;
			}
		}
		return DEFAULT;
	}
}

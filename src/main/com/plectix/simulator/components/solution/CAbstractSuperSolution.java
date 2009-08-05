package com.plectix.simulator.components.solution;

import java.util.*;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/**
 * This class describes solutions, which perform any changes to SuperStorage during
 * the simulation process. Notice that CSecondSolution is not that type, because 
 * we just fill SuperStorage in the beginning and that's all!
 */
/*package*/ abstract class CAbstractSuperSolution extends ComplexSolution {
	CAbstractSuperSolution(KappaSystem system) {
		super(system);
	}

	/**
	 * We use this method in order to add connected component to the solution
	 * @param component component to be added
	 */
	protected abstract void addConnectedComponent(IConnectedComponent component);
	
	public final void flushPoolContent(RuleApplicationPool pool) {
		Collection<CAgent> agents = pool.getStorage().getAgents();
		List<CAgent> agentsCopy = new ArrayList<CAgent>();
		agentsCopy.addAll(agents);
		if (!agents.isEmpty()) {
			List<IConnectedComponent> list = new ArrayList<IConnectedComponent>();
			while (!agentsCopy.isEmpty()) {
				CAgent agent = agentsCopy.get(0);
				IConnectedComponent component = SolutionUtils.getConnectedComponent(agent);
				list.add(component);
				for (CAgent agentFromComponent : component.getAgents()) {
					agentsCopy.remove(agentFromComponent);
				}
			}
			for (IConnectedComponent cc : list) {
				this.addConnectedComponent(cc);
			}
		}
		
		pool.clear();
	}
	
	public final void addInitialConnectedComponents(long quant, List<CAgent> agents) {
		for (IConnectedComponent component : SimulationUtils.buildConnectedComponents(agents)) {
			getSuperStorage().addOrEvenIncrement(quant, component);	
		}
	}
}

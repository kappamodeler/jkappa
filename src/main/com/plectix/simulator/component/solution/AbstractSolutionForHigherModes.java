package com.plectix.simulator.component.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.component.Agent;
import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

/**
 * This class describes solutions, which perform any changes to SuperStorage during
 * the simulation process. Notice that CSecondSolution is not that type, because 
 * we just fill SuperStorage in the beginning and that's all!
 */
/*package*/ abstract class AbstractSolutionForHigherModes extends AbstractComplexSolution {
	AbstractSolutionForHigherModes(KappaSystem system) {
		super(system);
	}

	/**
	 * We use this method in order to add connected component to the solution
	 * @param component component to be added
	 */
	protected abstract void addConnectedComponent(ConnectedComponentInterface component);
	
	public final void flushPoolContent(RuleApplicationPoolInterface pool) {
		Collection<Agent> agents = pool.getStorage().getAgents();
		List<Agent> agentsCopy = new ArrayList<Agent>();
		agentsCopy.addAll(agents);
		if (!agents.isEmpty()) {
			List<ConnectedComponentInterface> list = new ArrayList<ConnectedComponentInterface>();
			while (!agentsCopy.isEmpty()) {
				Agent agent = agentsCopy.get(0);
				ConnectedComponentInterface component = SolutionUtils.getConnectedComponent(agent);
				list.add(component);
				for (Agent agentFromComponent : component.getAgents()) {
					agentsCopy.remove(agentFromComponent);
				}
			}
			for (ConnectedComponentInterface cc : list) {
				this.addConnectedComponent(cc);
			}
		}
		
		pool.clear();
	}
	
	public final void addInitialConnectedComponents(long quantity, List<Agent> agents) {
		for (ConnectedComponentInterface component : SimulationUtils.buildConnectedComponents(agents)) {
			getSuperStorage().addOrEvenIncrement(quantity, component);	
		}
	}
}

package com.plectix.simulator.components.solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plectix.simulator.components.CAgent;
import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IConnectedComponent;
import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.SimulationUtils;

public abstract class CAbstractSuperSolution extends ComplexSolution {
	public CAbstractSuperSolution(KappaSystem system) {
		super(system);
	}

	public RuleApplicationPool prepareRuleApplicationPool() {
		return new StandardRuleApplicationPool(new StraightStorage());
	}
	
	public void addInjectionToPool(RuleApplicationPool pool, CInjection injection) {
		StraightStorage storage = pool.getStorage();
		if (injection.isSuper()) {
			storage.addConnectedComponent(getSuperStorage().extractComponent(injection));
		} else {
			if (injection.getImageAgent() != null) {
				IConnectedComponent component = SolutionUtils.getConnectedComponent(injection.getImageAgent());
				for (CAgent agent : component.getAgents()) {
					storage.addAgent(agent);
					getStraightStorage().removeAgent(agent);
				}	
			}
		}
	}
	
	protected abstract void addConnectedComponent(IConnectedComponent component);
	
	public void applyChanges(RuleApplicationPool pool) {
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

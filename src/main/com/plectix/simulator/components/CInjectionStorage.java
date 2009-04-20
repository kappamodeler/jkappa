package com.plectix.simulator.components;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.plectix.simulator.components.injections.CInjection;
import com.plectix.simulator.interfaces.IRandom;
import com.plectix.simulator.probability.CInjectionsRandomizer;

public class CInjectionStorage {
	private final Map<Integer, CInjection> injections = new TreeMap<Integer, CInjection>();
	private final CInjectionsRandomizer injectionsRandomizer = new CInjectionsRandomizer();
	private int maxId = -1;
	
	/**
	 * This method registers injection to current connected component with given id.
	 * @param inj injection to register
	 * @param id id of injection
	 */
	public final void addInjection(CInjection inj, int id) {
		if (inj != null) {
			maxId = Math.max(maxId, id);
			inj.setId(id);
			injections.put(id, inj);
			injectionsRandomizer.addInjection(inj);
		}
	}
	
	public final void addInjection(CInjection inj) {
		if (inj != null) {
			maxId++;
			inj.setId(maxId);
			injections.put(maxId, inj);
			injectionsRandomizer.addInjection(inj);
		}
	}

	/**
	 * This method unregisters given injection from current connected component
	 * @param injection injection to remove
	 */
	public final void removeInjection(CInjection injection) {
		if (injection == null) {
			return;
		}

		int id = injection.getId();

		if (injections.get(id) != null) {
			if (injection != injections.get(id)) {
				return;
			}
			CInjection inj = injections.remove(maxId);
			injectionsRandomizer.removeInjection(inj);
			if (id != maxId) {
				addInjection(inj, id);
			}
			maxId--;
		}
	}
	
	public CInjection getInjection(int id) {
		return injections.get(id);
	}
	
	public Collection<CInjection> getList() {
		return Collections.unmodifiableCollection(injections.values());
	}
	
	public CInjectionsRandomizer getRandomizer() {
		return injectionsRandomizer;
	}
	
	public int getCommonPower() {
		return injectionsRandomizer.getCommonPower();
	}
}

package com.plectix.simulator.components.injections;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.plectix.simulator.interfaces.IRandom;

public class CInjectionStorage {
	private final Map<Integer, CInjection> injections = new LinkedHashMap<Integer, CInjection>();
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
		}
	}
	
	public final void addInjection(CInjection inj) {
		if (inj != null) {
			maxId++;
			inj.setId(maxId);
			injections.put(maxId, inj);
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
	
	public CInjection getRandomInjection(IRandom random) {
		int randomId = random.getInteger(injections.size());
		return injections.get(randomId);
	}
	
	public int getCommonPower() {
		return injections.size();
	}
}

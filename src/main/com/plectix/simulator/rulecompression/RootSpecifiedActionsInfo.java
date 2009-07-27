package com.plectix.simulator.rulecompression;

import java.util.*;

/**
 * This is the set of actions, having the same agent as root
 * We need this object to compare actions sets.
 * @author evlasov
 *
 */
/*package*/ class RootSpecifiedActionsInfo {
	private final Set<ActionInfo> storage = new LinkedHashSet<ActionInfo>();
	
	public void addAction(ActionInfo ai) {
		storage.add(ai);
	}
	
	public String toString() {
		return storage.toString();
	}
}

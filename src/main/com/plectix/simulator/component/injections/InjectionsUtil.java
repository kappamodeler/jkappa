package com.plectix.simulator.component.injections;

import java.util.List;
import java.util.Stack;

import com.plectix.simulator.component.Site;

public final class InjectionsUtil {
	/**
	 * This method indicates if 2 injections are in clash
	 * 
	 * @param injections
	 *            list of injections with power = 2
	 * @return <tt>true</tt> if injections are in clash, otherwise
	 *         <tt>false</tt>
	 */
	public static final boolean isClash(List<Injection> injections) {
		Stack<Injection> injectionStack = new Stack<Injection>();
		injectionStack.addAll(injections);
		while (!injectionStack.isEmpty()) {
			Injection inj1 = injectionStack.pop();
			for (Injection inj2 : injectionStack) {
				for (Site siteCC1 : inj1.getSiteList())
					for (Site siteCC2 : inj2.getSiteList())
						if (siteCC1.getParentAgent().getId() == siteCC2
								.getParentAgent().getId())
							return true;
			}
		}
		return false;
	}
}

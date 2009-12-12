package com.plectix.simulator.io;

import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.util.OutputUtils;

public class SimulationDataOutputUtil {
	/**
	 * This method creates string representation of given rule, which using in
	 * XML output
	 * 
	 * @param rule
	 * @param isOcamlStyleObsName
	 *            <tt>true</tt> if option <code>--ocaml-style-obs-name</code> is
	 *            enabled, otherwise <tt>false</tt>
	 * @return string representation of given rule
	 */
	public static final String getData(Rule rule, boolean isOcamlStyleObsName) {
		StringBuffer sb = new StringBuffer();
		sb.append(OutputUtils.printPartRule(rule.getLeftHandSide(),
				isOcamlStyleObsName));
		sb.append("->");
		sb.append(OutputUtils.printPartRule(rule.getRightHandSide(),
				isOcamlStyleObsName));
		return sb.toString();
	}
}

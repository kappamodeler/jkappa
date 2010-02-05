package com.plectix.simulator.staticanalysis.contactmap;

/**
 * This class implements mode of create contact map.<br>
 * <code>MODEL</code> - means creates contact map by full model.<br>
 * <code>AGENT_OR_RULE</code> - means creates contact map with focusing.
 * @author avokhmin
 *
 */
public enum ContactMapMode {
	SEMANTIC, FOCUS_ON_AGENT_OR_RULE, SYNTACTIC;
}
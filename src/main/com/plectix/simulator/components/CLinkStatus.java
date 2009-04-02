package com.plectix.simulator.components;

/**
 * Status of LinkState.
 * <p>
 * Example:
 * <blockquote><pre>
 * A(x) - agent "A" with site "x", LinkStatus of "x" does "FREE".
 * A(x!_) or A(x!1),.. - agent "A" with site "x", LinkStatus of "x" does "BOUND".
 * A(x?) - agent "A" with site "x", LinkStatus of "x" does "WILDCARD".
 * </blockquote></pre> 
 * @author avokhmin
 */
public enum CLinkStatus {
	BOUND,
	WILDCARD,
	FREE;
}

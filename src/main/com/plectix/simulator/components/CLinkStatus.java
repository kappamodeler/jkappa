package com.plectix.simulator.components;

/**
 * This enumeration implements status of link. <br>
 * "Bound" status means that this site connected with another site.<br>
 * "Free" status means that this site is free from connections.<br>
 * "Wildcard" status means that we no know nothing about this site's connections.<br><br>
 * For example: <br>
 * <li>A(x) - link-status of site "x" is "FREE".</li><br>
 * <li>A(x!_) or A(x!1),.. - link-status of site "x" is "BOUND".</li><br>
 * <li>A(x?) - link-status of site "x" is "WILDCARD".</li><br>
 * @author avokhmin
 */
public enum CLinkStatus {
	BOUND,
	WILDCARD,
	FREE;
}

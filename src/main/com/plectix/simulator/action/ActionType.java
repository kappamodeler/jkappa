package com.plectix.simulator.action;

import com.plectix.simulator.component.Rule;

/**
 * This class implements Action type:<br>
 * <li><b>NONE</b> - none action type:<br>
 * Example:<br>
 * <code>A(x)->A(x)</code>, creates action with <code>NONE</code> type.
 * </li>
 * 
 * <li><b>BREAK</b> - break action type:<br>
 * Example:<br>
 * <code>A(x!1),B(y!1)->A(x),B(y)</code>, creates 2 actions
 * with <code>BREAK</code> type for site "x" from agent "A" and site "y" from
 * agent "B".
 * </li>
 * 
 * <li><b>DELETE</b> - delete action type:<br>
 * Example:<br>
 * <code>A(x)-></code>, creates action with <code>DELETE</code> type for agent "A".
 * </li>
 * 
 * <li><b>ADD</b> - add action type:<br>
 * Example:<br>
 * <code>->A(x)</code>, creates action <code>ADD</code> type for agent "A".
 * </li>
 * 
 * <li><b>BOUND</b> - bound action type:<br>
 * Example:<br>
 * <code>A(x),B(y)->A(x!1),B(y!1)</code>, creates 2 actions
 * with <code>BOUND</code> type for site "x" from agent "A" and site "y" from
 * agent "B".
 * </li>
 * 
 * <li><b>MODIFY</b> - modify action type:<br>
 * Example:<br>
 * <code>A(x~q)->A(x~fi)</code>, creates action <code>MODIFY</code> type for site "x"
 * from agent "A".
 * </li>
 * 
 * <br><br>
 * It's atomic action type, but reality rule contains difficult expression, who should be
 * convert to atomic actions.
 * @author avokhmin
 * @see Rule
 *
 */
public enum ActionType implements Comparable<ActionType> {
	NONE,
	BREAK,
	DELETE,
	ADD,
	BOUND,
	MODIFY;
}

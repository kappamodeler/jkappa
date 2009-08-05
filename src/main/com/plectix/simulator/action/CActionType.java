package com.plectix.simulator.action;

import com.plectix.simulator.components.CRule;

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
 * @see CRule
 *
 */
public enum CActionType {
	NONE(-1),
	BREAK(0),
	DELETE(1),
	ADD(2),
	BOUND(3),
	MODIFY(4);
	
	private int myId = -100;

	/**
	 * Constructor of CActionType.
	 * @param id given type id.
	 */
	private CActionType(int id) {
		myId = id;
	}

	/**
	 * This method returns id current type.
	 * @return id current type.
	 */
	public int getId() {
		return myId;
	}

	/**
	 * This method returns type by given id.
	 * @param id given id
	 * @return type by given id
	 */
	public static CActionType getById(int id) {
		if (id == -1) {
			return NONE;
		} else if (id == 0) {
			return BREAK;
		} else if (id == 1) {
			return DELETE;
		} else if (id == 2) {
			return ADD;
		} else if (id == 3) {
			return BOUND;
		} else if (id == 4) {
			return MODIFY;
		}
		return null;
	}
}

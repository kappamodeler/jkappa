package com.plectix.simulator.components.stories.storage;

public class AState<E> {
	/**
	 * AState<Boolean> for check Agent<br>
	 * AState<Boolean> for check FREE/BOUND, if <tt>true</tt> then "FREE",
	 * otherwise <tt>false</tt>
	 */
	// public static final Boolean FREE_LINK_STATE = true;
	// public static final Boolean BOUND_LINK_STATE = false;
	// public static final Boolean CHECK_AGENT = true;
	private E beforeState;
	private E afterState;

	public void setBeforeState(E state) {
		beforeState = state;
	}

	public void setAfterState(E state) {
		afterState = state;
	}

	public E getBeforeState() {
		return beforeState;
	}

	public E getAfterState() {
		return afterState;
	}

	public boolean isBeforeEqualsAfter(AState<E> stateIn) {
		return beforeState.equals(stateIn.afterState);
	}

	public boolean isAfterEqualsBefore(AState<E> stateIn) {
		return afterState.equals(stateIn.beforeState);
	}

	@Override
	public String toString() {
		String str = new String();
		if (beforeState != null)
			str = "BEFORE: " + beforeState.toString();
		if (afterState != null)
			str += " AFTER: " + afterState.toString();
		return str;
	}

	public boolean equalsBefore(AState<?> beforeIn) {
		if (beforeIn.afterState == null)
			if (beforeIn.beforeState.equals(beforeState))
				return true;
			else
				return false;
		if (beforeIn.afterState.equals(beforeState))
			return true;
		return false;
	}
}

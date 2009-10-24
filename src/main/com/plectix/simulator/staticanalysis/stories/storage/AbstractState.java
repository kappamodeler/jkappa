package com.plectix.simulator.staticanalysis.stories.storage;

public final class AbstractState<E> {
	/**
	 * AState<Boolean> for check Agent<br>
	 * AState<Boolean> for check FREE/BOUND, if <tt>true</tt> then "FREE",
	 * otherwise <tt>false</tt>
	 */
	private E beforeState;
	private E afterState;

	public final void setBeforeState(E state) {
		if(beforeState == null)
			beforeState = state;
	}

	public final void setAfterState(E state) {
		afterState = state;
	}

	public final E getBeforeState() {
		return beforeState;
	}

	public final E getAfterState() {
		return afterState;
	}

	public final boolean isBeforeEqualsAfter() {
		return beforeState.equals(afterState);
	}

	@Override
	public final String toString() {
		String str = new String();
		if (beforeState != null)
			str = "BEFORE: " + beforeState.toString();
		if (afterState != null)
			str += " AFTER: " + afterState.toString();
		return str;
	}
}

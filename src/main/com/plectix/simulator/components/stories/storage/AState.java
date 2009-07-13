package com.plectix.simulator.components.stories.storage;

public class AState<E> {
	/**
	 * AState<Boolean> for check Agent<br>
	 * AState<Boolean> for check FREE/BOUND, if <tt>true</tt> then "FREE",
	 * otherwise <tt>false</tt>
	 */
	private E beforeState;
	private E afterState;

	public void setBeforeState(E state) {
		if(beforeState == null)
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

	public boolean isBeforeEqualsAfter() {
		return beforeState.equals(afterState);
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
		if (beforeIn.afterState.equals(beforeState))
			return true;
		return false;
	}
}

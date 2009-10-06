package com.plectix.simulator.component;

import java.io.Serializable;

/**
 * This class implements internal state of fixed site.
 * In fact, this is just a wrapping object for internal state's string, which we'll further call
 * internal state's name.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public final class InternalState extends NamedEntity implements Serializable {

	public static final String DEFAULT_NAME = "DEFAULT_INTERNAL_STATE_NAME".intern();
	/**
	 * This field represents internal state of default (i.e. empty) site, which is the one and only 
	 */
	public static final InternalState EMPTY_STATE = new InternalState(DEFAULT_NAME);
	
	private String name;

	/**
	 * Constructor. Creates internal state by name.
	 * @param id id of internal state's name in Name Dictionary
	 */
	public InternalState(String name) {
		this.name = name.intern();
	}

	/**
	 * This method indicates if current internal state represents an empty one, which has 
	 * name == DEFAULT_NAME, i.e. there's no internal state in fact.
	 * @return <tt>true</tt>, if current internal state represents an empty one, 
	 * otherwise <tt>false</tt>.
	 */
	public final boolean isRankRoot() {
		return this.hasDefaultName();
	}

	/**
	 * This method changes current name on the given one
	 * @param id new value of this internal state's name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * This method returns internal state's name
	 * @return current internal state's name or "NO_INDEX" if this state's empty
	 */
	public final String getName() {
		return name;
	}

	// TODO is this method needed ?
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InternalState)) {
			return false;
		}
		return ((InternalState) obj).name.equals(this.name);
	}
	
	// TODO Comment
	/**
	 * This method tries to "insert" this internal state to the other one.<br><br>
	 * Internal state p is insertable in state q if:
     * <li>
     * p is an empty state (empty state can be inserted to any other)
     * </li><li>
     * name id's of these sites are equal
     * </li>
     * Other ways lead to "false".
     * </blockquote>
     * 
     * @param solutionInternalState state to compare to 
     * @return <tt>true</tt> if this state can be inserted to a given one, otherwise <tt>false</tt>
	 */
	public final boolean compareInternalStates(InternalState solutionInternalState) {
		if (this.hasDefaultName()) {
			return true;
		} else {
			return this.name.equals(solutionInternalState.getName());
		}
	}
	
	/**
	 * This method checks this state for having the same name as the other one.
	 * @param otherInternalState state to compare to
	 * @return <tt>true</tt> if the states has similar names, otherwise <tt>false</tt>
	 */
	public final boolean equalz(InternalState otherInternalState) {
		return (name.equals(otherInternalState.getName()));
	}

	@Override
	protected String getDefaultName() {
		return DEFAULT_NAME;
	}
}

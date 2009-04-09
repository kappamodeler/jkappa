package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.simulator.ThreadLocalData;
/**
 * This class implements internal state of fixed site.
 * In fact, this is just a wrapping object for internal state's string, which we'll further call
 * internal state's name.
 * @author avokhmin
 *
 */
@SuppressWarnings("serial")
public class CInternalState implements Serializable {

	/**
	 * This field represents internal state of default (i.e. empty) site, which is the one and only 
	 */
	public static final CInternalState EMPTY_STATE = new CInternalState(
			CSite.NO_INDEX);
	private static final int FREE_STATE = CSite.NO_INDEX;

	private int nameId;

	/**
	 * Constructor. Creates internal state by name.
	 * @param id id of internal state's name in Name Dictionary
	 */
	public CInternalState(int id) {
		this.nameId = id;
	}

	/**
	 * This method indicates if current internal state represents an empty one, which has 
	 * nameId == FREE_STATE == -1, i.e. there's no internal state in fact.
	 * @return <tt>true</tt>, if current internal state represents an empty one, 
	 * otherwise <tt>false</tt>.
	 */
	public final boolean isRankRoot() {
		return nameId == FREE_STATE;
	}

	/**
	 * This method changes current nameId on the given one
	 * @param id new value of this internal state's nameId
	 */
	public final void setNameId(int id) {
		this.nameId = id;
	}

	/**
	 * This method returns id of current internal state's name
	 * @return current internal state's name id
	 */
	public final int getNameId() {
		return nameId;
	}

	/**
	 * This method returns internal state's name
	 * @return current internal state's name or "NO_INDEX" if this state's empty
	 */
	public final String getName() {
		if(nameId == FREE_STATE)
			return "NO_INDEX";
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	// TODO is this method needed ?
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CInternalState)) {
			return false;
		}
		return ((CInternalState) obj).nameId == nameId;
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
	public final boolean compareInternalStates(CInternalState solutionInternalState) {
//		if (this.nameId != FREE_STATE
//				&& solutionInternalState.getNameId() == FREE_STATE)
//			return false;
		if (this.nameId == FREE_STATE)
//				&& solutionInternalState.getNameId() != FREE_STATE)
			return true;
//		if (!(this.nameId == solutionInternalState
//				.getNameId()))
//			return false;

		return this.nameId == solutionInternalState.getNameId();
	}
	
	/**
	 * This method checks this state for having the same name as the other one.
	 * @param otherInternalState state to compare to
	 * @return <tt>true</tt> if the states has similar names, otherwise <tt>false</tt>
	 */
	public final boolean equalz(CInternalState otherInternalState) {
//		if (nameId == FREE_STATE
//				&& solutionInternalState.getNameId() == FREE_STATE)
//			return true;
		if (nameId == otherInternalState
				.getNameId())
			return true;

		return false;
	}
}

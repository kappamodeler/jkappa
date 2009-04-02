package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.simulator.ThreadLocalData;
/**
 * Class implements Internal State of Site.
 * @author avokhmin
 *
 */
public class CInternalState extends CState implements IInternalState, Serializable {

	public static final CInternalState EMPTY_STATE = new CInternalState(
			CSite.NO_INDEX);
	/**
	 *	Means free state of InternalState.
	 */
	private static final int FREE_STATE = CSite.NO_INDEX;

	/**
	 * <code>{@link Integer}</code> value - nameId current InternalState.
	 * If (nameId == CSite.NO_INDEX) or (nameId == -1), should to undeer
	 */
	private int nameId;

	/**
	 * Standard constructor of InternalState.
	 * @param id - <code>{@link Integer}</code> value - nameId InternalState.
	 */
	public CInternalState(int id) {
		this.nameId = id;
	}

	/**
	 * Returns <tt>true</tt> value, if current InternalState hasn't get state (nameId = -1, Free InternalState), otherwise <tt>false</tt>.
	 */
	public final boolean isRankRoot() {
		return nameId == FREE_STATE;
	}

	/**
	 * Sets nameId current InternalState.
	 * @param id - <code>{@link Integer}</code> value.
	 */
	public final void setNameId(int id) {
		this.nameId = id;
	}

	/**
	 * Returns <code>{@link Integer}</code> value - nameId current InternalState.
	 */
	public final int getNameId() {
		return nameId;
	}

	/**
	 * Returns <code>{@link String}</code> value -  alphabetic representation method of current InternalState.
	 */
	public final String getName() {
		if(nameId == FREE_STATE)
			return "NO_INDEX";
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	// TODO is this method needed ?
	@Override
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
	 *	Examples:
     * <blockquote><pre>
     * A(x) - agent "A" with site "x", InternalState of site "x" does "FREE_STATE".
     * A(x~p) - agent "A" with site "x", InternalState of site "x" doesn't "FREE_STATE".
     * <p>
     * this.nameId != FREE_STATE, solutionInternalState.nameId == FREE_STATE;
     * this.compareInternalStates(solutionInternalState) returns "false"
     * <p>
     * this.nameId == FREE_STATE, solutionInternalState.nameId != FREE_STATE;
     * this.compareInternalStates(solutionInternalState) returns "true"
     * <p>
     * this.nameId != solutionInternalState.nameId returns "false"
     * <p>
     * in a different way returns "true"
     * </pre></blockquote>
     * 
     * @param solutionInternalState - <code>{@link IInternalState}</code> value - compares InternalState.
	 */
	public final boolean compareInternalStates(IInternalState solutionInternalState) {
		if (this.nameId != FREE_STATE
				&& solutionInternalState.getNameId() == FREE_STATE)
			return false;
		if (this.nameId == FREE_STATE
				&& solutionInternalState.getNameId() != FREE_STATE)
			return true;
		if (!(this.nameId == solutionInternalState
				.getNameId()))
			return false;

		return true;
	}
	
	/**
	 *	Examples:
     * <blockquote><pre>
     * A(x) - agent "A" with site "x", InternalState of site "x" does "FREE_STATE".
     * A(x~p) - agent "A" with site "x", InternalState of site "x" doesn't "FREE_STATE".
     * <p>
     * this.nameId == FREE_STATE, solutionInternalState.nameId == FREE_STATE;
     * this.compareInternalStates(solutionInternalState) returns "true"
     * <p>
     * this.nameId == solutionInternalState.nameId returns "true"
     * <p>
     * in a different way returns "false"
     * </pre></blockquote>
	 * @param solutionInternalState - <code>{@link IInternalState}</code> value - compares InternalState.
	 */
	public final boolean fullEqualityInternalStates(IInternalState solutionInternalState) {
		if (nameId == FREE_STATE
				&& solutionInternalState.getNameId() == FREE_STATE)
			return true;
		if (nameId == solutionInternalState
				.getNameId())
			return true;

		return false;
	}
}

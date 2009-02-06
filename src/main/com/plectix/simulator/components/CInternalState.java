package com.plectix.simulator.components;

import java.io.Serializable;

import com.plectix.simulator.interfaces.IInternalState;
import com.plectix.simulator.simulator.ThreadLocalData;

public class CInternalState extends CState implements IInternalState, Serializable {

	public static final CInternalState EMPTY_STATE = new CInternalState(
			CSite.NO_INDEX);
	
	private int nameId;

	public CInternalState(int id) {
		this.nameId = id;
	}

	public final boolean isRankRoot() {
		return nameId == CSite.NO_INDEX;
	}

	public final void setNameId(int id) {
		this.nameId = id;
	}

	public final int getNameId() {
		return nameId;
	}

	public final String getName() {
		return ThreadLocalData.getNameDictionary().getName(nameId);
	}

	public final int getStateNameId() {
		return nameId;
	}

	// is this method needed ?
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

}

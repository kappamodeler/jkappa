package com.plectix.simulator.components;

import com.plectix.simulator.SimulationMain;
import com.plectix.simulator.interfaces.IInternalState;

public class CInternalState extends CState implements IInternalState {

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

	@Override
	public final String getName() {
		return SimulationMain.getSimulationManager().getNameDictionary()
				.getName(nameId);
	}

	public final int getStateNameId() {
		return nameId;
	}

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

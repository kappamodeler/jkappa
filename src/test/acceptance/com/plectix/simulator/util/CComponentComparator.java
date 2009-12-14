package com.plectix.simulator.util;

import java.util.Collection;

import com.plectix.simulator.interfaces.ConnectedComponentInterface;
import com.plectix.simulator.util.string.ConnectedComponentToSmilesString;

public class CComponentComparator {
	public static ConnectedComponentInterface findComponent(
			ConnectedComponentInterface c,
			Collection<ConnectedComponentInterface> list) {
		int size = c.getAgents().size();
		for (ConnectedComponentInterface tmpC : list) {
			if (tmpC.getAgents().size() == size) {
				if (compareComponents(c, tmpC))
					return tmpC;
			}
		}
		return null;
	}

	private static boolean compareComponents(ConnectedComponentInterface firstComponent,
			ConnectedComponentInterface secondComponent) {
		String firstString = ConnectedComponentToSmilesString.getInstance()
				.toUniqueString(firstComponent);
		String secondString = ConnectedComponentToSmilesString.getInstance()
				.toUniqueString(secondComponent);
		if (firstString != null) {
			return firstString.equals(secondString);
		} else {
			return secondString == null;
		}
	}
}

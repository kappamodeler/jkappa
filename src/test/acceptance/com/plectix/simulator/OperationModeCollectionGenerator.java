package com.plectix.simulator;

import java.util.Collection;
import java.util.LinkedList;

public class OperationModeCollectionGenerator {

	public static final Integer OPERATION_MODES_NUMBER = 5;

	public static Collection<Object[]> generate(Collection<Object[]> fileNames,
			boolean allModes) {
		Collection<Object[]> collection = new LinkedList<Object[]>();
		for (Object[] objects : fileNames) {
			Object[] obj = new Object[objects.length + 1];
			for (int i = 0; i < objects.length; i++) {
				obj[i] = objects[i];
			}
			if (allModes) {
				for (int j = 1; j <= OPERATION_MODES_NUMBER; j++) {
					obj[objects.length] = j;
					collection.add(obj);
				}
			} else {
				obj[objects.length] = 1;
				collection.add(obj);
				
			}
		}
		return collection;

	}

}
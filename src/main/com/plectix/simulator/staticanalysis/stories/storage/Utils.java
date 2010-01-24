package com.plectix.simulator.staticanalysis.stories.storage;

import java.util.ArrayList;
import java.util.Map;

class Utils {
	
	public static final void buildCorrespondence(
			Map<WireHashKey, WireHashKey> mapWire,
			ArrayList<WireHashKey> arrayList1, ArrayList<WireHashKey> arrayList2)
			throws StoryStorageException {

		ArrayList<Integer> hashs1 = new ArrayList<Integer>();
		ArrayList<Integer> hashs2 = new ArrayList<Integer>();
		int k = arrayList1.size();
		for (int i = 0; i < k; i++) {
			hashs1.add(arrayList1.get(i).getSmallHash());
		}
		if (arrayList2.size() != k) {
			throw new StoryStorageException();
		}
		for (int i = 0; i < k; i++) {
			hashs2.add(arrayList2.get(i).getSmallHash());
		}

		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				if (hashs1.get(i).equals(hashs2.get(j))) {
					mapWire.put(arrayList1.get(i), arrayList2.get(j));
				}
			}
		}

	}

}

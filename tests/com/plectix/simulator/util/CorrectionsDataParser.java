package com.plectix.simulator.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CorrectionsDataParser extends Parser<Map<String, SortedSet<Long>>> {

	private static class ParseOptions {
		private boolean myNot = false;
		private boolean myAdd = true;
		private boolean myAnd = false;
		private ConnectionType myConnectionType = ConnectionType.EMPTY;

		public ConnectionType getConnectionType() {
			return myConnectionType;
		}

		public void setConnectionType(ConnectionType type) {
			myConnectionType = type;
		}

		public void resetFlags() {
			myNot = false;
			myAnd = false;
			myConnectionType = ConnectionType.EMPTY;
		}

		public boolean isAnd() {
			return myAnd;
		}

		public void setAnd(boolean myAnd) {
			this.myAnd = myAnd;
		}

		public boolean isNot() {
			return myNot;
		}

		public void setNot(boolean myNot) {
			this.myNot = myNot;
		}

		public boolean isAdd() {
			return myAdd;
		}

		public void setAdd(boolean myAdd) {
			this.myAdd = myAdd;
		}
	}

	private enum ConnectionType {
		FIND_A, FIND_B, LINK_AA, EMPTY;
	}

	private EasyFileReader myReader = getFileReader();

	public CorrectionsDataParser(String path) {
		super(path);
	}

	private void fill(Set<Long> set1, Set<Long> set2) {
		set1.clear();
		set1.addAll(set2);
	}

	private void connect(Set<Long> set, ConnectionType type) {
		switch (type) {
		case FIND_A: {
			fill(set, GreatFileUtility.findAndAddAPart(set));
			break;
		}
		case FIND_B: {
			fill(set, GreatFileUtility.findAndAddBPart(set));
			break;
		}
		case LINK_AA: {
			fill(set, GreatFileUtility.findAndAddAALink(set));
			break;
		}
		default:
		}
	}

	private void addOrRemoveAll(Set<Long> set1, Set<Long> set2,
			ParseOptions options) {
		boolean not = options.isNot();
		boolean add = options.isAdd();
		boolean and = options.isAnd();
		ConnectionType connectionType = options.getConnectionType();

		Set<Long> set = new TreeSet<Long>();

		if (and) {

			if (!not) {
				set.addAll(set2);
			} else {
				set.addAll(GreatFileUtility.not(set2));
			}
			connect(set, connectionType);
			fill(set1, SetUtilities.and(set1, set));

		} else {

			if (!not) {
				set.addAll(set2);
			} else {
				set.addAll(GreatFileUtility.not(set2));
			}

			connect(set, connectionType);

			if (add) {
				set1.addAll(set);
			} else {
				set1.removeAll(set);
			}
		}
	}

	private void processData(Set<Long> set, String command, String currentName,
			ParseOptions options) {

		switch (CorrectionsDataCommand.getByName(command)) {

		case GET_ALL_A:
			addOrRemoveAll(set, GreatFileUtility.allA(), options);
			break;
		case GET_ALL_B:
			addOrRemoveAll(set, GreatFileUtility.allB(), options);
			break;
		case GET_ALL_AA:
			addOrRemoveAll(set, GreatFileUtility.allAA(), options);
			break;
		case GET_ALL_X_INTERNAL:
			addOrRemoveAll(set, GreatFileUtility.allXInternal(), options);
			break;
		case GET_ALL_Y_INTERNAL:
			addOrRemoveAll(set, GreatFileUtility.allYInternal(), options);
			break;
		case GET_ALL_X_LINKED:
			addOrRemoveAll(set, GreatFileUtility.allXLinked(), options);
			break;
		case GET_ALL_Y_LINKED:
			addOrRemoveAll(set, GreatFileUtility.allYLinked(), options);
			break;
		case GET_ALL_K_INTERNAL:
			addOrRemoveAll(set, GreatFileUtility.allKInternal(), options);
			break;
		case GET_ALL_L_INTERNAL:
			addOrRemoveAll(set, GreatFileUtility.allLInternal(), options);
			break;
		case GET_ALL_Y:
			addOrRemoveAll(set, GreatFileUtility.allY(), options);
			break;
		case GET_ALL_L:
			addOrRemoveAll(set, GreatFileUtility.allL(), options);
			break;
		case GET_ALL_K_LINKED:
			addOrRemoveAll(set, GreatFileUtility.allKLinked(), options);
			break;
		case GET_ALL_L_LINKED:
			addOrRemoveAll(set, GreatFileUtility.allLLinked(), options);
			break;
		case GET_ALL_A_DOUBLE_LINKED:
			addOrRemoveAll(set, GreatFileUtility.allAADoubleLinked(), options);
			break;
		default:
		}
	}

	@Override
	protected Map<String, SortedSet<Long>> unsafeParse() {
		Map<String, SortedSet<Long>> map = new LinkedHashMap<String, SortedSet<Long>>();

		String line = "";
		String currentName = "";
		SortedSet<Long> set = new TreeSet<Long>();

		boolean flag = false;

		ParseOptions options = new ParseOptions();

		while (line != null) {
			if (line.startsWith("#name ")) {
				// flush
				if (!flag) {
					flag = true;
				} else {
					map.put(currentName, set);
					set = new TreeSet<Long>();
				}
				currentName = line.substring(6);
				options.setAdd(true);
			} else if (line.startsWith("#use ")) {
				String name = line.substring(5);
				SortedSet<Long> temp = map.get(name);
				if (temp != null) {
					addOrRemoveAll(set, temp, options);
				}
			} else if (line.startsWith("NO y")) {
				addOrRemoveAll(set, GreatFileUtility.allWithNoY(), options);
			} else if (line.startsWith("INCL B")) {
				connect(set, ConnectionType.FIND_B);
			} else if (line.startsWith("INCL A")) {
				connect(set, ConnectionType.FIND_A);
			} else if (line.startsWith("ALL")) {
				String command = line.substring(0, 6);
				processData(set, command, currentName, options);
			} else if (line.startsWith("MINUS")) {
				options.setAdd(false);
			} else if (line.startsWith("COMPL A")) {
				options.setConnectionType(ConnectionType.FIND_A);
				line = line.substring(8);
				continue;
			} else if (line.startsWith("LINK ALL A")) {
				connect(set, ConnectionType.LINK_AA);
			} else if (line.startsWith("LINK A")) {
				options.setConnectionType(ConnectionType.LINK_AA);
				line = line.substring(7);
				continue;
			} else if (line.startsWith("COMPL B")) {
				options.setConnectionType(ConnectionType.FIND_B);
				line = line.substring(8);
				continue;
			} else if (line.startsWith("AND")) {
				options.setAnd(true);
				line = line.substring(4);
				continue;
			} else if (line.startsWith("NOT")) {
				options.setNot(true);
				line = line.substring(4);
				continue;
			} else {
				addOrRemoveAll(set, GreatFileUtility.fillByLine(line), options);
			}

			options.resetFlags();

			line = myReader.getStringFromFile();
		}
		return map;
	}
}

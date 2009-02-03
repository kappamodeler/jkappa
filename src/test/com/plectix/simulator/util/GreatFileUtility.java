package com.plectix.simulator.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/*package*/class GreatFileUtility {

	private static boolean add(Set<Long> set, Long i) {
		if (set == null) {
			return false;
		}
		if (i != null) {
			set.add(i);
			return true;
		}
		return false;
	}

	private static void addAll(Set<Long> set, Collection<Integer> coll) {
		for (Integer l : coll) {
			add(set, (long) l);
		}
	}

	public static Set<Long> fillByLine(String line)
			throws NumberFormatException {
		Set<Long> set = new TreeSet<Long>();
		if (!("".equals(line))) {
			if ("E".equals(line)) {
				return set;
			}
			String[] pieces = line.split(" ");
			for (String str : pieces) {
				Piece number = new Piece();
				number = NumberQueryParser.parse(str);
				for (long i = number.getLower(); i <= number.getUpper(); i += number
						.getStep()) {

					add(set, i);

				}
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> allA() {
		int aQuantity = 137;
		Set<Long> set = new TreeSet<Long>();
		for (long i = 0; i < 7; i++) {
			add(set, i);
		}
		for (long i = 8; i < 73; i += 2) {
			add(set, i);
		}
		for (long i = 73; i <= aQuantity; i++) {
			add(set, i);
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> allXInternal() {
		Set<Long> set = new TreeSet<Long>();
		addAll(set, Arrays.asList(new Integer[] { 2, 4, 5, 8, 10, 14, 18, 22,
				26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 64, 66, 68, 70, 74, 76,
				77, 80, 84, 87, 88, 89, 91, 92, 93, 96, 100, 103, 104, 105,
				107, 108, 109, 112, 116, 119, 120, 121, 123, 124, 125, 128,
				130, 132, 133, 134, 135, 136, 137 }));

		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> allXLinked() {
		return Collections.unmodifiableSet(fillByLine("6=71 72-137"));
	}

	public static Set<Long> allYLinked() {
		return Collections.unmodifiableSet(fillByLine("60=70 126-137"));
	}

	public static Set<Long> allWithNoY() {
		return Collections.unmodifiableSet(fillByLine("0-2 6=10 72-77 79=93"));
	}

	public static Set<Long> allJustY() {
		return Collections.unmodifiableSet(fillByLine("0-2 6=10 72-77 79=93"));
	}

	public static Set<Long> allAA() {
		return Collections.unmodifiableSet(fillByLine("72-137"));
	}

	public static Set<Long> allB() {
		return Collections.unmodifiableSet(fillByLine("7=71"));
	}

	public static Set<Long> findAndAddBPart(Set<Long> aParts) {
		Set<Long> set = new TreeSet<Long>();
		for (Long l : aParts) {
			if (allB().contains(l + 1)) {
				set.add(l);
				set.add(l + 1);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> findAndAddAPart(Set<Long> bParts) {
		Set<Long> set = new TreeSet<Long>();
		for (Long l : bParts) {
			if (allA().contains(l - 1)) {
				set.add(l - 1);
				set.add(l);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> findAndAddAALink(Set<Long> aParts) {
		Set<Long> set = new TreeSet<Long>();
		for (Long l : aParts) {
			if (allAA().contains(l)) {
				if (l % 2 == 1) {
					set.add(l - 1);
					set.add(l);
				} else {
					set.add(l);
					set.add(l + 1);
				}
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> allLInternal() {
		return Collections.unmodifiableSet(fillByLine("45=59 71"));
	}

	public static Set<Long> allAADoubleLinked() {
		return Collections.unmodifiableSet(fillByLine("126-137"));
	}

	public static Set<Long> allL() {
		return Collections.unmodifiableSet(fillByLine("29=59"));
	}

	public static Set<Long> allLLinked() {
		return Collections.unmodifiableSet(fillByLine("61=71"));
	}

	public static Set<Long> allKLinked() {
		return allB();
	}

	public static Set<Long> allKInternal() {
		return Collections
				.unmodifiableSet(fillByLine("11 21=27 37=43 53=59 67=71"));
	}

	public static Set<Long> allYInternal() {
		return Collections.unmodifiableSet(fillByLine("5 16 18 24 26 "
				+ "32 34 40 42 48 50 56 58 64 68 70 82 84 90 92 98 100 "
				+ "106 108 111 113-117 119 121-125 130 134 136 137"));
	}

	public static Set<Long> not(Set<Long> set1) {
		Set<Long> set = new TreeSet<Long>();
		for (long i = 0; i <= 137; i++) {
			if (!set1.contains(i)) {
				set.add(i);
			}
		}
		return Collections.unmodifiableSet(set);
	}

	public static Set<Long> allY() {
		return Collections
				.unmodifiableSet(fillByLine("3 4 5 12=58 78=92 94-125"));
	}
}

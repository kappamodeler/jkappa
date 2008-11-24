package com.plectix.simulator.util;

import java.util.*;

public class Failer {
	private String myCurrentTestFileName;

	public Failer() {
	}

	public void loadTestFile(String file) {
		myCurrentTestFileName = file;
	}

	public void fail(String message) {
		org.junit.Assert.fail(message);
	}

	private void myFail(String message) {
		if (myCurrentTestFileName != null) {
			fail(myCurrentTestFileName + " : " + message);
		} else {
			fail(message);
		}
	}

	public void failOnMC(MessageConstructor mc) {
		if (!mc.isEmpty()) {
			fail(mc.getMessage());
		}
	}

	public void assertTrue(String message, boolean value) {
		if (!value) {
			myFail(message);
		}
	}

	public void assertFalse(String message, boolean value) {
		assertTrue(message, !value);
	}

	private String failedEquals(Object a, Object b) {
		return "expected " + a.toString() + ", but was " + b.toString();
	}

	public void assertEquals(String message, Object a, Object b) {
		String newMessage = message + " " + failedEquals(a, b);
		if (a == null) {
			assertTrue(newMessage, b == null);
		} else {
			assertTrue(newMessage, a.equals(b));
		}
	}

	private boolean close(double a, double b) {
		return (Math.abs(a - b) < 1e-10);
	}
	
	public void assertDoubleEquals(String message, double a, double b) {
		String newMessage = message + " " + failedEquals(a, b);
		assertTrue(newMessage, close(a, b));
	}
	
	public boolean collectionElementEquals(Object a, Object b) {
		if (a != null) {
			return a.equals(b);
		} else {
			return b == null;
		}
	}
	
	public <E> boolean collectionsEquals(Collection<E> a, Collection<E> b) {
		CollectionsComparator cc = new CollectionsComparator() {
			public boolean equals(Object a, Object b) {
				return collectionElementEquals(a, b);
			}
		};
		return cc.areEqual(a, b);
	}

	public <E> void assertEquals(String message, Collection<E> a,
			Collection<E> b) {
		String newMessage = message + " " + failedEquals(a, b);

		if (a == null) {
			assertTrue(newMessage + failedEquals(a, b), b == null);
		} else {
			if (b == null) {
				myFail(newMessage);
			} else {
				assertTrue(newMessage, collectionsEquals(a, b));
			}
		}
	}

	public void assertEmpty(String message, Collection<?> e) {
		assertSizeEquality(message, e, 0);
	}

	public void assertSizeEquality(String message, Collection<?> e,
			Integer expected) {
		if (expected == null) {
			myFail("test initialization error");
		}
		if (e == null) {
			assertEquals(message + " size : ", expected, 0);
		} else {
			assertEquals(message + " size : ", expected, e.size());
		}
	}
}

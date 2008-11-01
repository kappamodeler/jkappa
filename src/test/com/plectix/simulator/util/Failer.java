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
			assertTrue(newMessage + failedEquals(a, b), b == null);
		} else {
			assertTrue(newMessage, a.equals(b));
		}
	}

	// may be overrided for some needs, such as.. =)
	public boolean overrideEquals(Object a, Object b) {
		return a.equals(b);
	}
	
	public <E> boolean expandedEquals(Collection<E> a, Collection<E> b) {
		Stack<E> bStack = new Stack<E>();
		bStack.addAll(b);
		
		Collection<E> aCollection = new ArrayList<E>();
		aCollection.addAll(a);
				
		E aElement = null;
		E bElement;
		
		while (!bStack.isEmpty()) {
			boolean contains = false;
			bElement = bStack.pop();
			for (E elementA : aCollection) {
				if (elementA != null) {
					if (overrideEquals(elementA, bElement)) {
						aElement = elementA;
						contains = true;
						break;
					}
				} else {
					contains = (bElement == null);
				}
			}
			if (!contains) {
				return false;
			} else {
				aCollection.remove(aElement);
			}
		}
		
		if (!aCollection.isEmpty()) {
			return false;
		} else {
			return true;
		}
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
				assertTrue(newMessage, expandedEquals(a, b));
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

package com.plectix.simulator.util;

import static org.junit.Assert.fail;

import java.util.Collection;

public class Failer {
	private String myCurrentTestFileName;
	
	public Failer() {
	}
	
	public void loadTestFile(String file) {
		myCurrentTestFileName = file;
	}
	
	private void myFail(String message) {
		fail(myCurrentTestFileName + " : " + message);
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
	
	public void assertEmpty(String message, Collection<?> e) {
		assertSizeEquality(message, e, 0);
	}
	
	public void assertSizeEquality(String message, Collection<?> e, Integer expected) {
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

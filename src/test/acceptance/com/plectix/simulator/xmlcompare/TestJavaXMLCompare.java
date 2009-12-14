package com.plectix.simulator.xmlcompare;

import org.junit.Test;

import com.plectix.simulator.util.FileDirComparator;

public class TestJavaXMLCompare {

	// @Before
	// public void prepareXML() {
	// JavaXMLMaker xmlMaker = new JavaXMLMaker(PathFinder.SOURCE_DIR,
	// PathFinder.OUTPUT_DIR);
	// try {
	// xmlMaker.make();
	// } catch (Exception e) {
	// org.junit.Assert.fail(e.getMessage());
	// }
	// }

	/**
	 * IMPORTANT! Run JavaXMLMaker before this test this test compares the
	 * latest versions of XML-results with previous ones. it's useful to make
	 * sure, when you did some little changes, which final results should not
	 * depend on
	 */
	@Test
	public void compare() {
		try {
			String message = (new FileDirComparator(
					PathFinder.PREVIOUS_OUTPUT_DIR, PathFinder.OUTPUT_DIR,
					"xml")).compare();
			if (!"".equals(message)) {
				org.junit.Assert.fail(message);
			}
		} catch (Exception e) {
			org.junit.Assert.fail(e.getMessage());
		}
	}
}

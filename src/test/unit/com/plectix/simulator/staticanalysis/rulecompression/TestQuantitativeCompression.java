package com.plectix.simulator.staticanalysis.rulecompression;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.staticanalysis.LibraryOfRules;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.localviews.LibraryOfLocalViews;
import com.plectix.simulator.staticanalysis.localviews.LocalViewsMain;
import com.plectix.simulator.util.DefaultPropertiesForTest;

public class TestQuantitativeCompression extends DefaultPropertiesForTest{
	static LibraryOfRules libraryOfRules = TestsRuleCompressions.libraryOfRules;
	static LibraryOfLocalViews libraryOfViews = TestsRuleCompressions.libraryOfViews;
	static List<String> rules;
	static List<String> initial;
	static List<String> results;
	/*
	 * as example or smoke test
	 */

	static {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		List<Rule> rules = new LinkedList<Rule>();
		for (String s : libraryOfRules.getRules()) {
			try {
				rules.add(libraryOfRules.getRuleByString(s));
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
		}
		initial = new LinkedList<String>();
		LocalViewsMain views = null;
		initial.add(
				"R(l,r,Y48~u,Y68~u),E(r),R(l,r,Y48~u,Y68~p),G(a,b),So(d),G(a,b),Sh(pi,Y7~u)"
		);

		try {
			views = libraryOfViews.getLocalViews(initial);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		QuantitativeCompressor q = new QuantitativeCompressor(views);
		for (Rule rule : rules) {
//			System.out.println(rule);
			q.compress(rule);
		}

	}

	@Before
	public void clear() {
		rules = new LinkedList<String>();

		initial = new LinkedList<String>();

		results = new LinkedList<String>();
	}

	@After
	public void test() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		runTestCompress(rules, initial, results);
	}

	@Test
	public void testBugEng393() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		rules.add("Ste2(pheromone!1),Pheromone(ste2!1)->Ste2(pheromone)");

		initial.add("Ste2(pheromone!1),Pheromone(ste2!1)");

		results.add("Pheromone(ste2!_)-> ");

	}
	private void runTestCompress(List<String> rulesStr, List<String> initial,
			List<String> results) throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		LocalViewsMain views = libraryOfViews.getLocalViews(initial);

		List<Rule> rules = new LinkedList<Rule>();
		List<Rule> compressed = new LinkedList<Rule>();
		for (String s : rulesStr) {
			rules.add(libraryOfRules.getRuleByString(s));
		}
		for (String s : results) {
			compressed.add(libraryOfRules.getRuleByString(s));
		}
		QuantitativeCompressor q = new QuantitativeCompressor(views);
		testCompress(q, rules, compressed);
	}

	private void testCompress(QuantitativeCompressor q, List<Rule> rules,
			List<Rule> compressed) {
		for (Rule rule : rules) {
			q.compress(rule);
			Rule r = q.getCompressedRule();
			boolean yes = false;
			for (Rule c : compressed) {
				if (c.equalz(r)) {
					yes = true;
				}
			}
			assertTrue(yes);
		}
	}
}

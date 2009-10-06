package com.plectix.simulator.rulecompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.plectix.simulator.bestiary.LibraryOfLocalViews;
import com.plectix.simulator.component.complex.abstracting.AbstractAgent;
import com.plectix.simulator.component.complex.abstracting.AbstractSite;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;

public class TestRuleUtils {
	LibraryOfLocalViews libraryOfViews = TestsRuleCompressions.libraryOfViews;

	@Test
	public void testAddAllVariants() {
		Set<Integer> set1 = new LinkedHashSet<Integer>();
		Set<Integer> set2 = new LinkedHashSet<Integer>();
		Set<Integer> set3 = new LinkedHashSet<Integer>();
		Set<Integer> add = new LinkedHashSet<Integer>();
		set1.add(0);
		set1.add(1);
		set1.add(2);

		set2.add(1);
		set2.add(2);
		set2.add(3);

		set3.add(2);
		set3.add(3);
		set3.add(4);

		add.add(6);
		add.add(7);
		List<Set<Integer>> list = new LinkedList<Set<Integer>>();
		list.add(set1);
		list.add(set2);
		list.add(set3);

		for (Set<Integer> sets : RuleCompressionUtils.addAllVariants(list, add)) {
			assertTrue(sets.contains(6) ^ sets.contains(7));
			int i = 0;
			for (Set<Integer> ss : list) {
				if (sets.containsAll(ss)) {
					i++;
				}
			}
			assertTrue(i == 1);
		}

	}

	@Test
	public void testUniqueConponent() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> initial = new LinkedList<String>();
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");
		LocalViewsMain views = libraryOfViews.getLocalViews(initial);

		for (List<AbstractAgent> list : views.getLocalViews().values()) {
			for (AbstractAgent agent : list) {
				for (AbstractSite site : agent.getSitesMap().values()) {

					assertTrue(RuleCompressionUtils
							.uniqueConponent(site, views));
				}
			}
		}

		initial.clear();
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");
		initial.add("Sh(Y7~u!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");
		views = libraryOfViews.getLocalViews(initial);

		for (List<AbstractAgent> list : views.getLocalViews().values()) {
			for (AbstractAgent agent : list) {
				for (AbstractSite site : agent.getSitesMap().values()) {
					assertFalse(RuleCompressionUtils.uniqueConponent(site,
							views));
				}
			}
		}

		initial.clear();
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~u!1)");
		views = libraryOfViews.getLocalViews(initial);

		for (List<AbstractAgent> list : views.getLocalViews().values()) {
			for (AbstractAgent agent : list) {
				for (AbstractSite site : agent.getSitesMap().values()) {
					if (!site.getName().equals("Y48")) {
						assertFalse(RuleCompressionUtils.uniqueConponent(site,
								views));
					} else {
						assertTrue(RuleCompressionUtils.uniqueConponent(site,
								views));
					}
				}
			}
		}

	}

}

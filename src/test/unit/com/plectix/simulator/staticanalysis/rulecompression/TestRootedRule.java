package com.plectix.simulator.staticanalysis.rulecompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.staticanalysis.LibraryOfRules;
import com.plectix.simulator.staticanalysis.LinkStatus;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

public class TestRootedRule {
	private LibraryOfRules libraryOfRules = TestsRuleCompressions.libraryOfRules;

	@Test
	public void testFindCorrespondence() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		Rule rule1 = libraryOfRules
				.getRuleByString("A(x~u!1),B(x!1) ->A(x~u!1),B(x!1)");
		Rule rule2 = libraryOfRules
				.getRuleByString("A(x~p!1),B(x!1) ->A(x~p!1),B(x!1)");
		Rule rule3 = libraryOfRules
				.getRuleByString("B(x!1),A(x~u!1) ->B(x!1),A(x~u!1)");
		Rule rule4 = libraryOfRules
				.getRuleByString("B(x!1),A(x~p!1) ->B(x!1),A(x~p!1)");
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(rule1);
		rules.add(rule2);
		rules.add(rule3);
		rules.add(rule4);

		testCorrespondence(rules);
		rules.clear();

		for (String s : libraryOfRules.getRulesForCompress()) {
			rules.add(libraryOfRules.getRuleByString(s));
		}
		testCorrespondence(rules);

		rules.clear();
		rules.add(libraryOfRules
				.getRuleByString("A(x~u!1),B(x!1) ->A(x~p!1),B(x!1)"));
		rules
				.add(libraryOfRules
						.getRuleByString("A(x~u!1),B(x!1,y!2),C(z!2) ->A(x~p!1),B(x!1,y!2),C(z!2))"));
		rules.add(libraryOfRules.getRuleByString("A(x~u!_) ->A(x~p!_)"));
		rules.add(libraryOfRules.getRuleByString("A(x~u) ->A(x~p)"));
		testCorrespondence(rules);

	}

	private void testCorrespondence(List<Rule> rules) {
		for (Rule r : rules) {
			RuleMaster rm = new RuleMaster(r);
			for (RootedRule rr : rm.getAllRootedVersions()) {
				for (Rule r2 : rules) {
					RuleMaster rm2 = new RuleMaster(r2);
					for (RootedRule rr2 : rm2.getAllRootedVersions()) {
						if (!correspondenceRule(rr, rr2)) {
							break;
						}
					}
				}
			}
		}
	}

	private boolean correspondenceRule(RootedRule r1, RootedRule r2) {
		Map<Integer, Integer> map12 = r1.findCorrespondenceToRule(r2);
		Map<Integer, Integer> map21 = r2.findCorrespondenceToRule(r1);

		assertTrue((map12 == null && map21 == null)
				|| (map12 != null && map21 != null));

		if (map12 != null) {
			for (Integer i : map12.keySet()) {
				ShadowAgent shadowAgent = r2.getMapBefore().get(map12.get(i));
				ShadowAgent shadowAgent2 = r1.getMapBefore().get(i);
				if (shadowAgent2 != null) {
					if (shadowAgent != null) {
						assertTrue(shadowAgent.hasSimilarName(shadowAgent2));
					}
				} else {
					assertFalse(shadowAgent != null);
					shadowAgent = r2.getMapAfter().get(map12.get(i));
					shadowAgent2 = r1.getMapAfter().get(i);
					if (shadowAgent != null) {
						assertTrue(shadowAgent.hasSimilarName(shadowAgent2));
					}
				}
			}
			return true;

		}
		return false;

	}

	@Test
	public void testStringAction() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		for (String s : libraryOfRules.getElementaryRules()) {
			Rule rule = libraryOfRules.getRuleByString(s);
			RuleMaster master = new RuleMaster(rule);
			testActions(master);
		}
		for (String s : libraryOfRules.getRulesForCompress()) {
			Rule rule = libraryOfRules.getRuleByString(s);
			RuleMaster master = new RuleMaster(rule);
			testActions(master);
		}

		List<String> manyActionsRules = new LinkedList<String>();

		manyActionsRules.add("A(x~u,y~p,z!_) ->A(x~q,y~u,z)");
		manyActionsRules.add("A(x~u,y~p,z!_) ->A(x~q,y~u,z!1),B(x!1)");
		manyActionsRules
				.add("A(x~u!2,y~p!1,z!_),B(x!1),B(x!2) ->A(x~q,y~u,z!1),B(x!1)");
		manyActionsRules.add("A(x~u!2,y~p!1,z!_),B(x~u!1),B(x~p!2) ->");
		manyActionsRules.add("A(x~u!1),B(x!1) ->A(x~p!1),C(x!1)");
		manyActionsRules.add("A(x~u!1),B(x!1) ->A(x~p!1),C(x!1),D()");
		manyActionsRules.add("A(x~u!1),B(x!1) ->A(x~u!1),B(x!1)");

		for (String s : manyActionsRules) {
			Rule rule = libraryOfRules.getRuleByString(s);
			RuleMaster master = new RuleMaster(rule);
			testActions(master);
		}

	}

	private void testActions(RuleMaster master) {
		for (RootedRule rr : master.getAllRootedVersions()) {
			for (ShadowAgent sa : rr.getMapAfter().values()) {
				if (rr.getMapBefore().get(sa.getIdInRuleHandside()) == null) {
					assertTrue(contains(rr.getActionsString(), "ADD : "
							+ sa.getName()));
				}
			}

			for (ShadowAgent sa : rr.getMapBefore().values()) {
				if (rr.getMapAfter().get(sa.getIdInRuleHandside()) == null) {
					assertTrue(contains(rr.getActionsString(), "DELETE : "
							+ sa.getName()));
					continue;
				}
				for (Site site : sa.getSites()) {
					Site otherSite = rr.getMapAfter().get(
							sa.getIdInRuleHandside()).getSiteByName(
							site.getName());
					if (!otherSite.equalz(site)) {
						if (!otherSite.getInternalState().equalz(
								site.getInternalState())) {
							assertTrue(contains(rr.getActionsString(),
									"MODIFY : " + sa.getName() + "("
											+ site.getName()));
						}
						if (site.getLinkState().getStatusLink() == LinkStatus.FREE) {
							if (otherSite.getLinkState().getStatusLink() == LinkStatus.BOUND) {
								assertTrue(contains(rr.getActionsString(),
										"BOUND : " + sa.getName() + "("
												+ site.getName()));
							}
						} else {
							if (otherSite.getLinkState().getStatusLink() == LinkStatus.FREE) {
								assertTrue(contains(rr.getActionsString(),
										"BREAK : " + sa.getName() + "("
												+ site.getName()));
							} else {
								if (!otherSite.getLinkState().equalz(
										site.getLinkState())) {
									assertTrue(contains(rr.getActionsString(),
											"BREAK : " + sa.getName() + "("
													+ site.getName()));
									assertTrue(contains(rr.getActionsString(),
											"BOUND : " + sa.getName() + "("
													+ site.getName()));
								}
							}
						}
					}
				}
			}

		}
	}

	private boolean contains(Collection<String> actionsString, String string) {

		for (String s : actionsString) {
			if (s.startsWith(string)) {
				return true;
			}
		}
		return false;
	}

}

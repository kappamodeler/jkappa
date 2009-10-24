package com.plectix.simulator.staticanalysis.rulecompression;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.staticanalysis.LibraryOfRules;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.Site;

public class TestRuleMaster {
	LibraryOfRules libraryOfRules = TestsRuleCompressions.libraryOfRules;

	@Test
	public void testActionAgents() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		for (String r : libraryOfRules.getElementaryRules()) {
			Rule rule = libraryOfRules.getRuleByString(r);
			RuleMaster master = new RuleMaster(rule);

			for (ShadowAgent sa : master.getMapBefore().values()) {
				assertTrue(sa.isActionAgent());
			}
			for (ShadowAgent sa : master.getMapAfter().values()) {
				if (master.getMapBefore().get(sa.getIdInRuleHandside()) == null) {
					assertTrue(sa.isActionAgent());
				} else {
					assertFalse(sa.isActionAgent());
				}
			}
		}
	}

	@Test
	public void testAllRootedVersion() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		for (String r : libraryOfRules.getRulesForCompress()) {
			Rule rule = libraryOfRules.getRuleByString(r);
			RuleMaster master = new RuleMaster(rule);
			List<RootedRule> rules = master.getAllRootedVersions();
			testActionRoots(master, rules);
			testTestedRoots(master, rules);
		}
	}

	private void testTestedRoots(RuleMaster master, List<RootedRule> rules) {
		for (ShadowAgent sa : master.getMapBefore().values()) {
			if (!sa.isActionAgent()) {
				boolean t = false;
				for (RootedRule rr : rules) {
					if (rr.getRoots().contains(sa.getIdInRuleHandside())) {
						t = true;
						break;
					}
				}
				if (t) {
					RootedRule any = rules.get(0);
					for (Site s : any.getNeighboorsSites(any.getMapBefore()
							.get(sa.getIdInRuleHandside()))) {
						assertFalse(((ShadowAgent) (s.getLinkState()
								.getConnectedSite().getParentAgent()))
								.isActionAgent());
					}
				}
			}
		}
	}

	private void testActionRoots(RuleMaster master, List<RootedRule> rules) {
		for (ShadowAgent sa : master.getMapBefore().values()) {
			if (sa.isActionAgent()) {
				boolean t = false;
				for (RootedRule rr : rules) {
					if (rr.getRoots().contains(sa.getIdInRuleHandside())) {
						t = true;
						break;
					}
				}
				assertTrue(t);
			}
		}
		for (ShadowAgent sa : master.getMapAfter().values()) {
			if (sa.isActionAgent()) {
				boolean t = false;
				for (RootedRule rr : rules) {
					if (rr.getRoots().contains(sa.getIdInRuleHandside())) {
						t = true;
						break;
					}
				}
				assertTrue(t);
			}
		}
	}

}

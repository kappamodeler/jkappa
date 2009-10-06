package com.plectix.simulator.rulecompression;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.plectix.simulator.bestiary.LibraryOfLocalViews;
import com.plectix.simulator.bestiary.LibraryOfRules;
import com.plectix.simulator.component.Rule;
import com.plectix.simulator.component.complex.localviews.LocalViewsMain;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.ParseErrorException;

public class TestQualitativeCompression {
	LibraryOfRules libraryOfRules = TestsRuleCompressions.libraryOfRules;
	LibraryOfLocalViews libraryOfViews = TestsRuleCompressions.libraryOfViews;

	@Test
	public void testBuildGroups() {
		//fail("Not yet implemented");
	}

	@Test
	public void testAddRuleToGroup() {

	}

	@Test
	public void testCompressGroups() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		testCompressModify();
		testCompressAdd();
		testCompressDelete();
		testCompressBreak();
		testCompressModifyDivide();
		testCompressDivide();
		testBugBreak();
		testBugResponce();
		testBugYet();
	}

	private void testBugYet() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		List<String> rules = new LinkedList<String>();
		rules.add("R(l,r) , E(r) -> R(l!1,r) , E(r!1)");
		rules.add("R(l!1,r),E(r!1) -> R(l,r),E(r)");
		rules.add("E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r) -> E(r!1) , E(r!2) ,  R(l!1,r!3) , R(l!2,r!3)");
		rules.add("E(r!1) , E(r!2) , R(l!1,r!3) , R(l!2,r!3) -> E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r)");

		List<String> initial = new LinkedList<String>();
		initial.add("E(r)");
		initial.add("E(r!1),R(l!1,r)");
		initial.add("E(r!1),R(l!1,r!2),R(l!3,r!2),E(r!3)");
		initial.add("R(r,l)");

		List<String> results = new LinkedList<String>();
		results.add("R(r!_) -> R(r)");
		results.add("R(l!_,r) ->R(l,r)");
		results.add("R(l!_,r),R(l!_,r) -> R(l!_,r!1),R(l!_,r!1)");
		results.add("R(l) , E(r) -> R(l!1) , E(r!1)");

		runTestCompress(rules, initial, results);
	}

	private void testBugResponce() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		List<String> rules = new LinkedList<String>();
		rules.add("B(x!1,y,z~u?),B(x!1,y,z~u?) -> B(x,y,z~u?),B(x,y,z~u?)");
		rules.add("B(x!1,y!2,z~u?),A(x!1,y,z~u?),C(hi!2) -> B(x,y!1,z~u?),A(x,y,z~u?),C(hi!1) ");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x,z~u,y)");
		initial.add("B(y,x,z~u)");
		initial.add("A(x!1,z~u,y),B(y,x!1,z~u)");

		initial.add("C(hi)");
		List<String> results = new LinkedList<String>();
		results.add("A(x!_) ->A(x)");
		results.add("B(x!_) ->B(x)");
		// Because for A(x!1,z~u,y),B(y,x!1,z~u) we can't apply rules
		results.add("B(x!0),B(x!0)->B(x), B(x)");
		results.add("B(x!_,y!_)->B(x,y!_)");

		runTestCompress(rules, initial, results);
	}

	@Test
	public void testGetCompressedRule() {
		//fail("Not yet implemented");
	}

	@Test
	public void testExample() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<Rule> rules = new LinkedList<Rule>();
		for (String s : libraryOfRules.getRules()) {
			rules.add(libraryOfRules.getRuleByString(s));
		}
		List<String> initial = new LinkedList<String>();
		LocalViewsMain views = libraryOfViews.getLocalViews(initial);

		initial
				.add("R(l,r,Y48~u,Y68~u),E(r),R(l,r,Y48~u,Y68~u),E(r),G(a,b),So(d),G(a,b),Sh(pi,Y7~u)");
		QualitativeCompressor q = new QualitativeCompressor(views);

		q.buildGroups(rules);
		q.setLocalViews();
		q.compressGroups();
		// for (Rule r : rules) {
		// System.out.println(q.getCompressedRule(r));
		// }

	}

	private void testBugBreak() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("B(x!1,y!2),A(x!1,z~u),C(hi!2) -> B(x,y!1),A(x,z~u),C(hi!1)");
		rules.add("B(x!1,y~u),A(x!1) -> B(x,y~u),A(x)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x!1,z~u,y),B(y~u,x!1)");
		List<String> results = new LinkedList<String>();
		results.add("A(x!_) ->A(x)");
		results.add("B(x!_) ->B(x)");

		runTestCompress(rules, initial, results);

	}

	private void testCompressBreak() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules
				.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1) -> Sh(Y7~p,pi!1),G(a,b!2),So(d!2),R(Y48~p!1)");
		rules
				.add("Sh(Y7~p!2,pi!1),G(a!2,b),R(Y48~p!1) -> Sh(Y7~p,pi!1),G(a,b),R(Y48~p!1)");
		rules.add("Sh(Y7~p!1,pi),G(a!1,b) -> Sh(Y7~p,pi),G(a,b)");
		rules.add("Sh(Y7~p!1,pi),G(a!1,b) -> Sh(Y7~p,pi),G(a,b)");

		List<String> initial = new LinkedList<String>();
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");

		List<String> results = new LinkedList<String>();
		results.add("Sh(Y7!_) -> Sh(Y7~p)");
		runTestCompress(rules, initial, results);

		rules.clear();
		initial.clear();
		results.clear();

		rules.add("G(a,b!1) , So(d!1) -> G(a,b) , So(d)");
		rules
				.add("R(Y68~p!1) , G(a!1,b!2) , So(d!2) -> R(Y68~p!1) , G(a!1,b) , So(d)");
		rules
				.add("Sh(pi,Y7~p!2) , G(a!2,b!1) , So(d!1) -> Sh(pi,Y7~p!2) , G(a!2,b) , So(d)");
		rules
				.add("Sh(pi!_,Y7~p!2) , G(a!2,b!3) , So(d!3) -> Sh(pi!_,Y7~p!2) , G(a!2,b) , So(d)");
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");
		results.add("So(d!_) -> So(d)");
		results.add("G(b!_) -> G(b)");

		runTestCompress(rules, initial, results);
	}

	private void testCompressModifyDivide()
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("A(x~u,y~p)->A(x~u,y~u)");
		rules.add("A(x~p,y~p)->A(x~p,y~u)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x~u,y~p)");
		initial.add("A(x~p,y~p)");
		initial.add("A(x~s,y~p)");

		List<String> results = new LinkedList<String>();
		results.add("A(x~u?,y?)->A(x~u?,y~u?)");
		results.add("A(x~p?,y?)->A(x~p?,y~u?)");

		runTestCompress(rules, initial, results);

	}

	private void testCompressModify() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("A(x~u,y~p)->A(x~u,y~u)");
		rules.add("A(x~p,y~p)->A(x~p,y~u)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x~u,y~p)");
		initial.add("A(x~p,y~p)");

		List<String> results = new LinkedList<String>();
		results.add("A(y?)->A(y~u?)");
		runTestCompress(rules, initial, results);

	}

	private void testCompressAdd() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("A(x~u,y)->A(x~u,y!1),B(x!1)");
		rules.add("A(x~p,y)->A(x~p,y!1),B(x!1)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x~u,y)");
		initial.add("A(x~p,y)");

		List<String> results = new LinkedList<String>();
		results.add("A(y?)->A(y!1),B(x!1)");
		runTestCompress(rules, initial, results);

	}

	private void testCompressDelete() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("A(x~u,y!1),B(x!1)->A(x~u,y)");
		rules.add("A(x~p,y!1),B(x!1)->A(x~p,y)");
		rules.add("A(y!1,z!2),C(x!2),B(x!1) -> A(y,z!1),C(x!1)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x~u,y,z)");
		initial.add("A(x~p,y,z)");

		List<String> results = new LinkedList<String>();
		results.add("B(x!_) -> ");
		results.add("A(y!_) -> A(y)");
		runTestCompress(rules, initial, results);

	}

	private void testCompressDivide() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		List<String> rules = new LinkedList<String>();
		rules.add("A(x~u,y!1),B(x!1)->A(x~u,y),B(x)");
		rules.add("A(x~p,y!1),C(x!1)->A(x~p,y),C(x)");

		List<String> initial = new LinkedList<String>();
		initial.add("A(x~u,y!1),B(x!1)");
		initial.add("A(x~p,y!1),C(x!1)");

		List<String> results = new LinkedList<String>();
		results.add("A(y!_) -> A(y)");
		runTestCompress(rules, initial, results);

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
		QualitativeCompressor q = new QualitativeCompressor(views);
		q.buildGroups(rules);
		q.setLocalViews();
		q.compressGroups();

		testCompress(q, rules, compressed);
	}

	private void testCompress(QualitativeCompressor q, List<Rule> rules,
			List<Rule> compressed) {
		for (Rule r : rules) {
			r = q.getCompressedRule(r);
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

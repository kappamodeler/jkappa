package com.plectix.simulator.staticanalysis.rulecompression;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

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

public class TestQualitativeCompression {
	static LibraryOfRules libraryOfRules = TestsRuleCompressions.libraryOfRules;
	static LibraryOfLocalViews libraryOfViews = TestsRuleCompressions.libraryOfViews;
	static List<String> rules;
	static List<String> initial;
	static List<String> results;
	/*
	 * as example or smoke test
	 */
	
	static{
		
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
		try {
			views = libraryOfViews.getLocalViews(initial);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		initial.add("R(l,r,Y48~u,Y68~u),E(r),R(l,r,Y48~u,Y68~u),E(r),G(a,b),So(d),G(a,b),Sh(pi,Y7~u)");
		QualitativeCompressor q = new QualitativeCompressor(views);
		q.buildGroups(rules);
		q.setLocalViews();
		q.compressGroups();
	}

	@Before
	public void clear(){
		rules = new LinkedList<String>();

		initial = new LinkedList<String>();

		results = new LinkedList<String>();
	}
	@After
	public void test() throws IncompletesDisabledException, ParseErrorException, DocumentFormatException{
		runTestCompress(rules, initial, results);
	}
	

	@Test
	public void testBugYet() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		
		rules.add("R(l,r) , E(r) -> R(l!1,r) , E(r!1)");
		rules.add("R(l!1,r),E(r!1) -> R(l,r),E(r)");
		rules
				.add("E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r) -> E(r!1) , E(r!2) ,  R(l!1,r!3) , R(l!2,r!3)");
		rules
				.add("E(r!1) , E(r!2) , R(l!1,r!3) , R(l!2,r!3) -> E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r)");

		
		initial.add("E(r)");
		initial.add("E(r!1),R(l!1,r)");
		initial.add("E(r!1),R(l!1,r!2),R(l!3,r!2),E(r!3)");
		initial.add("R(r,l)");

		
		results.add("R(r!_) -> R(r)");
		results.add("R(l!_,r) ->R(l,r)");
		results.add("R(l!_,r),R(l!_,r) -> R(l!_,r!1),R(l!_,r!1)");
		results.add("R(l) , E(r) -> R(l!1) , E(r!1)");

	}
	@Test
	public void testBugEng393() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		rules.add("Ste2(pheromone!1),Pheromone(ste2!1)->Ste2(pheromone)");
		
		initial.add("Ste2(pheromone!1),Pheromone(ste2!1)");

		results.add("Pheromone(ste2!_)->");

	}
	
	@Test
	public void testBugEng391() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		
		rules.add("A(a!2),B(a,b!2),B(a,b!1),A(a!1) -> A(a!3),B(a!1,b!3),B(a!1,b!2),A(a!2)");
		rules.add("B(a,b),B(a,b) -> B(a!3,b),B(a!3,b)");
		rules.add("B(a,b!1),B(a,b),A(a!1) -> B(a!1,b!2),B(a!1,b),A(a!2)");
		
		initial.add("A(a!2),B(a,b!2),B(a,b!1),A(a!1)");
		initial.add("B(a,b),B(a,b)");
		initial.add("B(a,b!1),B(a,b),A(a!1)");
		

		results.add("B(a?),B(a?)->B(a!1),B(a!1)");

	}
	
	
	@Test
	public void testBugResponce() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {
		rules.add("B(x!1,y,z~u?),B(x!1,y,z~u?) -> B(x,y,z~u?),B(x,y,z~u?)");
		rules
				.add("B(x!1,y!2,z~u?),A(x!1,y,z~u?),C(hi!2) -> B(x,y!1,z~u?),A(x,y,z~u?),C(hi!1) ");

		initial.add("A(x,z~u,y)");
		initial.add("B(y,x,z~u)");
		initial.add("A(x!1,z~u,y),B(y,x!1,z~u)");

		initial.add("C(hi)");
		results.add("A(x!_) ->A(x)");
		results.add("B(x!_) ->B(x)");
		// Because for A(x!1,z~u,y),B(y,x!1,z~u) we can't apply rules
		results.add("B(x!0),B(x!0)->B(x), B(x)");
		results.add("B(x!_,y!_)->B(x,y!_)");

	}


	@Test
	public void testBugBreak() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		rules.add("B(x!1,y!2),A(x!1,z~u),C(hi!2) -> B(x,y!1),A(x,z~u),C(hi!1)");
		rules.add("B(x!1,y~u),A(x!1) -> B(x,y~u),A(x)");

		
		initial.add("A(x!1,z~u,y),B(y~u,x!1)");
		
		results.add("A(x!_) ->A(x)");
		results.add("B(x!_) ->B(x)");

	}
	@Test
	public void testCompressBreak() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		
		rules
				.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1) -> Sh(Y7~p,pi!1),G(a,b!2),So(d!2),R(Y48~p!1)");
		rules
				.add("Sh(Y7~p!2,pi!1),G(a!2,b),R(Y48~p!1) -> Sh(Y7~p,pi!1),G(a,b),R(Y48~p!1)");
		rules.add("Sh(Y7~p!1,pi),G(a!1,b) -> Sh(Y7~p,pi),G(a,b)");
		rules.add("Sh(Y7~p!1,pi),G(a!1,b) -> Sh(Y7~p,pi),G(a,b)");

		
		initial.add("Sh(Y7~p!3,pi!1),G(a!3,b!2),So(d!2),R(Y48~p!1)");

		
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

	}
	@Test
	public void testCompressModifyDivide()
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {

		
		rules.add("A(x~u,y~p)->A(x~u,y~u)");
		rules.add("A(x~p,y~p)->A(x~p,y~u)");

		
		initial.add("A(x~u,y~p)");
		initial.add("A(x~p,y~p)");
		initial.add("A(x~s,y~p)");

		
		results.add("A(x~u?,y?)->A(x~u?,y~u?)");
		results.add("A(x~p?,y?)->A(x~p?,y~u?)");


	}
	@Test
	public void testCompressModify() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		
		rules.add("A(x~u,y~p)->A(x~u,y~u)");
		rules.add("A(x~p,y~p)->A(x~p,y~u)");

		
		initial.add("A(x~u,y~p)");
		initial.add("A(x~p,y~p)");

		
		results.add("A(y?)->A(y~u?)");

	}
	@Test
	public void testCompressAdd() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		
		rules.add("A(x~u,y)->A(x~u,y!1),B(x!1)");
		rules.add("A(x~p,y)->A(x~p,y!1),B(x!1)");

		
		initial.add("A(x~u,y)");
		initial.add("A(x~p,y)");

		
		results.add("A(y?)->A(y!1),B(x!1)");

	}
	@Test
	public void testCompressDelete() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		
		rules.add("A(x~u,y!1),B(x!1)->A(x~u,y)");
		rules.add("A(x~p,y!1),B(x!1)->A(x~p,y)");
		rules.add("A(y!1,z!2),C(x!2),B(x!1) -> A(y,z!1),C(x!1)");

		
		initial.add("A(x~u,y,z)");
		initial.add("A(x~p,y,z)");

		
		results.add("B(x!_) -> ");
		results.add("A(y!_) -> A(y)");

	}
	@Test
	public void testCompressDivide() throws IncompletesDisabledException,
			ParseErrorException, DocumentFormatException {

		
		rules.add("A(x~u,y!1),B(x!1)->A(x~u,y),B(x)");
		rules.add("A(x~p,y!1),C(x!1)->A(x~p,y),C(x)");

		
		initial.add("A(x~u,y!1),B(x!1)");
		initial.add("A(x~p,y!1),C(x!1)");

		
		results.add("A(y!_) -> A(y)");

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

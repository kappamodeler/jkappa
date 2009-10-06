package com.plectix.simulator.bestiary;

import java.util.LinkedList;
import java.util.List;

import com.plectix.simulator.component.Rule;
import com.plectix.simulator.mocks.MockKappaSystemForRules;
import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.IncompletesDisabledException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.abstractmodel.ModelRule;
import com.plectix.simulator.parser.abstractmodel.reader.RulesParagraphReader;
import com.plectix.simulator.parser.builders.RuleBuilder;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.simulator.KappaSystemInterface;

public final class LibraryOfRules {

	private List<String> elementaryRules;
	private List<String> rulesForCompression;
	private List<String> rules;

	private final RulesParagraphReader ruleStringReader;
	private final KappaSystemInterface mockKappaSystem;
	private final RuleBuilder ruleBuilder;

	public LibraryOfRules() {
		ruleStringReader = new RulesParagraphReader(null,
				new AgentFactory(true));
		mockKappaSystem = new MockKappaSystemForRules();
		ruleBuilder = new RuleBuilder(mockKappaSystem);
		fullElementaryRules();
		fullCompressionRules();
		fullRules();
	}

	public final Rule getRuleByString(String ruleStr)
			throws IncompletesDisabledException, ParseErrorException,
			DocumentFormatException {
		KappaFileLine kl = new KappaFileLine(0, ruleStr);
		List<ModelRule> rules = new LinkedList<ModelRule>();
		ruleStringReader.fullRule(rules, 0, false, kl);

		return ruleBuilder.convert(rules.iterator().next());
	}

	public final List<String> getElementaryRules() {
		return elementaryRules;
	}

	public final List<String> getRulesForCompress() {
		return rulesForCompression;
	}

	private final void fullCompressionRules() {
		String[] list = {
				"R(Y68~p!1),G(a!1,b),So(d)->R(Y68~p!1),G(a!1,b!2),So(d!2)",
				"Ras(S1S2~gtp),Raf(x~u)->Ras(S1S2~gtp!1),Raf(x~u!1)",
				"Ras(S1S2~gtp!1),Raf(x!1) -> Ras(S1S2~gtp),Raf(x)",
				"Ras(S1S2~gtp),Raf(x~u) -> Ras(S1S2~gtp!1),Raf(x~u!1)",
				"Ras(S1S2~gtp!1),Raf(x~u!1) -> Ras(S1S2~gtp!1),Raf(x~p!1)",
				"Ras(S1S2~gtp!1),Raf(x!1) -> Ras(S1S2~gtp),Raf(x)",
				"PP2A1(s),Raf(x~p) -> PP2A1(s!1),Raf(x~p!1)",
				"PP2A1(s!1),Raf(x~p!1) -> PP2A1(s!1),Raf(x~u!1)",
				"PP2A1(s!1),Raf(x!1) -> PP2A1(s),Raf(x)",
				"Raf(x~p),MEK(S222~u) -> Raf(x~p!1),MEK(S222~u!1)",
				"Raf(x~p),MEK(s) -> Raf(x~p!1),MEK(s!1)",
				"Raf(x~p!1),MEK(S222~u!1) -> Raf(x~p!1),MEK(S222~p!1)",
				"Raf(x~p!1),MEK(S222!1) -> Raf(x~p),MEK(S222)",
				"Raf(x~p),MEK(S218~u) -> Raf(x~p!1),MEK(S218~u!1)",
				"Raf(x~p!1),MEK(S218~u!1) -> Raf(x~p!1),MEK(S218~p!1)",
				"Raf(x~p!1),MEK(s!1) -> Raf(x~p),MEK(s)",
				"Raf(x~p!1),MEK(S218!1) -> Raf(x~p),MEK(S218)",
				"PP2A2(s),MEK(S222~p) -> PP2A2(s!1),MEK(S222~p!1)",
				"PP2A2(s!1),MEK(S222~p!1) -> PP2A2(s!1),MEK(S222~u!1)",
				"PP2A2(s!1),MEK(S222!1) -> PP2A2(s),MEK(S222)",
				"PP2A2(s),MEK(S218~p) -> PP2A2(s!1),MEK(S218~p!1)",
				"PP2A2(s!1),MEK(S218~p!1) -> PP2A2(s!1),MEK(S218~u!1)",
				"PP2A2(s!1),MEK(S218!1) -> PP2A2(s),MEK(S218)",
				"MEK(s,S218~p,S222~p),ERK(T185~u) -> MEK(s!1,S218~p,S222~p),ERK(T185~u!1)",
				"MEK(s!1,S218~p,S222~p),ERK(T185~u!1) -> MEK(s!1,S218~p,S222~p),ERK(T185~p!1)",
				"MEK(s!1,S218~p,S222~p),ERK(T185!1) -> MEK(s,S218~p,S222~p),ERK(T185)",
				"MEK(s,S218~p,S222~p),ERK(Y187~u) -> MEK(s!1,S218~p,S222~p),ERK(Y187~u!1)",
				"MEK(s!1,S218~p,S222~p),ERK(Y187~u!1) -> MEK(s!1,S218~p,S222~p),ERK(Y187~p!1)",
				"MEK(s!1,S218~p,S222~p),ERK(Y187!1) -> MEK(s,S218~p,S222~p),ERK(Y187)",
				"MKP3(s),ERK(T185~p) -> MKP3(s!1),ERK(T185~p!1)",
				"MKP3(s!1),ERK(T185~p!1) -> MKP3(s!1),ERK(T185~u!1)",
				"MKP3(s!1),ERK(T185!1) -> MKP3(s),ERK(T185)",
				"MKP3(s),ERK(Y187~p) -> MKP3(s!1),ERK(Y187~p!1)",
				"MKP3(s!1),ERK(Y187~p!1) -> MKP3(s!1),ERK(Y187~u!1)",
				"MKP3(s!1),ERK(Y187!1) -> MKP3(s),ERK(Y187)", "A(x)->B(x)"

		};
		rulesForCompression = new LinkedList<String>();

		for (int i = 0; i < list.length; i++) {
			rulesForCompression.add(list[i]);
		}
	}

	private final void fullRules() {
		String[] list = {
				"R(l,r) , E(r) ->R(l!1,r) , E(r!1)",
				"R(l!1,r) , E(r!1) ->R(l,r) , E(r)",
				// "#R(l!1,r) , E(r!1) -> R(l,r) , E(r)",
				"E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r) ->E(r!1) , E(r!2) ,  R(l!1,r!3) , R(l!2,r!3)",
				"E(r!1) , E(r!2) , R(l!1,r!3) , R(l!2,r!3) -> E(r!1) , E(r!2) , R(l!1,r) , R(l!2,r)",
				"E(r!1) , E(r!2) , R(l!1,r!3,Y68~u) , R(l!2,r!3) -> E(r!1) , E(r!2) , R(l!1,r!3,Y68~p) , R(l!2,r!3)",
				"R(Y68~p) -> R(Y68~u)",
				"E(r!1) , E(r!2) , R(l!1,r!3,Y48~u) , R(l!2,r!3) -> E(r!1) , E(r!2) , R(l!1,r!3,Y48~p) , R(l!2,r!3)",
				"R(Y48~p) -> R(Y48~u)",
				"R(r!_,Y48~p!1) , Sh(pi!1,Y7~u) -> R(r!_,Y48~p!1), Sh(pi!1,Y7~p)",
				"Sh(pi!_,Y7~p) -> Sh(pi!_,Y7~u)",
				"Sh(pi,Y7~p) -> Sh(pi,Y7~u)",
				"R(Y68~p) , G(a,b) -> R(Y68~p!1) , G(a!1,b)",
				"R(Y68~p!1) , G (a!1,b) -> R(Y68~p) , G(a,b)",
				"R(Y68~p) , G(a,b!_) -> R(Y68~p!1) , G(a!1,b!_)",
				"R(Y68~p!1) , G(a!1,b!_) -> R(Y68~p) , G(a,b!_)",
				"R(Y68~p!1) , G(a!1,b) , So(d) -> R(Y68~p!1) , G(a!1,b!2) , So(d!2)",
				"R(Y68~p!1) , G(a!1,b!2) , So(d!2) -> R(Y68~p!1) , G(a!1,b) , So(d) ",
				"G(a,b) , So(d) -> G(a,b!1) , So(d!1)",
				"G(a,b!1) , So(d!1) -> G(a,b) , So(d)",
				"Sh(pi,Y7~p!2) , G(a!2,b) , So(d) -> Sh(pi,Y7~p!2) , G(a!2,b!1) , So(d!1)",
				"Sh(pi,Y7~p!2) , G(a!2,b!1) , So(d!1) -> Sh(pi,Y7~p!2) , G(a!2,b) , So(d)",
				"Sh(pi!_,Y7~p!2) , G(a!2,b) , So(d) -> Sh(pi!_,Y7~p!2) , G(a!2,b!3) , So(d!3)",
				"Sh(pi!_,Y7~p!2) , G(a!2,b!3) , So(d!3) -> Sh(pi!_,Y7~p!2) , G(a!2,b) , So(d)",
				"R(Y48~p) , Sh(pi,Y7~u) -> R(Y48~p!1) , Sh(pi!1,Y7~u)",
				"R(Y48~p!1) , Sh(pi!1,Y7~u) -> R(Y48~p) , Sh(pi,Y7~u)",
				"R(Y48~p) , Sh(pi,Y7~p) -> R(Y48~p!1) , Sh(pi!1,Y7~p)",
				"R(Y48~p!1) , Sh(pi!1,Y7~p) -> R(Y48~p) , Sh(pi,Y7~p)",
				"R(Y48~p) , Sh(pi,Y7~p!1) , G(a!1,b) -> R(Y48~p!2) , Sh(pi!2,Y7~p!1) , G(a!1,b)",
				"R(Y48~p!2) , Sh(pi!2,Y7~p!1) , G(a!1,b) -> R(Y48~p) , Sh(pi,Y7~p!1) , G(a!1,b)",
				"R(Y48~p) , Sh(pi,Y7~p!1) , G(a!1,b!3) , So(d!3) -> R(Y48~p!2) , Sh(pi!2,Y7~p!1) , G(a!1,b!3) , So(d!3)",
				"R(Y48~p!2) , Sh(pi!2,Y7~p!1) , G(a!1,b!3) , So(d!3) -> R(Y48~p) , Sh(pi,Y7~p!1) , G(a!1,b!3) , So(d!3)",
				"R(Y48~p!1) , Sh(pi!1,Y7~p) , G(a,b) -> R(Y48~p!1) , Sh(pi!1,Y7~p!2) , G(a!2,b)",
				"R(Y48~p!1) , Sh(pi!1,Y7~p!2) , G(a!2,b) -> R(Y48~p!1) , Sh(pi!1,Y7~p) , G(a,b)",
				"Sh(pi,Y7~p) , G(a,b) -> Sh(pi,Y7~p!1) , G(a!1,b)",
				"Sh(pi,Y7~p!1) , G(a!1,b) -> Sh(pi,Y7~p) , G(a,b)",
				"Sh(pi,Y7~p) , G(a,b!_) -> Sh(pi,Y7~p!1) , G(a!1,b!_)",
				"Sh(pi,Y7~p!1) , G(a!1,b!_) -> Sh(pi,Y7~p) , G(a,b!_)",
				"R(Y48~p!1) , Sh(pi!1,Y7~p) , G(a,b!3) , So(d!3) -> R(Y48~p!1) , Sh(pi!1,Y7~p!2) , G(a!2,b!3) , So(d!3)",
				"R(Y48~p!1) , Sh(pi!1,Y7~p!2) , G(a!2,b!3) , So(d!3) -> R(Y48~p!1) , Sh(pi!1,Y7~p) , G(a,b!3) , So(d!3)",
				"R(r!_,l) -> R(r,l)", "Sh(Y7~u!_) -> Sh(Y7~t)"

		};
		rules = new LinkedList<String>();

		for (int i = 0; i < list.length; i++) {
			rules.add(list[i]);
		}
	}

	private final void fullElementaryRules() {
		elementaryRules = new LinkedList<String>();
		elementaryRules.add(" -> A(x)");
		elementaryRules.add("A(x) ->");
		elementaryRules.add("A(x),B(x) -> A(x!1),b(x!1)");
		elementaryRules.add("A(x!1),B(x!1)->A(x),B(x)");
		elementaryRules.add("A(x~u) ->A(x~p)");
		elementaryRules.add("A(x!_) ->A(x)");
	}

	public final List<String> getRules() {
		return rules;
	}
}

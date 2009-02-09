package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.abstractmodel.*;
import com.plectix.simulator.parser.abstractmodel.perturbations.*;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractSpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.AbstractTimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractAddOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractDeleteOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractRateModification;
import com.plectix.simulator.parser.exceptions.DocumentFormatException;
import com.plectix.simulator.parser.exceptions.ParseErrorException;
import com.plectix.simulator.parser.exceptions.ParseErrorMessage;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.parser.util.StringUtil;
import com.plectix.simulator.simulator.SimulationArguments;

/*package*/class PerturbationsParagraphReader extends
		KappaParagraphReader<List<AbstractPerturbation>> {

	private AgentFactory myAgentFactory;

	public PerturbationsParagraphReader(KappaModel model,
			SimulationArguments arguments, AgentFactory factory) {
		super(model, arguments, factory);
		myAgentFactory = factory;
	}

	public final List<AbstractPerturbation> readComponent(
			KappaFileParagraph perturbationsParagraph)
			throws ParseErrorException, DocumentFormatException {
		List<AbstractPerturbation> perturbations = new ArrayList<AbstractPerturbation>();
		int pertubationID = 0;
		for (KappaFileLine perturbationStr : perturbationsParagraph.getLines()) {
			try {
				String st = perturbationStr.getLine();
				st = st.trim();

				AbstractCondition condition;
				AbstractModification modification;

				// condition
				if (st.indexOf("$T") == 0) {
					boolean greater = getGreater(st, 2);
					if (!greater) {
						throw new ParseErrorException(perturbationStr,
								ParseErrorMessage.WRONG_TIME_PERTURBATION_SYNTAX);
					}
					st = st.substring(2).trim();
					st = st.substring(1).trim();

					int index = st.indexOf("do");
					String timeStr = "";
					if (index != -1) {
						timeStr = st.substring(0, index).trim();
					} else {
						throw new ParseErrorException(perturbationStr,
								ParseErrorMessage.DO_EXPECTED);
					}
					double time = 0;

					try {
						time = Double.valueOf(timeStr);
					} catch (NumberFormatException e) {
						throw new ParseErrorException(perturbationStr,
								ParseErrorMessage.WRONG_TIME_BOUNDARY, timeStr);
					}

					condition = new AbstractTimeCondition(time);

				} else {
					condition = parseSpeciesExpression(st, perturbationStr);
				}

				StringUtil.checkString("do", st, perturbationStr);
				st = st.substring(st.indexOf("do") + 2);
				
				// modification
				modification = checkOnce(st, perturbationStr);
				if (modification == null) {
					modification = parseRateExpression(st, perturbationStr);
				}
				perturbations.add(new AbstractPerturbation(pertubationID++,
						condition, modification));
			} catch (ParseErrorException e) {
				e.setLineDescription(perturbationStr);
				throw e;
			}

		}
		return perturbations;
	}

	private final AbstractRateModification parseRateExpression(String st,
			KappaFileLine perturbationStr) throws ParseErrorException {
		boolean fail = false;
		if (st.length() > 0) {
			fail = Character.isLetter(st.charAt(0));
		} else {
			throw new ParseErrorException(perturbationStr,
					ParseErrorMessage.MODIFICATION_EXPECTED);
		}
		StringUtil.checkString("'", st, perturbationStr);
		st = st.substring(st.indexOf("'") + 1).trim();
		if (fail) {
			throw new ParseErrorException(perturbationStr,
					ParseErrorMessage.DO_EXPECTED);
		}
		StringUtil.checkString("'", st, perturbationStr);
		String ruleName = st.substring(0, st.indexOf("'")).trim();

		int index = st.indexOf(":=");
		StringUtil.checkString(":=", st, perturbationStr);
		st = st.substring(index + 2);

		LinearExpression expressionRHS = new RateExpressionParser().parse(st,
				perturbationStr);

		return new AbstractRateModification(ruleName, expressionRHS);
	}

	private final AbstractSpeciesCondition parseSpeciesExpression(String st,
			KappaFileLine perturbationStr) throws ParseErrorException {
		StringUtil.checkString("[", st, perturbationStr);

		st = st.substring(st.indexOf("[") + 1).trim();
		StringUtil.checkString("'", st, perturbationStr);
		st = st.substring(st.indexOf("'") + 1).trim();
		String argumentObservableName = StringUtil.getName(st);

		StringUtil.checkString("]", st, perturbationStr);
		st = st.substring(st.indexOf("]") + 1).trim();

		boolean greater = getGreater(st, 0);
		st = st.substring(1).trim();

		String condition = st.substring(0, st.indexOf("do"));
		LinearExpression expressionRHS = new SpeciesExpressionParser().parse(
				condition, perturbationStr);

		return new AbstractSpeciesCondition(argumentObservableName,
				expressionRHS, greater);
	}

	private final boolean getGreater(String line, int beginIndex)
			throws ParseErrorException {
		line = line.substring(beginIndex).trim();
		if (line.startsWith(">")) {
			return true;
		} else if (line.startsWith("<")) {
			return false;
		} else
			throw new ParseErrorException(ParseErrorMessage.SENSE_OF_INEQUALITY_EXPECTED, line);
	}

	private final AbstractOnceModification checkOnce(String st,
			KappaFileLine perturbationStr) throws ParseErrorException, DocumentFormatException {
		int indexAdd = st.indexOf("$ADDONCE");
		int indexDel = st.indexOf("$DELETEONCE");

		if (indexAdd == -1 && indexDel == -1)
			return null;
		if (indexAdd != -1 && indexDel != -1)
			throw new ParseErrorException(perturbationStr,
					ParseErrorMessage.$ADDONCE_OR_$DELETEONCE);

		boolean addOnce = false;

		String line = new String(st);
		if (indexAdd != -1) {
			line = line.substring(indexAdd + 8);
			addOnce = true;
		} else {
			line = line.substring(indexDel + 11);
		}

		double quantity = 1;
		int indexCount = line.indexOf("*");
		if (indexCount != -1) {

			String strCount = line.substring(0, indexCount).trim();
			if ("$INF".equals(strCount.trim())) {
				quantity = -1;
				if (addOnce) {
					throw new ParseErrorException(perturbationStr,
							ParseErrorMessage.$INF_USED_WITH_$ADDONCE);
				}
			} else {
				try {
					quantity = Double.valueOf(strCount);
				} catch (NumberFormatException e) {
					throw new ParseErrorException(perturbationStr,
							ParseErrorMessage.ONCE_QUANTITY_FORMAT, strCount);
				}	
			}
			line = line.substring(indexCount + 1);
		}
		
		List<AbstractAgent> agentList = myAgentFactory.parseAgent(line);

		AbstractOnceModification modification;
		if (addOnce) {
			modification = new AbstractAddOnceModification(agentList, quantity);
		} else {
			modification = new AbstractDeleteOnceModification(agentList, quantity);
		}

		return modification;
	}

}

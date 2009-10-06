package com.plectix.simulator.parser.abstractmodel.reader;

import java.util.ArrayList;
import java.util.List;

import com.plectix.simulator.parser.DocumentFormatException;
import com.plectix.simulator.parser.KappaFileLine;
import com.plectix.simulator.parser.KappaFileParagraph;
import com.plectix.simulator.parser.ParseErrorException;
import com.plectix.simulator.parser.ParseErrorMessage;
import com.plectix.simulator.parser.abstractmodel.ModelAgent;
import com.plectix.simulator.parser.abstractmodel.ModelPerturbation;
import com.plectix.simulator.parser.abstractmodel.perturbations.LinearExpression;
import com.plectix.simulator.parser.abstractmodel.perturbations.RateExpressionParser;
import com.plectix.simulator.parser.abstractmodel.perturbations.SpeciesExpressionParser;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.PerturbationCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.SpeciesCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.conditions.TimeCondition;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.AbstractOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelAddOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelDeleteOnceModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.ModelRateModification;
import com.plectix.simulator.parser.abstractmodel.perturbations.modifications.PerturbationModification;
import com.plectix.simulator.parser.util.AgentFactory;
import com.plectix.simulator.parser.util.StringUtil;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.util.InequalitySign;

/*package*/ final class PerturbationsParagraphReader extends
		KappaParagraphReader<List<ModelPerturbation>> {

	private final AgentFactory agentFactory;

	public PerturbationsParagraphReader(SimulationArguments arguments, AgentFactory factory) {
		super(arguments, factory);
		agentFactory = factory;
	}

	public final List<ModelPerturbation> readComponent(KappaFileParagraph perturbationsParagraph)
			throws ParseErrorException, DocumentFormatException {
		
		List<ModelPerturbation> perturbations = new ArrayList<ModelPerturbation>();
		int pertubationID = 0;
		for (KappaFileLine perturbationStr : perturbationsParagraph.getLines()) {
			try {
				String st = perturbationStr.getLine();
				st = st.trim();

				PerturbationCondition condition;
				PerturbationModification modification;

				// condition
				if (st.indexOf("$T") == 0) {
					InequalitySign inequalitySign = parseInequalitySign(st, 2);
					if (inequalitySign != InequalitySign.GREATER) {
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

					condition = new TimeCondition(time);

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
				perturbations.add(new ModelPerturbation(pertubationID++,
						condition, modification));
			} catch (ParseErrorException e) {
				e.setLineDescription(perturbationStr);
				throw e;
			}

		}
		return perturbations;
	}

	private final ModelRateModification parseRateExpression(String line,
			KappaFileLine perturbationLine) throws ParseErrorException {
		boolean fail = false;
		if (line.length() > 0) {
			fail = Character.isLetter(line.charAt(0));
		} else {
			throw new ParseErrorException(perturbationLine,
					ParseErrorMessage.MODIFICATION_EXPECTED);
		}
		StringUtil.checkString("'", line, perturbationLine);
		line = line.substring(line.indexOf("'") + 1).trim();
		if (fail) {
			throw new ParseErrorException(perturbationLine,
					ParseErrorMessage.DO_EXPECTED);
		}
		StringUtil.checkString("'", line, perturbationLine);
		String ruleName = line.substring(0, line.indexOf("'")).trim();

		int index = line.indexOf(":=");
		StringUtil.checkString(":=", line, perturbationLine);
		line = line.substring(index + 2);

		LinearExpression expressionRHS = new RateExpressionParser().parse(line,
				perturbationLine);

		return new ModelRateModification(ruleName, expressionRHS);
	}

	private final SpeciesCondition parseSpeciesExpression(String line,
			KappaFileLine perturbationLine) throws ParseErrorException {
		StringUtil.checkString("[", line, perturbationLine);

		line = line.substring(line.indexOf("[") + 1).trim();
		StringUtil.checkString("'", line, perturbationLine);
		line = line.substring(line.indexOf("'") + 1).trim();
		String argumentObservableName = StringUtil.parseRuleName(line);

		StringUtil.checkString("]", line, perturbationLine);
		line = line.substring(line.indexOf("]") + 1).trim();

		InequalitySign inequalitySign = parseInequalitySign(line, 0);
		line = line.substring(1).trim();

		String condition = line.substring(0, line.indexOf("do"));
		LinearExpression expressionRHS = new SpeciesExpressionParser().parse(
				condition, perturbationLine);

		return new SpeciesCondition(argumentObservableName,
				expressionRHS, inequalitySign);
	}

	private final InequalitySign parseInequalitySign(String line, int beginIndex)
			throws ParseErrorException {
		line = line.substring(beginIndex).trim();
		if (line.startsWith(">")) {
			return InequalitySign.GREATER;
		} else if (line.startsWith("<")) {
			return InequalitySign.LESS;
		} else
			throw new ParseErrorException(ParseErrorMessage.SENSE_OF_INEQUALITY_EXPECTED, line);
	}

	private final AbstractOnceModification checkOnce(String line,
			KappaFileLine perturbationLine) throws ParseErrorException, DocumentFormatException {
		int indexAdd = line.indexOf("$ADDONCE");
		int indexDel = line.indexOf("$DELETEONCE");

		if (indexAdd == -1 && indexDel == -1)
			return null;
		if (indexAdd != -1 && indexDel != -1)
			throw new ParseErrorException(perturbationLine,
					ParseErrorMessage.$ADDONCE_OR_$DELETEONCE);

		boolean addOnce = false;

		String lineCopy = new String(line);
		if (indexAdd != -1) {
			lineCopy = lineCopy.substring(indexAdd + 8);
			addOnce = true;
		} else {
			lineCopy = lineCopy.substring(indexDel + 11);
		}

		double quantity = 1;
		int indexCount = lineCopy.indexOf("*");
		if (indexCount != -1) {

			String strCount = lineCopy.substring(0, indexCount).trim();
			if ("$INF".equals(strCount.trim())) {
				quantity = -1;
				if (addOnce) {
					throw new ParseErrorException(perturbationLine,
							ParseErrorMessage.$INF_USED_WITH_$ADDONCE);
				}
			} else {
				try {
					quantity = Double.valueOf(strCount);
				} catch (NumberFormatException e) {
					throw new ParseErrorException(perturbationLine,
							ParseErrorMessage.ONCE_QUANTITY_FORMAT, strCount);
				}	
			}
			lineCopy = lineCopy.substring(indexCount + 1);
		}
		
		List<ModelAgent> agentList = agentFactory.parseAgent(lineCopy);

		AbstractOnceModification modification;
		if (addOnce) {
			modification = new ModelAddOnceModification(agentList, quantity);
		} else {
			modification = new ModelDeleteOnceModification(agentList, quantity);
		}

		return modification;
	}

}

package com.plectix.simulator.simulator.api.steps;

import java.util.List;

import com.plectix.simulator.simulator.KappaSystem;
import com.plectix.simulator.simulator.api.OperationType;
import com.plectix.simulator.staticanalysis.Rule;
import com.plectix.simulator.staticanalysis.StaticAnalysisException;
import com.plectix.simulator.staticanalysis.rulecompression.RuleCompressionType;

public class RuleCompressionOperation extends AbstractOperation<List<Rule>> {
	private final KappaSystem kappaSystem;
	private final RuleCompressionType type;
	
	public RuleCompressionOperation(KappaSystem kappaSystem, RuleCompressionType type) {
		super(kappaSystem.getSimulationData(), OperationType.RULE_COMPRESSION);
		this.kappaSystem = kappaSystem;
		this.type = type;
//		switch (type) {
//		case QUALITATIVE:	{
//			kappaSystem.getSimulationData().getSimulationArguments().setRunQualitativeCompression(true);
//			break;
//		}
//		case QUANTITATIVE: {
//			kappaSystem.getSimulationData().getSimulationArguments().setRunQuantitativeCompression(true);
//			break;
//		}
//		}
			
	}
	
	protected List<Rule> performDry() throws StaticAnalysisException {
		List<Rule> compressedRules = kappaSystem.compressRules(type, kappaSystem.getRules());
		return compressedRules;
	}

	@Override
	protected boolean noNeedToPerform() {
		//TODO we can optimize this
		return kappaSystem.getRuleCompressionBuilder() != null;
	}

	@Override
	protected List<Rule> retrievePreparedResult() {
		//TODO we can optimize this
		return kappaSystem.getRuleCompressionBuilder().getResults().getCompressedRules();
	}
}

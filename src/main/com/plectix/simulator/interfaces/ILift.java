package com.plectix.simulator.interfaces;

import java.util.List;

public interface ILift {

	class LiftElement{
		
		private IRule rule;
		
		private IInjection injection;
		
//		public LiftElement(IRule rule, IInjection injection) {
//			this.rule=rule;
//			this.injection = injection;
//		}

//		public IRule getRule(){
//			return rule;
//		}
		
		public IInjection getInjection(){
			return injection;
		}
		
	}

	public List<LiftElement> getLiftElements();
}

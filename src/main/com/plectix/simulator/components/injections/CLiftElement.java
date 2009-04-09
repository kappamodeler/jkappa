package com.plectix.simulator.components.injections;

import java.io.Serializable;

import com.plectix.simulator.components.CConnectedComponent;
import com.plectix.simulator.components.CSite;

/**
 * Class implements LiftElement, contains in Site. 
 * @author avokhmin
 * @see CSite
 */
@SuppressWarnings("serial")
public final class CLiftElement implements Serializable {
	
	private final CConnectedComponent connectedComponent;
	private final CInjection injection;

	/**
	 * Basic constructor.
	 * @param cc {@link CConnectedComponent} where contains <b>injectiuons</b>
	 * @param injection {@link CInjection injection} from <b>cc</b>
	 */
	public CLiftElement (CConnectedComponent cc, CInjection injection){
		this.connectedComponent = cc;
		this.injection = injection;
	}
	
	/**
	 * Returns {@link CConnectedComponent} from current LiftElement.
	 */
	public CConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	/**
	 * Returns {@link CInjection} from current LiftElement.
	 */
	public CInjection getInjection(){
		return injection;
	}
	
}

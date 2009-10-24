package com.plectix.simulator.simulationclasses.injections;

import java.io.Serializable;

import com.plectix.simulator.staticanalysis.ConnectedComponent;
import com.plectix.simulator.staticanalysis.Site;

/**
 * Class implements LiftElement, contains in Site.
 * 
 * @author avokhmin
 * @see Site
 */
@SuppressWarnings("serial")
public final class LiftElement implements Serializable {

	private final ConnectedComponent connectedComponent;
	private final Injection injection;

	/**
	 * Basic constructor.
	 * 
	 * @param component
	 *            {@link ConnectedComponent} where contains <b>injectiuons</b>
	 * @param injection
	 *            {@link Injection injection} from <b>cc</b>
	 */
	public LiftElement(ConnectedComponent component, Injection injection) {
		this.connectedComponent = component;
		this.injection = injection;
	}

	/**
	 * Returns {@link ConnectedComponent} from current LiftElement.
	 */
	public final ConnectedComponent getConnectedComponent() {
		return connectedComponent;
	}

	/**
	 * Returns {@link Injection} from current LiftElement.
	 */
	public final Injection getInjection() {
		return injection;
	}
}

package com.plectix.simulator.rulecompression;

import com.plectix.simulator.components.CSite;
import com.plectix.simulator.parser.util.IdGenerator;

/*package*/ class SubstanceMaster {
	private final IdGenerator linkIndexGenerator = new IdGenerator();
	
	public void connect(CSite one, CSite two) {
		if (one.getLinkState().getConnectedSite() == two 
				&& two.getLinkState().getConnectedSite() == one) {
			// reject connection
			// TODO remove?
			return;
		}
		one.getLinkState().connectSite(two);
		two.getLinkState().connectSite(one);
		int linkIndex = this.generateNextLinkIndex();
		one.setLinkIndex(linkIndex);
		two.setLinkIndex(linkIndex);
	}

	public void breakConnection(CSite one) {
		one.getLinkState().setFree();
	}

	private int generateNextLinkIndex() {
		return (int)linkIndexGenerator .generateNext();
	}
}

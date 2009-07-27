package com.plectix.simulator.gui.lib;


/**
 * This is simply an empty panel
 * 
 * @author ecemis
 */
public class MainPanel extends GridBagPanel {
	
	public MainPanel() {
		super();
	}

	/**
	 * This method is called automatically by the Spring Framework
	 * after all beans have been created.  Here we set up our sub-
	 * panels: the tree panel on the left and the table panel on 
	 * the right, with a split pane to contain them.
	 */
	public void initialize() {
		// nothing to do
	}
	
}
